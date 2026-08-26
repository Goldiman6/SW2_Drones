package com.drone.model;

import java.io.Serializable;

/**
 * Modelo .
 */
public class Drone implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String tipo; // "Agricultura" o "Vigilancia"
    private String serial;
    private String modelo;
    private String fabricante;
    private double peso;
    
    // Campos opcionales según el tipo
    private Double capacidadTanque; // Para Agricultura
    private Boolean deteccionTermica; // Para Vigilancia

    public Drone() {}

    public Drone(String id, String tipo, String serial, String modelo, String fabricante, double peso, 
                 Double capacidadTanque, Boolean deteccionTermica) {
        this.id = id;
        this.tipo = tipo;
        this.serial = serial;
        this.modelo = modelo;
        this.fabricante = fabricante;
        this.peso = peso;
        this.capacidadTanque = capacidadTanque;
        this.deteccionTermica = deteccionTermica;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getSerial() { return serial; }
    public void setSerial(String serial) { this.serial = serial; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getFabricante() { return fabricante; }
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public Double getCapacidadTanque() { return capacidadTanque; }
    public void setCapacidadTanque(Double capacidadTanque) { this.capacidadTanque = capacidadTanque; }

    public Boolean getDeteccionTermica() { return deteccionTermica; }
    public void setDeteccionTermica(Boolean deteccionTermica) { this.deteccionTermica = deteccionTermica; }
}
