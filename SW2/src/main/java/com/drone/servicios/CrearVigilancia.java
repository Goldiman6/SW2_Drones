package com.drone.servicios;

import com.drone.dao.DroneDAO;
import com.drone.model.Drone;
import com.drone.model.Vigilancia;

public class CrearVigilancia implements FactoryCreator {
    private DroneDAO dao = new DroneDAO();

    @Override
    public Drone crearYGuardar(String id, String serial, String modelo, String fabricante, double peso, double capacidad, boolean termica) throws Exception {
        Vigilancia v = new Vigilancia();
        v.setId(id);
        v.setSerial(serial);
        v.setModelo(modelo);
        v.setFabricante(fabricante);
        v.setPeso(peso);
        v.setDeteccionTermica(termica);
        
        boolean saved = dao.guardarDrone(v);
        if (!saved) {
            throw new Exception("Error al guardar el dron de Vigilancia en la BD.");
        }
        return v;
    }
}
