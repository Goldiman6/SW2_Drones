package com.drone.model;

public class Piloto {
    private int id;
    private String nombre;
    private String modelo;
    private int experiencia;
    private String telefono;

    public Piloto() {}

    public Piloto(int id, String nombre, String modelo, int experiencia, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.modelo = modelo;
        this.experiencia = experiencia;
        this.telefono = telefono;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getExperiencia() { return experiencia; }
    public void setExperiencia(int experiencia) { this.experiencia = experiencia; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
