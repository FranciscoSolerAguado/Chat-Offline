package org.fran.chatoffline.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "adjunto")
@XmlAccessorType(XmlAccessType.FIELD)
public class Adjunto {

    @XmlElement
    private String nombreArchivo;

    @XmlElement
    private String rutaArchivo;

    @XmlElement
    private long tamano;

    @XmlElement
    private String tipoMime;

    public Adjunto() {
    }

    public Adjunto(String nombreArchivo, String rutaArchivo, long tamano, String tipoMime) {
        this.nombreArchivo = nombreArchivo;
        this.rutaArchivo = rutaArchivo;
        this.tamano = tamano;
        this.tipoMime = tipoMime;
    }


    public String getNombreArchivo() {
        return nombreArchivo;
    }


    public String getRutaArchivo() {
        return rutaArchivo;
    }


    public String getTipoMime() {
        return tipoMime;
    }


    @Override
    public String toString() {
        return nombreArchivo + " (" + tamano + " bytes)";
    }
}
