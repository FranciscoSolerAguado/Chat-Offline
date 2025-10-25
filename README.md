# Chat-Offline

Chat-Offline es una aplicación de escritorio de chat punto a punto que no requiere conexión a Internet. Los usuarios pueden registrarse, iniciar sesión y chatear con otros usuarios en la misma red local.

## Características

*   **Inicio de sesión y registro de usuarios:** Los usuarios pueden crear una cuenta y iniciar sesión en la aplicación.
*   **Chat en tiempo real:** Los usuarios pueden enviar y recibir mensajes en tiempo real.
*   **Lista de usuarios:** Vea una lista de todos los usuarios registrados en la aplicación.
*   **Perfiles de usuario:** Vea el perfil de otros usuarios.
*   **No requiere conexión a Internet:** La aplicación funciona en una red local sin necesidad de una conexión a Internet.

## Cómo funciona

La aplicación está construida con JavaFX y utiliza un archivo XML (`usuarios.xml`) para almacenar los datos de los usuarios. Cuando un usuario inicia sesión, la aplicación carga la lista de otros usuarios y les permite iniciar una conversación.

El flujo de la aplicación es el siguiente:

1.  **Pantalla de carga:** Se muestra una pantalla de carga durante 3 segundos.
2.  **Inicio de sesión:** Se solicita al usuario que inicie sesión o se registre.
3.  **Pantalla principal:** Después de iniciar sesión, se muestra la pantalla principal con la lista de usuarios.
4.  **Chat:** Al hacer clic en un usuario, se abre una ventana de chat para conversar con ese usuario.

## Estructura del proyecto

El proyecto está organizado en los siguientes paquetes:

*   `controller`: Contiene los controladores de JavaFX para cada vista.
*   `dataAccess`: Contiene la lógica para leer y escribir en el archivo `usuarios.xml`.
*   `model`: Contiene las clases de modelo para `Usuario` y `GestorUsuarios`.
*   `utils`: Contiene clases de utilidad, como el registrador.

## Cómo ejecutar el proyecto

Para ejecutar el proyecto, necesita tener instalado Java y Maven. Luego, puede ejecutar el siguiente comando en la raíz del proyecto:

```bash
mvn clean javafx:run
```
