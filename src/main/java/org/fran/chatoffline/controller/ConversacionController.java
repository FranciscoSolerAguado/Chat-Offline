package org.fran.chatoffline.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.fran.chatoffline.model.Mensaje;
import org.fran.chatoffline.service.ConversacionService;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;

public class ConversacionController {

    @FXML
    private VBox contenedorMensajes;

    @FXML
    private TextField campoMensaje;

    @FXML
    private ScrollPane scrollMensajes;

    @FXML
    private void initialize() {
        cargarMensajesDesdeXML();
    }

    /**
     * Carga los mensajes desde el archivo XML de conversación.
     * Usa JAXB para deserializar los objetos.
     */
    private void cargarMensajesDesdeXML() {
        try {
            // Cargar el archivo XML
            JAXBContext contexto = JAXBContext.newInstance(ConversacionService.class);
            Unmarshaller lector = contexto.createUnmarshaller();
            ConversacionService conversacion = (ConversacionService)
                    lector.unmarshal(new File("src/main/resources/conversaciones.xml"));

            // Mostrar los mensajes en la interfaz
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
     * Si el remitente es "Tú", se alinea a la derecha.
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

        // Desplazar automáticamente hacia abajo
        scrollMensajes.layout();
        scrollMensajes.setVvalue(1.0);
    }

    /**
     * Evento del botón enviar.
     * Añade un nuevo mensaje al chat (y en el futuro lo guarda en XML).
     */
    @FXML
    private void enviarMensaje() {
        String texto = campoMensaje.getText().trim();

        if (!texto.isEmpty()) {
            Mensaje nuevo = new Mensaje("Tú", "Juan", texto);
            agregarMensaje(nuevo);
            campoMensaje.clear();

            // Aquí podrías agregar la lógica para guardar el mensaje en el XML
        }
    }
}