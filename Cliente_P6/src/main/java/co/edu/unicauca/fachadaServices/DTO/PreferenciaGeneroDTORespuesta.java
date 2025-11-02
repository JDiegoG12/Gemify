package co.edu.unicauca.fachadaServices.DTO;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) que representa una preferencia calculada por género
 * musical.
 * <p>
 * Esta clase encapsula el resultado del conteo de reproducciones para un género
 * específico, indicando cuántas veces un usuario ha escuchado canciones de dicho
 * género.
 * <p>
 * Implementa {@link Serializable} para permitir que sus instancias sean
 * transmitidas a través de Java RMI desde el Servidor de Preferencias hacia el
 * Cliente. Las anotaciones de Lombok simplifican la clase generando
 * automáticamente los métodos necesarios.
 *
 * @see PreferenciasDTORespuesta
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreferenciaGeneroDTORespuesta implements Serializable {

    /**
     * El nombre del género musical (ej. "Rock", "Pop", "Salsa").
     * <p>
     * Corresponde al género de las canciones que han sido contabilizadas.
     */
    private String nombreGenero;

    /**
     * El número total de veces que el usuario ha escuchado canciones de este género.
     * <p>
     * Este valor es el resultado de la agregación de reproducciones.
     */
    private Integer numeroPreferencias;
}