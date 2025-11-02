package co.edu.unicauca.infoii.correo.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa los metadatos de una canción recibida mediante RabbitMQ
 * desde el servidor de canciones (Go).
 * <p>
 * Este objeto se utiliza para la deserialización automática de mensajes JSON
 * publicados en la cola {@code notificaciones_canciones}. Contiene los campos
 * esenciales requeridos para generar la notificación de correo electrónico:
 * título, artista, género, idioma y un mensaje adicional.
 * <p>
 * Está anotado con {@code @Data} y {@code @NoArgsConstructor} de Lombok para facilitar
 * la serialización/deserialización con Jackson.
 */
@Data
@NoArgsConstructor
public class CancionAlmacenarDTOInput {

    private String titulo;
    private String artista;
    private String genero;
    private String idioma;
    private String mensaje;
}