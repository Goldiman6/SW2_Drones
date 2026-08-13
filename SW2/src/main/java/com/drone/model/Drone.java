package com.drone.model;

import java.io.Serializable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Contiene la informacion de drone (Modelo)
 */
public class Drone implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String serial;
    private String modelo;
    private String fabricante;
    private float peso;
    private String piloto;
    private List<Sensor> sensores;

    public Drone() {
        this.sensores = new ArrayList<>();
    }

    public Drone(int id, String serial, String modelo, String fabricante, float peso, String piloto, List<Sensor> sensores) {
        this.id = id;
        this.serial = serial;
        this.modelo = modelo;
        this.fabricante = fabricante;
        this.peso = peso;
        this.piloto = piloto;
        this.sensores = sensores != null ? sensores : new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSerial() { return serial; }
    public void setSerial(String serial) { this.serial = serial; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getFabricante() { return fabricante; }
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }

    public float getPeso() { return peso; }
    public void setPeso(float peso) { this.peso = peso; }

    public String getPiloto() { return piloto; }
    public void setPiloto(String piloto) { this.piloto = piloto; }

    public List<Sensor> getSensores() { return sensores; }
    public void setSensores(List<Sensor> sensores) { this.sensores = sensores; }
}
