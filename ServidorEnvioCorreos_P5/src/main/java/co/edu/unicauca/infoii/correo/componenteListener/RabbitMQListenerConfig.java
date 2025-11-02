package co.edu.unicauca.infoii.correo.componenteListener;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración para el listener de RabbitMQ en el servidor de envío de correos.
 * <p>
 * Esta clase define los componentes necesarios para que Spring AMQP pueda recibir mensajes
 * desde RabbitMQ y convertirlos automáticamente de formato JSON a objetos Java (DTOs).
 * Implementa el patrón de <i>Dependency Injection</i> para configurar la infraestructura
 * de mensajería de forma declarativa.
 * <p>
 * Específicamente, configura:
 * <ul>
 *   <li>Un convertidor de mensajes JSON basado en Jackson</li>
 *   <li>Una fábrica de contenedores de listeners que utiliza dicho convertidor</li>
 * </ul>
 * Esto permite que los métodos anotados con {@link org.springframework.amqp.rabbit.annotation.RabbitListener}
 * reciban directamente objetos Java (por ejemplo, {@link co.edu.unicauca.infoii.correo.DTOs.CancionAlmacenarDTOInput})
 * en lugar de arreglos de bytes sin procesar.
 * 
 */
@Configuration
public class RabbitMQListenerConfig {

    /**
     * Define un convertidor de mensajes que utiliza la librería Jackson para
     * serializar y deserializar mensajes entre JSON y objetos Java.
     * <p>
     * Este bean es esencial para la interoperabilidad entre el servidor de canciones (Go),
     * que publica mensajes en formato JSON, y el servidor de correos (Java), que los consume
     * como objetos DTO tipados.
     * 
     * @return una instancia de {@link Jackson2JsonMessageConverter} configurada por defecto
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Configura una fábrica de contenedores de listeners de RabbitMQ que utiliza
     * el convertidor JSON definido en {@link #jackson2JsonMessageConverter()}.
     * <p>
     * Esta fábrica es utilizada internamente por Spring para crear los hilos y recursos
     * necesarios para escuchar las colas de RabbitMQ. Al establecer el convertidor de mensajes,
     * se garantiza que todos los mensajes entrantes se deserialicen automáticamente a objetos Java.
     * 
     * @param connectionFactory fábrica de conexiones gestionada por Spring Boot para RabbitMQ
     * @param jackson2JsonMessageConverter convertidor JSON a Java
     * @return una fábrica de contenedores configurada para usar conversión JSON
     * 
     * @implNote Este bean reemplaza la configuración predeterminada de Spring Boot para
     *           listeners de RabbitMQ, permitiendo un control fino sobre el proceso de
     *           conversión de mensajes.
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter);
        return factory;
    }
}