package dtos

// CancionDTOOutput define la estructura de los metadatos de una canción
// que se enviará como respuesta al cliente al listar el catálogo.
type CancionDTOOutput struct {
	Titulo  string `json:"titulo"`
	Artista string `json:"artista"`
	Genero  string `json:"genero"`
	Idioma  string `json:"idioma"`
}
