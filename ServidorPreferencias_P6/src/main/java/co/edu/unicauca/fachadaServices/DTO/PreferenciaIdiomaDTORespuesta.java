package co.edu.unicauca.fachadaServices.DTO;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PreferenciaIdiomaDTORespuesta implements Serializable {
    private String nombreIdioma;
    private Integer numeroPreferencias;
}