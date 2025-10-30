package org.fran.chatoffline.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.fran.chatoffline.model.Usuario;
import org.fran.chatoffline.utils.LoggerUtil;

import java.util.logging.Logger;

public class PerfilUsuarioController {
    private static final Logger LOGGER = LoggerUtil.getLogger();

    private MainController mainController;
    private Usuario usuarioLogueado;
    private Usuario usuarioDelPerfil;

    @FXML
    private Label lblNombreContacto;
    @FXML
    private Label lblGmailContacto;
    @FXML
    private Label lblTelefonoContacto;
    @FXML
    private Label lblEstadoContacto;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Recibe los datos necesarios para configurar la vista del perfil.
     * @param usuarioDelPerfil El usuario cuyo perfil se va a mostrar.
     * @param usuarioLogueado El usuario que ha iniciado la sesión.
     */
    public void setDatosPerfil(Usuario usuarioDelPerfil, Usuario usuarioLogueado) {
        LOGGER.info("Configurando datos del perfil del usuario.");
        this.usuarioDelPerfil = usuarioDelPerfil;
        this.usuarioLogueado = usuarioLogueado;
        actualizarVista();
    }

    /**
     * Método que actualiza los datos que se muestran en la vista del perfil del usuario
     * Y le asigna los datos del usuario del perfil a los distintos label.
     */
    private void actualizarVista() {
        LOGGER.info("Actualizando vista del perfil del usuario.");
        if (usuarioDelPerfil == null) {
            LOGGER.severe("No se ha proporcionado un usuario para mostrar en el perfil.");
            return;
        }

        if (lblNombreContacto != null) {
            lblNombreContacto.setText(usuarioDelPerfil.getNombre());
        }
        if (lblGmailContacto != null) {
            lblGmailContacto.setText(usuarioDelPerfil.getEmail());
        }
        if (lblTelefonoContacto != null) {
            lblTelefonoContacto.setText(usuarioDelPerfil.getTelefono());
        }

        // Lógica para mostrar el estado "Activo" o "Desconectado"
        if (lblEstadoContacto != null) {
            // Comprueba si el perfil que se muestra es el del propio usuario logueado
            if (usuarioLogueado != null && usuarioLogueado.getIdUsuario().equals(usuarioDelPerfil.getIdUsuario())) {
                lblEstadoContacto.setText("Activo");
                lblEstadoContacto.getStyleClass().removeAll("estado-inactivo");
                lblEstadoContacto.getStyleClass().add("estado-activo");
            } else {
                lblEstadoContacto.setText("Desconectado");
                lblEstadoContacto.getStyleClass().removeAll("estado-activo");
                lblEstadoContacto.getStyleClass().add("estado-inactivo");
            }
        }
        LOGGER.info("Vista del perfil del usuario actualizada exitosamente.");
    }

    /**
     * Método que maneja el cierre del perfil del usuario
     * Despues llama al método cerrarVistaSecundariaYRefrescar() del mainController para que los datos de la pantalla principal se muestren correctamente
     */
    @FXML
    private void handleCerrar() {
        LOGGER.info("Cerrando perfil del usuario.");
        if (mainController != null) {
            mainController.cerrarVistaSecundariaYRefrescar();
        } else {
            LOGGER.severe("MainController es nulo. No se puede cerrar la vista correctamente.");
        }
    }
}
