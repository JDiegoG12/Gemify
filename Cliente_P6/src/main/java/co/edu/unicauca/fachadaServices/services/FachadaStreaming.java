package co.edu.unicauca.fachadaServices.services;

import co.edu.unicauca.infraestructura.StreamingAudioClient;

/**
 * Implementación del patrón Facade para acceder a las funcionalidades del
 * servidor de Streaming.
 * <p>
 * Esta clase proporciona un punto de acceso simplificado para que las capas
 * superiores de la aplicación (como la vista) puedan iniciar, detener y
 * gestionar la reproducción de audio en streaming. Oculta la complejidad de la
 * comunicación gRPC y el manejo de hilos, que son responsabilidad de
 * {@link StreamingAudioClient}.
 *
 * @see co.edu.unicauca.vista.Menu
 * @see StreamingAudioClient
 */
public class FachadaStreaming {

    /**
     * Instancia del cliente gRPC que maneja la comunicación real con el
     * servidor de streaming y la lógica de reproducción.
     */
    private final StreamingAudioClient clienteStreaming;

    /**
     * Construye una nueva instancia de la fachada de streaming.
     * <p>
     * En el constructor se inicializa el cliente gRPC, apuntando a la dirección
     * y puerto predeterminados del servidor de streaming.
     */
    public FachadaStreaming() {
        // La dirección y el puerto están codificados en el constructor del cliente.
        this.clienteStreaming = new StreamingAudioClient("localhost", 50051);
    }

    /**
     * Inicia la reproducción de una canción.
     * <p>
     * Delega la llamada al {@link StreamingAudioClient}, que se encargará de
     * gestionar el flujo de datos y la reproducción en hilos separados.
     *
     * @param nombreCancion el nombre del archivo de la canción a reproducir.
     * @param idUsuario el ID del usuario que solicita la reproducción.
     */
    public void reproducirCancion(String nombreCancion, int idUsuario) {
        this.clienteStreaming.reproducirCancion(nombreCancion, idUsuario);
    }

    /**
     * Detiene cualquier reproducción de audio que esté actualmente en curso.
     * <p>
     * Delega la llamada al método correspondiente en el cliente de streaming.
     */
    public void detenerReproduccion() {
        this.clienteStreaming.detenerReproduccion();
    }

    /**
     * Cierra de forma ordenada todos los recursos asociados al cliente de streaming.
     * <p>
     * Es crucial llamar a este método al finalizar la aplicación para liberar
     * la conexión de red (canal gRPC) y detener los pools de hilos.
     *
     * @throws InterruptedException si el hilo es interrumpido mientras espera el
     *                              cierre de los recursos.
     */
    public void shutdown() throws InterruptedException {
        this.clienteStreaming.shutdown();
    }
}