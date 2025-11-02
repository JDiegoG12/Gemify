package co.edu.unicauca.fachadaServices.services.componenteComunicacionServidorCanciones;

import co.edu.unicauca.fachadaServices.DTO.CancionDTOEntrada;
import feign.Headers;
import feign.RequestLine;
import java.util.List;

/**
 * Interfaz declarativa para el cliente Feign que se comunica con el Servidor de
 * Canciones.
 * <p>
 * Utiliza anotaciones de Feign para definir las características de la petición
 * HTTP que se debe realizar. Feign generará dinámicamente una implementación de
 * esta interfaz en tiempo de ejecución, encargándose de toda la lógica de
 * comunicación de red.
 * <p>
 * Esta aproximación permite definir clientes REST de una manera limpia y
 * declarativa, separando la definición de la llamada de su ejecución.
 *
 * @see ComunicacionServidorCanciones
 * @see <a href="https://github.com/OpenFeign/feign">OpenFeign</a>
 */
public interface CancionesRemoteClient {

    /**
     * Define una petición HTTP GET al endpoint {@code /canciones} del Servidor de
     * Canciones.
     * <p>
     * <b>Anotaciones:</b>
     * <ul>
     *   <li>{@code @RequestLine("GET /canciones")}: Especifica el método HTTP (GET) y
     *       la ruta del recurso a consumir. Esta ruta se concatena con la
     *       URL base definida en el constructor de Feign.</li>
     *   <li>{@code @Headers("Accept: application/json")}: Añade una cabecera a la
     *       petición, indicando que el cliente espera recibir una respuesta en
     *       formato JSON.</li>
     * </ul>
     *
     * @return una lista de objetos {@link CancionDTOEntrada}, que Feign
     *         deserializará automáticamente a partir del cuerpo de la respuesta
     *         JSON gracias al decodificador Jackson configurado.
     */
    @RequestLine("GET /canciones")
    @Headers("Accept: application/json")
    List<CancionDTOEntrada> obtenerCanciones();

}