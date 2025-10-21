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
import org.fran.chatoffline.utils.LoggerUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController {
    private static final Logger LOGGER = LoggerUtil.getLogger();

    @FXML
    private HBox topBar;
    @FXML
    private StackPane mainArea;
    @FXML
    private VBox chatListContainer;
    @FXML
    private Circle statusIndicator;
    @FXML
    private Label statusLabel;

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

    /**
     * Método que establece el usuario actual para la aplicación
     * @param usuario
     */
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        LOGGER.info("Usuario actual establecido: " + usuario.getNombre());
        if (statusLabel != null) {
            statusLabel.setText(usuario.getNombre() + " (Activo)");
        }
        if (statusIndicator != null) {
            statusIndicator.setVisible(true);
        }
        Platform.runLater(this::cargarListaUsuarios);
    }

    /**
     * Metodo que carga la lista de usuario, menos el usuario actual
     */
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
                //Carga todos menos el usuario actual
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
            controller.setUsuarioActual(this.usuarioActual);
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
            // Pasa tanto el usuario del perfil como el usuario logueado
            controller.setDatosPerfil(usuario, this.usuarioActual);


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
            LOGGER.log(Level.SEVERE, "Error al recargar la vista principal (main.fxml).", e);
        }
    }


    /**
     * Llama a getFileFromResource y devuelve la ruta convertida a archivo gracias a getFileFromResource
     * @return
     */
    private File getUsuariosFile() {
        return getFileFromResource("/org/fran/chatoffline/usuarios.xml");
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
