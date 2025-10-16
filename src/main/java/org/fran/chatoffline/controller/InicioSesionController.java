package org.fran.chatoffline.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.fran.chatoffline.dataAccess.XMLManager;
import org.fran.chatoffline.model.GestorUsuarios;
import org.fran.chatoffline.model.Usuario;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InicioSesionController {
    private static final Logger LOGGER = Logger.getLogger(InicioSesionController.class.getName());
    private static final String USUARIOS_XML_PATH = "src/main/resources/org/fran/chatoffline/usuarios.xml";

    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnEntrar;
    @FXML
    private Hyperlink linkRegistro;
    @FXML
    private HBox topBar;

    @FXML
    private void initialize() {
        btnEntrar.setOnAction(e -> iniciarSesion());
        linkRegistro.setOnAction(e -> abrirRegistro());
    }

    private void iniciarSesion() {
        String email = txtEmail.getText().trim();
        String pass = txtPassword.getText().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            mostrarAlerta("Por favor, introduce tu email y contraseña.");
            return;
        }

        LOGGER.info("Intento de inicio de sesión para: " + email);

        File usuariosFile = new File(USUARIOS_XML_PATH);
        if (!usuariosFile.exists()) {
            LOGGER.warning("Archivo de usuarios no encontrado. Nadie puede iniciar sesión.");
            mostrarAlerta("Credenciales incorrectas.");
            return;
        }

        GestorUsuarios coleccionUsuarios = new GestorUsuarios();
        try {
            coleccionUsuarios = XMLManager.readXML(coleccionUsuarios, USUARIOS_XML_PATH);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al leer el archivo de usuarios.", e);
            mostrarAlerta("Error del sistema. Por favor, contacta al administrador.");
            return;
        }

        Optional<Usuario> usuarioEncontrado = coleccionUsuarios.getUsuarios().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();

        if (usuarioEncontrado.isPresent() && usuarioEncontrado.get().validarPassword(pass)) {
            LOGGER.info("Inicio de sesión exitoso para: " + email);
            navegarAPantallaPrincipal(usuarioEncontrado.get());
        } else {
            LOGGER.warning("Fallo de inicio de sesión para: " + email);
            mostrarAlerta("Credenciales incorrectas.");
        }
    }

    private void navegarAPantallaPrincipal(Usuario usuarioLogueado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/fran/chatoffline/ui/main.fxml"));
            Parent mainContent = loader.load();

            // Obtener el controlador de la nueva ventana
            MainController mainController = loader.getController();
            // Pasar el usuario que ha iniciado sesión
            mainController.setUsuarioActual(usuarioLogueado);

            Stage stage = (Stage) topBar.getScene().getWindow();
            stage.getScene().setRoot(mainContent);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error fatal al cargar la ventana principal.", e);
            mostrarAlerta("Error fatal al cargar la aplicación.");
        }
    }

    private void abrirRegistro() {
        try {
            Parent registroContent = FXMLLoader.load(getClass().getResource("/org/fran/chatoffline/ui/registro.fxml"));
            Stage stage = (Stage) topBar.getScene().getWindow();
            stage.getScene().setRoot(registroContent);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar la ventana de registro.", e);
            mostrarAlerta("Error al cargar la ventana de registro.");
        }
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void handleMinimize() {
        Stage stage = (Stage) topBar.getScene().getWindow();
        stage.setIconified(true);
    }

    private boolean isMaximized = false;

    @FXML
    private void handleToggleMaximize() {
        Stage stage = (Stage) topBar.getScene().getWindow();
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        if (isMaximized) {
            stage.setWidth(1200);
            stage.setHeight(800);
            stage.setX(screenBounds.getMinX() + (screenBounds.getWidth() - 1000) / 2);
            stage.setY(screenBounds.getMinY() + (screenBounds.getHeight() - 700) / 2);
            isMaximized = false;
        } else {
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            isMaximized = true;
        }
    }

    @FXML
    private void handleClose() {
        Platform.exit();
    }
}
