package com.drone.model;

import java.io.Serializable;

public abstract class Drone implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String serial;
    private String modelo;
    private String fabricante;
    private double peso;

    public Drone() {}

    public Drone(String id, String serial, String modelo, String fabricante, double peso) {
        this.id = id;
        this.serial = serial;
        this.modelo = modelo;
        this.fabricante = fabricante;
        this.peso = peso;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSerial() { return serial; }
    public void setSerial(String serial) { this.serial = serial; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getFabricante() { return fabricante; }
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }
}
