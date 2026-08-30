package com.drone.model;

public class Agricultura extends Drone {
    private double capacidadTanque;

    public Agricultura() {
        super();
    }

    public Agricultura(String id, String serial, String modelo, String fabricante, double peso, double capacidadTanque) {
        super(id, serial, modelo, fabricante, peso);
        this.capacidadTanque = capacidadTanque;
    }

    public double getCapacidadTanque() {
        return capacidadTanque;
    }

    public void setCapacidadTanque(double capacidadTanque) {
        this.capacidadTanque = capacidadTanque;
    }

    @Override
    public IPrototype clonar() {
        Agricultura clone = new Agricultura();
        clone.setId(this.getId());
        clone.setSerial(this.getSerial());
        clone.setModelo(this.getModelo());
        clone.setFabricante(this.getFabricante());
        clone.setPeso(this.getPeso());
        clone.setCapacidadTanque(this.getCapacidadTanque());
        return clone;
    }
}
