package org.fran.chatoffline.model;

import jakarta.xml.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@XmlRootElement(name = "conversacion")
@XmlAccessorType(XmlAccessType.FIELD)
public class Conversacion {

    /**
     * Lista de mensajes en la conversación
     * Utilizamos @XmlElementWrapper para envolver la lista en un elemento padre <mensajes>
     * y @XmlElement para definir el nombre de cada elemento individual <mensaje>
     */
    @XmlElementWrapper(name = "mensajes")
    @XmlElement(name = "mensaje")
    private List<Mensaje> mensajes = new ArrayList<>();

}
