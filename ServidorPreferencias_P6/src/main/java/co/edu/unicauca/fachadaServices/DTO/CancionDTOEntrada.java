package co.edu.unicauca.fachadaServices.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para mapear la respuesta JSON del Servidor de Canciones.
 * Contiene los metadatos de una canción del catálogo.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancionDTOEntrada {
        private String titulo;
    private String artista;
    private String genero;
    private String idioma; 
}