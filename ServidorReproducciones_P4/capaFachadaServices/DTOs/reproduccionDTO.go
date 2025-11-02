package dtos

// ReproduccionDTOInput define la estructura de datos que se recibe en la
// petición POST para registrar una nueva reproducción.
type ReproduccionDTOInput struct {
	IdUsuario int    `json:"idUsuario"`
	Titulo    string `json:"titulo"`
}
