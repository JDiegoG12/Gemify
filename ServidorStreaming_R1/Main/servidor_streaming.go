// Package main es el punto de entrada del servidor de streaming.
// Inicializa y levanta un servidor gRPC que expone el servicio de streaming de audio,
// verificando previamente la existencia de la carpeta de archivos de audio requerida.
package main

import (
	"fmt"
	"log"
	"net"
	"os"
	"servidor_streaming/Controladores"
	ss "servidor_streaming/servicios_streaming"

	"google.golang.org/grpc"
)

// main inicializa el servidor gRPC de streaming.
// Verifica que la carpeta "../AudiosCompartidos" exista (requerida para acceder a los archivos MP3),
// configura el listener en el puerto 50051 y registra el servicio AudioService.
// El servidor se ejecuta de forma bloqueante hasta que se interrumpe o ocurre un error fatal.
func main() {
	fmt.Println("Iniciando Servidor de Streaming gRPC...")

	rutaAudios := "../AudiosCompartidos"
	fmt.Printf("   -> Verificando la existencia de la carpeta de audios en: %s\n", rutaAudios)
	if _, err := os.Stat(rutaAudios); os.IsNotExist(err) {
		log.Fatalf("Error critico: La carpeta compartida '%s' no se encuentra. Por favor, creela y coloque los archivos MP3 alli.", rutaAudios)
	}
	fmt.Println("   -> Verificacion exitosa. La carpeta de audios fue encontrada.")

	puerto := ":50051"
	lis, err := net.Listen("tcp", puerto)
	if err != nil {
		log.Fatalf("Fallo al escuchar en el puerto %s: %v", puerto, err)
	}

	s := grpc.NewServer()

	ss.RegisterAudioServiceServer(s, &Controladores.ServidorDeStreaming{})

	fmt.Printf("Servidor de Streaming gRPC listo y escuchando en el puerto %s\n", puerto)

	if err := s.Serve(lis); err != nil {
		log.Fatalf("Fallo al iniciar el servidor gRPC de streaming: %v", err)
	}
}
