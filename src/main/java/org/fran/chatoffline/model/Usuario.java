package org.fran.chatoffline.model;

public class Usuario {
    private String nombre;
    private String telefono;
    private String estado;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Usuario(String nombre, String telefono, String estado) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.estado = estado;
    }
}
