// El paquete main es el punto de entrada para la ejecución del microservicio
// Servidor de Reproducciones.
package main

import (
	"fmt"
	"log"
	"net/http"
	controladores "tendencias/capaConotroladores"
)

// La función main es la responsable de arrancar el servidor HTTP.
//
// Realiza las siguientes tareas:
//  1. Inicializa el controlador principal de la aplicación, que a su vez
//     inicializa todas las capas subyacentes (fachada, repositorio).
//  2. Registra los manejadores de rutas (endpoints) de la API REST.
//  3. Pone al servidor a escuchar peticiones en un puerto TCP específico.
func main() {
	fmt.Println("Iniciando Servidor de Reproducciones...")

	ctrl := controladores.NuevoControladorTendencias()

	// Se registra un único manejador para la ruta "/reproducciones".
	// Este manejador actúa como un multiplexor (router) simple que delega la
	// petición al handler correspondiente (GET o POST) basándose en el método HTTP.
	// Este enfoque promueve un diseño de API RESTful, donde un mismo recurso
	// (en este caso, "reproducciones") responde a diferentes verbos.
	http.HandleFunc("/reproducciones", func(w http.ResponseWriter, r *http.Request) {
		switch r.Method {
		case http.MethodGet:
			ctrl.ListarReproduccionesHandler(w, r)
		case http.MethodPost:
			ctrl.RegistrarReproduccionHandler(w, r)
		default:
			// Si se recibe un método HTTP no soportado (ej. PUT, DELETE),
			// se responde con un error 405 Method Not Allowed.
			http.Error(w, "Metodo no permitido", http.StatusMethodNotAllowed)
		}
	})

	// Se define el puerto en el que escuchará el servidor.
	puerto := ":5002"
	fmt.Printf(" Servidor de Reproducciones escuchando en el puerto %s\n", puerto)
	fmt.Println("Endpoints disponibles:")
	fmt.Println("  - POST /reproducciones")
	fmt.Println("  - GET  /reproducciones?idUsuario={id}")

	// http.ListenAndServe inicia el servidor y bloquea la ejecución, esperando
	// indefinidamente por nuevas peticiones. Si la función retorna un error
	// (ej. el puerto ya está en uso), se registra como un error fatal y el
	// programa termina.
	if err := http.ListenAndServe(puerto, nil); err != nil {
		log.Fatalf("Error critico al iniciar el servidor en el puerto %s: %v", puerto, err)
	}
}
