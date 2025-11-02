// Package Fachada implementa la capa de fachada del servidor de streaming.
// Orquesta la lógica de negocio para la transmisión de archivos de audio,
// encapsulando la interacción con el sistema de archivos y delegando el envío
// de fragmentos mediante un callback proporcionado por el controlador.
// Aplica el patrón de diseño Fachada para desacoplar la lógica de transporte (gRPC) de la lógica de dominio.
package Fachada

import (
	"fmt"
	"io"
	"os"
	"path/filepath"

	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"
)

// TransmitirCancion lee un archivo de audio desde el sistema de archivos y lo transmite
// en fragmentos mediante un callback de envío.
// Busca el archivo en la carpeta "../AudiosCompartidos" relativa al directorio de ejecución.
// Retorna un error gRPC tipado si el archivo no se encuentra, si hay un error de lectura,
// o si la conexión con el cliente se interrumpe durante la transmisión.
// La función es tolerante a la cancelación por parte del cliente y finaliza de forma limpia.
func TransmitirCancion(nombreCancion string, enviarFragmento func(fragmento []byte) error) error {
	fmt.Printf("    Fachada: Procesando solicitud para la cancion: %s\n", nombreCancion)

	rutaCarpetaCompartida, _ := filepath.Abs("../AudiosCompartidos")
	filePath := filepath.Join(rutaCarpetaCompartida, nombreCancion)

	fmt.Printf("    Fachada: Buscando archivo en la ruta absoluta: %s\n", filePath)

	file, err := os.Open(filePath)
	if err != nil {
		fmt.Printf("xxx Fachada: Error, no se pudo abrir el archivo %s. Motivo: %v\n", filePath, err)
		return status.Errorf(codes.NotFound, "Cancion '%s' no encontrada.", nombreCancion)
	}
	defer file.Close()

	buffer := make([]byte, 32*1024)
	fragmentoNum := 1

	for {
		bytesLeidos, err := file.Read(buffer)

		if err == io.EOF {
			fmt.Println("    Fachada: Fin del archivo. Transmision completada.")
			break
		}
		if err != nil {
			fmt.Printf("xxx Fachada: Error leyendo el archivo: %v\n", err)
			return status.Errorf(codes.Internal, "Error interno al leer el archivo de la cancion.")
		}

		if err := enviarFragmento(buffer[:bytesLeidos]); err != nil {
			if st, ok := status.FromError(err); ok && st.Code() == codes.Canceled {
				fmt.Println("    Fachada: El cliente cancelo la conexion. Deteniendo el envio.")
				return nil
			}

			fmt.Printf("xxx Fachada: Error inesperado al enviar fragmento: %v\n", err)
			return status.Errorf(codes.Unavailable, "La conexion con el cliente se interrumpio.")
		}

		fmt.Printf("    Fachada: Fragmento #%d (%d bytes) enviado...\n", fragmentoNum, bytesLeidos)
		fragmentoNum++
	}

	return nil
}
