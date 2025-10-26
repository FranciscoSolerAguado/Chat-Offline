package org.fran.chatoffline.utils;

import javafx.scene.control.Alert;

import java.util.logging.Logger;

public class Utils {
    private static final Logger LOGGER = LoggerUtil.getLogger();
    /**
     * Metodo que crea una alerta para mostrar un mensaje
     * @param mensaje
     */
    public static void mostrarAlerta(String mensaje) {
        LOGGER.info("Mostrando alerta: " + mensaje);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
