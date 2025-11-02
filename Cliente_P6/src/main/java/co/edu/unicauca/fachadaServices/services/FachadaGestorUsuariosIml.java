package co.edu.unicauca.fachadaServices.services;

import java.rmi.RemoteException;

import co.edu.unicauca.capaDeControladores.ControladorPreferenciasUsuariosInt;
import co.edu.unicauca.fachadaServices.DTO.PreferenciasDTORespuesta;

/**
 * Implementación del patrón Facade en el lado del cliente.
 * <p>
 * Esta clase simplifica la interacción con el servicio remoto de preferencias.
 * Su propósito es ocultar los detalles de la comunicación RMI (Java Remote
 * Method Invocation) a las capas superiores de la aplicación cliente, como la
 * vista ({@code Menu}).
 * <p>
 * Actúa como un punto de entrada único y local para que la vista solicite
 * las preferencias del usuario, delegando la llamada de red al objeto remoto.
 *
 * @see co.edu.unicauca.vista.Menu
 */
public class FachadaGestorUsuariosIml {

    /**
     * Referencia al objeto remoto (stub) que representa al controlador en el
     * Servidor de Preferencias.
     * <p>
     * Este objeto es el proxy a través del cual se realizan las llamadas RMI.
     */
    private final ControladorPreferenciasUsuariosInt objRemoto;

    /**
     * Construye una nueva instancia de la fachada.
     *
     * @param objRemoto el objeto stub/proxy obtenido del registro RMI, que
     *                  representa la conexión con el servidor.
     */
    public FachadaGestorUsuariosIml(ControladorPreferenciasUsuariosInt objRemoto) {
        this.objRemoto = objRemoto;
    }

    /**
     * Invoca el método remoto para obtener las preferencias calculadas de un
     * usuario específico.
     * <p>
     * Esta función simplemente delega la llamada al método correspondiente en el
     * objeto remoto, manejando la comunicación de red de forma transparente para
     * el llamador.
     *
     * @param id el identificador único del usuario cuyas preferencias se desean
     *           consultar.
     * @return un objeto {@link PreferenciasDTORespuesta} que contiene las listas
     *         de preferencias por género, artista e idioma.
     * @throws RemoteException si ocurre un error durante la comunicación de red
     *                         con el servidor RMI (ej. servidor no disponible,
     *                         problemas de red, etc.).
     */
    public PreferenciasDTORespuesta getReferencias(Integer id) throws RemoteException {
        return this.objRemoto.getReferencias(id);
    }
}