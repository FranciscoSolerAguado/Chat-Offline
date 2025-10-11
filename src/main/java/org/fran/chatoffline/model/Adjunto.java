package org.fran.chatoffline.model;

import javax.xml.bind.annotation.*;

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

    public Adjunto() {}

    public Adjunto(String nombreArchivo, String rutaArchivo, long tamano, String tipoMime) {
        this.nombreArchivo = nombreArchivo;
        this.rutaArchivo = rutaArchivo;
        this.tamano = tamano;
        this.tipoMime = tipoMime;
    }


    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }

    public long getTamano() { return tamano; }
    public void setTamano(long tamano) { this.tamano = tamano; }

    public String getTipoMime() { return tipoMime; }
    public void setTipoMime(String tipoMime) { this.tipoMime = tipoMime; }

    @Override
    public String toString() {
        return nombreArchivo + " (" + tamano + " bytes)";
    }
}
