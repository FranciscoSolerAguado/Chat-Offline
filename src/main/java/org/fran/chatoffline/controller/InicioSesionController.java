package org.fran.chatoffline.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

public class InicioSesionController {
    private static final Logger LOGGER = Logger.getLogger(ConversacionController.class.getName());
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
        String email = txtEmail.getText();
        String pass = txtPassword.getText();

        if (email.isEmpty() || pass.isEmpty()) {
            mostrarAlerta("Por favor, completa todos los campos.");
        } else {
            System.out.println("Intento de inicio de sesión con: " + email);
            try {
                Node MainContent = FXMLLoader.load(getClass().getResource("/org/fran/chatoffline/ui/main.fxml"));
                // Obtener la escena actual y reemplazar el contenido
                Stage stage = (Stage) topBar.getScene().getWindow();
                stage.getScene().setRoot((Parent) MainContent);
            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Error al cargar la ventana principal.");
            }
            // Aquí podrás validar contra usuarios.xml
        }
    }

    private void abrirRegistro() {
        System.out.println("Abrir ventana de registro...");
        try {
            Node registroContent = FXMLLoader.load(getClass().getResource("/org/fran/chatoffline/ui/registro.fxml"));
            // Obtener la escena actual y reemplazar el contenido
            Stage stage = (Stage) topBar.getScene().getWindow();
            stage.getScene().setRoot((Parent) registroContent);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar la ventana de registro.");
        }

    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }



    /**
     * Método que maneja el evento de minimizar la ventana
     */
    @FXML
    private void handleMinimize() {
        Stage stage = (Stage) topBar.getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * Método que maneja el evento de maximizar o restaurar la ventana.
     */
    private boolean isMaximized = false;

    @FXML
    private void handleToggleMaximize() {
        Stage stage = (Stage) topBar.getScene().getWindow();
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        if (isMaximized) {
            // Restaurar tamaño y centrar
            stage.setWidth(1200);
            stage.setHeight(800);
            stage.setX(screenBounds.getMinX() + (screenBounds.getWidth() - 1000) / 2);
            stage.setY(screenBounds.getMinY() + (screenBounds.getHeight() - 700) / 2);
            isMaximized = false;
        } else {
            // Maximizar manualmente
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            isMaximized = true;
        }
    }

    /**
     * Método que maneja el evento de cerrar la aplicación.
     */
    @FXML
    private void handleClose() {
        Platform.exit();
    }
}
