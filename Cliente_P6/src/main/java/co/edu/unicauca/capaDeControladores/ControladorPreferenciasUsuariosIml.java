
package co.edu.unicauca.capaDeControladores;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import co.edu.unicauca.fachadaServices.DTO.PreferenciasDTORespuesta;
import co.edu.unicauca.fachadaServices.services.FachadaGestorUsuariosIml;


public class ControladorPreferenciasUsuariosIml extends UnicastRemoteObject implements ControladorPreferenciasUsuariosInt {

    private FachadaGestorUsuariosIml servicioFachadaPreferencias;

    public ControladorPreferenciasUsuariosIml(FachadaGestorUsuariosIml servicioFachadaPreferencias) throws RemoteException {
        super();
        this.servicioFachadaPreferencias = servicioFachadaPreferencias;
    }

    @Override
    public PreferenciasDTORespuesta getReferencias(Integer id) throws RemoteException {
        return this.servicioFachadaPreferencias.getReferencias(id);
    }
}


