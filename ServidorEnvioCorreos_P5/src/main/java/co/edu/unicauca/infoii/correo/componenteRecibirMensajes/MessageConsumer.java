package co.edu.unicauca.infoii.correo.componenteRecibirMensajes;

import co.edu.unicauca.infoii.correo.DTOs.CancionAlmacenarDTOInput;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Componente encargado de consumir mensajes de la cola de RabbitMQ y simular el envío de correos electrónicos.
 * <p>
 * Esta clase implementa el patrón de <i>Message Listener</i> para reaccionar de forma asíncrona
 * a la publicación de nuevas canciones en el sistema. Al recibir un mensaje desde la cola
 * {@code notificaciones_canciones}, genera una simulación de correo electrónico que incluye:
 * <ul>
 *   <li>Metadatos de la canción (título, artista, género, idioma)</li>
 *   <li>Fecha y hora de registro</li>
 *   <li>Una frase motivadora seleccionada aleatoriamente</li>
 * </ul>
 * <p>
 * Forma parte de la capa de infraestructura del servidor de envío de correos, actuando como
 * adaptador entre el sistema de mensajería (RabbitMQ) y la lógica de notificación.
 * 
 */
@Service
public class MessageConsumer {

    /**
     * Conjunto de frases motivadoras predefinidas para incluir en los correos simulados.
     * <p>
     * Estas frases cumplen con el requerimiento funcional de añadir un mensaje inspirador
     * en cada notificación de nueva canción.
     */
    private static final String[] FRASES_MOTIVADORAS = {
        "¡La música es el lenguaje universal!",
        "¡Que el ritmo no pare!",
        "¡Sube el volumen y olvida los problemas!",
        "Cada canción es una nueva historia por descubrir.",
        "La vida suena mejor con música."
    };

    /**
     * Generador de números aleatorios utilizado para seleccionar una frase motivadora.
     * <p>
     * Se inicializa una sola vez para evitar la sobrecarga de crear múltiples instancias.
     */
    private final Random random = new Random();

    /**
     * Método listener que se ejecuta automáticamente cuando llega un mensaje a la cola de RabbitMQ.
     * <p>
     * Este método es invocado por Spring AMQP al detectar un nuevo mensaje en la cola
     * {@code notificaciones_canciones}. Deserializa el mensaje en un objeto
     * {@link CancionAlmacenarDTOInput} y genera una simulación de correo electrónico en la consola.
     * 
     * @param cancionRecibida objeto DTO con los metadatos de la canción recién registrada
     * 
     * @implNote Este método no lanza excepciones, ya que cualquier error en la simulación
     *           de correo no debe interrumpir el flujo del sistema ni reencolar el mensaje.
     *           En un entorno real, se registrarían logs y se implementaría manejo de errores.
     * 
     * @see CancionAlmacenarDTOInput
     * @see RabbitListener
     */
    @RabbitListener(queues = "notificaciones_canciones")
    public void receiveMessage(CancionAlmacenarDTOInput cancionRecibida) {
        System.out.println("\n============================================");
        System.out.println(" Mensaje recibido de la cola 'notificaciones_canciones'");
        
        System.out.println("-> Datos crudos recibidos: " + cancionRecibida.toString());

        System.out.println("\n Simulando envío de correo electrónico...");
        
        // Fecha y hora actual
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fechaFormateada = ahora.format(formatter);
        
        // Seleccionar una frase motivadora al azar
        String fraseMotivadora = FRASES_MOTIVADORAS[random.nextInt(FRASES_MOTIVADORAS.length)];

        // Imprimir el cuerpo del correo simulado
        System.out.println("\n--- INICIO DEL CORREO ---");
        System.out.println("Asunto: ¡Nueva canción añadida a Gemify!");
        System.out.println("Fecha de registro: " + fechaFormateada);
        System.out.println("\nHola,");
        System.out.println("Te informamos que se ha añadido una nueva canción a nuestro catálogo:");
        
        System.out.println("\n  - Título:  " + cancionRecibida.getTitulo());
        System.out.println("  - Artista: " + cancionRecibida.getArtista());
        System.out.println("  - Género:  " + cancionRecibida.getGenero());
        System.out.println("  - Idioma:  " + cancionRecibida.getIdioma());
        
        System.out.println("\n" + fraseMotivadora);
        System.out.println("\nGracias por ser parte de Gemify.");
        System.out.println("--- FIN DEL CORREO ---");
        System.out.println("============================================");
    }
}