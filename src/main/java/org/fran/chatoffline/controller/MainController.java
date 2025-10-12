package org.fran.chatoffline.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

public class MainController {
    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    @FXML
    private HBox topBar;

    @FXML
    private StackPane mainArea; // nuevo: contenedor donde se cargará perfilUsuario.fxml

    @FXML
    private HBox chatHbox; // nuevo: botón para abrir conversación

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private void initialize() {
        topBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        topBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) topBar.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        Platform.runLater(() -> {
            Scene scene = topBar.getScene();
            if (scene == null) return;
            Parent root = scene.getRoot();
            Set<Node> nodes = root.lookupAll(".add-btn");
            nodes.forEach(node -> {
                if (node instanceof Button) {
                    ((Button) node).setOnAction(e -> abrirPerfilUsuario());
                }
            });
        });

        if (chatHbox != null) {
            chatHbox.setOnMouseClicked(e -> abrirConversacion());
        }

    }

    private void abrirConversacion() {
        try {
            Parent conversacion = FXMLLoader.load(getClass().getResource("/org/fran/chatoffline/ui/conversacion.fxml"));
            if (mainArea != null) {
                mainArea.getChildren().setAll(conversacion); // reemplaza contenido del StackPane central
            } else {
                // fallback: reemplaza raíz si mainArea no está disponible
                Stage stage = (Stage) topBar.getScene().getWindow();
                stage.getScene().setRoot(conversacion);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.severe("Error al cargar conversacion.fxml: " + e.getMessage());
        }
    }


    private void abrirPerfilUsuario() {
        try {
            Parent perfil = FXMLLoader.load(getClass().getResource("/org/fran/chatoffline/ui/perfilUsuario.fxml"));
            if (mainArea != null) {
                mainArea.getChildren().setAll(perfil); // reemplaza contenido del StackPane central
            } else {
                // fallback: reemplaza raíz si mainArea no está disponible
                Stage stage = (Stage) topBar.getScene().getWindow();
                stage.getScene().setRoot(perfil);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.severe("Error al cargar perfilUsuario.fxml: " + e.getMessage());
        }
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
