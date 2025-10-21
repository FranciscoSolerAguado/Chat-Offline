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
import org.fran.chatoffline.utils.LoggerUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InicioSesionController {
    private static final Logger LOGGER = LoggerUtil.getLogger();

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

        File usuariosFile = getUsuariosFile();
        if (usuariosFile == null || !usuariosFile.exists() || usuariosFile.length() == 0) {
            LOGGER.warning("Archivo de usuarios no encontrado o vacío. Nadie puede iniciar sesión.");
            mostrarAlerta("Credenciales incorrectas.");
            return;
        }

        GestorUsuarios coleccionUsuarios = new GestorUsuarios();
        try {
            coleccionUsuarios = XMLManager.readXML(new GestorUsuarios(), usuariosFile.getAbsolutePath());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error crítico al leer el archivo de usuarios.", e);
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

    /**
     * Llama a getFileFromResource y devuelve la ruta convertida a archivo gracias a getFileFromResource
     * @return
     */
    private File getUsuariosFile() {
        final String resourcePath = "/org/fran/chatoffline/usuarios.xml";
        return getFileFromResource(resourcePath);
    }

    /**
     * Metodo que busca el archivo de usuarios.xml
     * @param resourcePath
     * @return
     */
    private File getFileFromResource(String resourcePath) {
        try {
            // Intenta obtener la URL del recurso utilizando el ClassLoader.
            // Esto buscará el archivo en las carpetas de recursos del proyecto (classpath).
            URL resourceUrl = getClass().getResource(resourcePath);

            // Si resourceUrl es null, significa que el recurso no se encontró como un archivo físico.
            // Este es el escenario típico cuando la aplicación se ejecuta desde un archivo JAR.
            if (resourceUrl == null) {
                URL dirUrl = getClass().getResource("/");
                if (dirUrl == null) {
                    throw new IOException("No se puede encontrar la raíz del classpath.");
                }


                File rootDir = new File(dirUrl.toURI());

                // Construimos la ruta del archivo de salida. Estará en el mismo directorio que el JAR,
                // siguiendo la estructura de paquetes. El substring(1) elimina la barra inicial de resourcePath.
                File outputFile = new File(rootDir, resourcePath.substring(1));

                // Aseguramos que todos los directorios padre para este archivo existan.
                // Si no existen, los crea. Por ejemplo, para "data/config/settings.xml", crearía "data/config/".
                outputFile.getParentFile().mkdirs();

                // Devolvemos el objeto File que apunta a la ubicación fuera del JAR.
                return outputFile;

            } else {
                // Si resourceUrl no es null, el recurso se encontró directamente (típico en un IDE).
                // Convertimos la URL a un URI y luego a un objeto File.
                return new File(resourceUrl.toURI());
            }
        } catch (URISyntaxException | IOException e) {
            LOGGER.log(Level.SEVERE, "Error crítico al obtener la ruta del archivo: " + resourcePath, e);
            return null;
        }
    }


    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Metodo que maneja el minimizado de la pantalla
     */
    @FXML
    private void handleMinimize() {
        Stage stage = (Stage) topBar.getScene().getWindow();
        stage.setIconified(true);
    }

    private boolean isMaximized = false;

    /**
     * Metodo que maneja el maximizado de la pantalla
     */
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

    /**
     * Metodo que maneja el cierre de la pantalla
     */
    @FXML
    private void handleClose() {
        Platform.exit();
    }
}
