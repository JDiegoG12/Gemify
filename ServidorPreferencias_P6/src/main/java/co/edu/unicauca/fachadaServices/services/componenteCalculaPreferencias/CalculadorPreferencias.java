package co.edu.unicauca.fachadaServices.services.componenteCalculaPreferencias;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import co.edu.unicauca.fachadaServices.DTO.CancionDTOEntrada;
import co.edu.unicauca.fachadaServices.DTO.PreferenciaArtistaDTORespuesta;
import co.edu.unicauca.fachadaServices.DTO.PreferenciaGeneroDTORespuesta;
import co.edu.unicauca.fachadaServices.DTO.PreferenciaIdiomaDTORespuesta;
import co.edu.unicauca.fachadaServices.DTO.PreferenciasDTORespuesta;
import co.edu.unicauca.fachadaServices.DTO.ReproduccionesDTOEntrada;

/**
 * Componente de lógica de negocio responsable de calcular las preferencias
 * musicales de un usuario.
 * <p>
 * Esta clase contiene el algoritmo principal que procesa el catálogo de canciones
 * y el historial de reproducciones de un usuario para generar estadísticas
 * agregadas por género, artista e idioma.
 *
 * @see co.edu.unicauca.fachadaServices.services.PreferenciasServiceImpl
 */
public class CalculadorPreferencias {

    /**
     * Calcula las preferencias musicales de un usuario basándose en su historial
     * de reproducciones y el catálogo de canciones.
     * <p>
     * El algoritmo sigue estos pasos:
     * <ol>
     *   <li>Convierte la lista del catálogo de canciones en un mapa para una
     *       búsqueda eficiente por título.</li>
     *   <li>Inicializa contadores para géneros, artistas e idiomas.</li>
     *   <li>Itera sobre el historial de reproducciones del usuario. Por cada
     *       reproducción, busca la canción correspondiente en el mapa del catálogo.</li>
     *   <li>Si la canción se encuentra, incrementa los contadores para el género,
     *       artista e idioma de esa canción.</li>
     *   <li>Convierte los contadores en listas de DTOs de respuesta.</li>
     *   <li>Ordena cada lista de preferencias de mayor a menor número de
     *       reproducciones.</li>
     *   <li>Ensambla y retorna el objeto {@link PreferenciasDTORespuesta} final.</li>
     * </ol>
     *
     * @param idUsuario el identificador del usuario para el cual se calculan las
     *                  preferencias.
     * @param catalogoCompleto una lista de todas las canciones disponibles en el
     *                         sistema, obtenida del Servidor de Canciones.
     * @param reproduccionesUsuario una lista del historial de reproducciones
     *                              específico para el {@code idUsuario}, obtenida
     *                              del Servidor de Reproducciones.
     * @return un objeto {@link PreferenciasDTORespuesta} poblado con las listas
     *         de preferencias calculadas y ordenadas.
     */
    public PreferenciasDTORespuesta calcular(Integer idUsuario, List<CancionDTOEntrada> catalogoCompleto, List<ReproduccionesDTOEntrada> reproduccionesUsuario) {
        
        System.out.println("--> CalculadorPreferencias: Iniciando calculo para el usuario " + idUsuario);
        System.out.println("    Recibidas " + catalogoCompleto.size() + " canciones del catalogo y " + reproduccionesUsuario.size() + " reproducciones.");

        // Crea un mapa del catálogo usando el TÍTULO de la canción como clave
        // para una búsqueda O(1). Se manejan duplicados manteniendo el primer elemento.
        Map<String, CancionDTOEntrada> mapaCatalogo = catalogoCompleto.stream()
                .collect(Collectors.toMap(CancionDTOEntrada::getTitulo, cancion -> cancion, (existente, nueva) -> existente));

        // Inicializa los mapas que funcionarán como contadores.
        Map<String, Integer> contadorGeneros = new HashMap<>();
        Map<String, Integer> contadorArtistas = new HashMap<>();
        Map<String, Integer> contadorIdiomas = new HashMap<>();

        // Itera sobre las reproducciones del usuario para agregar los datos.
        for (ReproduccionesDTOEntrada reproduccion : reproduccionesUsuario) {
            // Busca los metadatos de la canción reproducida en el mapa del catálogo.
            CancionDTOEntrada cancion = mapaCatalogo.get(reproduccion.getTitulo());

            if (cancion != null) {
                // Si la canción existe en el catálogo, incrementa los contadores correspondientes.
                String genero = cancion.getGenero();
                String artista = cancion.getArtista();
                String idioma = cancion.getIdioma();

                contadorGeneros.put(genero, contadorGeneros.getOrDefault(genero, 0) + 1);
                contadorArtistas.put(artista, contadorArtistas.getOrDefault(artista, 0) + 1);
                contadorIdiomas.put(idioma, contadorIdiomas.getOrDefault(idioma, 0) + 1);
            }
        }
        
        // Convierte los mapas de contadores en listas de DTOs y las ordena.
        List<PreferenciaGeneroDTORespuesta> prefsGeneros = contadorGeneros.entrySet().stream()
                .map(entry -> new PreferenciaGeneroDTORespuesta(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingInt(PreferenciaGeneroDTORespuesta::getNumeroPreferencias).reversed())
                .collect(Collectors.toList());

        List<PreferenciaArtistaDTORespuesta> prefsArtistas = contadorArtistas.entrySet().stream()
                .map(entry -> new PreferenciaArtistaDTORespuesta(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingInt(PreferenciaArtistaDTORespuesta::getNumeroPreferencias).reversed())
                .collect(Collectors.toList());
        
        List<PreferenciaIdiomaDTORespuesta> prefsIdiomas = contadorIdiomas.entrySet().stream()
                .map(entry -> {
                    PreferenciaIdiomaDTORespuesta dto = new PreferenciaIdiomaDTORespuesta();
                    dto.setNombreIdioma(entry.getKey());
                    dto.setNumeroPreferencias(entry.getValue());
                    return dto;
                })
                .sorted(Comparator.comparingInt(PreferenciaIdiomaDTORespuesta::getNumeroPreferencias).reversed())
                .collect(Collectors.toList());

        // Ensambla el objeto de respuesta final con todas las listas de preferencias.
        PreferenciasDTORespuesta respuesta = new PreferenciasDTORespuesta();
        respuesta.setIdUsuario(idUsuario);
        respuesta.setPreferenciasGeneros(prefsGeneros);
        respuesta.setPreferenciasArtistas(prefsArtistas);
        respuesta.setPreferenciasIdiomas(prefsIdiomas);

        System.out.println("--> CalculadorPreferencias: Calculo finalizado.");
        return respuesta;
    }
}