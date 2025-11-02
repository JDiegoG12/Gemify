// Package capaaccesoadatos proporciona el repositorio de datos para la gestión de canciones.
// Implementa el patrón Singleton para garantizar una única instancia del catálogo en memoria
// y ofrece operaciones seguras para entornos concurrentes.
package capaaccesoadatos

import (
	// DTOs define las estructuras de transferencia de datos utilizadas por el repositorio.
	dtos "almacenamiento/capaFachadaServices/DTOs"
	"fmt"
	"os"
	"path/filepath"
	"sync"
)

// RepositorioCanciones gestiona el catálogo de canciones en memoria y el almacenamiento de archivos de audio.
// Utiliza un mutex para garantizar la seguridad en entornos concurrentes y sigue el patrón Singleton.
type RepositorioCanciones struct {
	mu       sync.Mutex
	catalogo []dtos.CancionDTOOutput
}

var (
	instancia *RepositorioCanciones
	once      sync.Once
)

// GetRepositorioCanciones devuelve la única instancia del repositorio de canciones.
// Inicializa el catálogo con datos de ejemplo la primera vez que se invoca.
func GetRepositorioCanciones() *RepositorioCanciones {
	once.Do(func() {
		instancia = &RepositorioCanciones{}
		instancia.poblarCatalogo()
	})
	return instancia
}

// poblarCatalogo inicializa el catálogo en memoria con un conjunto de canciones de ejemplo.
// Este método es privado y solo se invoca durante la inicialización del singleton.
func (r *RepositorioCanciones) poblarCatalogo() {
	r.catalogo = []dtos.CancionDTOOutput{
		{
			Titulo:  "Afuera",
			Artista: "Caifanes",
			Genero:  "Rock",
			Idioma:  "Español",
		},
		{
			Titulo:  "Como Te Voy a Olvidar",
			Artista: "Los Ángeles Azules",
			Genero:  "Cumbia",
			Idioma:  "Español",
		},
		{
			Titulo:  "De Musica Ligera",
			Artista: "Soda Stereo",
			Genero:  "Rock",
			Idioma:  "Español",
		},
		{
			Titulo:  "Flaca",
			Artista: "Andrés Calamaro",
			Genero:  "Rock",
			Idioma:  "Español",
		},
		{
			Titulo:  "Lamento Boliviano",
			Artista: "Enanitos Verdes",
			Genero:  "Rock",
			Idioma:  "Español",
		},
		{
			Titulo:  "Lloraras",
			Artista: "Oscar D'León",
			Genero:  "Salsa",
			Idioma:  "Español",
		},
		{
			Titulo:  "Pedro Navaja",
			Artista: "Rubén Blades",
			Genero:  "Salsa",
			Idioma:  "Español",
		},
		{
			Titulo:  "Tren al Sur",
			Artista: "Los Prisioneros",
			Genero:  "Rock",
			Idioma:  "Español",
		},
	}
}

// ListarCanciones devuelve una copia segura del catálogo actual de canciones.
// La operación es thread-safe gracias al uso de un mutex.
// Retorna un slice nuevo para evitar modificaciones externas al catálogo interno.
func (r *RepositorioCanciones) ListarCanciones() []dtos.CancionDTOOutput {
	r.mu.Lock()
	defer r.mu.Unlock()

	copiaCatalogo := make([]dtos.CancionDTOOutput, len(r.catalogo))
	copy(copiaCatalogo, r.catalogo)
	return copiaCatalogo
}

// GuardarCancion almacena un archivo de audio en el sistema de archivos y registra
// sus metadatos en el catálogo en memoria.
// Los archivos se guardan en la carpeta "../AudiosCompartidos" con el nombre "{titulo}.mp3".
// La operación es thread-safe y devuelve un error si falla la escritura del archivo
// o la creación del directorio.
func (r *RepositorioCanciones) GuardarCancion(titulo string, genero string, artista string, idioma string, data []byte) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	rutaCarpetaCompartida := "../AudiosCompartidos"

	if err := os.MkdirAll(rutaCarpetaCompartida, os.ModePerm); err != nil {
		return fmt.Errorf("error creando directorio de audios: %v", err)
	}

	fileName := fmt.Sprintf("%s.mp3", titulo)
	filePath := filepath.Join(rutaCarpetaCompartida, fileName)

	if err := os.WriteFile(filePath, data, 0644); err != nil {
		return fmt.Errorf("error al guardar archivo: %v", err)
	}

	nuevaCancion := dtos.CancionDTOOutput{
		Titulo:  titulo,
		Artista: artista,
		Genero:  genero,
		Idioma:  idioma,
	}
	r.catalogo = append(r.catalogo, nuevaCancion)

	return nil
}
