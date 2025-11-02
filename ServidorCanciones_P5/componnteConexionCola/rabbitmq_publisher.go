// Package componnteconexioncola gestiona la conexión y publicación de mensajes
// en RabbitMQ para notificar eventos del sistema, como el registro de nuevas canciones.
// Proporciona una interfaz simple y segura para la integración con el middleware de mensajería.
package componnteconexioncola

import (
	"encoding/json"
	"fmt"

	"github.com/streadway/amqp"
)

// RabbitPublisher encapsula la conexión y el canal de RabbitMQ necesarios
// para publicar mensajes en una cola específica.
// Es responsable de la gestión de recursos (conexión y canal) y de la serialización de mensajes.
type RabbitPublisher struct {
	conn    *amqp.Connection
	channel *amqp.Channel
	queue   amqp.Queue
}

// NotificacionCancion representa la estructura del mensaje enviado a RabbitMQ
// cuando se registra una nueva canción. Contiene los metadatos esenciales
// requeridos por el servidor de correos para generar la notificación.
type NotificacionCancion struct {
	Titulo  string `json:"titulo"`
	Artista string `json:"artista"`
	Genero  string `json:"genero"`
	Idioma  string `json:"idioma"`
	Mensaje string `json:"mensaje"`
}

// NewRabbitPublisher establece una conexión con el servidor RabbitMQ,
// crea un canal y declara la cola "notificaciones_canciones".
// Utiliza credenciales fijas y se conecta a localhost:5672.
// Retorna un nuevo publisher listo para usar o un error si falla la inicialización.
// En caso de error, garantiza que no queden recursos abiertos.
func NewRabbitPublisher() (*RabbitPublisher, error) {
	conn, err := amqp.Dial("amqp://admin:1234@localhost:5672/")
	if err != nil {
		return nil, fmt.Errorf("error conectando a RabbitMQ: %v", err)
	}

	ch, err := conn.Channel()
	if err != nil {
		conn.Close()
		return nil, fmt.Errorf("error abriendo canal: %v", err)
	}

	q, err := ch.QueueDeclare(
		"notificaciones_canciones",
		true,
		false,
		false,
		false,
		nil,
	)
	if err != nil {
		ch.Close()
		conn.Close()
		return nil, fmt.Errorf("error declarando cola: %v", err)
	}

	return &RabbitPublisher{
		conn:    conn,
		channel: ch,
		queue:   q,
	}, nil
}

// PublicarNotificacion serializa un objeto NotificacionCancion a JSON
// y lo publica en la cola configurada de RabbitMQ.
// El mensaje se marca como persistente para garantizar su entrega incluso
// en caso de reinicio del servidor de mensajería.
// Retorna un error si falla la serialización o la publicación.
func (p *RabbitPublisher) PublicarNotificacion(msg NotificacionCancion) error {
	body, err := json.Marshal(msg)
	if err != nil {
		return fmt.Errorf("error convirtiendo mensaje a JSON: %v", err)
	}

	err = p.channel.Publish(
		"",
		p.queue.Name,
		false,
		false,
		amqp.Publishing{
			DeliveryMode: amqp.Persistent,
			ContentType:  "application/json",
			Body:         body,
		},
	)
	if err != nil {
		return fmt.Errorf("error publicando mensaje: %v", err)
	}

	fmt.Println("Notificación enviada a RabbitMQ:", string(body))
	return nil
}

// Cerrar libera los recursos asociados al publisher: cierra el canal y la conexión con RabbitMQ.
// Debe llamarse al finalizar la ejecución del servidor para una terminación limpia.
func (p *RabbitPublisher) Cerrar() {
	if p.channel != nil {
		p.channel.Close()
	}
	if p.conn != nil {
		p.conn.Close()
	}
}
