// Package Controladores contiene los controladores del servidor de streaming.
// Implementa los métodos definidos en el servicio gRPC y actúa como intermediario
// entre la capa de transporte (gRPC) y la lógica de negocio (fachada),
// siguiendo el patrón de diseño Controlador.
package Controladores

import (
	"fmt"
	"servidor_streaming/Fachada"
	ss "servidor_streaming/servicios_streaming"
)

// ServidorDeStreaming implementa la interfaz generada por gRPC para el servicio AudioService.
// Actúa como adaptador entre el protocolo gRPC y la lógica de negocio de la fachada.
type ServidorDeStreaming struct {
	// UnimplementedAudioServiceServer se incluye para compatibilidad futura con gRPC.
	// Permite que el servidor compile incluso si no se implementan todos los métodos del servicio.
	ss.UnimplementedAudioServiceServer
}

// StreamAudio maneja las solicitudes de streaming de audio del cliente.
// Recibe el nombre de un archivo de canción y transmite su contenido en fragmentos
// mediante un stream gRPC unidireccional del servidor al cliente.
// Delega la lectura y fragmentación del archivo a la capa de fachada,
// pasando un callback que encapsula la lógica de envío específica de gRPC.
// Retorna un error si la canción no se encuentra o si ocurre un fallo durante la transmisión.
func (s *ServidorDeStreaming) StreamAudio(req *ss.PeticionDTO, stream ss.AudioService_StreamAudioServer) error {
	nombreCancion := req.GetNombreCancion()

	fmt.Printf("--> Controlador: Peticion gRPC recibida para la cancion: %s\n", nombreCancion)

	enviarFragmentoCallback := func(fragmento []byte) error {
		res := &ss.FragmentoCancion{
			Data: fragmento,
		}
		return stream.Send(res)
	}

	fmt.Println("    Controlador: Delegando a la fachada para iniciar la transmision.")
	err := Fachada.TransmitirCancion(nombreCancion, enviarFragmentoCallback)
	if err != nil {
		fmt.Printf("xxx Controlador: Error durante la transmision de la fachada: %v\n", err)
		return err
	}

	fmt.Printf("<-- Controlador: Transmision de '%s' completada exitosamente.\n", nombreCancion)
	return nil
}
