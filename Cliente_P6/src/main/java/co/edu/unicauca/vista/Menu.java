package co.edu.unicauca.vista;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

import co.edu.unicauca.fachadaServices.DTO.CancionDTO;
import co.edu.unicauca.fachadaServices.DTO.PreferenciasDTORespuesta;
import co.edu.unicauca.fachadaServices.services.FachadaGestorUsuariosIml;
import co.edu.unicauca.utilidades.CancionesHttpClient;
import co.edu.unicauca.utilidades.StreamingAudioClient;
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
        streamingClient.reproducirCancion(nombreArchivo);
        
        System.out.println("\n+++ Reproduciendo '" + cancionSeleccionada.getTitulo() + "'... Presione Enter para detener. +++");
        try {
            System.in.read();
            while (System.in.available() > 0) System.in.read();
        } catch (IOException e) {
            // Ignorado.
        }
        
        streamingClient.detenerReproduccion();
        System.out.println("--- Reproduccion detenida. Volviendo al menu principal. ---");
    }

    private void opcionVerPreferencias() {
        streamingClient.detenerReproduccion();
        System.out.println("\nConsultando sus preferencias...");
        try {
            PreferenciasDTORespuesta respuesta = this.objFachada.getReferencias(this.userId);
            System.out.println("\n== Sus Preferencias ==");

            System.out.println("\nGeneros mas escuchados:");
            if (respuesta.getPreferenciasGeneros().isEmpty()) {
                System.out.println("   - Aun no ha escuchado canciones.");
            } else {
                respuesta.getPreferenciasGeneros().forEach(genero ->
                    System.out.println("   - " + genero.getNombreGenero() + " (" + genero.getNumeroPreferencias() + " veces)"));
            }

            System.out.println("\nArtistas mas escuchados:");
            if (respuesta.getPreferenciasArtistas().isEmpty()) {
                System.out.println("   - Aun no ha escuchado canciones.");
            } else {
                respuesta.getPreferenciasArtistas().forEach(artista ->
                    System.out.println("   - " + artista.getNombreArtista() + " (" + artista.getNumeroPreferencias() + " veces)"));
            }
        } catch (RemoteException e) {
            System.out.println("Error al consultar preferencias: " + e.getMessage());
        }
    }

    
}
