package org.fran.chatoffline.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    private Usuario usuarioActual;

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
    }

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        LOGGER.info("Usuario actual establecido: " + usuario.getNombreUsuario());
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

    private void abrirConversacion(Usuario contacto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/fran/chatoffline/ui/conversacion.fxml"));
            Parent view = loader.load();

            ConversacionController controller = loader.getController();
            controller.setMainController(this);
            controller.setContacto(contacto);

            mainArea.getChildren().setAll(view);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar la vista de conversación.", e);
        }
    }

    public void abrirPerfilUsuario(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/fran/chatoffline/ui/perfilUsuario.fxml"));
            Parent view = loader.load();

            PerfilUsuarioController controller = loader.getController();
            controller.setMainController(this);
            controller.setUsuario(usuario);

            mainArea.getChildren().setAll(view);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar la vista de perfil de usuario.", e);
        }
    }

    /**
     * Recarga toda la vista principal (main.fxml).
     * Este método es llamado por los controladores secundarios para "volver" a la pantalla principal.
     */
    public void cerrarVistaSecundariaYRefrescar() {
        try {
            // Obtener la escena actual
            Scene scene = topBar.getScene();
            if (scene == null) {
                LOGGER.severe("No se puede obtener la escena para recargar la vista principal.");
                return;
            }

            // Cargar de nuevo el main.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/fran/chatoffline/ui/main.fxml"));
            Parent newRoot = loader.load();

            // Obtener el nuevo controlador y pasarle el usuario actual para mantener el estado
            MainController newMainController = loader.getController();
            newMainController.setUsuarioActual(this.usuarioActual);

            // Reemplazar la raíz de la escena con la nueva vista cargada
            scene.setRoot(newRoot);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al recargar la vista principal (main.fxml).", e);
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
