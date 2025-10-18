package org.fran.chatoffline.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.fran.chatoffline.model.Usuario;

import java.util.logging.Logger;

public class PerfilUsuarioController {

    private static final Logger LOGGER = Logger.getLogger(PerfilUsuarioController.class.getName());

    private MainController mainController;
    private Usuario usuarioActual;
    private Usuario contactoActual;


    @FXML
    private Label lblNombreContacto;

    @FXML
    private Label lblGmailContacto;

    @FXML
    private Label lblTelefonoContacto;

    @FXML
    private Label lblEstadoContacto;


    @FXML
    private Button btnCerrar;

    /**
     * Permite al MainController inyectarse a sí mismo para la comunicación.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    @FXML
    private void initialize(){
        btnCerrar.setOnAction(e -> handleCerrar());
    }

    /**
     * Recibe el usuario cuyo perfil se va a mostrar.
     */
    public void setContacto(Usuario contacto) {
        this.contactoActual = contacto;
        if (lblNombreContacto != null) {
            lblNombreContacto.setText(contacto.getNombre());
        } else {
            LOGGER.warning("La etiqueta lblNombreContacto es nula. Revisa el fx:id en conversacion.fxml.");
        }

        if (lblGmailContacto != null) {
            lblGmailContacto.setText(contacto.getEmail());
        } else {
            LOGGER.warning("La etiqueta lblGmailContacto es nula. Revisa el fx:id en conversacion.fxml.");
        }

        if (lblTelefonoContacto != null) {
            lblTelefonoContacto.setText(contacto.getTelefono());
        }else {
            LOGGER.warning("La etiqueta lblTelefonoContacto es nula. Revisa el fx:id en conversacion.fxml.");
        }

        if (lblEstadoContacto != null) {
            lblEstadoContacto.setText("En linea");
        }else {
            LOGGER.warning("La etiqueta lblEstadoContacto es nula. Revisa el fx:id en conversacion.fxml.");
        }
    }
    /**
     * Maneja el evento del botón "Cerrar" o "Volver".
     * Notifica al MainController para que limpie la vista y refresque la lista de chats.
     * DEBES AÑADIR UN BOTÓN EN perfilUsuario.fxml Y ASIGNARLE onAction="#handleCerrar"
     */
    @FXML
    private void handleCerrar() {
        if (mainController != null) {
            LOGGER.info("Cerrando perfil y refrescando la lista de chats.");
            mainController.cerrarVistaSecundariaYRefrescar();
        } else {
            LOGGER.severe("MainController es nulo. No se puede cerrar la vista correctamente.");
        }
    }
}
