package co.edu.unicauca.fachadaServices.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) que representa los metadatos de una canción.
 * <p>
 * Esta clase se utiliza como un objeto de datos plano para transferir la
 * información de una canción entre diferentes capas y servicios. Específicamente,
 * se usa para:
 * <ul>
 *     <li>Mapear la respuesta JSON del {@code Servidor de Canciones} cuando se
 *         solicita el catálogo de música.</li>
 *     <li>Mostrar la información de las canciones en la vista del cliente.</li>
 * </ul>
 * <p>
 * Las anotaciones de Lombok {@code @Data}, {@code @NoArgsConstructor} y
 * {@code @AllArgsConstructor} simplifican la clase generando automáticamente
 * getters, setters, constructores y otros métodos comunes.
 *
 * @see co.edu.unicauca.infraestructura.CancionesHttpClient
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancionDTO {

    /**
     * El título de la canción.
     */
    private String titulo;

    /**
     * El nombre del artista o banda que interpreta la canción.
     */
    private String artista;

    /**
     * El género musical al que pertenece la canción (ej. "Rock", "Pop").
     */
    private String genero;

    /**
     * El idioma principal de la canción (ej. "Español", "Inglés").
     */
    private String idioma;

    /**
     * Sobrescribe el método {@code toString()} generado por Lombok para proporcionar
     * una representación en cadena de texto formateada y alineada.
     * <p>
     * Este formato es ideal para ser mostrado en una consola o en logs,
     * presentando los datos de la canción de manera clara y legible.
     *
     * @return una cadena de texto con el título, artista, género e idioma
     *         de la canción, formateada como una tabla.
     */
    @Override
    public String toString() {
        return String.format(
            "Titulo: %-25s | Artista: %-20s | Genero: %-10s | Idioma: %s",
            titulo, artista, genero, idioma
        );
    }
}