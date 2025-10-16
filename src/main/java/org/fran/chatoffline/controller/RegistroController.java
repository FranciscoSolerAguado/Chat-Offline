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
import org.fran.chatoffline.dataAccess.XMLManager;
import org.fran.chatoffline.model.GestorUsuarios;
import org.fran.chatoffline.model.Usuario;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class RegistroController {
    private static final Logger LOGGER = Logger.getLogger(RegistroController.class.getName());
    private static final String USUARIOS_XML_PATH = "src/main/resources/org/fran/chatoffline/usuarios.xml";


    private static final Pattern GMAIL_REGEX = Pattern.compile("^[a-zA-Z0-9._%+-]+@gmail\\.com$");
    private static final Pattern TELEFONO_REGEX = Pattern.compile("^[6789]\\d{8}$");

    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtTelefono;
    @FXML
    private Button btnEntrar;
    @FXML
    private Hyperlink linkRegistro;
    @FXML
    private HBox topBar;

    @FXML
    private void initialize() {
        btnEntrar.setOnAction(e -> registrarse());
        linkRegistro.setOnAction(e -> abrirInicioSesion());
    }

    private void registrarse() {

        String email = txtEmail.getText().trim();
        String contrasena = txtPassword.getText().trim();
        String telefono = txtTelefono.getText().trim();

        // Validaciones de formato
        if (email.isEmpty() || contrasena.isEmpty() || telefono.isEmpty()) {
            mostrarAlerta("Por favor, completa todos los campos.");
            return;
        }
        if (!GMAIL_REGEX.matcher(email).matches()) {
            LOGGER.warning("El formato del correo electrónico no es válido: " + email);
            mostrarAlerta("El formato del correo electrónico no es válido, tiene que acabar en @gmail.com");
            return;

        }
        if (!TELEFONO_REGEX.matcher(telefono).matches()) {
            LOGGER.warning("El formato del teléfono no es válido: " + telefono);
            mostrarAlerta("El formato del teléfono no es válido, tiene que empezar por 6, 7, 8 o 9 y tener 8 dígitos");
        }

        LOGGER.info("Intento de registro para el email: " + email);

        File usuariosFile = new File(USUARIOS_XML_PATH);
        GestorUsuarios coleccionUsuarios = new GestorUsuarios();

        if (usuariosFile.exists()) {
            try {
                coleccionUsuarios = XMLManager.readXML(coleccionUsuarios, USUARIOS_XML_PATH);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al leer el archivo de usuarios. Se creará uno nuevo.", e);
            }
        }

        final String finalEmail = email;
        boolean usuarioExiste = coleccionUsuarios.getUsuarios().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(finalEmail));

        if (usuarioExiste) {
            LOGGER.warning("Intento de registro para un email ya existente: " + email);
            mostrarAlerta("El correo electrónico ya está registrado. Por favor, inicia sesión.");
            return;
        }

        String nombreUsuario = email.split("@")[0];
        Usuario nuevoUsuario = new Usuario(UUID.randomUUID().toString(), nombreUsuario, email, contrasena, telefono);
        coleccionUsuarios.addUsuario(nuevoUsuario);

        boolean guardadoExitoso = XMLManager.writeXML(coleccionUsuarios, USUARIOS_XML_PATH);

        if (guardadoExitoso) {
            LOGGER.info("Usuario registrado con éxito: " + email);
            mostrarAlerta("¡Registro completado con éxito! Ahora puedes iniciar sesión.");
            abrirInicioSesion();
        } else {
            LOGGER.severe("Fallo al guardar el archivo XML para el nuevo usuario: " + email);
            mostrarAlerta("Error: No se pudo completar el registro. Inténtalo de nuevo.");
        }
    }

    private void abrirInicioSesion() {
        try {
            Parent inicioSesionContent = FXMLLoader.load(getClass().getResource("/org/fran/chatoffline/ui/inicioSesion.fxml"));
            Stage stage = (Stage) topBar.getScene().getWindow();
            stage.getScene().setRoot(inicioSesionContent);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar la ventana de inicio de sesión", e);
            mostrarAlerta("Error al cargar la ventana de inicio de sesión.");
        }
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
