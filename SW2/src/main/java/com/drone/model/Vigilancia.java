package com.drone.model;

public class Vigilancia extends Drone {
    private boolean deteccionTermica;

    public Vigilancia() {
        super();
    }

    public Vigilancia(String id, String serial, String modelo, String fabricante, double peso, boolean deteccionTermica) {
        super(id, serial, modelo, fabricante, peso);
        this.deteccionTermica = deteccionTermica;
    }

    public boolean isDeteccionTermica() {
        return deteccionTermica;
    }

    public void setDeteccionTermica(boolean deteccionTermica) {
        this.deteccionTermica = deteccionTermica;
    }
}
