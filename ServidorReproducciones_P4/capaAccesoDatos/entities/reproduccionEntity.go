// Package entitys define las estructuras de datos que modelan las entidades
// principales del dominio en la capa de acceso a datos.
package entitys

// ReproduccionEntity representa el modelo de datos para una única reproducción
// almacenada en el sistema.
//
// Esta struct es la representación interna de una reproducción en el repositorio
// y se utiliza para la persistencia en memoria. Las etiquetas (tags) `json`
// definen cómo se serializará esta estructura cuando se exponga a través de
// un endpoint REST, asegurando nombres de campo consistentes en formato camelCase.
type ReproduccionEntity struct {
	// IdUsuario es el identificador numérico del usuario que realizó la reproducción.
	IdUsuario int `json:"idUsuario"`

	// Titulo es el nombre de la canción que fue reproducida.
	Titulo string `json:"titulo"`

	// FechaHora es una cadena de texto que representa la marca de tiempo
	// (timestamp) en la que se registró la reproducción.
	FechaHora string `json:"fechaHora"`
}
