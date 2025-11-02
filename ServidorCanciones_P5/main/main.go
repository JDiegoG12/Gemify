package main

import (
	controlador "almacenamiento/capaControladores"
	"fmt"
	"net/http"
)

func main() {
	// 1. Se crea una instancia del controlador. Esto inicializa todas las capas.
	ctrl := controlador.NuevoControladorAlmacenamientoCanciones()

	// 2. Se registra el manejador para la ruta POST existente.
	http.HandleFunc("/canciones/almacenamiento", ctrl.AlmacenarAudioCancion)

	// NUEVO: Se registra el manejador para la nueva ruta GET que listará las canciones.
	http.HandleFunc("/canciones", ctrl.ListarCanciones)

	// 3. Se informa en consola que el servidor está listo y qué endpoints ofrece.
	fmt.Println(" Servidor de Canciones iniciado. Escuchando en el puerto 5000...")
	fmt.Println("Endpoints disponibles:")
	fmt.Println("  - GET  /canciones")
	fmt.Println("  - POST /canciones/almacenamiento")

	// 4. Se inicia el servidor HTTP.
	if err := http.ListenAndServe(":5000", nil); err != nil {
		fmt.Println("Error iniciando el servidor:", err)
	}
}
