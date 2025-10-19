package org.fran.chatoffline.model;

import jakarta.xml.bind.annotation.*;


import java.util.List;

@XmlRootElement(name = "conversaciones")
@XmlAccessorType(XmlAccessType.FIELD)
public class GestorConversacion {

    @XmlElementWrapper(name = "mensajes")
    @XmlElement(name = "mensaje")
    private List<Mensaje> mensajes;

    public List<Mensaje> getMensajes() {
        return mensajes;
    }

    public void setMensajes(List<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }
}
