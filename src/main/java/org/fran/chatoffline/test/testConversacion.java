package org.fran.chatoffline.test;

import org.fran.chatoffline.dataAccess.XMLManager;
import org.fran.chatoffline.model.Mensaje;
import org.fran.chatoffline.service.ConversacionService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class testConversacion {

    public static void main(String[] args) {
        System.out.println("--- Iniciando test simple de Conversacion ---");

        // 1. Crear una conversación de prueba con dos mensajes
        ConversacionService conversacionOriginal = new ConversacionService();
        List<Mensaje> mensajes = new ArrayList<>();
        mensajes.add(new Mensaje("Usuario1", "Usuario2", "Hola mundo!"));
        mensajes.add(new Mensaje("Usuario2", "Usuario1", "Hola! ¿Qué tal?"));
        conversacionOriginal.setMensajes(mensajes);

        String filename = "C:\\Users\\franc\\Desktop\\Chat-Offline\\src\\main\\resources\\conversacionTest.xml";

        // 2. Escribir la conversación en un archivo XML
        System.out.println("Escribiendo conversación en " + filename + "...");
        boolean escrituraExitosa = XMLManager.writeXML(conversacionOriginal, filename);

        if (escrituraExitosa) {
            System.out.println("Escritura exitosa.");
        } else {
            System.out.println("Fallo en la escritura.");
            return; // Salir si la escritura falla
        }

        // 3. Leer la conversación desde el archivo XML
        System.out.println("Leyendo conversación desde " + filename + "...");
        ConversacionService conversacionLeida = new ConversacionService();
        conversacionLeida = XMLManager.readXML(conversacionLeida, filename);

        // 4. Mostrar los resultados y verificar
        if (conversacionLeida != null && conversacionLeida.getMensajes() != null) {
            System.out.println("Lectura exitosa. Se leyeron " + conversacionLeida.getMensajes().size() + " mensajes:");
            for (Mensaje msg : conversacionLeida.getMensajes()) {
                System.out.println(" -> " + msg);
            }

            // Comparación simple
            if (conversacionOriginal.getMensajes().size() == conversacionLeida.getMensajes().size()) {
                System.out.println("\nVERIFICACIÓN: El número de mensajes coincide.");
            } else {
                System.out.println("\nVERIFICACIÓN: ¡Error! El número de mensajes no coincide.");
            }

        } else {
            System.out.println("Fallo en la lectura. El objeto es nulo o está incompleto.");
        }

        // 5. Limpiar el archivo de prueba
        new File(filename).delete();
        System.out.println("\nArchivo de prueba eliminado. Test finalizado.");
    }
}
