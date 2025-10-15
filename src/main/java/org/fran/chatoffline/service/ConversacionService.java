package org.fran.chatoffline.service;

import jakarta.xml.bind.annotation.*;
import org.fran.chatoffline.model.Mensaje;


import java.util.List;

@XmlRootElement(name = "conversacion")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConversacionService {

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
