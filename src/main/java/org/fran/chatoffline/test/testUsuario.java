package org.fran.chatoffline.test;

import org.fran.chatoffline.DataAccess.XMLManager;
import org.fran.chatoffline.model.Usuario;

import java.io.File;

public class testUsuario {

    public static void main(String[] args) {
        // --- Test para escribir y leer un solo usuario ---
        System.out.println("--- Iniciando test de XMLManager con un solo Usuario ---");

        // 1. Crear un objeto Usuario de prueba
        Usuario usuarioOriginal = new Usuario("test01", "Test User", "test@example.com", "password123");
        String filename = "C:\\Users\\franc\\Desktop\\Chat-Offline\\src\\main\\resources\\usuarioTest.xml";

        // 2. Escribir el objeto en un archivo XML
        System.out.println("Escribiendo usuario en " + filename + "...");
        boolean escrituraExitosa = XMLManager.writeXML(usuarioOriginal, filename);

        if (escrituraExitosa) {
            System.out.println("Escritura exitosa.");
        } else {
            System.out.println("Fallo en la escritura.");
            return; // Salir si la escritura falla
        }

        // 3. Leer el objeto desde el archivo XML
        System.out.println("Leyendo usuario desde " + filename + "...");
        // Se necesita una instancia inicial para que JAXB sepa la clase a la que deserializar
        Usuario usuarioLeido = new Usuario();
        usuarioLeido = XMLManager.readXML(usuarioLeido, filename);

        // 4. Mostrar los resultados y verificar
        if (usuarioLeido != null && usuarioLeido.getIdUsuario() != null) {
            System.out.println("Lectura exitosa. Datos del usuario leído:");
            System.out.println("ID: " + usuarioLeido.getIdUsuario());
            System.out.println("Nombre: " + usuarioLeido.getNombreUsuario());
            System.out.println("Email: " + usuarioLeido.getEmail());

            // Comparación simple
            if (usuarioOriginal.getIdUsuario().equals(usuarioLeido.getIdUsuario())) {
                System.out.println("\nVERIFICACIÓN: El ID del usuario original y el leído coinciden.");
            } else {
                System.out.println("\nVERIFICACIÓN: ¡Error! Los datos no coinciden.");
            }

        } else {
            System.out.println("Fallo en la lectura. El objeto es nulo o está incompleto.");
        }

        // 5. Limpiar el archivo de prueba
        new File(filename).delete();
        System.out.println("\nArchivo de prueba eliminado. Test finalizado.");
    }
}
