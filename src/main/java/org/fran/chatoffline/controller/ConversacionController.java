package org.fran.chatoffline.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fran.chatoffline.model.GestorConversacion;
import org.fran.chatoffline.model.Mensaje;
import org.fran.chatoffline.model.Usuario;


import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConversacionController {
    private static final Logger LOGGER = Logger.getLogger(ConversacionController.class.getName());
    private static final String CONVERSACIONES_XML_PATH = "/org/fran/chatoffline/conversaciones.xml";

    private MainController mainController;
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

    public void setContacto(Usuario contacto) {
        this.contactoActual = contacto;
        if (lblNombreContacto != null) {
            lblNombreContacto.setText(contacto.getNombreUsuario());
        } else {
            LOGGER.warning("La etiqueta lblNombreContacto es nula. Revisa el fx:id en conversacion.fxml.");
        }
        // Una vez que tenemos el contacto, cargamos su conversación específica
        cargarMensajesDesdeXML();
    }

    @FXML
    private void initialize() {
        // La carga de mensajes ahora se dispara desde setContacto()
        if (topBar != null) {
            topBar.setOnMouseClicked(e -> {
                if (mainController != null && contactoActual != null) {
                    mainController.abrirPerfilUsuario(contactoActual);
                }
            });
        }
    }

    private void cargarMensajesDesdeXML() {
        contenedorMensajes.getChildren().clear(); // Limpiar mensajes anteriores

        URL resourceUrl = getClass().getResource(CONVERSACIONES_XML_PATH);
        if (resourceUrl == null) {
            LOGGER.warning("No se encontró el archivo de conversaciones en la ruta: " + CONVERSACIONES_XML_PATH);
            // No hay archivo, no hay nada que cargar.
            return;
        }

        try {
            JAXBContext contexto = JAXBContext.newInstance(GestorConversacion.class);
            Unmarshaller lector = contexto.createUnmarshaller();
            GestorConversacion conversacion = (GestorConversacion) lector.unmarshal(resourceUrl);

            if (conversacion != null && conversacion.getMensajes() != null) {
                for (Mensaje m : conversacion.getMensajes()) {
                    // Aquí en el futuro filtrarías los mensajes entre el usuario actual y el contactoActual
                    agregarMensaje(m);
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

        // Esta lógica debería basarse en el ID del remitente, no en el nombre "Tú"
        if (mensaje.getRemitente().equalsIgnoreCase("Tú")) { // A mejorar en el futuro
            etiquetaMensaje.getStyleClass().add("mensaje-derecha");
            contenedor.setAlignment(Pos.CENTER_RIGHT);
        } else {
            etiquetaMensaje.getStyleClass().add("mensaje-izquierda");
            contenedor.setAlignment(Pos.CENTER_LEFT);
        }

        contenedor.getChildren().add(etiquetaMensaje);
        contenedorMensajes.getChildren().add(contenedor);

        // Desplazar automáticamente hacia abajo
        scrollMensajes.layout();
        scrollMensajes.setVvalue(1.0);
    }

    @FXML
    private void enviarMensaje() {
        String texto = campoMensaje.getText().trim();

        if (!texto.isEmpty() && contactoActual != null) {
            // La lógica del remitente debería usar el ID del usuario logueado
            Mensaje nuevo = new Mensaje("Tú", contactoActual.getNombreUsuario(), texto);
            agregarMensaje(nuevo);
            campoMensaje.clear();
            // Aquí iría la lógica para guardar el nuevo mensaje en el XML
        }
    }
}
