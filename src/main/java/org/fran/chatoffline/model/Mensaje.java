package org.fran.chatoffline.model;

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.fran.chatoffline.dataAccess.LocalDateTimeAdapter;

import jakarta.xml.bind.annotation.*;

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
    private Adjunto adjunto;


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

    public String getRemitente() {
        return remitente;
    }


    public String getDestinatario() {
        return destinatario;
    }


    public String getContenido() {
        return contenido;
    }


    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }


    public Adjunto getAdjunto() {
        return adjunto;
    }


    public boolean tieneAdjunto() {
        return adjunto != null;
    }
}