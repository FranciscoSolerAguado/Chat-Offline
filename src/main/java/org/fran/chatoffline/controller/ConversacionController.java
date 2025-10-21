package org.fran.chatoffline.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import org.fran.chatoffline.dataAccess.XMLManager;
import org.fran.chatoffline.model.Adjunto;
import org.fran.chatoffline.model.GestorConversacion;
import org.fran.chatoffline.model.Mensaje;
import org.fran.chatoffline.model.Usuario;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ConversacionController {
    private static final Logger LOGGER = Logger.getLogger(ConversacionController.class.getName());

    private MainController mainController;
    private Usuario usuarioActual;
    private Usuario contactoActual;

    @FXML
    private VBox contenedorMensajes;
    @FXML
    private HBox topBar;
    @FXML
    private TextField campoMensaje;
    @FXML
    private ScrollPane scrollMensajes;
    @FXML
    private Label lblNombreContacto;
    @FXML
    private Button btnExportarConversacion;
    @FXML
    private Button btnEmpaquetarZip;


    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    /**
     * Establece el nombre del contacto en el Hbox superior con el que se va a chatear, actualiza la interfaz
     * y carga el historial de mensajes correspondiente.
     *
     * @param contacto El contacto que con el que se va a chatear, y por tanto del que cargará la conversación y su nombre.
     */
    public void setContacto(Usuario contacto) {
        this.contactoActual = contacto;
        if (lblNombreContacto != null) {
            lblNombreContacto.setText(contacto.getNombre());
        }
        cargarMensajesDesdeXML();
    }

    /**
     * Método que maneja el evento de clicar en los botones "Exportar Conversación" y "Empaquetar en ZIP".
     * Cuando se clica en ellos se llama a los métodos que hacen las acciones indicadas
     */
    @FXML
    private void initialize() {
        if (topBar != null) {
            topBar.setOnMouseClicked(e -> {
                if (mainController != null && contactoActual != null) {
                    mainController.abrirPerfilUsuario(contactoActual);
                }
            });

            btnExportarConversacion.setOnAction(e -> exportarConversacion());
            btnEmpaquetarZip.setOnAction(e -> empaquetarConversacionEnZip());
        }
    }

    /**
     * Carga los mensajes en el contenedor de Mensajes, limpiandolo primero de todo, luego recibe el archivo conversaciones.xml desde getConversacionesFile()
     * Dentro de un try catch creamos un GestorConversación(clase encargada de gestionar las conversaciones) con la ruta del archivo de conversaciones.xml
     * Este GestorConversación llama a XMLManager con su método readXML()(que recibe un elemento, en este caso un GestorConversación, y la ruta
     * del archivo de conversaciones.xml)
     * Luego con un for each sacamos los mensajes del archivo y lo mostramos por la Interfaz gr´afica con agregarMensaje(Mensaje mensaje)
     */
    private void cargarMensajesDesdeXML() {
        contenedorMensajes.getChildren().clear();
        File conversacionFile = getConversacionesFile();
        if (conversacionFile == null || !conversacionFile.exists() || conversacionFile.length() == 0) {
            LOGGER.info("No hay conversaciones guardadas o el archivo no se puede localizar.");
            return;
        }

        try {
            GestorConversacion conversacion = XMLManager.readXML(new GestorConversacion(), conversacionFile.getAbsolutePath());
            if (conversacion != null && conversacion.getMensajes() != null) {
                for (Mensaje m : conversacion.getMensajes()) {
                    /**
                     * El if se asegura de que solo se procesen (agregarMensaje(m)) los mensajes que se han intercambiado
                     * entre el usuario que está usando la aplicación y el contacto que ha seleccionado, ignorando todas las demás
                     * conversaciones guardadas en el archivo XML
                     */
                    if ((m.getRemitente().equals(usuarioActual.getNombre()) && m.getDestinatario().equals(contactoActual.getNombre())) ||
                            (m.getRemitente().equals(contactoActual.getNombre()) && m.getDestinatario().equals(usuarioActual.getNombre()))) {
                        agregarMensaje(m);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al leer o procesar el archivo de conversaciones.", e);
        }
    }

    /**
     * Este método maneja la existencia del archivo conversaciones.xml
     *
     * @return el archivo que ha creado si no existe o la ruta directamente si existe
     */
    private File getConversacionesFile() {
        File archivo = new File("src/main/resources/org/fran/chatoffline/conversaciones.xml");
        try {
            archivo.getParentFile().mkdirs();
            if (!archivo.exists()) archivo.createNewFile();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo crear el archivo de conversaciones.", e);
            return null;
        }
        return archivo;
    }

    /**
     * Este método añade un mensaje a la conversación que se muestra en la interfaz gráfica
     * Tambien maneja el añadido los adjuntos y las imágenes
     *
     * @param mensaje El mensaje que se va a añadir en la GUI
     */
    private void agregarMensaje(Mensaje mensaje) {
        HBox contenedor = new HBox();
        contenedor.setPadding(new Insets(5, 10, 5, 10));
        contenedor.setFillHeight(false);

        VBox contenidoMensaje = new VBox(5);
        Label etiquetaMensaje = new Label(mensaje.getContenido());
        etiquetaMensaje.setWrapText(true);
        etiquetaMensaje.setMaxWidth(300);
        contenidoMensaje.getChildren().add(etiquetaMensaje);

        if (mensaje.tieneAdjunto()) {
            Adjunto adj = mensaje.getAdjunto();
            File archivoAdjunto = new File(adj.getRutaArchivo());
            if (archivoAdjunto.exists()) {
                String mimeType = adj.getTipoMime();
                //Si es una imagen
                if (mimeType != null && mimeType.startsWith("image")) {
                    Image imagen = new Image(archivoAdjunto.toURI().toString());
                    ImageView imageView = new ImageView(imagen);
                    imageView.setFitWidth(200);
                    imageView.setPreserveRatio(true);
                    Tooltip.install(imageView, new Tooltip("Haz clic para guardar la imagen"));
                    //Si clicamos en una imagen se exportar el archivo y nos permite descargarlo
                    imageView.setOnMouseClicked(event -> exportarAdjunto(archivoAdjunto));
                    contenidoMensaje.getChildren().add(imageView);
                    //Si es un video
                } else if (mimeType != null && mimeType.startsWith("video")) {
                    Media media = new Media(archivoAdjunto.toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    MediaView mediaView = new MediaView(mediaPlayer);
                    mediaView.setFitWidth(300);
                    mediaView.setPreserveRatio(true);
                    Tooltip.install(mediaView, new Tooltip("Clic izquierdo para reproducir/pausar.\nClic derecho para guardar."));
                    //Para reproducir los videos
                    mediaView.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                                mediaPlayer.pause();
                            } else {
                                mediaPlayer.play();
                            }
                        } else if (event.getButton() == MouseButton.SECONDARY) {
                            exportarAdjunto(archivoAdjunto);
                        }
                    });
                    contenidoMensaje.getChildren().add(mediaView);
                    //Si es un pdf
                } else if (mimeType != null && mimeType.equals("application/pdf")) {
                    Label pdfLabel = new Label("📄 " + adj.getNombreArchivo());
                    pdfLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-cursor: hand;");
                    Tooltip.install(pdfLabel, new Tooltip("Haz clic para guardar el PDF"));
                    //Haciendo clic se guarda
                    pdfLabel.setOnMouseClicked(event -> exportarAdjunto(archivoAdjunto));
                    contenidoMensaje.getChildren().add(pdfLabel);
                    //Si es un archivo
                } else {
                    Label fileLabel = new Label("📁 " + adj.getNombreArchivo());
                    fileLabel.setStyle("-fx-text-fill: #444; -fx-font-style: italic; -fx-cursor: hand;");
                    Tooltip.install(fileLabel, new Tooltip("Haz clic para guardar el archivo"));
                    //Haciendo clic se guarda
                    fileLabel.setOnMouseClicked(event -> exportarAdjunto(archivoAdjunto));
                    contenidoMensaje.getChildren().add(fileLabel);
                }
            }
        }

        //El color del mensaje depende de quien sea quien envie el mensaje
        //Este formato es para el usuario actual, es decir tus mensajes, esto aparecen a la derecha
        if (usuarioActual != null && mensaje.getRemitente().equals(usuarioActual.getNombre())) {
            etiquetaMensaje.setStyle("-fx-background-color: #c2e7ff; -fx-background-radius: 10px; -fx-padding: 8px 12px;");
            contenedor.setAlignment(Pos.CENTER_RIGHT);

            //Este formato para el otro usuario con el que se esta hablando, aparecen a la izquierda
        } else {
            etiquetaMensaje.setStyle("-fx-background-color: #e6e6e6; -fx-background-radius: 10px; -fx-padding: 8px 12px;");
            contenedor.setAlignment(Pos.CENTER_LEFT);
        }

        contenedor.getChildren().add(contenidoMensaje);
        contenedorMensajes.getChildren().add(contenedor);
        Platform.runLater(() -> scrollMensajes.setVvalue(1.0));
    }

    /**
     * Método que sirve para exportar los archivos y guardarlos, con la clase FileChooser
     *
     * @param archivoOriginal el archivo que recibe para guardar (haciendo clic en el desde la interfaz gráfica)
     */
    private void exportarAdjunto(File archivoOriginal) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Archivo Adjunto");
        fileChooser.setInitialFileName(archivoOriginal.getName());

        File destino = fileChooser.showSaveDialog(null);
        if (destino == null) {
            return; // El usuario canceló la selección
        }

        // Copiar el archivo original al destino
        try (InputStream in = new FileInputStream(archivoOriginal);
             OutputStream out = new FileOutputStream(destino)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            LOGGER.info("Adjunto exportado exitosamente a: " + destino.getAbsolutePath());
            mostrarAlerta("Adjunto guardado en: " + destino.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al exportar el adjunto.", e);
            mostrarAlerta("Error al guardar el archivo.");
        }
    }

    /**
     * Metodo utilizado por el botonEnviar en la ui para enviar el mensaje
     */
    @FXML
    private void enviarMensaje() {
        String texto = campoMensaje.getText().trim();
        if (texto.isEmpty() || usuarioActual == null || contactoActual == null) return;

        Mensaje nuevoMensaje = new Mensaje(usuarioActual.getNombre(), contactoActual.getNombre(), texto);
        agregarMensaje(nuevoMensaje);
        campoMensaje.clear();
        guardarMensajeEnXML(nuevoMensaje);
    }

    /**
     * Metodo utilizado por el botonAdjuntar en la ui para adjuntar un archivo
     * Usando la clase FileChooser
     */
    @FXML
    private void adjuntarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Archivo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos Soportados", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.mp4", "*.mov", "*.avi", "*.pdf"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );

        File archivoSeleccionado = fileChooser.showOpenDialog(null);
        if (archivoSeleccionado == null) return;

        File carpetaMedia = new File("src/main/resources/org/fran/chatoffline/media");
        if (!carpetaMedia.exists()) carpetaMedia.mkdirs();

