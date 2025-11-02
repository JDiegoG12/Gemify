package co.edu.unicauca.fachadaServices.services;

import java.rmi.RemoteException;
import co.edu.unicauca.fachadaServices.DTO.PreferenciasDTORespuesta;

/**
 * Define el contrato para el servicio de cálculo de preferencias.
 * <p>
 * Esta interfaz desacopla la capa de comunicación (como el controlador RMI) de
 * la implementación concreta de la lógica de negocio. Cualquier clase que
 * implemente esta interfaz debe ser capaz de procesar y devolver las
 * preferencias musicales de un usuario.
 * <p>
 * Este es un ejemplo del Principio de Inversión de Dependencias, donde las capas
 * superiores dependen de abstracciones (interfaces) en lugar de concreciones
 * (clases).
 *
 * @see PreferenciasServiceImpl
 * @see co.edu.unicauca.capaDeControladores.ControladorPreferenciasUsuariosIml
 */
public interface IPreferenciasService {

    /**
     * Orquesta el proceso de cálculo y recuperación de las preferencias musicales
     * para un usuario específico.
     * <p>
     * La implementación de este método se encargará de comunicarse con los
     * microservicios necesarios (Servidor de Canciones y Servidor de Reproducciones),
     * obtener los datos crudos y procesarlos para generar las estadísticas de
     * preferencias.
     *
     * @param id el identificador único del usuario para el cual se calcularán las
     *           preferencias.
     * @return un objeto {@link PreferenciasDTORespuesta} que contiene las listas
     *         de preferencias agregadas y ordenadas por género, artista e idioma.
     * @throws RemoteException se declara para ser compatible con el framework RMI.
     *                         Aunque la implementación podría manejar errores de
     *                         comunicación interna (HTTP), esta declaración es
     *                         necesaria para que la capa del controlador RMI pueda
     *                         propagar errores de red relacionados con la propia
     *                         llamada remota.
     */
    public PreferenciasDTORespuesta getReferencias(Integer id) throws RemoteException;
}