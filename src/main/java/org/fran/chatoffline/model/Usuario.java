package org.fran.chatoffline.model;

import java.time.LocalDateTime;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "usuario")
@XmlAccessorType(XmlAccessType.FIELD)
public class Usuario {

    @XmlAttribute
    private String idUsuario;

    @XmlElement
    private String nombreUsuario;

    @XmlElement
    private String email;

    @XmlElement
    private LocalDateTime fechaRegistro;

    @XmlElement
    private boolean activo;

    @XmlElement
    private String rutaAvatar;


    public Usuario() {
    }

    public Usuario(String idUsuario, String nombreUsuario, String email) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.fechaRegistro = LocalDateTime.now();
        this.activo = true;
    }


    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getRutaAvatar() {
        return rutaAvatar;
    }

    public void setRutaAvatar(String rutaAvatar) {
        this.rutaAvatar = rutaAvatar;
    }


    public void desactivar() {
        this.activo = false;
    }

    @Override
    public String toString() {
        return nombreUsuario + " (" + (activo ? "activo" : "inactivo") + ")";
    }
}
