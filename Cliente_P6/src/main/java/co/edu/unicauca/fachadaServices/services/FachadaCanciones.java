package co.edu.unicauca.fachadaServices.services;

import java.util.List;
import co.edu.unicauca.fachadaServices.DTO.CancionDTO;
import co.edu.unicauca.infraestructura.CancionesHttpClient;

/**
 * Implementación del patrón Facade para acceder a las funcionalidades del
 * microservicio de Canciones.
 * <p>
 * Esta clase proporciona un punto de acceso simplificado y unificado para que
 * las capas superiores de la aplicación (como la vista) interactúen con el
 * servicio de canciones. Su responsabilidad es ocultar la complejidad de la
 * comunicación de red subyacente, que es manejada por
 * {@link CancionesHttpClient}.
 *
 * @see co.edu.unicauca.vista.Menu
 * @see CancionesHttpClient
 */
public class FachadaCanciones {

    /**
     * Instancia del cliente HTTP que se encarga de la comunicación real
     * con el servidor de canciones.
     */
    private final CancionesHttpClient clienteCanciones;

    /**
     * Construye una nueva instancia de la fachada de canciones.
     * <p>
     * En el constructor se inicializa el cliente HTTP, que será utilizado para
     * todas las operaciones relacionadas con el servicio de canciones.
     */
    public FachadaCanciones() {
        this.clienteCanciones = new CancionesHttpClient();
    }

    /**
     * Obtiene el catálogo completo de canciones disponibles en el sistema.
     * <p>
     * Delega la llamada al {@link CancionesHttpClient} para que realice la
     * petición REST al microservicio correspondiente.
     *
     * @return una lista de objetos {@link CancionDTO} con los metadatos de las
     *         canciones. Si ocurre un error durante la comunicación, el cliente
     *         subyacente devolverá una lista vacía.
     */
    public List<CancionDTO> listarCanciones() {
        return this.clienteCanciones.listarCanciones();
    }
}