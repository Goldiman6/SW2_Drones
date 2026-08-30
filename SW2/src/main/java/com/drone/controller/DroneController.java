package com.drone.controller;

import com.drone.dao.DroneDAO;
import com.drone.dao.Singleton;
import com.drone.model.Agricultura;
import com.drone.model.Drone;
import com.drone.model.Vigilancia;
import com.drone.servicios.Builder;
import com.drone.servicios.CrearAgricultura;
import com.drone.servicios.CrearVigilancia;
import com.drone.servicios.FactoryCreator;
import com.drone.servicios.Prototype;
import java.util.List;
import java.util.UUID;

/**
 * Controlador de Drones (Componente 'Controller' en MVC).
 * 
 * Actúa como intermediario entre la Vista (interfaz gráfica) y los Servicios (logica de negocio).
 * Se encarga de recibir los eventos del usuario, instanciar las clases correctas a través
 * de los patrones de diseño (Factory, Builder, Prototype) y gestionar las operaciones CRUD en la BD.
 */
public class DroneController {
    
    private Prototype prototypeManager = new Prototype();
    private DroneDAO droneDAO = new DroneDAO();

    public List<Drone> getAllDrones() {
        try {
            return droneDAO.listarDrones();
        } catch (Exception e) {
            System.err.println("Error en el controlador al obtener drones: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    public String testSingletonConnection() {
        try {
            Singleton s1 = Singleton.getInstance();
            Singleton s2 = Singleton.getInstance();
            
            // Tambin probamos el hashcode de la Conexin real de base de datos
            int hashConn1 = s1.getConnection().hashCode();
            int hashConn2 = s2.getConnection().hashCode();
            
            boolean connected = s1.getConnection() != null;
            
            return "--- PRUEBA DE PATRN SINGLETON ---\n\n" +
                   "Instancia 1 (Singleton) hashCode: " + s1.hashCode() + "\n" +
                   "Instancia 2 (Singleton) hashCode: " + s2.hashCode() + "\n" +
                   "Son la misma instancia Singleton? : " + (s1 == s2 ? "S" : "NO") + "\n\n" +
                   "Conexin BD 1 hashCode: " + hashConn1 + "\n" +
                   "Conexin BD 2 hashCode: " + hashConn2 + "\n" +
                   "Comparten la misma conexin real? : " + (hashConn1 == hashConn2 ? "S" : "NO") + "\n\n" +
                   "Estado de Conexin: " + (connected ? "ACTIVA" : "INACTIVA");
        } catch (Exception e) {
            return "Error al probar Singleton: " + e.getMessage();
        }
    }

    /**
     * Calcula el siguiente ID numrico consecutivo basndose en los drones existentes en la BD.
     */
    private String generarSiguienteId() {
        try {
            List<Drone> drones = droneDAO.listarDrones();
            int maxId = 0;
            for (Drone d : drones) {
                try {
                    // Intenta convertir el ID a nmero para saber cul es el ms alto
                    int currentId = Integer.parseInt(d.getId());
                    if (currentId > maxId) {
                        maxId = currentId;
                    }
                } catch (NumberFormatException e) {
                    // Si hay algn ID viejo tipo UUID o clon (ej. "3_c123"), lo ignora en el conteo
                }
            }
            return String.valueOf(maxId + 1);
        } catch (Exception e) {
            // Fallback en caso de error
            return String.valueOf((int)(Math.random() * 10000));
        }
    }

    public Drone addDroneFactory(String tipo, String serial, String modelo, String fabricante, double peso, double capacidad, boolean termica) throws Exception {
        String id = generarSiguienteId();
        FactoryCreator factory;
        if ("Agricultura".equalsIgnoreCase(tipo)) {
            factory = new CrearAgricultura();
        } else {
            factory = new CrearVigilancia();
        }
        return factory.crearYGuardar(id, serial, modelo, fabricante, peso, capacidad, termica);
    }

    public Agricultura addDroneBuilder(String serial, String modelo, String fabricante, double peso, double capacidad) throws Exception {
        String id = generarSiguienteId();
        Builder builder = new Builder();
        builder.SetId(id);
        builder.SetSerial(serial);
        builder.SetModelo(modelo);
        builder.SetFabricante(fabricante);
        builder.SetPeso(peso);
        builder.SetcapacidadTanque(capacidad);
        
        return builder.buildAndSave();
    }

    public Vigilancia cloneDronePrototype(Vigilancia original) throws Exception {
        String cacheKey = "VIG_" + original.getId();
        prototypeManager.addPrototipo(cacheKey, original);
        
        // Generamos un sufijo corto aleatorio para evitar colisiones en la Base de Datos
        // si se clona varias veces el mismo objeto original.
        String suffix = "_" + UUID.randomUUID().toString().substring(0, 3);
        String newId = original.getId() + suffix;
        String newSerial = original.getSerial() + suffix;
        
        // El ID no debe exceder el lmite de la BD, as que lo cortamos si es muy largo
        if (newId.length() > 15) newId = newId.substring(0, 15);
        if (newSerial.length() > 20) newSerial = newSerial.substring(0, 20);
        
        Vigilancia clon = prototypeManager.cloneAndSave(cacheKey, newId, newSerial);
        if (clon == null) {
            throw new Exception("Error al clonar usando Prototype.");
        }
        return clon;
    }

    /** CRUD: Actualizar Dron existente */
    public void updateDrone(String id, String tipo, String serial, String modelo, String fabricante, double peso, double capacidad, boolean termica) throws Exception {
        Drone d;
        if ("Agricultura".equalsIgnoreCase(tipo)) {
            Agricultura a = new Agricultura(id, serial, modelo, fabricante, peso, capacidad);
            d = a;
        } else {
            Vigilancia v = new Vigilancia(id, serial, modelo, fabricante, peso, termica);
            d = v;
        }
        boolean ok = droneDAO.actualizarDrone(d);
        if (!ok) {
            throw new Exception("No se pudo actualizar el dron.");
        }
    }

    /** CRUD: Eliminar Dron existente */
    public void deleteDrone(String id) throws Exception {
        boolean ok = droneDAO.eliminarDrone(id);
        if (!ok) {
            throw new Exception("No se pudo eliminar el dron.");
        }
    }
}
