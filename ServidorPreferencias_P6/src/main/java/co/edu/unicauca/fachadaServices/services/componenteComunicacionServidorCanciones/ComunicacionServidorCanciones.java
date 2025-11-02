package co.edu.unicauca.fachadaServices.services.componenteComunicacionServidorCanciones;

import co.edu.unicauca.fachadaServices.DTO.CancionDTOEntrada;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Componente responsable de la comunicación síncrona vía REST con el
 * microservicio {@code Servidor de Canciones}.
 * <p>
 * Esta clase utiliza la librería OpenFeign para construir un cliente HTTP
 * declarativo. Su función principal es abstraer los detalles de la
 * comunicación de red, proporcionando un método simple para obtener el
 * catálogo completo de canciones.
 *
 * @see CancionesRemoteClient
 * @see co.edu.unicauca.fachadaServices.services.PreferenciasServiceImpl
 */
public class ComunicacionServidorCanciones {

    /**
     * URL base del Servidor de Canciones. Todas las peticiones definidas en
     * {@link CancionesRemoteClient} se resolverán relativas a esta URL.
     */
    private static final String BASE_URL = "http://localhost:5000";

    /**
     * Instancia del cliente Feign, generado dinámicamente a partir de la
     * interfaz {@link CancionesRemoteClient}.
     */
    private final CancionesRemoteClient client;

    /**
     * Construye una nueva instancia del componente de comunicación.
     * <p>
     * En el constructor se configura y construye el cliente Feign:
     * <ul>
     *   <li><b>{@code Feign.builder()}</b>: Inicia el proceso de construcción del cliente.</li>
     *   <li><b>{@code .decoder(new JacksonDecoder())}</b>: Especifica que las respuestas
     *       JSON deben ser decodificadas y mapeadas a objetos Java utilizando la
     *       librería Jackson.</li>
     *   <li><b>{@code .target(...)}</b>: Vincula la interfaz {@code CancionesRemoteClient}
     *       con la URL base, creando una implementación concreta y funcional del
     *       cliente REST.</li>
     * </ul>
     */
    public ComunicacionServidorCanciones() {
        this.client = Feign.builder()
                .decoder(new JacksonDecoder())
                .target(CancionesRemoteClient.class, BASE_URL);
    }

    /**
     * Realiza una llamada remota al Servidor de Canciones para obtener el
     * catálogo completo.
     * <p>
     * Invoca el método {@code obtenerCanciones()} definido en la interfaz
     * {@link CancionesRemoteClient}. Incluye manejo de errores básico para
     * prevenir que la aplicación falle si el servidor no está disponible.
     *
     * @return una lista de objetos {@link CancionDTOEntrada} que representan el
     *         catálogo de canciones. Si la comunicación falla o el servidor no
     *         retorna datos, devuelve una lista vacía para garantizar la
     *         seguridad del llamador.
     */
    public List<CancionDTOEntrada> obtenerCancionesRemotas() {
        System.out.println("--> ComunicacionServidorCanciones: Realizando peticion GET a " + BASE_URL + "/canciones");
        try {
            List<CancionDTOEntrada> canciones = client.obtenerCanciones();
            System.out.println("--> ComunicacionServidorCanciones: Se recibieron " + canciones.size() + " canciones.");
            return canciones != null ? canciones : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("ERROR al comunicar con el Servidor de Canciones: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}