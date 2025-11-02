// Package repositorios contiene las implementaciones de la capa de acceso a datos.
// Su responsabilidad es abstraer las operaciones de persistencia (en este caso,
// en memoria) de la lógica de negocio.
package repositorios

import (
	"fmt"
	"strings"
	"sync"
	entities "tendencias/capaAccesoDatos/entities"
	"time"
)

// RepositorioReproducciones gestiona la colección de registros de reproducción en
// memoria. Utiliza un mutex (mu) para garantizar la seguridad en operaciones
// concurrentes (lecturas y escrituras simultáneas) sobre el slice de
// reproducciones.
type RepositorioReproducciones struct {
	mu             sync.Mutex
	reproducciones []entities.ReproduccionEntity
}

var (
	instancia *RepositorioReproducciones
	once      sync.Once
)

// GetRepositorio implementa el patrón de diseño Singleton.
//
// Garantiza que solo exista una única instancia de RepositorioReproducciones
// durante todo el ciclo de vida de la aplicación. Esto es crucial para
// asegurar que todos los componentes trabajen sobre el mismo conjunto de datos
// en memoria. La estructura sync.Once asegura que la inicialización ocurra
// solo una vez, de forma segura en entornos concurrentes.
func GetRepositorio() *RepositorioReproducciones {
	once.Do(func() {
		instancia = &RepositorioReproducciones{}
		instancia.poblarDatosDeEjemplo()
	})
	return instancia
}

// poblarDatosDeEjemplo inicializa el repositorio con un conjunto de datos
// predefinidos. Este método se llama una única vez durante la creación de la
// instancia Singleton y es útil para propósitos de prueba y demostración.
func (r *RepositorioReproducciones) poblarDatosDeEjemplo() {
	r.reproducciones = []entities.ReproduccionEntity{
		{IdUsuario: 1, Titulo: "Lamento Boliviano", FechaHora: "2025-10-20 10:00:00"},
		{IdUsuario: 2, Titulo: "De Musica Ligera", FechaHora: "2025-10-20 10:05:00"},
		{IdUsuario: 1, Titulo: "Lloraras", FechaHora: "2025-10-20 10:10:00"},
		{IdUsuario: 1, Titulo: "Flaca", FechaHora: "2025-10-21 11:00:00"},
	}
	fmt.Println("--> Repositorio de Reproducciones inicializado con datos de ejemplo.")
}

// AgregarReproduccion añade un nuevo registro de reproducción a la colección en
// memoria.
//
// Antes de almacenar, limpia el título de la canción para eliminar la extensión
// ".mp3" si está presente, garantizando la consistencia de los datos. La
// operación está protegida por un mutex para evitar condiciones de carrera.
func (r *RepositorioReproducciones) AgregarReproduccion(idUsuario int, titulo string) {
	r.mu.Lock()
	defer r.mu.Unlock()

	tituloLimpio := strings.TrimSuffix(titulo, ".mp3")

	nuevaReproduccion := entities.ReproduccionEntity{
		IdUsuario: idUsuario,
		Titulo:    tituloLimpio,
		FechaHora: time.Now().Format("2006-01-02 15:04:05"),
	}

	r.reproducciones = append(r.reproducciones, nuevaReproduccion)
	fmt.Printf("--> Reproduccion almacenada en el repositorio: %+v\n", nuevaReproduccion)
}

// ListarTodasLasReproducciones devuelve un slice con todos los registros de
// reproducción almacenados en el repositorio. La operación es segura para
// la concurrencia.
func (r *RepositorioReproducciones) ListarTodasLasReproducciones() []entities.ReproduccionEntity {
	r.mu.Lock()
	defer r.mu.Unlock()
	return r.reproducciones
}

// ListarReproduccionesPorUsuario busca y devuelve todos los registros de
// reproducción asociados a un identificador de usuario específico.
//
// Itera sobre la colección completa y filtra los resultados. La operación está
// protegida por un mutex.
func (r *RepositorioReproducciones) ListarReproduccionesPorUsuario(idUsuario int) []entities.ReproduccionEntity {
	r.mu.Lock()
	defer r.mu.Unlock()

	reproduccionesDelUsuario := []entities.ReproduccionEntity{}
	for _, repro := range r.reproducciones {
		if repro.IdUsuario == idUsuario {
			reproduccionesDelUsuario = append(reproduccionesDelUsuario, repro)
		}
	}

	fmt.Printf("--> Consulta al repositorio: Se encontraron %d reproducciones para el usuario %d\n", len(reproduccionesDelUsuario), idUsuario)
	return reproduccionesDelUsuario
}
