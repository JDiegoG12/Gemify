package controladores // Cambiado a plural para consistencia

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strconv" // NUEVO: Paquete para convertir strings a números.
	dtos "tendencias/capaFachadaServices/DTOs"
	"tendencias/capaFachadaServices/fachada"
)

type ControladorTendencias struct {
	fachada *fachada.FachadaTendencias
}

// NuevoControladorTendencias es el constructor del Controlador
func NuevoControladorTendencias() *ControladorTendencias {
	return &ControladorTendencias{
		fachada: fachada.NuevaFachadaTendencias(),
	}
}

/**
 * Handler para el endpoint POST /reproducciones
 * Recibe en el cuerpo del JSON un objeto con { "idUsuario": int, "titulo": "string" }
 * y lo registra como una nueva reproducción.
 */
func (c *ControladorTendencias) RegistrarReproduccionHandler(w http.ResponseWriter, r *http.Request) {
	// ECO: Imprime la llegada de la petición.
	fmt.Println("--> Controlador: Peticion POST a /reproducciones recibida.")

	// Solo aceptamos peticiones POST.
	if r.Method != http.MethodPost {
		http.Error(w, "Metodo no permitido", http.StatusMethodNotAllowed)
		return
	}

	var dto dtos.ReproduccionDTOInput
	if err := json.NewDecoder(r.Body).Decode(&dto); err != nil {
		http.Error(w, "Cuerpo de la peticion JSON invalido", http.StatusBadRequest)
		return
	}

	// MODIFICADO: Pasamos los campos correctos (idUsuario, Titulo) a la fachada.
	c.fachada.RegistrarReproduccion(dto.IdUsuario, dto.Titulo)

	w.WriteHeader(http.StatusCreated)
	fmt.Fprintf(w, "Reproduccion registrada correctamente")
}

/**
 * Handler para el endpoint GET /reproducciones
 * Acepta un query param 'idUsuario' para filtrar las reproducciones.
 * Ejemplo: GET /reproducciones?idUsuario=1
 * Si no se provee 'idUsuario', devuelve todas las reproducciones.
 */
func (c *ControladorTendencias) ListarReproduccionesHandler(w http.ResponseWriter, r *http.Request) {
	// ECO: Imprime la llegada de la petición.
	fmt.Println("--> Controlador: Peticion GET a /reproducciones recibida.")

	// Solo aceptamos peticiones GET.
	if r.Method != http.MethodGet {
		http.Error(w, "Metodo no permitido", http.StatusMethodNotAllowed)
		return
	}

	// MODIFICADO: Lógica para leer el query param 'idUsuario'.
	idUsuarioStr := r.URL.Query().Get("idUsuario")

	var reproducciones interface{} // Usamos una interfaz para poder asignar distintos tipos de resultado.

	if idUsuarioStr != "" {
		// Si se proporcionó un idUsuario, lo convertimos a entero.
		idUsuario, err := strconv.Atoi(idUsuarioStr)
		if err != nil {
			http.Error(w, "El parametro 'idUsuario' debe ser un numero entero.", http.StatusBadRequest)
			return
		}
		// Llamamos al nuevo método de la fachada para filtrar.
		reproducciones = c.fachada.ObtenerReproduccionesPorUsuario(idUsuario)
	} else {
		// Si no hay idUsuario, obtenemos todas las reproducciones.
		reproducciones = c.fachada.ObtenerTodasLasReproducciones()
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(reproducciones)
}
