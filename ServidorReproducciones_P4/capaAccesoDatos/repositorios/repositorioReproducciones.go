package repositorios

import (
	"fmt"
	"strings"
	"sync"
	entities "tendencias/capaAccesoDatos/entities"
	"time"
)

type RepositorioReproducciones struct {
	mu             sync.Mutex
	reproducciones []entities.ReproduccionEntity
}

var (
	instancia *RepositorioReproducciones
	once      sync.Once
)

func GetRepositorio() *RepositorioReproducciones {
	once.Do(func() {
		instancia = &RepositorioReproducciones{}
		instancia.poblarDatosDeEjemplo()
	})
	return instancia
}

func (r *RepositorioReproducciones) poblarDatosDeEjemplo() {
	r.reproducciones = []entities.ReproduccionEntity{
		{IdUsuario: 1, Titulo: "Lamento Boliviano", FechaHora: "2025-10-20 10:00:00"},
		{IdUsuario: 2, Titulo: "De Musica Ligera", FechaHora: "2025-10-20 10:05:00"},
		{IdUsuario: 1, Titulo: "Lloraras", FechaHora: "2025-10-20 10:10:00"},
		{IdUsuario: 1, Titulo: "Flaca", FechaHora: "2025-10-21 11:00:00"},
	}
	fmt.Println("--> Repositorio de Reproducciones inicializado con datos de ejemplo.")
}

func (r *RepositorioReproducciones) AgregarReproduccion(idUsuario int, titulo string) {
	r.mu.Lock()
	defer r.mu.Unlock()

	// strings.TrimSuffix elimina el sufijo si existe, si no, no hace nada.
	tituloLimpio := strings.TrimSuffix(titulo, ".mp3")

	nuevaReproduccion := entities.ReproduccionEntity{
		IdUsuario: idUsuario,
		Titulo:    tituloLimpio,
		FechaHora: time.Now().Format("2006-01-02 15:04:05"),
	}

	r.reproducciones = append(r.reproducciones, nuevaReproduccion)
	fmt.Printf("--> Reproduccion almacenada en el repositorio: %+v\n", nuevaReproduccion)
}

func (r *RepositorioReproducciones) ListarTodasLasReproducciones() []entities.ReproduccionEntity {
	r.mu.Lock()
	defer r.mu.Unlock()
	return r.reproducciones
}

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
