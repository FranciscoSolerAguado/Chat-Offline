package org.fran.chatoffline.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.fran.chatoffline.dataAccess.XMLManager;
import org.fran.chatoffline.model.GestorUsuarios;
import org.fran.chatoffline.model.Usuario;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController {
    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());
    private static final String USUARIOS_XML_PATH = "src/main/resources/org/fran/chatoffline/usuarios.xml";

    @FXML
    private HBox topBar;
    @FXML
    private StackPane mainArea;
    @FXML
    private VBox chatListContainer;

    private Usuario usuarioActual; // Almacena el usuario que ha iniciado sesión

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private void initialize() {
        // Configurar el arrastre de la ventana
        topBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        topBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) topBar.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    /**
     * Este método es llamado por el InicioSesionController para pasar el usuario logueado.
     * @param usuario El usuario que ha iniciado sesión.
     */
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        LOGGER.info("Usuario actual establecido: " + usuario.getNombreUsuario());
        // Una vez que tenemos el usuario, cargamos la lista de chats filtrada.
        Platform.runLater(this::cargarListaUsuarios);
    }

    private void cargarListaUsuarios() {
        if (usuarioActual == null) {
            LOGGER.severe("No se puede cargar la lista de usuarios porque el usuario actual es nulo.");
            return;
        }

        chatListContainer.getChildren().clear();

        File usuariosFile = new File(USUARIOS_XML_PATH);
        if (!usuariosFile.exists()) {
            LOGGER.warning("El archivo de usuarios no existe. La lista de chats estará vacía.");
            return;
        }

        GestorUsuarios coleccionUsuarios = new GestorUsuarios();
        try {
            coleccionUsuarios = XMLManager.readXML(coleccionUsuarios, USUARIOS_XML_PATH);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al leer el archivo de usuarios.", e);
            return;
        }

        if (coleccionUsuarios.getUsuarios() != null) {
            for (Usuario usuario : coleccionUsuarios.getUsuarios()) {
                //Solo añade el chat si el ID no es el del usuario actual.
                if (!usuario.getIdUsuario().equals(usuarioActual.getIdUsuario())) {
                    HBox chatHBox = crearChatHBox(usuario);
                    chatListContainer.getChildren().add(chatHBox);
                }
            }
        }
    }

    private HBox crearChatHBox(Usuario usuario) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getStyleClass().add("chatHbox");

        Circle avatar = new Circle(15);
        avatar.getStyleClass().add("avatar");

        Label nameLabel = new Label(usuario.getNombreUsuario());
        nameLabel.getStyleClass().add("chat-name");

        Region region = new Region();
        HBox.setHgrow(region, javafx.scene.layout.Priority.ALWAYS);

        Button infoButton = new Button("+");
        infoButton.getStyleClass().add("masInformacionBoton");

        hbox.setOnMouseClicked(e -> abrirConversacion(usuario));
        infoButton.setOnAction(e -> abrirPerfilUsuario(usuario));

        hbox.getChildren().addAll(avatar, nameLabel, region, infoButton);
        return hbox;
    }

    private void abrirConversacion(Usuario usuario) {
        LOGGER.info("Abriendo conversación con: " + usuario.getNombreUsuario());
        mostrarEnMainArea("conversacion.fxml");
    }

    private void abrirPerfilUsuario(Usuario usuario) {
        LOGGER.info("Abriendo perfil de: " + usuario.getNombreUsuario());
        mostrarEnMainArea("perfilUsuario.fxml");
    }

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
            LOGGER.log(Level.SEVERE, "Error al cargar " + fxmlFileName, e);
        }
    }

    @FXML
    private void handleMinimize() {
        Stage stage = (Stage) topBar.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleToggleMaximize() {
        // ... (código de maximizar)
    }

    @FXML
    private void handleClose() {
        Platform.exit();
    }
}
