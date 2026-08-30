package com.drone.servicios;

import com.drone.dao.DroneDAO;
import com.drone.model.Agricultura;
import com.drone.model.Drone;

public class CrearAgricultura implements FactoryCreator {
    private DroneDAO dao = new DroneDAO();

    @Override
    public Drone crearYGuardar(String id, String serial, String modelo, String fabricante, double peso, double capacidad, boolean termica) throws Exception {
        Agricultura a = new Agricultura();
        a.setId(id);
        a.setSerial(serial);
        a.setModelo(modelo);
        a.setFabricante(fabricante);
        a.setPeso(peso);
        a.setCapacidadTanque(capacidad);
        
        boolean saved = dao.guardarDrone(a);
        if (!saved) {
            throw new Exception("Error al guardar el dron de Agricultura en la BD.");
        }
        return a;
    }
}
