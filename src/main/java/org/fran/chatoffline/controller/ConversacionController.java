package org.fran.chatoffline.controller;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fran.chatoffline.model.Mensaje;
import org.fran.chatoffline.service.ConversacionService;


import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class ConversacionController {
    private static final Logger LOGGER = Logger.getLogger(ConversacionController.class.getName());

    // Referencia al controlador principal para delegar la navegación
    private MainController mainController;

    @FXML
    private VBox contenedorMensajes;

    @FXML
    private HBox topBar;

    @FXML
    private TextField campoMensaje;

    @FXML
    private ScrollPane scrollMensajes;

    /**
     * Permite al MainController inyectarse a sí mismo para que este
     * controlador pueda comunicarse con él.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        cargarMensajesDesdeXML();

        // Asigna el evento de clic directamente al HBox de la barra superior.
        if (topBar != null) {
            topBar.setOnMouseClicked(e -> {
                if (mainController != null) {
                    // Pide al controlador principal que muestre la vista de perfil.
                    mainController.mostrarEnMainArea("perfilUsuario.fxml");
                } else {
                    LOGGER.warning("MainController no está disponible. No se puede abrir el perfil.");
                }
            });
        }
    }

    /**
     * Carga los mensajes desde el archivo XML de conversación.
     * Usa JAXB para deserializar los objetos.
     */
    private void cargarMensajesDesdeXML() {
        try {
            JAXBContext contexto = JAXBContext.newInstance(ConversacionService.class);
            Unmarshaller lector = contexto.createUnmarshaller();
            ConversacionService conversacion = (ConversacionService)
                    lector.unmarshal(new File("src/main/resources/conversaciones.xml"));

            List<Mensaje> mensajes = conversacion.getMensajes();
            for (Mensaje m : mensajes) {
                agregarMensaje(m);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Agrega un mensaje visualmente al contenedor.
     */
    private void agregarMensaje(Mensaje mensaje) {
        HBox contenedor = new HBox();
        contenedor.setPadding(new Insets(5, 10, 5, 10));

        Label etiquetaMensaje = new Label(mensaje.getContenido());
        etiquetaMensaje.setWrapText(true);

        if (mensaje.getRemitente().equalsIgnoreCase("Tú")) {
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
    }

    /**
     * Evento del botón enviar.
     */
    @FXML
    private void enviarMensaje() {
        String texto = campoMensaje.getText().trim();

        if (!texto.isEmpty()) {
            Mensaje nuevo = new Mensaje("Tú", "Juan", texto);
            agregarMensaje(nuevo);
            campoMensaje.clear();
        }
    }
}
