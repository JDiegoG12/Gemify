package co.edu.unicauca.vista;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

import co.edu.unicauca.fachadaServices.DTO.CancionDTO;
import co.edu.unicauca.fachadaServices.DTO.PreferenciasDTORespuesta;
import co.edu.unicauca.fachadaServices.services.FachadaGestorUsuariosIml;
import co.edu.unicauca.infraestructura.CancionesHttpClient;
import co.edu.unicauca.infraestructura.StreamingAudioClient;
import co.edu.unicauca.utilidades.UtilidadesConsola;

/**
 * Clase que representa el menú interactivo del cliente.
 */
public class Menu {

    private final FachadaGestorUsuariosIml objFachada;
    private final Integer userId;
    private final CancionesHttpClient cancionesClient;
    private final StreamingAudioClient streamingClient;

    public Menu(FachadaGestorUsuariosIml objFachada, Integer userId) {
        this.objFachada = objFachada;
        this.userId = userId;
        this.cancionesClient = new CancionesHttpClient();
        this.streamingClient = new StreamingAudioClient("localhost", 50051);
    }

    public void ejecutarMenuPrincipal() {
        int opcion;
        do {
            System.out.println("\n=== Menú Principal ===");
            System.out.println("1. Listar canciones ");
            System.out.println("2. Reproducir canción");
            System.out.println("3. Ver mis preferencias");
            System.out.println("4. Salir");
            System.out.println("======================");

            opcion = UtilidadesConsola.leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    opcionListarCanciones();
                    break;
                case 2:
                    opcionReproducirCancion();
                    break;
                case 3:
                    opcionVerPreferencias();
                    break;
                case 4:
                    System.out.println("Cerrando aplicacion... Gracias por usar Gemify!");
                    try {
                        streamingClient.shutdown();
                    } catch (InterruptedException e) {
                        System.err.println("Error al cerrar la conexion gRPC: " + e.getMessage());
                    }
                    break;
                default:
                    System.out.println("Opcion no valida. Intente nuevamente.");
            }
        } while (opcion != 4);
    }
    
    /**
     * Lógica para la Opción 1: Listar canciones con todos los detalles.
     * Mantiene el formato original.
     */
    private void opcionListarCanciones() {
        streamingClient.detenerReproduccion();
        System.out.println("\nObteniendo catalogo de canciones desde el servidor...");
        List<CancionDTO> canciones = this.cancionesClient.listarCanciones();
        if (canciones.isEmpty()) {
            System.out.println("No se encontraron canciones o hubo un error al conectar con el servidor.");
        } else {
            System.out.println("\n== Catalogo de Canciones Detallado ==");
            for (CancionDTO cancion : canciones) {
                // Usa el método toString() que definimos en CancionDTO para la vista completa.
                System.out.println(" - " + cancion.toString());
            }
            System.out.println("=========================================");
        }
    }

    /**
     * Lógica para la Opción 2: Muestra una lista simplificada y numerada para la selección
     * e inicia la reproducción.
     */
    private void opcionReproducirCancion() {
        streamingClient.detenerReproduccion();
        
        System.out.println("\n--- Seleccionar Cancion para Reproducir ---");
        
        // 1. Obtenemos el catálogo del servidor.
        List<CancionDTO> catalogo = this.cancionesClient.listarCanciones();

        if (catalogo == null || catalogo.isEmpty()) {
            System.out.println("No hay canciones disponibles para reproducir.");
            return;
        }

        // 2. Mostramos la lista SIMPLIFICADA y NUMERADA solo con Título y Artista.
        System.out.println("\n== Elija una cancion ==");
        for (int i = 0; i < catalogo.size(); i++) {
            CancionDTO cancion = catalogo.get(i);
            System.out.printf("%d. %s - %s\n", (i + 1), cancion.getTitulo(), cancion.getArtista());
        }
        System.out.println("=======================");

        // 3. Pedimos al usuario que elija un número y validamos la entrada.
        int numeroCancion = 0;
        while (numeroCancion < 1 || numeroCancion > catalogo.size()) {
            numeroCancion = UtilidadesConsola.leerEntero("Ingrese el numero de la cancion: ");
            if (numeroCancion < 1 || numeroCancion > catalogo.size()) {
                System.out.println("Numero no valido. Por favor, elija un numero de la lista.");
            }
        }

        // 4. Obtenemos la canción y construimos el nombre del archivo ("Titulo.mp3").
        CancionDTO cancionSeleccionada = catalogo.get(numeroCancion - 1);
        String nombreArchivo = cancionSeleccionada.getTitulo() + ".mp3";

        // 5. Inicia la reproducción y la lógica de espera/detención.
        streamingClient.reproducirCancion(nombreArchivo, this.userId);
        
        System.out.println("\n Reproduciendo " + cancionSeleccionada.getTitulo());
        System.out.println(" Presione ENTER para detener la reproduccion o continuar al menú...");
        try {
            System.in.read();
            while (System.in.available() > 0) System.in.read();
        } catch (IOException e) {
            // Ignorado.
        }
        
        streamingClient.detenerReproduccion();
        System.out.println("--- Volviendo al menu principal. ---");
    }

     private void opcionVerPreferencias() {
        streamingClient.detenerReproduccion();
        System.out.println("\nConsultando sus preferencias desde el servidor...");
        try {
            // 1. Obtenemos el objeto de respuesta completo del servidor de preferencias.
            PreferenciasDTORespuesta respuesta = this.objFachada.getReferencias(this.userId);

            // Primero, verificamos si hay alguna preferencia.
            boolean sinPreferencias = (respuesta.getPreferenciasGeneros() == null || respuesta.getPreferenciasGeneros().isEmpty())
                                   && (respuesta.getPreferenciasArtistas() == null || respuesta.getPreferenciasArtistas().isEmpty())
                                   && (respuesta.getPreferenciasIdiomas() == null || respuesta.getPreferenciasIdiomas().isEmpty());

            if (sinPreferencias) {
                System.out.println("\n== Sus Preferencias ==");
                System.out.println("Aun no tiene suficientes reproducciones para calcular sus preferencias.");
                return;
            }
            
            System.out.println("\n== Sus Preferencias ==");

            // 2. Mostramos las preferencias por Género.
            System.out.println("\n-- Generos mas escuchados --");
            respuesta.getPreferenciasGeneros().forEach(genero ->
                System.out.printf("   - %s (%d veces)\n", genero.getNombreGenero(), genero.getNumeroPreferencias()));

            // 3. Mostramos las preferencias por Artista.
            System.out.println("\n-- Artistas mas escuchados --");
            respuesta.getPreferenciasArtistas().forEach(artista ->
                System.out.printf("   - %s (%d veces)\n", artista.getNombreArtista(), artista.getNumeroPreferencias()));

            // --- INICIO DEL CAMBIO ---
            // 4. Mostramos las nuevas preferencias por Idioma.
            System.out.println("\n-- Idiomas mas escuchados --");
            // Verificamos que la lista no sea nula antes de intentar recorrerla.
            if (respuesta.getPreferenciasIdiomas() != null && !respuesta.getPreferenciasIdiomas().isEmpty()) {
                respuesta.getPreferenciasIdiomas().forEach(idioma ->
                    System.out.printf("   - %s (%d veces)\n", idioma.getNombreIdioma(), idioma.getNumeroPreferencias()));
            } else {
                // Esto no debería pasar si hay otras preferencias, pero es una buena verificación.
                System.out.println("   - No hay datos de idiomas.");
            }
            // --- FIN DEL CAMBIO ---

        } catch (RemoteException e) {
            System.out.println("ERROR al consultar las preferencias: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ocurrio un error inesperado: " + e.getMessage());
        }
    }

    
}
