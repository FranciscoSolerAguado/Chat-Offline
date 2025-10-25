package org.fran.chatoffline.controller;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.fran.chatoffline.utils.LoggerUtil;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

public class InicioController {
    private static final Logger LOGGER = LoggerUtil.getLogger();
    @FXML
    private AnchorPane rootPane;

    /**
     * Método que se ejecuta al inicializar la vista.
     * Configura una pantalla de caraga de 3 segundos antes de cambiar a la vista principal.
     */
    @FXML
    public void initialize() { //PANTALLA DE CARGA
        // Espera 3 segundos antes de cambiar a pantalla completa
        LOGGER.info("Cargando pantalla de carga...");
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> ponerPantallaCompleta());
        pause.play();
        LOGGER.info("Pantalla de carga cargada exitosamente.");
    }

    /**
     * Cambia la vista actual a la vista principal y ajusta la ventana.
     */
    private void ponerPantallaCompleta() {
        LOGGER.info("Poniendo pantalla completa...");
        try {
            // Ruta absoluta del FXML en `src/main/resources/org/fran/chatoffline/ui/main.fxml`
            URL fxmlUrl = getClass().getResource("/org/fran/chatoffline/ui/inicioSesion.fxml");
            if (fxmlUrl == null) {
                throw new IllegalStateException("No se encontró `src/main/resources/org/fran/chatoffline/ui/main.fxml`");
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            Scene newScene = new Scene(root);

            // Obtener el Stage actual desde el rootPane
            Stage stage = (Stage) rootPane.getScene().getWindow();

            // Asignar la nueva escena
            stage.setScene(newScene);

            // Evitar modo maximizado/pantalla completa y ajustar tamaño a la pantalla visible
            stage.setFullScreen(false);
            stage.setMaximized(false);
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
        }
        LOGGER.info("Pantalla completa cargada exitosamente.");
    }
}

