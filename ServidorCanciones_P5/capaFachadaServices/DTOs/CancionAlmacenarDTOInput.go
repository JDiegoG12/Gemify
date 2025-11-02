// Package dtos define las estructuras de transferencia de datos (DTOs) utilizadas
// en la comunicación entre las capas del servidor de canciones.
package dtos

// CancionAlmacenarDTOInput representa los metadatos de una canción proporcionados
// por un administrador al registrar una nueva canción.
// Los campos se mapean automáticamente desde JSON gracias a las etiquetas struct.
type CancionAlmacenarDTOInput struct {
	Titulo  string `json:"titulo"`
	Genero  string `json:"genero"`
	Artista string `json:"artista"`
	Idioma  string `json:"idioma"`
}
