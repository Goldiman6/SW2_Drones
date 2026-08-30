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
 * Controlador de Drones (componente 'Controller' del patron MVC).
 *
 * Actua como intermediario entre la Vista (interfaz grafica) y la capa
 * de servicios / persistencia. Su responsabilidad es:
 *   1. Recibir los eventos del usuario enviados desde DroneView.
 *   2. Delegar la creacion de drones al patron correcto (Factory, Builder, Prototype).
 *   3. Delegar la persistencia al DroneDAO.
 *   4. Retornar los resultados a la Vista para que los muestre.
 *
 * Siguiendo el principio SOLID de 'abierto/cerrado', se puede agregar
 * un nuevo patron creacional anadiendo solo un metodo nuevo aqui y
 * una clase nueva en el paquete 'servicios', sin tocar ningun otro archivo.
 */
public class DroneController {

    /** Gestor del patron Prototype. Mantiene un cache de prototipos en memoria. */
    private Prototype prototypeManager = new Prototype();

    /** DAO para operaciones CRUD directas (listar, actualizar, eliminar). */
    private DroneDAO droneDAO = new DroneDAO();

    /**
     * Retorna la lista completa de drones almacenados en la base de datos.
     * Este metodo es llamado por la Vista para poblar la tabla principal.
     *
     * @return Lista de drones (puede ser vacia si no hay registros o hay error de conexion).
     */
    public List<Drone> getAllDrones() {
        try {
            return droneDAO.listarDrones();
        } catch (Exception e) {
            System.err.println("Error en el controlador al obtener drones: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    /**
     * Genera y retorna un texto demostrativo del patron Singleton.
     * Obtiene dos referencias a la instancia Singleton y compara sus hashCodes
     * y los hashCodes de sus conexiones para demostrar que son identicos.
     *
     * @return Cadena de texto con los resultados de la prueba para mostrar en un Alert.
     */
    public String testSingletonConnection() {
        try {
            Singleton s1 = Singleton.getInstance();
            Singleton s2 = Singleton.getInstance();

            int hashConn1 = s1.getConnection().hashCode();
            int hashConn2 = s2.getConnection().hashCode();
            boolean connected = s1.getConnection() != null;

            return "--- PRUEBA DEL PATRON SINGLETON ---\n\n" +
                   "Instancia 1 (Singleton) hashCode: " + s1.hashCode() + "\n" +
                   "Instancia 2 (Singleton) hashCode: " + s2.hashCode() + "\n" +
                   "Son la misma instancia? : " + (s1 == s2 ? "SI" : "NO") + "\n\n" +
                   "Conexion BD 1 hashCode: " + hashConn1 + "\n" +
                   "Conexion BD 2 hashCode: " + hashConn2 + "\n" +
                   "Comparten la misma conexion real? : " + (hashConn1 == hashConn2 ? "SI" : "NO") + "\n\n" +
                   "Estado de Conexion: " + (connected ? "ACTIVA" : "INACTIVA");
        } catch (Exception e) {
            return "Error al probar Singleton: " + e.getMessage();
        }
    }

    /**
     * Calcula el siguiente ID numerico consecutivo basandose en los drones existentes.
     * Recorre todos los drones, busca el ID numerico mas alto y le suma 1.
     * Los IDs no numericos (como clones con sufijo "_abc") son ignorados en el conteo.
     *
     * @return El siguiente ID como String (ej: "1", "2", "3"...).
     */
    private String generarSiguienteId() {
        try {
            List<Drone> drones = droneDAO.listarDrones();
            int maxId = 0;
            for (Drone d : drones) {
                try {
                    int currentId = Integer.parseInt(d.getId());
                    if (currentId > maxId) maxId = currentId;
                } catch (NumberFormatException e) {
                    // ID no numerico (ej. clon "3_ab2"): se ignora sin problema
                }
            }
            return String.valueOf(maxId + 1);
        } catch (Exception e) {
            return String.valueOf((int)(Math.random() * 10000));
        }
    }

    /**
     * Crea un nuevo Drone usando el patron Factory Method.
     * Segun el 'tipo' recibido, instancia la fabrica concreta correcta
     * (CrearAgricultura o CrearVigilancia) y le delega la creacion y guardado.
     *
     * @param tipo      "Agricultura" o "Vigilancia"
     * @param serial    Numero de serie del dron
     * @param modelo    Nombre del modelo
     * @param fabricante Empresa fabricante
     * @param peso      Peso en kilogramos
     * @param capacidad Capacidad del tanque (solo para Agricultura)
     * @param termica   Si tiene deteccion termica (solo para Vigilancia)
     * @return El objeto Drone creado y guardado en la BD.
     * @throws Exception Si ocurre un error al guardar en la BD.
     */
    public Drone addDroneFactory(String tipo, String serial, String modelo, String fabricante,
                                  double peso, double capacidad, boolean termica) throws Exception {
        String id = generarSiguienteId();
        FactoryCreator factory;
        if ("Agricultura".equalsIgnoreCase(tipo)) {
            factory = new CrearAgricultura();
        } else {
            factory = new CrearVigilancia();
        }
        return factory.crearYGuardar(id, serial, modelo, fabricante, peso, capacidad, termica);
    }

    /**
     * Crea un nuevo Drone de Agricultura usando el patron Builder.
     * El Builder configura el objeto paso a paso (SetId, SetModelo, etc.)
     * antes de llamar a buildAndSave() que lo guarda en la BD.
     * Esto demuestra que el Builder construye objetos complejos de forma controlada.
     *
     * @param serial    Numero de serie del dron
     * @param modelo    Nombre del modelo
     * @param fabricante Empresa fabricante
     * @param peso      Peso en kilogramos
     * @param capacidad Capacidad del tanque en litros
     * @return El objeto Agricultura ensamblado y guardado.
     * @throws Exception Si el Builder falla al guardar.
     */
    public Agricultura addDroneBuilder(String serial, String modelo, String fabricante,
                                        double peso, double capacidad) throws Exception {
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

    /**
     * Clona un Drone de Vigilancia existente usando el patron Prototype.
     * Agrega el original al cache, genera un sufijo aleatorio para evitar
     * colisiones de ID en la BD, y delega la clonacion al gestor Prototype.
     *
     * @param original El drone de Vigilancia a clonar.
     * @return La copia clonada con nuevo ID y serial, guardada en la BD.
     * @throws Exception Si el clon es null o hay error al guardar.
     */
    public Vigilancia cloneDronePrototype(Vigilancia original) throws Exception {
        String cacheKey = "VIG_" + original.getId();
        prototypeManager.addPrototipo(cacheKey, original);

        // Sufijo aleatorio corto para evitar colisiones en la BD al clonar varias veces
        String suffix = "_" + UUID.randomUUID().toString().substring(0, 3);
        String newId     = original.getId()     + suffix;
        String newSerial = original.getSerial() + suffix;

        // Truncar si excede el limite de caracteres de la columna en la BD
        if (newId.length()     > 15) newId     = newId.substring(0, 15);
        if (newSerial.length() > 20) newSerial = newSerial.substring(0, 20);

        Vigilancia clon = prototypeManager.cloneAndSave(cacheKey, newId, newSerial);
        if (clon == null) {
            throw new Exception("Error al clonar usando Prototype.");
        }
        return clon;
    }

    /**
     * Actualiza los datos de un dron existente en la BD.
     *
     * @param id        ID del dron a actualizar
     * @param tipo      "Agricultura" o "Vigilancia"
     * @param serial    Nuevo serial
     * @param modelo    Nuevo modelo
     * @param fabricante Nuevo fabricante
     * @param peso      Nuevo peso
     * @param capacidad Nueva capacidad del tanque (Agricultura)
     * @param termica   Nuevo valor de deteccion termica (Vigilancia)
     * @throws Exception Si el DAO no puede actualizar el registro.
     */
    public void updateDrone(String id, String tipo, String serial, String modelo,
                             String fabricante, double peso, double capacidad, boolean termica) throws Exception {
        Drone d;
        if ("Agricultura".equalsIgnoreCase(tipo)) {
            d = new Agricultura(id, serial, modelo, fabricante, peso, capacidad);
        } else {
            d = new Vigilancia(id, serial, modelo, fabricante, peso, termica);
        }
        boolean ok = droneDAO.actualizarDrone(d);
        if (!ok) throw new Exception("No se pudo actualizar el dron con ID: " + id);
    }

    /**
     * Elimina un dron de la base de datos por su ID.
     *
     * @param id ID del dron a eliminar.
     * @throws Exception Si el DAO no puede eliminar el registro.
     */
    public void deleteDrone(String id) throws Exception {
        boolean ok = droneDAO.eliminarDrone(id);
        if (!ok) throw new Exception("No se pudo eliminar el dron con ID: " + id);
    }
}
