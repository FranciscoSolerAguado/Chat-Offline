package org.fran.chatoffline.dataAccess;

import org.fran.chatoffline.model.Usuario;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "usuarios")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLManagerCollection {

    @XmlElement(name = "usuario")
    private List<Usuario> usuarios;

    public XMLManagerCollection() {
        this.usuarios = new ArrayList<>();
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public void addUsuario(Usuario usuario) {
        if (this.usuarios == null) {
            this.usuarios = new ArrayList<>();
        }
        this.usuarios.add(usuario);
    }

    // --- Métodos genéricos añadidos ---

    public static <T> boolean writeXML(T c, String filename) {
        boolean result = false;
        try {
            JAXBContext context = JAXBContext.newInstance(c.getClass());
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            m.marshal(c, new File(filename));
            result = true;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T> T readXML(T c, String filename) {
        T result = c;
        try {
            JAXBContext context = JAXBContext.newInstance(c.getClass());
            Unmarshaller um = context.createUnmarshaller();
            result = (T) um.unmarshal(new File(filename));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return result;
    }
}
