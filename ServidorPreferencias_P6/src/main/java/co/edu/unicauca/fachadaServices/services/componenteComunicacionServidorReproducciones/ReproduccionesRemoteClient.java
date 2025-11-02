package co.edu.unicauca.fachadaServices.services.componenteComunicacionServidorReproducciones;

import co.edu.unicauca.fachadaServices.DTO.ReproduccionesDTOEntrada;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.List;

/**
 * Interfaz declarativa para el cliente Feign que se comunica con el Servidor de
 * Reproducciones.
 * <p>
 * Utiliza anotaciones de Feign para definir una petición HTTP GET que permite
 * consultar el historial de reproducciones de un usuario específico. Feign se
 * encarga de generar la implementación de esta interfaz en tiempo de ejecución.
 *
 * @see ComunicacionServidorReproducciones
 * @see <a href="https://github.com/OpenFeign/feign">OpenFeign</a>
 */
public interface ReproduccionesRemoteClient {

    /**
     * Define una petición HTTP GET al endpoint {@code /reproducciones} del Servidor
     * de Reproducciones, utilizando un parámetro de consulta para filtrar por usuario.
     * <p>
     * <b>Anotaciones:</b>
     * <ul>
     *   <li>{@code @RequestLine("GET /reproducciones?idUsuario={idUsuario}")}:
     *       Especifica el método HTTP (GET) y la plantilla de la URL. El
     *       marcador de posición {@code {idUsuario}} será reemplazado por el valor
     *       del parámetro anotado con {@code @Param}.</li>
     *   <li>{@code @Headers("Accept: application/json")}: Indica que el cliente
     *       espera recibir una respuesta en formato JSON.</li>
     *   <li>{@code @Param("idUsuario")}: Vincula el parámetro del método Java
     *       {@code idUsuario} con el marcador de posición {@code {idUsuario}} en la
     *       plantilla de la URL.</li>
     * </ul>
     *
     * @param idUsuario el identificador del usuario por el cual se filtrarán las
     *                  reproducciones.
     * @return una lista de objetos {@link ReproduccionesDTOEntrada}, que Feign
     *         deserializará automáticamente a partir de la respuesta JSON del servidor.
     */
    @RequestLine("GET /reproducciones?idUsuario={idUsuario}")
    @Headers("Accept: application/json")
    List<ReproduccionesDTOEntrada> obtenerReproducciones(@Param("idUsuario") Integer idUsuario);
}