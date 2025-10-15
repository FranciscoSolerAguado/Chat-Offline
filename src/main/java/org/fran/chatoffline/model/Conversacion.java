package org.fran.chatoffline.model;

import jakarta.xml.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@XmlRootElement(name = "conversacion")
@XmlAccessorType(XmlAccessType.FIELD)
public class Conversacion {

    @XmlAttribute
    private String idConversacion;

    @XmlElement
    private String usuario1;

    @XmlElement
    private String usuario2;

    /**
     * Lista de mensajes en la conversación
     * Utilizamos @XmlElementWrapper para envolver la lista en un elemento padre <mensajes>
     * y @XmlElement para definir el nombre de cada elemento individual <mensaje>
     */
    @XmlElementWrapper(name = "mensajes")
    @XmlElement(name = "mensaje")
    private List<Mensaje> mensajes = new ArrayList<>();

    @XmlElement
    private LocalDateTime fechaInicio;

    @XmlElement
    private LocalDateTime fechaUltimoMensaje;


    public Conversacion() { }

    public Conversacion(String idConversacion, String usuario1, String usuario2) {
        this.idConversacion = idConversacion;
        this.usuario1 = usuario1;
        this.usuario2 = usuario2;
        this.fechaInicio = LocalDateTime.now();
    }


    public String getIdConversacion() { return idConversacion; }
    public void setIdConversacion(String idConversacion) { this.idConversacion = idConversacion; }

    public String getUsuario1() { return usuario1; }
    public void setUsuario1(String usuario1) { this.usuario1 = usuario1; }

    public String getUsuario2() { return usuario2; }
    public void setUsuario2(String usuario2) { this.usuario2 = usuario2; }

    public List<Mensaje> getMensajes() { return mensajes; }
    public void setMensajes(List<Mensaje> mensajes) { this.mensajes = mensajes; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaUltimoMensaje() { return fechaUltimoMensaje; }
    public void setFechaUltimoMensaje(LocalDateTime fechaUltimoMensaje) { this.fechaUltimoMensaje = fechaUltimoMensaje; }


//    public void agregarMensaje(Mensaje mensaje) {
//        mensajes.add(mensaje);
//        this.fechaUltimoMensaje = mensaje.getFechaEnvio();
//    }
//
//    public long contarMensajesPorUsuario(String nombreUsuario) {
//        return mensajes.stream()
//                .filter(m -> m.getRemitente().equals(nombreUsuario))
//                .count();
//    }

    public int getNumeroTotalMensajes() {
        return mensajes.size();
    }

    @Override
    public String toString() {
        return "Conversación entre " + usuario1 + " y " + usuario2 +
                " (" + mensajes.size() + " mensajes)";
    }
}
