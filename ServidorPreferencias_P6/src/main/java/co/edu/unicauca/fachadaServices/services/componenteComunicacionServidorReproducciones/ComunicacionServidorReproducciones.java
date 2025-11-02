package co.edu.unicauca.fachadaServices.services.componenteComunicacionServidorReproducciones;

import co.edu.unicauca.fachadaServices.DTO.ReproduccionesDTOEntrada;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Componente responsable de la comunicación síncrona vía REST con el
 * microservicio {@code Servidor de Reproducciones}.
 * <p>
 * Esta clase utiliza la librería OpenFeign para construir un cliente HTTP
 * que consume los endpoints del servidor de reproducciones. Su función
 * es abstraer la comunicación de red, proporcionando un método para obtener
 * el historial de reproducciones de un usuario específico.
 *
 * @see ReproduccionesRemoteClient
 * @see co.edu.unicauca.fachadaServices.services.PreferenciasServiceImpl
 */
public class ComunicacionServidorReproducciones {

    /**
     * URL base del Servidor de Reproducciones. Todas las peticiones definidas en
     * {@link ReproduccionesRemoteClient} se resolverán relativas a esta URL.
     */
    private static final String BASE_URL = "http://localhost:5002";
    
    /**
     * Instancia del cliente Feign, generado dinámicamente a partir de la
     * interfaz {@link ReproduccionesRemoteClient}.
     */
    private final ReproduccionesRemoteClient client;

    /**
     * Construye una nueva instancia del componente de comunicación.
     * <p>
     * En el constructor se configura y construye el cliente Feign:
     * <ul>
     *   <li><b>{@code Feign.builder()}</b>: Inicia el proceso de construcción del cliente.</li>
     *   <li><b>{@code .decoder(new JacksonDecoder())}</b>: Especifica que las respuestas
     *       JSON deben ser decodificadas y mapeadas a objetos Java utilizando la
     *       librería Jackson.</li>
     *   <li><b>{@code .target(...)}</b>: Vincula la interfaz {@code ReproduccionesRemoteClient}
     *       con la URL base, creando una implementación concreta y funcional del
     *       cliente REST.</li>
     * </ul>
     */
    public ComunicacionServidorReproducciones() {
        this.client = Feign.builder()
                .decoder(new JacksonDecoder())
                .target(ReproduccionesRemoteClient.class, BASE_URL);
    }

    /**
     * Realiza una llamada remota al Servidor de Reproducciones para obtener el
     * historial de un usuario específico.
     * <p>
     * Invoca el método {@code obtenerReproducciones()} definido en la interfaz
     * {@link ReproduccionesRemoteClient}, pasando el ID del usuario como
     * parámetro. Incluye manejo de errores para garantizar que la aplicación
     * no falle si el servicio remoto no está disponible.
     *
     * @param idUsuario el identificador único del usuario cuyo historial de
     *                  reproducciones se desea consultar.
     * @return una lista de objetos {@link ReproduccionesDTOEntrada} que representan
     *         el historial. Si la comunicación falla o no hay datos, devuelve
     *         una lista vacía.
     */
    public List<ReproduccionesDTOEntrada> obtenerReproduccionesRemotas(Integer idUsuario) {
        System.out.println("--> ComunicacionServidorReproducciones: Realizando peticion GET a " + BASE_URL + "/reproducciones?idUsuario=" + idUsuario);
        try {
            List<ReproduccionesDTOEntrada> reproducciones = client.obtenerReproducciones(idUsuario);
            System.out.println("--> ComunicacionServidorReproducciones: Se recibieron " + reproducciones.size() + " reproducciones.");
            return reproducciones != null ? reproducciones : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("ERROR al comunicar con el Servidor de Reproducciones: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}