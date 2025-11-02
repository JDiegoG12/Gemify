package co.edu.unicauca.main;

import co.edu.unicauca.capaDeControladores.ControladorPreferenciasUsuariosInt;
import co.edu.unicauca.configuracion.lector.LectorPropiedadesConfig;
import co.edu.unicauca.configuracion.servicios.ClienteDeObjetos;
import co.edu.unicauca.fachadaServices.services.FachadaGestorUsuariosIml;
import co.edu.unicauca.modelo.Usuario;
import co.edu.unicauca.utilidades.UtilidadesConsola;
import co.edu.unicauca.vista.Menu;

import java.util.Arrays;
import java.util.List;

/**
 * Punto de entrada de la aplicación cliente de Gemify.
 * <p>
 * Esta clase gestiona el flujo inicial del sistema, que incluye:
 * <ul>
 *   <li>Autenticación simulada con usuarios quemados (máximo 3 intentos)</li>
 *   <li>Conexión con el servidor de preferencias mediante RMI</li>
 *   <li>Lanzamiento del menú interactivo para el usuario autenticado</li>
 * </ul>
 * 
 */
public class Main {

    /**
     * Lista inmutable de usuarios predefinidos para la autenticación simulada.
     * Cada usuario tiene un ID único, nickname y contraseña en texto claro.
     */
    private static final List<Usuario> USUARIOS_QUEMADOS = Arrays.asList(
        new Usuario(1, "juan", "1234"),
        new Usuario(2, "ana", "abcd"),
        new Usuario(3, "carlos", "pass")
    );

    /**
     * Número máximo de intentos permitidos para iniciar sesión.
     * Valor predeterminado: 3 intentos.
     */
    private static final int MAX_INTENTOS = 3;

    /**
     * Método principal que inicia la ejecución del cliente.
     * <p>
     * Solicita credenciales al usuario, valida contra la lista de usuarios quemados,
     * y en caso de éxito, establece conexión con el servidor de preferencias mediante RMI
     * y lanza el menú interactivo.
     * 
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        System.out.println("=== ¡Bienvenido a Gemify! ===");

        Usuario usuarioAutenticado = null;
        for (int intento = 1; intento <= MAX_INTENTOS; intento++) {
            System.out.println("\n(Intento " + intento + " de " + MAX_INTENTOS + ")");
            String nickname = UtilidadesConsola.leerCadena("Nickname: ");
            String password = UtilidadesConsola.leerCadena("Contraseña: ");

            usuarioAutenticado = autenticarUsuario(nickname, password);
            if (usuarioAutenticado != null) {
                break;
            }

            if (intento < MAX_INTENTOS) {
                System.out.println("Credenciales incorrectas. Intente nuevamente.");
            }
        }

        if (usuarioAutenticado == null) {
            System.out.println("\nDemasiados intentos fallidos. Acceso denegado.");
            return;
        }

        System.out.println("\n¡Inicio de sesión exitoso! Bienvenido, " + usuarioAutenticado.getNickname() + ".\n");

        // Configuración de conexión RMI con el servidor de preferencias
        int puertoNS = Integer.parseInt(LectorPropiedadesConfig.get("ns.port"));
        String direccionIPNS = LectorPropiedadesConfig.get("ns.host");
        String identificadorObjetoRemoto = "objControladorPreferenciasUsuarios";

        ControladorPreferenciasUsuariosInt objRemoto = 
            ClienteDeObjetos.obtenerObjetoRemoto(direccionIPNS, puertoNS, identificadorObjetoRemoto);
        
        FachadaGestorUsuariosIml objFachada = new FachadaGestorUsuariosIml(objRemoto);
        Menu objMenu = new Menu(objFachada, usuarioAutenticado.getId());
        objMenu.ejecutarMenuPrincipal();
    }

    /**
     * Valida las credenciales de un usuario contra la lista de usuarios quemados.
     * <p>
     * Compara el nickname y la contraseña proporcionados con los usuarios predefinidos.
     * La comparación es sensible a mayúsculas y minúsculas.
     * 
     * @param nickname el nombre de usuario ingresado
     * @param password la contraseña ingresada
     * @return el objeto {@link Usuario} si las credenciales son válidas, {@code null} en caso contrario
     */
    private static Usuario autenticarUsuario(String nickname, String password) {
        return USUARIOS_QUEMADOS.stream()
            .filter(u -> u.getNickname().equals(nickname) && u.getPassword().equals(password))
            .findFirst()
            .orElse(null);
    }
}