// Copiamos el archivo original al destino
        File destino = new File(carpetaMedia, archivoSeleccionado.getName());
        try (InputStream in = new FileInputStream(archivoSeleccionado);
             OutputStream out = new FileOutputStream(destino)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error copiando archivo adjunto.", e);
            return;
        }

        String mime = getMimeType(archivoSeleccionado);
        Adjunto adjunto = new Adjunto(destino.getName(), destino.getAbsolutePath(),
                destino.length(), mime);

        Mensaje mensaje = new Mensaje(usuarioActual.getNombre(), contactoActual.getNombre(), "📎 Archivo adjunto: " + destino.getName(), adjunto);

        agregarMensaje(mensaje);
        guardarMensajeEnXML(mensaje);
    }


    /**
     * Metodo utilizado por el botonExportarConversacion en la ui para exportar la conversación
     */
    @FXML
    private void exportarConversacion() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar Conversación");
        fileChooser.setInitialFileName("conversacion_" + contactoActual.getNombre() + ".csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"));
        File file = fileChooser.showSaveDialog(null);

        if (file == null) {
            return;
        }

        File conversacionFile = getConversacionesFile();
        //Si no hay conversacion
        if (conversacionFile == null || !conversacionFile.exists() || conversacionFile.length() == 0) {
            LOGGER.info("No hay conversación para exportar.");
            return;
        }

        //Leemos el archivo de conversaciones
        GestorConversacion gestor = XMLManager.readXML(new GestorConversacion(), conversacionFile.getAbsolutePath());
        if (gestor == null || gestor.getMensajes() == null) {
            LOGGER.info("No hay mensajes para exportar.");
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("Fecha y Hora,Remitente,Contenido,Adjunto");

            gestor.getMensajes().stream()
                    .filter(m -> (m.getRemitente().equals(usuarioActual.getNombre()) && m.getDestinatario().equals(contactoActual.getNombre())) ||
                            (m.getRemitente().equals(contactoActual.getNombre()) && m.getDestinatario().equals(usuarioActual.getNombre())))
                    .sorted(Comparator.comparing(Mensaje::getFechaEnvio))
                    .map(this::convertirMensajeACSV)
                    .forEach(writer::println);

            LOGGER.info("Conversación exportada exitosamente a: " + file.getAbsolutePath());

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al exportar la conversación.", e);
        }
    }

    /**
     * Metodo utilizado por el botonResumenStreams en la ui para generar un resumen de la conversacion
     */
    @FXML
    private void generarResumen() {
        File conversacionFile = getConversacionesFile();
        if (conversacionFile == null || !conversacionFile.exists() || conversacionFile.length() == 0) {
            mostrarAlerta("No hay conversación para analizar.");
            return;
        }
        GestorConversacion gestor = XMLManager.readXML(new GestorConversacion(), conversacionFile.getAbsolutePath());
        if (gestor == null || gestor.getMensajes() == null) {
            mostrarAlerta("No hay mensajes para analizar.");
            return;
        }
        List<Mensaje> mensajesConversacion = gestor.getMensajes().stream()
                .filter(m -> (m.getRemitente().equals(usuarioActual.getNombre()) && m.getDestinatario().equals(contactoActual.getNombre())) ||
                        (m.getRemitente().equals(contactoActual.getNombre()) && m.getDestinatario().equals(usuarioActual.getNombre())))
                .collect(Collectors.toList());
        if (mensajesConversacion.isEmpty()) {
            mostrarAlerta("No hay mensajes en esta conversación.");
            return;
        }
        long totalMensajes = mensajesConversacion.size();
        Map<String, Long> mensajesPorUsuario = mensajesConversacion.stream()
                .collect(Collectors.groupingBy(Mensaje::getRemitente, Collectors.counting()));


        List<String> stopWords = Arrays.asList("que", "de", "la", "el", "en", "y", "a", "los", "del", "un", "una", "es", "no", "si", "para", "con", "mi", "te", "se", "lo", "su", "me", "por", "qué", "pero", "como", "más", "este", "esta");


        Map<String, Long> conteoPalabras = mensajesConversacion.stream()
                .flatMap(m -> Arrays.stream(m.getContenido().toLowerCase().split("\\s+")))
                .map(palabra -> palabra.replaceAll("[^a-záéíóúñ]", ""))
                .filter(palabra -> !palabra.isEmpty() && !stopWords.contains(palabra) && palabra.length() > 3)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));


        String palabrasMasComunes = conteoPalabras.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(entry -> entry.getKey() + " (" + entry.getValue() + " veces)")
                .collect(Collectors.joining("\n- "));


        StringBuilder resumen = new StringBuilder();
        resumen.append("Resumen de la Conversación con ").append(contactoActual.getNombre()).append("\n");
        resumen.append("====================================================\n\n");
        resumen.append("▪ Total de mensajes: ").append(totalMensajes).append("\n\n");
        resumen.append("▪ Mensajes por usuario:\n");
        mensajesPorUsuario.forEach((usuario, cantidad) ->
                resumen.append("  - ").append(usuario).append(": ").append(cantidad).append(" mensajes\n")
        );
        resumen.append("\n▪ 5 palabras más comunes (más de 3 letras):\n- ");
        resumen.append(palabrasMasComunes.isEmpty() ? "No hay suficientes palabras." : palabrasMasComunes);

        mostrarResumenEnDialogo(resumen.toString());
    }

    /**
     * Metodo que crea una alerta para mostrar el resumen de la conversacion
     * @param contenido
     */
    private void mostrarResumenEnDialogo(String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Resumen de la Conversación");
        alert.setHeaderText("Análisis de la conversación con " + contactoActual.getNombre());
        TextArea textArea = new TextArea(contenido);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(400, 250);

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    /**
     * Metodo que crea una alerta para mostrar un mensaje
     * @param mensaje
     */
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Metodo que convierte un mensaje a CSV, usado en el metodo exportarConversacion
     * @param m
     * @return String con el mensaje convertido a CSV
     */
    private String convertirMensajeACSV(Mensaje m) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fecha = m.getFechaEnvio().format(formatter);
        String remitente = m.getRemitente();
        String contenido = "\"" + m.getContenido().replace("\"", "\"\"") + "\"";
        String adjunto = m.tieneAdjunto() ? m.getAdjunto().getNombreArchivo() : "N/A";

        return String.join(",", fecha, remitente, contenido, adjunto);
    }


    /**
     * Este método devuelve el tipo MIME de un archivo (extension)
     * @param file
     * @return
     */
    private String getMimeType(File file) {

        // Obtener el nombre del archivo y convertirlo a minúsculas.
        String name = file.getName().toLowerCase();


        // Imagen
        if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif")) {
            // Si es una imagen, construye el tipo MIME como "image/" seguido de la extensión (sin el punto).
            // Por ejemplo, para "foto.jpg", devuelve "image/jpg".
            return "image/" + name.substring(name.lastIndexOf(".") + 1);
        }

        // Video
        if (name.endsWith(".mp4") || name.endsWith(".mov") || name.endsWith(".avi")) {

            return "video/" + name.substring(name.lastIndexOf(".") + 1);
        }


        // PDF
        if (name.endsWith(".pdf")) {
            // Para PDF, el tipo MIME estándar es "application/pdf".
            return "application/pdf";
        }

        //    Caso por defecto: Si la extensión no coincide con ninguno de los tipos anteriores.
        //    Se devuelve "application/octet-stream", que es un tipo MIME genérico que indica
        //    que el archivo es un flujo de bytes binarios y que el navegador o la aplicación
        //    deberían tratarlo como un archivo descargable o de tipo desconocido.
        return "application/octet-stream";
    }

    /**
     * Metodo usado por el metodo exportanConversacion y enviarMensaje
     * Guarda el mensaje en el arhcivo xml
     * @param mensaje
     */
    private void guardarMensajeEnXML(Mensaje mensaje) {
        File conversacionFile = getConversacionesFile();
        if (conversacionFile == null) return;

        /**
         * cargamos una conversación existente desde un archivo XML o inicializamos una nueva conversación vacía si el archivo no existe o está vacío.
         */
        GestorConversacion conversacion = new GestorConversacion();
        if (conversacionFile.exists() && conversacionFile.length() > 0) {
            conversacion = XMLManager.readXML(new GestorConversacion(), conversacionFile.getAbsolutePath());
        }
        if (conversacion.getMensajes() == null) {
            conversacion.setMensajes(new ArrayList<>());
        }

        conversacion.getMensajes().add(mensaje);
        XMLManager.writeXML(conversacion, conversacionFile.getAbsolutePath());
    }

    /**
     * Metodo para empaquetar la conversacion en un zip
     * Describeme el metodo
     *
     */
    @FXML
    private void empaquetarConversacionEnZip() {
        File conversacionFile = getConversacionesFile();
        if (conversacionFile == null || !conversacionFile.exists() || gestorMensajesVacio(conversacionFile)) {
            mostrarAlerta("No hay conversación para empaquetar.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Empaquetar Conversación en ZIP");
        fileChooser.setInitialFileName("conversacion_" + contactoActual.getNombre() + ".zip");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP (*.zip)", "*.zip"));
        File zipFile = fileChooser.showSaveDialog(null);
        if (zipFile == null) return;

        GestorConversacion gestor = XMLManager.readXML(new GestorConversacion(), conversacionFile.getAbsolutePath());
        List<Mensaje> mensajes = gestor.getMensajes().stream()
                .filter(m -> (m.getRemitente().equals(usuarioActual.getNombre()) && m.getDestinatario().equals(contactoActual.getNombre())) ||
                        (m.getRemitente().equals(contactoActual.getNombre()) && m.getDestinatario().equals(usuarioActual.getNombre())))
                .sorted(Comparator.comparing(Mensaje::getFechaEnvio))
                .collect(Collectors.toList());

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            // Añadir el historial de chat
            zos.putNextEntry(new ZipEntry("conversacion.txt"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (Mensaje m : mensajes) {
                String linea = String.format("[%s] %s: %s\n", m.getFechaEnvio().format(formatter), m.getRemitente(), m.getContenido());
                zos.write(linea.getBytes(StandardCharsets.UTF_8));
            }
            zos.closeEntry();

            // Añadir los adjuntos
            for (Mensaje m : mensajes) {
                if (m.tieneAdjunto()) {
                    File adjuntoFile = new File(m.getAdjunto().getRutaArchivo());
                    if (adjuntoFile.exists()) {
                        zos.putNextEntry(new ZipEntry("adjuntos/" + adjuntoFile.getName()));
                        try (FileInputStream fis = new FileInputStream(adjuntoFile)) {
                            fis.transferTo(zos);
                        }
                        zos.closeEntry();
                    }
                }
            }
            mostrarAlerta("Conversación empaquetada en: " + zipFile.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al empaquetar la conversación en ZIP.", e);
            mostrarAlerta("Error al crear el archivo ZIP.");
        }
    }


    private boolean gestorMensajesVacio(File file) {
        GestorConversacion gestor = XMLManager.readXML(new GestorConversacion(), file.getAbsolutePath());
        return gestor == null || gestor.getMensajes() == null || gestor.getMensajes().isEmpty();
    }
}
