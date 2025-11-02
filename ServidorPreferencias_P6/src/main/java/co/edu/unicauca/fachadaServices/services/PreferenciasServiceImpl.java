package co.edu.unicauca.fachadaServices.services;

import java.util.List;
import co.edu.unicauca.fachadaServices.DTO.CancionDTOEntrada;
import co.edu.unicauca.fachadaServices.DTO.PreferenciasDTORespuesta;
import co.edu.unicauca.fachadaServices.DTO.ReproduccionesDTOEntrada;
import co.edu.unicauca.fachadaServices.services.componenteCalculaPreferencias.CalculadorPreferencias;
import co.edu.unicauca.fachadaServices.services.componenteComunicacionServidorCanciones.ComunicacionServidorCanciones;
import co.edu.unicauca.fachadaServices.services.componenteComunicacionServidorReproducciones.ComunicacionServidorReproducciones;

/**
 * Implementación concreta del servicio de cálculo de preferencias.
 * <p>
 * Esta clase actúa como una fachada que orquesta la colaboración entre
 * diferentes componentes para cumplir con la tarea de calcular las preferencias
 * de un usuario. Es el corazón de la lógica de negocio del Servidor de
 * Preferencias.
 *
 * @see IPreferenciasService
 */
public class PreferenciasServiceImpl implements IPreferenciasService {

    /**
     * Componente cliente para comunicarse con el Servidor de Canciones.
     */
    private final ComunicacionServidorCanciones comunicacionServidorCanciones;
    
    /**
     * Componente cliente para comunicarse con el Servidor de Reproducciones.
     */
    private final ComunicacionServidorReproducciones comunicacionServidorReproducciones;
    
    /**
     * Componente que contiene el algoritmo para procesar los datos y calcular las
     * preferencias.
     */
    private final CalculadorPreferencias calculadorPreferencias;

    /**
     * Construye una nueva instancia del servicio de preferencias.
     * <p>
     * En el constructor se inicializan todas las dependencias necesarias. Este
     * enfoque se conoce como Inyección de Dependencias manual.
     */
    public PreferenciasServiceImpl() {
        this.comunicacionServidorCanciones = new ComunicacionServidorCanciones();
        this.comunicacionServidorReproducciones = new ComunicacionServidorReproducciones();
        this.calculadorPreferencias = new CalculadorPreferencias();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Esta implementación orquesta el flujo de trabajo para calcular las
     * preferencias:
     * <ol>
     *   <li>Realiza una llamada REST síncrona al Servidor de Canciones para
     *       obtener el catálogo completo.</li>
     *   <li>Realiza otra llamada REST síncrona al Servidor de Reproducciones para
     *       obtener el historial del usuario especificado.</li>
     *   <li>Delega los datos obtenidos al {@link CalculadorPreferencias} para que
     *       realice el procesamiento y la agregación.</li>
     *   <li>Retorna el resultado final empaquetado en un
     *       {@link PreferenciasDTORespuesta}.</li>
     * </ol>
     */
    @Override
    public PreferenciasDTORespuesta getReferencias(Integer id) {
        System.out.println("--> Fachada de Preferencias: Obteniendo datos para el usuario con ID: " + id);
        
        // 1. Obtener el catálogo completo de canciones.
        List<CancionDTOEntrada> catalogoCanciones = this.comunicacionServidorCanciones.obtenerCancionesRemotas();
        
        // ECO: Imprime las canciones obtenidas.
        System.out.println("    Fachada de Preferencias: Canciones obtenidas del Servidor de Canciones:");
        for (CancionDTOEntrada cancion : catalogoCanciones) {
            System.out.printf("      - Titulo: %s, Artista: %s, Genero: %s, Idioma: %s\n", 
                cancion.getTitulo(), cancion.getArtista(), cancion.getGenero(), cancion.getIdioma());
        }

        // 2. Obtener el historial de reproducciones del usuario.
        List<ReproduccionesDTOEntrada> reproduccionesUsuario = this.comunicacionServidorReproducciones.obtenerReproduccionesRemotas(id);
        
        // ECO: Imprime las reproducciones obtenidas.
        System.out.println("    Fachada de Preferencias: Reproducciones obtenidas del Servidor de Reproducciones para el usuario " + id + ":");
        for (ReproduccionesDTOEntrada reproduccion : reproduccionesUsuario) {
            System.out.printf("      - Titulo: %s, Fecha: %s\n", 
                reproduccion.getTitulo(), reproduccion.getFechaHora());
        }

        // 3. Pasar los datos al calculador para que procese y devuelva el resultado.
        return this.calculadorPreferencias.calcular(id, catalogoCanciones, reproduccionesUsuario);
    }
}