package org.fran.chatoffline.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.fran.chatoffline.dataAccess.XMLManager;
import org.fran.chatoffline.model.GestorUsuarios;
import org.fran.chatoffline.model.Usuario;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController {
    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

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
        LOGGER.info("Usuario actual establecido: " + usuario.getNombre());
        Platform.runLater(this::cargarListaUsuarios);
    }

    private void cargarListaUsuarios() {
        if (usuarioActual == null) {
            LOGGER.severe("No se puede cargar la lista de usuarios porque el usuario actual es nulo.");
            return;
        }

        chatListContainer.getChildren().clear();

        File usuariosFile = getUsuariosFile();
        if (usuariosFile == null || !usuariosFile.exists() || usuariosFile.length() == 0) {
            LOGGER.warning("El archivo de usuarios no existe o está vacío. La lista de chats estará vacía.");
            return;
        }

        GestorUsuarios coleccionUsuarios = new GestorUsuarios();
        try {
            coleccionUsuarios = XMLManager.readXML(new GestorUsuarios(), usuariosFile.getAbsolutePath());
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

    /**
     * Crea el Hbox donde va cada usuario
     * @param usuario el usuario que se va a mostrar en el Hbox
     *                Si hacemos clic en "+" se muestra el perfil de ese usuario
     *                Y si clicamos directamente sobre el Hbox se abre la conversacion
     * @return
     */
    private HBox crearChatHBox(Usuario usuario) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getStyleClass().add("chatHbox");

        Circle avatar = new Circle(15);
        avatar.getStyleClass().add("avatar");

        Label nameLabel = new Label(usuario.getNombre());
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

    /**
     * Metodo que carga una conversacion cuando se hace clic en un chat en el programa
     * @param contacto
     */
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

    /**
     * Método que abre un perfil de un usuario
     * @param usuario el usuario del que se quiere mostrar su perfil
     */
    public void abrirPerfilUsuario(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/fran/chatoffline/ui/perfilUsuario.fxml"));
            Parent view = loader.load();

            PerfilUsuarioController controller = loader.getController();
            controller.setMainController(this);
            controller.setContacto(usuario);

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


    private File getUsuariosFile() {
        final String resourcePath = "/org/fran/chatoffline/usuarios.xml";
        return getFileFromResource(resourcePath);
    }

    private File getFileFromResource(String resourcePath) {
        try {
            URL resourceUrl = getClass().getResource(resourcePath);
            if (resourceUrl == null) {
                URL dirUrl = getClass().getResource("/");
                if (dirUrl == null) throw new IOException("No se puede encontrar la raíz del classpath.");
                File rootDir = new File(dirUrl.toURI());
                File outputFile = new File(rootDir, resourcePath.substring(1));
                outputFile.getParentFile().mkdirs();
                return outputFile;
            }
            return new File(resourceUrl.toURI());
        } catch (URISyntaxException | IOException e) {
            LOGGER.log(Level.SEVERE, "Error crítico al obtener la ruta del archivo: " + resourcePath, e);
            return null;
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
