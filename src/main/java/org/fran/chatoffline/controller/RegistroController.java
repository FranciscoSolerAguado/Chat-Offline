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
import org.fran.chatoffline.utils.ReggexUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class RegistroController {
    private static final Logger LOGGER = LoggerUtil.getLogger();

    private static final Pattern GMAIL_REGEX = ReggexUtil.GMAIL_REGEX;
    private static final Pattern TELEFONO_REGEX = ReggexUtil.TELEFONO_REGEX;


    @FXML
    private TextField txtNombre;
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

    /**
     * Método que maneja el registro de un usuario
     */
    private void registrarse() {
        LOGGER.info("Intentando registrarse...");
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        String contrasena = txtPassword.getText().trim();
        String telefono = txtTelefono.getText().trim();


        if (email.isEmpty() || contrasena.isEmpty() || telefono.isEmpty()) {
            mostrarAlerta("Por favor, completa todos los campos.");
            return;
        }
        if (!GMAIL_REGEX.matcher(email).matches()) {
            mostrarAlerta("El correo electrónico debe ser una dirección de @gmail.com válida.");
            return;
        }
        if (!TELEFONO_REGEX.matcher(telefono).matches()) {
            mostrarAlerta("El formato del teléfono no es válido. Debe tener 9 dígitos y empezar por 6, 7, 8 o 9.");
            return;
        }

        LOGGER.info("Intento de registro para el email: " + email);

        File usuariosFile = getUsuariosFile();
        if (usuariosFile == null) {
            mostrarAlerta("Error crítico: No se puede acceder a la ubicación de almacenamiento de usuarios.");
            return;
        }

        GestorUsuarios coleccionUsuarios = new GestorUsuarios();
        if (usuariosFile.exists() && usuariosFile.length() > 0) {
            try {
                coleccionUsuarios = XMLManager.readXML(new GestorUsuarios(), usuariosFile.getAbsolutePath());
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
        Usuario nuevoUsuario = new Usuario(UUID.randomUUID().toString(), nombre, email, telefono, contrasena, LocalDateTime.now(), true);
        coleccionUsuarios.addUsuario(nuevoUsuario);

        boolean guardadoExitoso = XMLManager.writeXML(coleccionUsuarios, usuariosFile.getAbsolutePath());

        if (guardadoExitoso) {
            LOGGER.info("Usuario registrado con éxito: " + email);
            mostrarAlerta("¡Registro completado con éxito! Ahora puedes iniciar sesión.");
            abrirInicioSesion();
        } else {
            LOGGER.severe("Fallo al guardar el archivo XML para el nuevo usuario: " + email);
            mostrarAlerta("Error: No se pudo completar el registro. Inténtalo de nuevo.");
        }
        LOGGER.info("Registro finalizado.");
    }

    /**
     * Llama a getFileFromResource y devuelve la ruta convertida a archivo gracias a getFileFromResource
     * @return
     */
    private File getUsuariosFile() {
        LOGGER.info("Obteniendo ruta del archivo de usuarios...");
        return getFileFromResource("/org/fran/chatoffline/usuarios.xml");
    }

    /**
     * Metodo que busca el archivo de usuarios.xml
     * @param resourcePath
     * @return
     */
    private File getFileFromResource(String resourcePath) {
        LOGGER.info("Buscando archivo de usuarios...");
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

    /**
     * Metodo que abre la ventana de inicio de sesion
     * Si hacemos clic en "¿Ya tienes una cuenta?"
     */
    private void abrirInicioSesion() {
        LOGGER.info("Abriendo ventana de inicio de sesión...");
        try {
            Parent inicioSesionContent = FXMLLoader.load(getClass().getResource("/org/fran/chatoffline/ui/inicioSesion.fxml"));
            Stage stage = (Stage) topBar.getScene().getWindow();
            stage.getScene().setRoot(inicioSesionContent);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar la ventana de inicio de sesión", e);
            mostrarAlerta("Error al cargar la ventana de inicio de sesión.");
        }
        LOGGER.info("Ventana de inicio de sesión abierta exitosamente.");
    }

    /**
     * Metodo que crea una alerta para mostrarla
     * @param msg
     */
    private void mostrarAlerta(String msg) {
        LOGGER.info("Mostrando alerta: " + msg);
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
        LOGGER.info("Minimizando la pantalla...");
        Stage stage = (Stage) topBar.getScene().getWindow();
        stage.setIconified(true);
    }

    private boolean isMaximized = false;

    /**
     * Metodo que maneja el maximizado de la pantalla
     */
    @FXML
    private void handleToggleMaximize() {
        LOGGER.info("Maximización de la pantalla...");
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
        LOGGER.info("Maximización de la pantalla finalizada.");
    }

    /**
     * Metodo que maneja el cierre de la pantalla
     */
    @FXML
    private void handleClose() {
        LOGGER.info("Cerrando la pantalla...");
        Platform.exit();
    }
}
