package fachada

import (
	"fmt"
	entities "tendencias/capaAccesoDatos/entities"
	"tendencias/capaAccesoDatos/repositorios"
)

type FachadaTendencias struct {
	repo *repositorios.RepositorioReproducciones
}

// NuevaFachadaTendencias es el constructor de la fachada.
func NuevaFachadaTendencias() *FachadaTendencias {
	fmt.Println("--> Inicializando Fachada de Tendencias...")
	return &FachadaTendencias{
		repo: repositorios.GetRepositorio(),
	}
}

/**
 * Fachada para registrar una nueva reproducción.
 * Recibe el id del usuario y el título de la canción.
 * Delega la lógica de almacenamiento al repositorio.
 */
func (f *FachadaTendencias) RegistrarReproduccion(idUsuario int, titulo string) {
	// ECO: Imprime que la petición ha llegado a la fachada.
	fmt.Printf("--> Fachada: Peticion para registrar reproduccion del usuario %d, cancion '%s'\n", idUsuario, titulo)
	f.repo.AgregarReproduccion(idUsuario, titulo)
}

/**
 * Fachada para obtener un listado de absolutamente todas las reproducciones.
 * Delega la llamada al repositorio.
 */
func (f *FachadaTendencias) ObtenerTodasLasReproducciones() []entities.ReproduccionEntity {
	// ECO: Imprime que la petición ha llegado a la fachada.
	fmt.Println("--> Fachada: Peticion para obtener todas las reproducciones.")
	return f.repo.ListarTodasLasReproducciones()
}

/**
 * Fachada para obtener las reproducciones de un usuario específico.
 * Delega la llamada al repositorio.
 */
func (f *FachadaTendencias) ObtenerReproduccionesPorUsuario(idUsuario int) []entities.ReproduccionEntity {
	// ECO: Imprime que la petición ha llegado a la fachada.
	fmt.Printf("--> Fachada: Peticion para obtener reproducciones del usuario %d\n", idUsuario)
	return f.repo.ListarReproduccionesPorUsuario(idUsuario)
}
