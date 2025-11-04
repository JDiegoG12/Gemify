
# 💎 Gemify:  Sistema Distribuido de Streaming de Música

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white) ![Go](https://img.shields.io/badge/Go-1.24.5-00ADD8?style=for-the-badge&logo=go&logoColor=white) ![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white) ![gRPC](https://img.shields.io/badge/gRPC-4285F4?style=for-the-badge&logo=grpc&logoColor=white) ![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white) ![REST API](https://img.shields.io/badge/REST_API-000000?style=for-the-badge) ![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

**Gemify** es un proyecto académico de laboratorio para la materia de Sistemas Distribuidos, diseñado para simular la arquitectura de microservicios de una plataforma de streaming de música como Spotify. El sistema está construido utilizando una combinación de tecnologías síncronas y asíncronas, con servicios desarrollados en **Java** y **Go**, para demostrar patrones de comunicación complejos en un entorno distribuido.

---

## ✨ Tecnologías y Patrones

### 🛠️ Tecnologías Utilizadas
*   **Lenguajes:** Java 17, Go (Golang)
*   **Frameworks y Librerías:**
    *   **Java:** Spring Framework (para RabbitMQ), Java RMI, Java Sound API (javax.sound), JLayer (para decodificación MP3), Jackson (para JSON).
    *   **Go:** Net/http (para REST), Librerías oficiales de gRPC y Protocol Buffers.
*   **Protocolos de Comunicación:**
    *   **Síncrono:** REST/HTTP, gRPC, Java RMI.
    *   **Asíncrono:** AMQP (con RabbitMQ).
*   **Serialización de Datos:** JSON, Protocol Buffers (Protobuf).
*   **Gestión de Proyectos:** Apache Maven (para Java).

### 📐 Patrones de Diseño Aplicados
*   **Patrón Capas:** La arquitectura de cada microservicio está segmentada en capas de Controladores, Fachada y Acceso a Datos para separar responsabilidades.
*   **Modelo-Vista-Controlador (MVC):** Utilizado para estructurar la interacción, especialmente en el cliente de consola y los servicios con APIs.
*   **Data Transfer Object (DTO):** Se emplean clases y structs DTO para encapsular y estandarizar los datos que viajan entre capas y servicios.

---

## 🧩 Componentes del Sistema

### 👤 Cliente (Java)
Es la interfaz de usuario final, implementada como una aplicación de consola interactiva.
*   **Funcionalidades:** Iniciar sesión (simulado), listar el catálogo de canciones, solicitar la reproducción de una canción por streaming y consultar sus preferencias musicales.

### 🎵 Servidor de Canciones (Go)
El gestor central de los metadatos y archivos de audio de las canciones.
*   **Responsabilidad:** Almacenar información y notificar al sistema sobre nuevas adiciones.
*   **Comunicaciones:**
    *   **Recibe (REST):** Peticiones `POST` de un administrador (vía Postman) para añadir nuevas canciones.
    *   **Responde (REST):** Peticiones `GET` del Cliente para listar el catálogo.
    *   **Publica (RabbitMQ):** Envía un mensaje a una cola cada vez que una canción nueva es agregada.

### 🎬 Servidor de Streaming (Go)
El corazón de la funcionalidad de reproducción, encargado de transmitir el audio.
*   **Responsabilidad:** Enviar fragmentos de una canción a un cliente que lo solicite.
*   **Comunicaciones:**
    *   **Recibe (gRPC):** Una petición de un cliente con el nombre de la canción a reproducir.
    *   **Responde (gRPC Streaming):** Envía un flujo de datos continuo con los fragmentos (chunks) del archivo de audio.
    *   **Publica (Asíncrono):** Notifica al `Servidor de Reproducciones` los metadatos de la canción reproducida.

### 📈 Servidor de Reproducciones (Go)
Lleva el registro del historial de escuchas de los usuarios.
*   **Responsabilidad:** Almacenar y proveer datos sobre qué canciones ha escuchado cada usuario.
*   **Comunicaciones:**
    *   **Recibe (Asíncrono):** La información de las canciones reproducidas desde el `Servidor de Streaming`.
    *   **Responde (REST):** Peticiones `GET` del `Servidor de Preferencias` para obtener el historial de un usuario.

### ⭐ Servidor de Preferencias (Java)
El cerebro analítico del sistema, capaz de calcular los gustos de un usuario.
*   **Responsabilidad:** Analizar el historial de reproducciones y el catálogo para determinar las preferencias por género y artista.
*   **Comunicaciones:**
    *   **Consulta (REST):** Se conecta de forma síncrona al `Servidor de Canciones` y al `Servidor de Reproducciones`.
    *   **Responde (Java RMI):** Expone un método remoto para que el Cliente pueda solicitar sus preferencias calculadas.

### ✉️ Servidor de Envío de Correo (Java)
Un servicio de soporte que simula el envío de notificaciones por correo.
*   **Responsabilidad:** Reaccionar a la adición de nuevas canciones.
*   **Comunicaciones:**
    *   **Consume (RabbitMQ):** Está suscrito a la cola de notificaciones. Al recibir un mensaje, procesa los datos y simula el envío de un correo, mostrando la información en consola.

---

## 🧩 Diagrama de Contenedores
<img width="2001" height="1021" alt="ParcialLabDistribuidos-REQ2-CONTENEDORES drawio (2)" src="https://github.com/user-attachments/assets/b10c5fe9-8b84-4add-a8e3-5deb488afb94" />

## 🧩 Diagrama de Componentes
<img width="5578" height="2159" alt="ParcialLabDistribuidos-REQ2-COMPONENTES drawio (2)" src="https://github.com/user-attachments/assets/120ab1c3-9d0c-435a-82ed-d4dd57a74f85" />


---

## 👥 Autores

Este proyecto fue desarrollado como parte del laboratorio de Sistemas Distribuidos por:

*   **Juan Diego Gómez Garcés** 
*   **Ana Sofía Arango Yanza** 
