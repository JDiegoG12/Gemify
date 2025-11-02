package co.edu.unicauca.capaDeControladores;

import java.rmi.Remote;
import java.rmi.RemoteException;
import co.edu.unicauca.fachadaServices.DTO.PreferenciasDTORespuesta;

//Hereda de la clase Remote, lo cual convierte a esta interfaz en un objeto remoto
public interface ControladorPreferenciasUsuariosInt extends Remote {
    //Definición del método remoto
    public PreferenciasDTORespuesta getReferencias(Integer id) throws RemoteException;
}