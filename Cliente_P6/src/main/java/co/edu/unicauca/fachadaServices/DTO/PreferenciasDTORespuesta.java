package co.edu.unicauca.fachadaServices.DTO;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * Data Transfer Object (DTO) que encapsula el conjunto completo de preferencias
 * musicales calculadas para un usuario.
 * <p>
 * Esta clase es el objeto principal de respuesta que el Servidor de Preferencias
 * envía al Cliente a través de Java RMI. Agrupa las preferencias del usuario
 * en tres categorías principales: por género, por artista y por idioma.
 * <p>
 * Implementa {@link Serializable} para ser compatible con la serialización de RMI.
 * La anotación {@code @Data} de Lombok se encarga de generar los métodos
 * de acceso (getters/setters) y otros métodos de utilidad.
 *
 * @see co.edu.unicauca.fachadaServices.services.IPreferenciasService
 */
@Data
public class PreferenciasDTORespuesta implements Serializable {

    /**
     * El identificador único del usuario al que pertenecen estas preferencias.
     */
    private int idUsuario;

    /**
     * Una lista de las preferencias del usuario por género musical.
     * <p>
     * Cada elemento de la lista contiene un género y el número de veces que el
     * usuario ha escuchado canciones de ese género. La lista suele estar ordenada
     * de mayor a menor preferencia.
     *
     * @see PreferenciaGeneroDTORespuesta
     */
    private List<PreferenciaGeneroDTORespuesta> preferenciasGeneros;

    /**
     * Una lista de las preferencias del usuario por artista.
     * <p>
     * Cada elemento contiene un artista y el número total de reproducciones de sus
     * canciones por parte del usuario. La lista suele estar ordenada de mayor a
     * menor preferencia.
     *
     * @see PreferenciaArtistaDTORespuesta
     */
    private List<PreferenciaArtistaDTORespuesta> preferenciasArtistas;

    /**
     * Una lista de las preferencias del usuario por idioma de la canción.
     * <p>
     * Cada elemento contiene un idioma y el número de veces que el usuario ha
     * escuchado canciones en ese idioma. La lista suele estar ordenada de mayor
     * a menor preferencia.
     *
     * @see PreferenciaIdiomaDTORespuesta
     */
    private List<PreferenciaIdiomaDTORespuesta> preferenciasIdiomas;
}