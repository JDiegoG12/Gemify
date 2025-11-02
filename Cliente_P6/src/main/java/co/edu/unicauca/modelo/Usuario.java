package co.edu.unicauca.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa un usuario del sistema de streaming.
 * Contiene la información básica necesaria para la autenticación simulada.
 * Los usuarios se almacenan de forma quemada en memoria durante la ejecución.
 * 
 */
@Data // Genera automáticamente: getters, setters, toString(), equals() y hashCode()
@NoArgsConstructor // Genera un constructor sin argumentos (importante para Jackson)
@AllArgsConstructor 
public class Usuario {

    /**
     * Identificador único del usuario.
     */
    private Integer id;

    /**
     * Nombre de usuario (nickname) que se muestra en la interfaz.
     */
    private String nickname;

    /**
     * Contraseña del usuario (almacenada en texto claro, solo para simulación).
     */
    private String password;

}