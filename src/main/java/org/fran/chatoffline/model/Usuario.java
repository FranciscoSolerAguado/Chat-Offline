package org.fran.chatoffline.model;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.fran.chatoffline.dataAccess.LocalDateTimeAdapter;



import java.time.LocalDateTime;

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
    private String telefono; // Nuevo campo

    @XmlElement
    private String password;

    @XmlElement
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime fechaRegistro;

    @XmlElement
    private boolean activo;

    @XmlElement
    private String rutaAvatar;


    public Usuario() {
    }

    public Usuario(String idUsuario, String nombreUsuario, String email, String password, String telefono) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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


    public boolean validarPassword(String input) {
        return this.password != null && this.password.equals(input);
    }

    @Override
    public String toString() {
        return nombreUsuario + " (" + (activo ? "activo" : "inactivo") + ")";
    }
}