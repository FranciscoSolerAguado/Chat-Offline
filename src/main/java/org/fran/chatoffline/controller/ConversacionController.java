package org.fran.chatoffline.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fran.chatoffline.dataAccess.XMLManager;
import org.fran.chatoffline.model.GestorConversacion;
import org.fran.chatoffline.model.Mensaje;
import org.fran.chatoffline.model.Usuario;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        } else {
            LOGGER.warning("La etiqueta lblNombreContacto es nula. Revisa el fx:id en conversacion.fxml.");
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

        Label etiquetaMensaje = new Label(mensaje.getContenido());
        etiquetaMensaje.setWrapText(true);

        if (usuarioActual != null && mensaje.getRemitente().equals(usuarioActual.getNombre())) {
            etiquetaMensaje.getStyleClass().add("mensaje-derecha");
            contenedor.setAlignment(Pos.CENTER_RIGHT);
        } else {
            etiquetaMensaje.getStyleClass().add("mensaje-izquierda");
            contenedor.setAlignment(Pos.CENTER_LEFT);
        }

        contenedor.getChildren().add(etiquetaMensaje);
        contenedorMensajes.getChildren().add(contenedor);

        scrollMensajes.layout();
        scrollMensajes.setVvalue(1.0);
        Platform.runLater(() -> scrollMensajes.setVvalue(1.0));

    }

    @FXML
    private void enviarMensaje() {
        String texto = campoMensaje.getText().trim();
        if (texto.isEmpty() || usuarioActual == null || contactoActual == null) {
            return;
        }

        Mensaje nuevoMensaje = new Mensaje(usuarioActual.getNombre(), contactoActual.getNombre(), texto);
        agregarMensaje(nuevoMensaje);
        campoMensaje.clear();

        File conversacionFile = getConversacionesFile();
        if (conversacionFile == null) {
            LOGGER.severe("No se pudo obtener la ruta del archivo de conversaciones. No se puede guardar el mensaje.");
            return;
        }

       GestorConversacion conversacion = new GestorConversacion();
        if (conversacionFile.exists() && conversacionFile.length() > 0) {
            try {
                conversacion = XMLManager.readXML(new GestorConversacion(), conversacionFile.getAbsolutePath());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al leer el archivo de conversaciones para guardar. Se creará uno nuevo.", e);
            }
        }
        if (conversacion.getMensajes() == null) {
            conversacion.setMensajes(new ArrayList<>());
        }

        conversacion.getMensajes().add(nuevoMensaje);

        boolean guardadoExitoso = XMLManager.writeXML(conversacion, conversacionFile.getAbsolutePath());
        if (guardadoExitoso) {
            LOGGER.info("Mensaje guardado exitosamente.");
        } else {
            LOGGER.severe("Fallo al guardar el mensaje en el archivo XML.");
        }
    }

    private File getConversacionesFile() {
        String ruta = System.getProperty("user.home") + File.separator + "conversaciones.xml";
        File archivo = new File(ruta);
        try {
            if (!archivo.exists()) {
                archivo.createNewFile();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo crear el archivo de conversaciones.", e);
            return null;
        }
        return archivo;
    }

    private File getFileFromResource(String resourcePath) {
        try {
            URL resourceUrl = getClass().getResource(resourcePath);
            if (resourceUrl == null) {
                URL dirUrl = getClass().getResource("/");
                if (dirUrl == null) throw new IOException("No se puede encontrar la raíz del classpath.");
                File rootDir = new File(dirUrl.toURI());
                File outputFile = new File(rootDir, resourcePath.substring(1));
                outputFile.getParentFile().mkdirs();
                return outputFile;
            }
            return new File(resourceUrl.toURI());
        } catch (URISyntaxException | IOException e) {
            LOGGER.log(Level.SEVERE, "Error crítico al obtener la ruta del archivo: " + resourcePath, e);
            return null;
        }
    }
}
