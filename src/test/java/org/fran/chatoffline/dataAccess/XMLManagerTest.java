package org.fran.chatoffline.dataAccess;

import org.fran.chatoffline.model.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class XMLManagerTest {

    private static final String TEST_XML_FILENAME = "usuarios_test.xml";
    private XMLManagerCollection coleccionUsuarios;

    @BeforeEach
    void setUp() {
        // Prepara una colección limpia antes de cada test
        coleccionUsuarios = new XMLManagerCollection();
    }

    @AfterEach
    void tearDown() {
        // Limpia el archivo de prueba después de cada test para no dejar basura
        try {
            Files.deleteIfExists(Paths.get(TEST_XML_FILENAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testInsertarYLeerDosUsuarios() {
        // 1. Preparación: Crear dos usuarios de prueba
        Usuario usuario1 = new Usuario("id1", "Usuario Uno", "uno@test.com", "pass123");
        Usuario usuario2 = new Usuario("id2", "Usuario Dos", "dos@test.com", "pass456");

        // 2. Acción: Añadirlos a la colección y escribir el XML
        coleccionUsuarios.addUsuario(usuario1);
        coleccionUsuarios.addUsuario(usuario2);
        boolean escrituraExitosa = XMLManager.writeXML(coleccionUsuarios, TEST_XML_FILENAME);

        // 3. Verificación (Escritura)
        assertTrue(escrituraExitosa, "El método writeXML debería devolver true.");
        File file = new File(TEST_XML_FILENAME);
        assertTrue(file.exists(), "El archivo XML de prueba debería haber sido creado.");

        // 4. Acción: Leer los datos del XML
        XMLManagerCollection coleccionLeida = new XMLManagerCollection();
        coleccionLeida = XMLManager.readXML(coleccionLeida, TEST_XML_FILENAME);

        // 5. Verificación (Lectura y Contenido)
        assertNotNull(coleccionLeida, "La colección leída no debería ser nula.");
        assertNotNull(coleccionLeida.getUsuarios(), "La lista de usuarios leída no debería ser nula.");
        assertEquals(2, coleccionLeida.getUsuarios().size(), "Debería haber 2 usuarios en la colección leída.");

        // Verificar datos del primer usuario
        Usuario usuarioLeido1 = coleccionLeida.getUsuarios().get(0);
        assertEquals("id1", usuarioLeido1.getIdUsuario());
        assertEquals("Usuario Uno", usuarioLeido1.getNombreUsuario());

        // Verificar datos del segundo usuario
        Usuario usuarioLeido2 = coleccionLeida.getUsuarios().get(1);
        assertEquals("id2", usuarioLeido2.getIdUsuario());
        assertEquals("Usuario Dos", usuarioLeido2.getNombreUsuario());
    }
}
