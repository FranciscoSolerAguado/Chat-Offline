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
    private String nombre;

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
    private String rutaFotoPerfil;


    public Usuario() {
    }

    public Usuario(String idUsuario, String nombre, String nombreUsuario, String email, String telefono, String password, LocalDateTime fechaRegistro, boolean activo, String rutaFotoPerfil) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.telefono = telefono;
        this.password = password;
        this.fechaRegistro = fechaRegistro;
        this.activo = activo;
        this.rutaFotoPerfil = rutaFotoPerfil;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public String getRutaFotoPerfil() {
        return rutaFotoPerfil;
    }

    public void setRutaFotoPerfil(String rutaFotoPerfil) {
        this.rutaFotoPerfil = rutaFotoPerfil;
    }

    public boolean validarPassword(String input) {
        return this.password != null && this.password.equals(input);
    }

    @Override
    public String toString() {
        return nombreUsuario + " (" + (activo ? "activo" : "inactivo") + ")";
    }
}