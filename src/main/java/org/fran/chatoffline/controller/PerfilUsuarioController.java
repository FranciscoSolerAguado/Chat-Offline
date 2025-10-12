package org.fran.chatoffline.controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

public class PerfilUsuarioController {
    private static final Logger LOGGER = Logger.getLogger(ConversacionController.class.getName());

    @FXML
    private Button btnCerrar;

    @FXML
    private void initialize() {
        btnCerrar.setOnAction(e -> cerrar());

    }

    private void cerrar() {
            try {
                Node MainContent = FXMLLoader.load(getClass().getResource("/org/fran/chatoffline/ui/main.fxml"));
                // Obtener la escena actual y reemplazar el contenido
                Stage stage = (Stage) btnCerrar.getScene().getWindow();
                stage.getScene().setRoot((Parent) MainContent);
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

}
