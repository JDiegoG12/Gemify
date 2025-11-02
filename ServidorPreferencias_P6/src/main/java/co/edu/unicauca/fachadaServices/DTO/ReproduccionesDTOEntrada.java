package co.edu.unicauca.fachadaServices.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para mapear la respuesta JSON del Servidor de Reproducciones.
 * Contiene los datos de una única reproducción.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReproduccionesDTOEntrada {
    
    // El JSON del servidor Go usa 'idUsuario'.
    @JsonProperty("idUsuario")
    private Integer idUsuario;

    // El JSON del servidor Go usa 'titulo'.
    @JsonProperty("titulo")
    private String titulo;

    // El JSON del servidor Go usa 'fechaHora'.
    @JsonProperty("fechaHora")
    private String fechaHora;
}