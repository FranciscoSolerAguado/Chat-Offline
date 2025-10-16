package org.fran.chatoffline.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.fran.chatoffline.model.Usuario;

import java.util.logging.Logger;

public class PerfilUsuarioController {

    private static final Logger LOGGER = Logger.getLogger(PerfilUsuarioController.class.getName());

    private MainController mainController;
    private Usuario usuarioMostrado;

    @FXML
    private Button btnCerrar;

    /**
     * Permite al MainController inyectarse a sí mismo para la comunicación.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize(){
        btnCerrar.setOnAction(e -> handleCerrar());
    }

    /**
     * Recibe el usuario cuyo perfil se va a mostrar.
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioMostrado = usuario;
        // Aquí actualizarías las etiquetas del FXML con los datos del usuario
        LOGGER.info("Mostrando perfil de: " + usuario.getNombreUsuario());
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
