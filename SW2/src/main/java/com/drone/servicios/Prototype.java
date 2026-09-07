package com.drone.servicios;

import com.drone.dao.DroneDAO;
import com.drone.model.Vigilancia;
import java.util.HashMap;
import java.util.Map;

/**
 * Patron Prototype - Capa de Servicios.
 *
 * Almacena prototipos de drones de Vigilancia en una cache (Map).
 * La responsabilidad de clonar recae enteramente en esta capa de servicios,
 * manteniendo el Modelo como un POJO puro sin logica creacional.
 *
 * Exclusivo para drones de Vigilancia segun requerimiento del sistema.
 */
public class Prototype {

    /** Cache interna de prototipos. Clave: identificador del prototipo. */
    private Map<String, Vigilancia> Cache = new HashMap<>();

    /** DAO para persistir el clon resultante en la base de datos. */
    private DroneDAO dao = new DroneDAO();

    /**
     * Agrega un dron de Vigilancia al cache de prototipos.
     *
     * @param key   Identificador del prototipo en el cache.
     * @param drone Instancia de Vigilancia a registrar como prototipo.
     */
    public void addPrototipo(String key, Vigilancia drone) {
        Cache.put(key, drone);
    }

    /**
     * Clona un dron de Vigilancia existente en el cache, asignandole
     * nuevos identificadores, y luego lo persiste en la base de datos.
     *
     * La clonacion se realiza manualmente (campo por campo) dentro de esta
     * capa de servicios, sin delegar responsabilidad al Modelo.
     *
     * @param key       Clave del prototipo a clonar del cache.
     * @param newId     Nuevo ID para el clon (debe ser unico en la BD).
     * @param newSerial Nuevo numero de serie para el clon.
     * @return El objeto Vigilancia clonado y guardado en BD, o null si la clave no existe.
     * @throws Exception Si ocurre un error al persistir el clon en la base de datos.
     */
    public Vigilancia cloneAndSave(String key, String newId, String newSerial) throws Exception {
        Vigilancia droneOriginal = Cache.get(key);
        if (droneOriginal == null) {
            return null;
        }
        Vigilancia clon = clonar(droneOriginal, newId, newSerial);
        boolean saved = dao.guardarDrone(clon);
        if (!saved) {
            throw new Exception("Error al guardar clon (Prototype) en la BD.");
        }
        return clon;
    }

    /**
     * Realiza la clonacion pura de un dron de Vigilancia, SIN persistencia.
     * Metodo utilizable en tests unitarios sin necesidad de conexion a BD.
     *
     * @param original  El dron original a clonar.
     * @param newId     Nuevo ID para el clon.
     * @param newSerial Nuevo serial para el clon.
     * @return Una nueva instancia de Vigilancia con los datos del original y nuevos identificadores.
     */
    public Vigilancia clonar(Vigilancia original, String newId, String newSerial) {
        Vigilancia clon = new Vigilancia();
        clon.setId(newId);
        clon.setSerial(newSerial);
        clon.setModelo(original.getModelo());
        clon.setFabricante(original.getFabricante());
        clon.setPeso(original.getPeso());
        clon.setDeteccionTermica(original.isDeteccionTermica());
        return clon;
    }
}
