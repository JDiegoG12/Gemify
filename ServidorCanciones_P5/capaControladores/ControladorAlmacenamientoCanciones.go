// Package controlador gestiona las peticiones HTTP entrantes para el almacenamiento y listado de canciones.
// Actúa como intermediario entre la capa de presentación (HTTP) y la lógica de negocio (fachada),
// implementando el patrón de diseño Controlador del modelo MVC.
package controlador

import (
	// DTOs define las estructuras de datos para la transferencia de información.
	dtos "almacenamiento/capaFachadaServices/DTOs"
	// Fachada encapsula la lógica de negocio y el acceso a datos.
	capafachada "almacenamiento/capaFachadaServices/fachada"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
)

// ControladorAlmacenamientoCanciones maneja las operaciones HTTP relacionadas con el catálogo de canciones.
// Coordina las solicitudes de listado y almacenamiento delegando en la fachada correspondiente.
type ControladorAlmacenamientoCanciones struct {
	fachada *capafachada.FachadaAlmacenamiento
}

// NuevoControladorAlmacenamientoCanciones crea e inicializa una nueva instancia del controlador.
// Inyecta la fachada de almacenamiento como dependencia.
func NuevoControladorAlmacenamientoCanciones() *ControladorAlmacenamientoCanciones {
	return &ControladorAlmacenamientoCanciones{
		fachada: capafachada.NuevaFachadaAlmacenamiento(),
	}
}

// ListarCanciones maneja las peticiones GET a /canciones.
// Obtiene el catálogo completo de canciones desde la fachada y responde con un JSON.
// Retorna un error 405 si el método HTTP no es GET.
func (thisC *ControladorAlmacenamientoCanciones) ListarCanciones(w http.ResponseWriter, r *http.Request) {
	fmt.Println(" Petición REST para listar canciones recibida.")

	if r.Method != http.MethodGet {
		http.Error(w, "Método no permitido", http.StatusMethodNotAllowed)
		return
	}

	canciones := thisC.fachada.ListarCanciones()

	w.Header().Set("Content-Type", "application/json")

	err := json.NewEncoder(w).Encode(canciones)
	if err != nil {
		http.Error(w, "Error al generar la respuesta JSON", http.StatusInternalServerError)
	}
}

// AlmacenarAudioCancion maneja las peticiones POST a /canciones/audio.
// Recibe un archivo de audio y metadatos (título, artista, género, idioma) mediante un formulario multipart,
// valida la entrada, y delega el almacenamiento a la fachada.
// Retorna un error 405 si el método no es POST, 400 si los datos son inválidos,
// o 500 si ocurre un error interno durante el guardado.
func (thisC *ControladorAlmacenamientoCanciones) AlmacenarAudioCancion(w http.ResponseWriter, r *http.Request) {
	fmt.Println(" Petición REST para almacenar una canción recibida.")

	if r.Method != http.MethodPost {
		http.Error(w, "Método no permitido", http.StatusMethodNotAllowed)
		return
	}

	if err := r.ParseMultipartForm(50 << 20); err != nil {
		http.Error(w, "Archivo demasiado grande", http.StatusBadRequest)
		return
	}

	file, _, err := r.FormFile("archivo")
	if err != nil {
		http.Error(w, "Error leyendo el archivo", http.StatusBadRequest)
		return
	}
	defer file.Close()

	data, err := io.ReadAll(file)
	if err != nil {
		http.Error(w, "Error al procesar el archivo", http.StatusInternalServerError)
		return
	}

	dto := dtos.CancionAlmacenarDTOInput{
		Titulo:  r.FormValue("titulo"),
		Artista: r.FormValue("artista"),
		Genero:  r.FormValue("genero"),
		Idioma:  r.FormValue("idioma"),
	}

	err = thisC.fachada.GuardarCancion(dto, data)
	if err != nil {
		http.Error(w, "Error al guardar la canción", http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusCreated)
	fmt.Fprintf(w, "Canción '%s' almacenada exitosamente.", dto.Titulo)
}
