// Package controladores define los manejadores de peticiones HTTP (handlers)
// que exponen la funcionalidad del microservicio a través de una API REST.
//
// Actúa como la capa de entrada, recibiendo las solicitudes, validándolas
// y delegando la responsabilidad del procesamiento a la capa de fachada.
package controladores

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"
	dtos "tendencias/capaFachadaServices/DTOs"
	"tendencias/capaFachadaServices/fachada"
)

// ControladorTendencias encapsula las dependencias necesarias para los handlers,
// como la referencia a la fachada de negocio.
type ControladorTendencias struct {
	fachada *fachada.FachadaTendencias
}

// NuevoControladorTendencias es el constructor que crea una nueva instancia de
// ControladorTendencias, inyectando la dependencia de la fachada.
func NuevoControladorTendencias() *ControladorTendencias {
	return &ControladorTendencias{
		fachada: fachada.NuevaFachadaTendencias(),
	}
}

// RegistrarReproduccionHandler gestiona las peticiones POST al endpoint /reproducciones.
//
// Su función es recibir los datos de una nueva reproducción en formato JSON
// desde el cuerpo de la petición, decodificarlos a un DTO y pasarlos a la
// fachada para su almacenamiento. Responde con un estado HTTP 201 (Created)
// si la operación es exitosa.
func (c *ControladorTendencias) RegistrarReproduccionHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("--> Controlador: Peticion POST a /reproducciones recibida.")

	if r.Method != http.MethodPost {
		http.Error(w, "Metodo no permitido", http.StatusMethodNotAllowed)
		return
	}

	var dto dtos.ReproduccionDTOInput
	if err := json.NewDecoder(r.Body).Decode(&dto); err != nil {
		http.Error(w, "Cuerpo de la peticion JSON invalido", http.StatusBadRequest)
		return
	}

	c.fachada.RegistrarReproduccion(dto.IdUsuario, dto.Titulo)

	w.WriteHeader(http.StatusCreated)
	fmt.Fprintf(w, "Reproduccion registrada correctamente")
}

// ListarReproduccionesHandler gestiona las peticiones GET al endpoint /reproducciones.
//
// Este handler implementa una doble funcionalidad basada en la presencia de un
// parámetro de consulta (query param):
//   - Si se incluye el parámetro `idUsuario` (ej. /reproducciones?idUsuario=1),
//     obtiene y devuelve solo las reproducciones de ese usuario.
//   - Si no se incluye el parámetro, obtiene y devuelve el listado completo de
//     todas las reproducciones almacenadas.
//
// La respuesta siempre se codifica en formato JSON.
func (c *ControladorTendencias) ListarReproduccionesHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("--> Controlador: Peticion GET a /reproducciones recibida.")

	if r.Method != http.MethodGet {
		http.Error(w, "Metodo no permitido", http.StatusMethodNotAllowed)
		return
	}

	idUsuarioStr := r.URL.Query().Get("idUsuario")

	var reproducciones interface{}

	if idUsuarioStr != "" {
		idUsuario, err := strconv.Atoi(idUsuarioStr)
		if err != nil {
			http.Error(w, "El parametro 'idUsuario' debe ser un numero entero.", http.StatusBadRequest)
			return
		}
		reproducciones = c.fachada.ObtenerReproduccionesPorUsuario(idUsuario)
	} else {
		reproducciones = c.fachada.ObtenerTodasLasReproducciones()
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(reproducciones)
}
