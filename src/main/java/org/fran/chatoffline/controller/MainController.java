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
    private StackPane mainArea;

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

        // Carga la vista de conversación por defecto al iniciar
        Platform.runLater(() -> abrirConversacion());

        // Configura los botones que ya existen en main.fxml
        setupExistingButtons();
    }

    /**
     * Configura los eventos para los botones que forman parte de la UI principal (main.fxml).
     */
    private void setupExistingButtons() {
        Platform.runLater(() -> {
            Scene scene = topBar.getScene();
            if (scene == null) return;
            Parent root = scene.getRoot();

            // Botón para abrir perfil de usuario
            Set<Node> masInfoBotones = root.lookupAll(".masInformacionBoton");
            masInfoBotones.forEach(node -> {
                if (node instanceof Button) {
                    ((Button) node).setOnAction(e -> mostrarEnMainArea("perfilUsuario.fxml"));
                }
            });

            // HBox para abrir una conversación
            Set<Node> chatHboxes = root.lookupAll(".chatHbox");
            chatHboxes.forEach(node -> {
                node.setOnMouseClicked(e -> abrirConversacion());
            });
        });
    }

    /**
     * Carga la vista de conversación y le pasa una referencia de este controlador.
     */
    private void abrirConversacion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/fran/chatoffline/ui/conversacion.fxml"));
            Parent conversacionView = loader.load();

            // Obtiene el controlador de la vista cargada
            ConversacionController conversacionController = loader.getController();
            // Le pasa una referencia de este MainController
            conversacionController.setMainController(this);

            if (mainArea != null) {
                mainArea.getChildren().setAll(conversacionView);
            } else {
                LOGGER.severe("mainArea es nulo. No se puede cargar la vista.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.severe("Error al cargar conversacion.fxml: " + e.getMessage());
        }
    }

    /**
     * Método genérico para cargar cualquier FXML en el área principal.
     * @param fxmlFileName El nombre del archivo FXML (ej. "perfilUsuario.fxml")
     */
    public void mostrarEnMainArea(String fxmlFileName) {
        try {
            String resourcePath = "/org/fran/chatoffline/ui/" + fxmlFileName;
            Parent view = FXMLLoader.load(getClass().getResource(resourcePath));
            if (mainArea != null) {
                mainArea.getChildren().setAll(view);
            } else {
                LOGGER.severe("mainArea es nulo. No se puede cargar " + fxmlFileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.severe("Error al cargar " + fxmlFileName + ": " + e.getMessage());
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
