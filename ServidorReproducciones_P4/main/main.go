package main

import (
	"fmt"
	"log"
	"net/http"
	controladores "tendencias/capaConotroladores" // Asegúrate de que esta ruta sea correcta
)

func main() {
	fmt.Println("Iniciando Servidor de Reproducciones...")

	ctrl := controladores.NuevoControladorTendencias()

	// MODIFICADO: Rutas más estándar para una API REST.
	// POST /reproducciones   -> Para crear un nuevo recurso (una reproducción).
	// GET  /reproducciones   -> Para listar los recursos (con filtro opcional).
	http.HandleFunc("/reproducciones", func(w http.ResponseWriter, r *http.Request) {
		switch r.Method {
		case http.MethodGet:
			ctrl.ListarReproduccionesHandler(w, r)
		case http.MethodPost:
			ctrl.RegistrarReproduccionHandler(w, r)
		default:
			// Si es otro método (PUT, DELETE, etc.), devolvemos un error.
			http.Error(w, "Metodo no permitido", http.StatusMethodNotAllowed)
		}
	})

	// CAMBIO CLAVE: Cambiamos el puerto a 5002.
	puerto := ":5002"
	fmt.Printf(" Servidor de Reproducciones escuchando en el puerto %s\n", puerto)
	fmt.Println("Endpoints disponibles:")
	fmt.Println("  - POST /reproducciones")
	fmt.Println("  - GET  /reproducciones?idUsuario={id}")

	if err := http.ListenAndServe(puerto, nil); err != nil {
		log.Fatalf("Error critico al iniciar el servidor en el puerto %s: %v", puerto, err)
	}
}
