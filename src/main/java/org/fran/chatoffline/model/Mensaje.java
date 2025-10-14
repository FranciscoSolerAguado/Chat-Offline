package org.fran.chatoffline.model;

import org.fran.chatoffline.DataAccess.LocalDateTimeAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;

@XmlRootElement(name = "mensaje")
@XmlAccessorType(XmlAccessType.FIELD)
public class Mensaje {

    @XmlAttribute
    private String idMensaje;

    @XmlElement
    private String remitente;

    @XmlElement
    private String destinatario;

    @XmlElement
    private String contenido;

    @XmlElement
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime fechaEnvio;

    @XmlElement
    private Adjunto adjunto;  // Puede ser null si no hay adjunto


    public Mensaje() {
    }

    public Mensaje(String remitente, String destinatario, String contenido) {
        this.idMensaje = java.util.UUID.randomUUID().toString();
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.contenido = contenido;
        this.fechaEnvio = LocalDateTime.now();
    }

    public Mensaje(String remitente, String destinatario, String contenido, Adjunto adjunto) {
        this(remitente, destinatario, contenido);
        this.adjunto = adjunto;
    }


    public String getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(String idMensaje) {
        this.idMensaje = idMensaje;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Adjunto getAdjunto() {
        return adjunto;
    }

    public void setAdjunto(Adjunto adjunto) {
        this.adjunto = adjunto;
    }


    public boolean tieneAdjunto() {
        return adjunto != null;
    }

    @Override
    public String toString() {
        return "[" + fechaEnvio + "] " + remitente + ": " + contenido +
                (tieneAdjunto() ? " (Adjunto: " + adjunto.getNombreArchivo() + ")" : "");
    }
}