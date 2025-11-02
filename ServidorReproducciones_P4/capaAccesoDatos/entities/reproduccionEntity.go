package entitys

// ReproduccionEntity representa una única reproducción de una canción por un usuario.
// Esta es la estructura que se almacena en el repositorio.
type ReproduccionEntity struct {
	IdUsuario int    `json:"idUsuario"`
	Titulo    string `json:"titulo"`
	FechaHora string `json:"fechaHora"`
}
