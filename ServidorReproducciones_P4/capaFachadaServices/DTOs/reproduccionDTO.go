// Package dtos define los Data Transfer Objects (DTOs) utilizados para la
// comunicación entre las capas del servicio y con clientes externos.
package dtos

// ReproduccionDTOInput define la estructura de datos que se espera recibir en el
// cuerpo de una petición POST para registrar una nueva reproducción.
//
// Esta struct sirve como un contrato de datos entre el cliente (en este caso,
// el Servidor de Streaming) y este microservicio. Las etiquetas (tags) `json`
// son utilizadas por el decodificador `encoding/json` para mapear los campos
// del cuerpo JSON entrante a los campos de esta struct.
type ReproduccionDTOInput struct {
	// IdUsuario es el identificador numérico del usuario que realizó la
	// reproducción. Se mapea desde el campo "idUsuario" del JSON.
	IdUsuario int `json:"idUsuario"`

	// Titulo es el nombre de la canción que fue reproducida.
	// Se mapea desde el campo "titulo" del JSON.
	Titulo string `json:"titulo"`
}
