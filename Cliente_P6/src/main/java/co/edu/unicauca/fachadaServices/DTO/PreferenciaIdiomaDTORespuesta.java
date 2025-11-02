package co.edu.unicauca.fachadaServices.DTO;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) que representa una preferencia calculada por idioma.
 * <p>
 * Esta clase encapsula el resultado del conteo de reproducciones para un idioma
 * específico, indicando cuántas veces un usuario ha escuchado canciones en dicho
 * idioma.
 * <p>
 * Implementa {@link Serializable} para permitir que sus instancias sean
 * transmitidas a través de Java RMI desde el Servidor de Preferencias hacia el
 * Cliente. La anotación {@code @Data} de Lombok genera automáticamente los
 * getters, setters, y otros métodos estándar.
 *
 * @see PreferenciasDTORespuesta
 */
@Data
@NoArgsConstructor
public class PreferenciaIdiomaDTORespuesta implements Serializable {

    /**
     * El nombre del idioma (ej. "Español", "Inglés").
     * <p>
     * Corresponde al idioma de las canciones que han sido contabilizadas.
     */
    private String nombreIdioma;

    /**
     * El número total de veces que el usuario ha escuchado canciones en este idioma.
     * <p>
     * Este valor es el resultado de la agregación de reproducciones.
     */
    private Integer numeroPreferencias;
}