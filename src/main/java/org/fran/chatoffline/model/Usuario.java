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
    private String email;

    @XmlElement
    private String telefono;

    @XmlElement
    private String password;

    @XmlElement
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime fechaRegistro;

    @XmlElement
    private boolean activo;


    public Usuario() {
    }

    public Usuario(String idUsuario, String nombre, String email, String telefono, String password, LocalDateTime fechaRegistro, boolean activo) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.password = password;
        this.fechaRegistro = fechaRegistro;
        this.activo = activo;
    }

    public String getIdUsuario() {
        return idUsuario;
    }


    public String getNombre() {
        return nombre;
    }


    public String getEmail() {
        return email;
    }


    public String getTelefono() {
        return telefono;
    }


    public boolean validarPassword(String input) {
        return this.password != null && this.password.equals(input);
    }

    @Override
    public String toString() {
        return nombre + " (" + (activo ? "activo" : "inactivo") + ")";
    }
}