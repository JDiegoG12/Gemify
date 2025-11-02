package clienteReproducciones

import (
	"bytes"
	"encoding/json"
	"fmt"
	"net/http"
)

// Constante para la URL del endpoint del Servidor de Reproducciones.
const urlServidorReproducciones = "http://localhost:5002/reproducciones"

// DTOInput define la estructura del cuerpo JSON que enviaremos.
// Debe coincidir con el DTO que espera el Servidor de Reproducciones.
type DTOInput struct {
	IdUsuario int    `json:"idUsuario"`
	Titulo    string `json:"titulo"`
}

/**
 * RegistrarReproduccion se comunica vía REST con el Servidor de Reproducciones
 * para notificar que una canción ha sido escuchada.
 *
 * @param idUsuario El ID del usuario que escuchó la canción.
 * @param titulo El título de la canción escuchada.
 * @return Un error si la comunicación falla o el servidor responde con un error.
 */
func RegistrarReproduccion(idUsuario int, titulo string) error {
	fmt.Printf("--> Comunicacion Externa: Notificando reproduccion al servidor de tendencias (Usuario: %d, Cancion: %s)\n", idUsuario, titulo)

	// 1. Construir el cuerpo de la petición.
	cuerpoPeticion := DTOInput{
		IdUsuario: idUsuario,
		Titulo:    titulo,
	}

	// 2. Codificar el cuerpo a JSON.
	jsonData, err := json.Marshal(cuerpoPeticion)
	if err != nil {
		return fmt.Errorf("error al codificar la peticion a JSON: %w", err)
	}

	// 3. Realizar la petición POST.
	resp, err := http.Post(urlServidorReproducciones, "application/json", bytes.NewBuffer(jsonData))
	if err != nil {
		return fmt.Errorf("error al realizar la peticion POST al servidor de reproducciones: %w", err)
	}
	defer resp.Body.Close()

	// 4. Verificar la respuesta del servidor.
	// Esperamos un código de estado 201 (Created).
	if resp.StatusCode != http.StatusCreated {
		return fmt.Errorf("el servidor de reproducciones respondio con un codigo de estado inesperado: %d", resp.StatusCode)
	}

	fmt.Println("--> Comunicacion Externa: Reproduccion notificada exitosamente.")
	return nil
}
