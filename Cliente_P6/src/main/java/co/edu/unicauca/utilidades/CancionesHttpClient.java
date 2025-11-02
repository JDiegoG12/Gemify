package co.edu.unicauca.utilidades;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.unicauca.fachadaServices.DTO.CancionDTO;

/**
 * Cliente HTTP para interactuar con el Servidor de Canciones (implementado en Go).
 * <p>
 * Esta clase encapsula la lógica de comunicación REST con el endpoint del catálogo de canciones,
 * permitiendo al cliente obtener metadatos de las canciones disponibles en formato JSON.
 * Utiliza la API estándar {@link java.net.http.HttpClient} y Jackson para serialización/deserialización.
 * 
 */
public class CancionesHttpClient {

    /**
     * URL base del servidor de canciones.
     * <p>
     * Valor predeterminado: {@code http://localhost:5000}.
     * Debe coincidir con la dirección y puerto en los que se ejecuta el servidor Go.
     */
    private static final String BASE_URL = "http://localhost:5000";

    /**
     * Instancia reutilizable del cliente HTTP de Java.
     * <p>
     * Se inicializa una sola vez para mejorar el rendimiento y evitar la sobrecarga
     * de crear múltiples conexiones.
     */
    private final HttpClient httpClient;

    /**
     * Mapeador de objetos para conversión entre JSON y objetos Java.
     * <p>
     * Utiliza la librería Jackson para deserializar la respuesta JSON del servidor
     * en una lista de objetos {@link CancionDTO}.
     */
    private final ObjectMapper objectMapper;

    /**
     * Constructor que inicializa las dependencias necesarias para la comunicación HTTP.
     * <p>
     * Crea una nueva instancia de {@link HttpClient} y {@link ObjectMapper}.
     */
    public CancionesHttpClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Obtiene el catálogo completo de canciones desde el servidor de canciones.
     * <p>
     * Realiza una solicitud HTTP GET al endpoint {@code /canciones} y deserializa
     * la respuesta JSON en una lista de objetos {@link CancionDTO}.
     * 
     * @return una lista inmutable de {@link CancionDTO} con los metadatos de las canciones disponibles.
     *         Si ocurre un error de red, de parsing o el servidor responde con un código distinto de 200,
     *         se devuelve una lista vacía para evitar interrupciones en la ejecución.
     * 
     * @throws RuntimeException si ocurre un error inesperado durante la comunicación (aunque se maneja internamente).
     * 
     * @implNote Este método está diseñado para ser tolerante a fallos: nunca lanza excepciones al cliente,
     *           sino que las registra y devuelve un estado seguro (lista vacía).
     */
    public List<CancionDTO> listarCanciones() {
        URI uri = URI.create(BASE_URL + "/canciones");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String jsonBody = response.body();
                return objectMapper.readValue(jsonBody, new TypeReference<List<CancionDTO>>() {});
            } else {
                System.out.println("Error al conectar con el servidor de canciones. Código de estado: " + response.statusCode());
            }

        } catch (Exception e) {
            System.out.println("Excepción al intentar listar las canciones: " + e.getMessage());
        }

        return Collections.emptyList();
    }
}