package com.drone.servicios;

import com.drone.dao.DroneDAO;
import com.drone.model.Agricultura;

public class Builder {
    private Agricultura drone;
    private DroneDAO dao;

    public Builder() {
        this.dao = new DroneDAO();
        this.Reset();
    }

    public void Reset() {
        this.drone = new Agricultura();
    }

    public void SetId(String id) {
        this.drone.setId(id);
    }

    public void SetSerial(String serial) {
        this.drone.setSerial(serial);
    }

    public void SetModelo(String modelo) {
        this.drone.setModelo(modelo);
    }

    public void SetFabricante(String fabricante) {
        this.drone.setFabricante(fabricante);
    }

    public void SetPeso(double peso) {
        this.drone.setPeso(peso);
    }

    public void SetcapacidadTanque(double capacidadTanque) {
        this.drone.setCapacidadTanque(capacidadTanque);
    }

    /**
     * Construye el Dron y delega su guardado en la Base de Datos.
     */
    public Agricultura buildAndSave() throws Exception {
        boolean saved = dao.guardarDrone(this.drone);
        if (!saved) {
            throw new Exception("Error al guardar Dron mediante Builder en la BD.");
        }
        return this.drone;
    }
}
