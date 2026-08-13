package com.drone.model;

import java.io.Serializable;

public class Sensor implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String tipo;
    private String fabricante;

    public Sensor() {}

    public Sensor(int id, String tipo, String fabricante) {
        this.id = id;
        this.tipo = tipo;
        this.fabricante = fabricante;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getFabricante() { return fabricante; }
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }

    @Override
    public String toString() {
        return tipo + " (" + fabricante + ")";
    }
}
