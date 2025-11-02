// Package Controladores contiene los manejadores de peticiones gRPC del servidor de streaming.
// Su función es interpretar las solicitudes RPC, invocar la lógica de negocio subyacente
// y gestionar la comunicación de streaming con el cliente.
package Controladores

import (
	"fmt"
	"servidor_streaming/Fachada"
	clienteReproducciones "servidor_streaming/capaComunicacionExterna"
	ss "servidor_streaming/servicios_streaming"
)

// ServidorDeStreaming implementa la interfaz generada por gRPC para el servicio AudioService.
// Sirve como punto de entrada para todas las llamadas RPC relacionadas con el audio.
type ServidorDeStreaming struct {
	// ss.UnimplementedAudioServiceServer se embebe para asegurar compatibilidad
	// hacia adelante, permitiendo añadir nuevos métodos al servicio proto
	// sin romper la compilación del servidor existente.
	ss.UnimplementedAudioServiceServer
}

// StreamAudio maneja la petición gRPC para transmitir una canción.
// Al recibir una solicitud, realiza dos acciones concurrentemente:
//  1. Inicia una operación asíncrona (en una goroutine) para notificar al
//     Servidor de Reproducciones sobre la nueva reproducción.
//  2. Delega la lógica de lectura y transmisión del archivo de audio a la capa de Fachada,
//     manteniendo el flujo de datos principal hacia el cliente.
//
// Retorna un error gRPC si la canción no es encontrada o si la transmisión falla.
func (s *ServidorDeStreaming) StreamAudio(req *ss.PeticionDTO, stream ss.AudioService_StreamAudioServer) error {
	nombreCancion := req.GetNombreCancion()
	idUsuario := req.GetIdUsuario() // Obtenemos el nuevo campo idUsuario.

	fmt.Printf("--> Controlador: Peticion gRPC recibida del usuario %d para la cancion: %s\n", idUsuario, nombreCancion)

	// INICIO DE LA OPERACIÓN ASÍNCRONA
	// Se lanza una goroutine para notificar la reproducción.
	// El `go` keyword hace que esta función se ejecute en un hilo separado,
	// por lo que el código principal (el streaming) no espera a que termine.
	go func() {
		err := clienteReproducciones.RegistrarReproduccion(int(idUsuario), nombreCancion)
		if err != nil {
			// Si la notificación falla, solo se imprime un log en el servidor de streaming.
			// No se detiene la transmisión de audio al cliente.
			fmt.Printf("xxx Controlador (Asincrono): Error al notificar la reproduccion: %v\n", err)
		}
	}()
	// FIN DE LA OPERACIÓN ASÍNCRONA

	// El callback encapsula la lógica de envío de gRPC, manteniendo la fachada agnóstica.
	enviarFragmentoCallback := func(fragmento []byte) error {
		res := &ss.FragmentoCancion{
			Data: fragmento,
		}
		return stream.Send(res)
	}

	fmt.Println("    Controlador: Delegando a la fachada para iniciar la transmision de audio.")
	err := Fachada.TransmitirCancion(nombreCancion, enviarFragmentoCallback)
	if err != nil {
		fmt.Printf("xxx Controlador: Error durante la transmision de la fachada: %v\n", err)
		return err
	}

	fmt.Printf("<-- Controlador: Transmision de '%s' completada exitosamente.\n", nombreCancion)
	return nil
}
