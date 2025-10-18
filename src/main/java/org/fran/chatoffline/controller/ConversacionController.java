package org.fran.chatoffline.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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


    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void setContacto(Usuario contacto) {
        this.contactoActual = contacto;
        if (lblNombreContacto != null) {
            lblNombreContacto.setText(contacto.getNombre());
        }
        cargarMensajesDesdeXML();
    }

    @FXML
    private void initialize() {
        if (topBar != null) {
            topBar.setOnMouseClicked(e -> {
                if (mainController != null && contactoActual != null) {
                    mainController.abrirPerfilUsuario(contactoActual);
                }
            });

            btnExportarConversacion.setOnAction(e -> exportarConversacion());
        }
    }

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

    private void agregarMensaje(Mensaje mensaje) {
        HBox contenedor = new HBox();
        contenedor.setPadding(new Insets(5, 10, 5, 10));
        contenedor.setFillHeight(false);

        VBox contenidoMensaje = new VBox(5);
        Label etiquetaMensaje = new Label(mensaje.getContenido());
        etiquetaMensaje.setWrapText(true);
        etiquetaMensaje.setMaxWidth(300);
        contenidoMensaje.getChildren().add(etiquetaMensaje);

        // Mostrar el adjunto si existe
        if (mensaje.tieneAdjunto()) {
            Adjunto adj = mensaje.getAdjunto();
            File archivoAdjunto = new File(adj.getRutaArchivo());
            if (archivoAdjunto.exists()) {
                if (adj.getTipoMime() != null && adj.getTipoMime().startsWith("image")) {
                    Image imagen = new Image(archivoAdjunto.toURI().toString());
                    ImageView imageView = new ImageView(imagen);
                    imageView.setFitWidth(200);
                    imageView.setPreserveRatio(true);
                    contenidoMensaje.getChildren().add(imageView);
                } else if (adj.getTipoMime() != null && adj.getTipoMime().startsWith("video")) {
                    Media media = new Media(archivoAdjunto.toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    MediaView mediaView = new MediaView(mediaPlayer);

                    mediaView.setFitWidth(300);
                    mediaView.setPreserveRatio(true);

                    mediaView.setOnMouseClicked(event -> {
                        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                            mediaPlayer.pause();
                        } else {
                            mediaPlayer.play();
                        }
                    });
                    contenidoMensaje.getChildren().add(mediaView);
                }
            }
        }

        if (usuarioActual != null && mensaje.getRemitente().equals(usuarioActual.getNombre())) {
            etiquetaMensaje.setStyle("-fx-background-color: #c2e7ff; -fx-background-radius: 10px; -fx-padding: 8px 12px;");
            contenedor.setAlignment(Pos.CENTER_RIGHT);
        } else {
            etiquetaMensaje.setStyle("-fx-background-color: #e6e6e6; -fx-background-radius: 10px; -fx-padding: 8px 12px;");
            contenedor.setAlignment(Pos.CENTER_LEFT);
        }

        contenedor.getChildren().add(contenidoMensaje);
        contenedorMensajes.getChildren().add(contenedor);

        Platform.runLater(() -> scrollMensajes.setVvalue(1.0));
    }

    @FXML
    private void enviarMensaje() {
        String texto = campoMensaje.getText().trim();
        if (texto.isEmpty() || usuarioActual == null || contactoActual == null) return;

        Mensaje nuevoMensaje = new Mensaje(usuarioActual.getNombre(), contactoActual.getNombre(), texto);
        agregarMensaje(nuevoMensaje);
        campoMensaje.clear();
        guardarMensajeEnXML(nuevoMensaje);
    }

    @FXML
    private void adjuntarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen o video");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos multimedia", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.mp4", "*.mov", "*.avi")
        );

        File archivoSeleccionado = fileChooser.showOpenDialog(null);
        if (archivoSeleccionado == null) return;

        // Crear carpeta media/
        File carpetaMedia = new File("src/main/resources/org/fran/chatoffline/media");
        if (!carpetaMedia.exists()) carpetaMedia.mkdirs();

        // Copiar el archivo
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

        Mensaje mensaje = new Mensaje(usuarioActual.getNombre(), contactoActual.getNombre(),
                "📎 Archivo adjunto: " + destino.getName(), adjunto);

        agregarMensaje(mensaje);
        guardarMensajeEnXML(mensaje);
    }

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
        if (conversacionFile == null || !conversacionFile.exists() || conversacionFile.length() == 0) {
            LOGGER.info("No hay conversación para exportar.");
            return;
        }

        GestorConversacion gestor = XMLManager.readXML(new GestorConversacion(), conversacionFile.getAbsolutePath());
        if (gestor == null || gestor.getMensajes() == null) {
            LOGGER.info("No hay mensajes para exportar.");
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Escribir cabecera del CSV
            writer.println("Fecha y Hora,Remitente,Contenido,Adjunto");

            // Procesar con Streams
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

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private String convertirMensajeACSV(Mensaje m) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fecha = m.getFechaEnvio().format(formatter);
        String remitente = m.getRemitente();
        // Escapar comillas y comas en el contenido
        String contenido = "\"" + m.getContenido().replace("\"", "\"\"") + "\"";
        String adjunto = m.tieneAdjunto() ? m.getAdjunto().getNombreArchivo() : "N/A";

        return String.join(",", fecha, remitente, contenido, adjunto);
    }


    private String getMimeType(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif"))
            return "image/" + name.substring(name.lastIndexOf(".") + 1);
        if (name.endsWith(".mp4") || name.endsWith(".mov") || name.endsWith(".avi"))
            return "video/" + name.substring(name.lastIndexOf(".") + 1);
        return "application/octet-stream";
    }

    private void guardarMensajeEnXML(Mensaje mensaje) {
        File conversacionFile = getConversacionesFile();
        if (conversacionFile == null) return;

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
}
