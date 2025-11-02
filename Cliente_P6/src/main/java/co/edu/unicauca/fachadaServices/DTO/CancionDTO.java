package co.edu.unicauca.fachadaServices.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa los metadatos de una canción, simplificado con Lombok.
 * Se utiliza para la comunicación entre el cliente y el servidor de canciones.
 */
@Data // Genera automáticamente: getters, setters, toString(), equals() y hashCode()
@NoArgsConstructor // Genera un constructor sin argumentos (importante para Jackson)
@AllArgsConstructor // Genera un constructor con todos los campos como argumentos
public class CancionDTO {

    private String titulo;
    private String artista;
    private String genero;
    private String idioma;

    @Override
    public String toString() {
        return String.format(
            "Titulo: %-25s | Artista: %-20s | Genero: %-10s | Idioma: %-10s",
            titulo, artista, genero, idioma
        );
    }

}