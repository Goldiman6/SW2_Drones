package com.drone.servicios;

import com.drone.dao.DroneDAO;
import com.drone.model.Vigilancia;
import java.util.HashMap;
import java.util.Map;

public class Prototype {
    private Map<String, Vigilancia> Cache = new HashMap<>();
    private DroneDAO dao = new DroneDAO();

    public void addPrototipo(String key, Vigilancia drone) {
        Cache.put(key, drone);
    }

    /**
     * Clona el dron solicitado, le asigna nuevos identificadores, y lo guarda en la BD.
     */
    public Vigilancia cloneAndSave(String key, String newId, String newSerial) throws Exception {
        Vigilancia drone = Cache.get(key);
        if (drone != null) {
            Vigilancia clon = (Vigilancia) drone.clonar();
            clon.setId(newId);
            clon.setSerial(newSerial);
            
            boolean saved = dao.guardarDrone(clon);
            if (!saved) {
                throw new Exception("Error al guardar clon (Prototype) en la BD.");
            }
            return clon;
        }
        return null;
    }
}
