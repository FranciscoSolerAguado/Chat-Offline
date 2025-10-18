package org.fran.chatoffline.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConversacionController {
    private static final Logger LOGGER = Logger.getLogger(ConversacionController.class.getName());

    private MainController mainController;
    private Usuario usuarioActual;
    private Usuario contactoActual;

    @FXML private VBox contenedorMensajes;
    @FXML private HBox topBar;
    @FXML private TextField campoMensaje;
    @FXML private ScrollPane scrollMensajes;
    @FXML private Label lblNombreContacto;

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
