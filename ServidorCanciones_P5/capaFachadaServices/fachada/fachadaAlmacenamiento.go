// Package capafachada implementa la capa de fachada del servidor de canciones.
// Coordina la lógica de negocio entre el controlador, el repositorio de datos
// y el componente de mensajería (RabbitMQ), aplicando el patrón de diseño Fachada.
package capafachada

import (
	// Repositorio de acceso a datos para operaciones de persistencia.
	capaaccesoadatos "almacenamiento/capaAccesoADatos"
	// DTOs para la transferencia de datos entre capas.
	dtos "almacenamiento/capaFachadaServices/DTOs"
	// Componente para la publicación de mensajes en RabbitMQ.
	componnteconexioncola "almacenamiento/componnteConexionCola"
	"fmt"
)

// FachadaAlmacenamiento orquesta las operaciones de listado y almacenamiento de canciones.
// Gestiona la interacción con el repositorio de datos y, de forma opcional,
// publica notificaciones en RabbitMQ al registrar una nueva canción.
type FachadaAlmacenamiento struct {
	repo         *capaaccesoadatos.RepositorioCanciones
	conexionCola *componnteconexioncola.RabbitPublisher
}

// NuevaFachadaAlmacenamiento crea e inicializa una nueva instancia de la fachada.
// Inyecta las dependencias necesarias: repositorio (singleton) y publicador de RabbitMQ.
// Si la conexión a RabbitMQ falla, se registra un error pero la fachada sigue funcionando
// para operaciones de catálogo.
func NuevaFachadaAlmacenamiento() *FachadaAlmacenamiento {
	fmt.Println(" Inicializando fachada de almacenamiento...")

	repo := capaaccesoadatos.GetRepositorioCanciones()

	conexionCola, err := componnteconexioncola.NewRabbitPublisher()
	if err != nil {
		fmt.Println(" Error al conectar con RabbitMQ:", err)
		conexionCola = nil
	}

	return &FachadaAlmacenamiento{
		repo:         repo,
		conexionCola: conexionCola,
	}
}

// ListarCanciones delega en el repositorio la obtención del catálogo completo de canciones.
// Registra un eco de la operación en la consola.
func (thisF *FachadaAlmacenamiento) ListarCanciones() []dtos.CancionDTOOutput {
	fmt.Println(" Petición de listar canciones recibida en la fachada.")
	return thisF.repo.ListarCanciones()
}

// GuardarCancion orquesta el almacenamiento de una nueva canción.
// Primero, publica una notificación en RabbitMQ (si la conexión está disponible),
// y luego delega en el repositorio el guardado del archivo de audio y sus metadatos.
// La operación de guardado no se interrumpe si falla la notificación por RabbitMQ,
// garantizando la disponibilidad del servicio principal.
func (thisF *FachadaAlmacenamiento) GuardarCancion(objCancion dtos.CancionAlmacenarDTOInput, data []byte) error {
	if thisF.conexionCola != nil {
		notificacion := componnteconexioncola.NotificacionCancion{
			Titulo:  objCancion.Titulo,
			Artista: objCancion.Artista,
			Genero:  objCancion.Genero,
			Idioma:  objCancion.Idioma,
			Mensaje: "Nueva canción almacenada: " + objCancion.Titulo + " de " + objCancion.Artista,
		}
		if err := thisF.conexionCola.PublicarNotificacion(notificacion); err != nil {
			fmt.Printf("Advertencia: no se pudo enviar la notificación a RabbitMQ: %v\n", err)
		}
	} else {
		fmt.Println("Advertencia: La conexión a RabbitMQ no está disponible. No se enviará la notificación.")
	}

	return thisF.repo.GuardarCancion(objCancion.Titulo, objCancion.Genero, objCancion.Artista, objCancion.Idioma, data)
}
