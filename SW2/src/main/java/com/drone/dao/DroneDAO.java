package com.drone.dao;

import com.drone.model.Agricultura;
import com.drone.model.Drone;
import com.drone.model.Vigilancia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a datos (Data Access Object - DAO) para la entidad Drone.
 *
 * Centraliza y abstrae todas las operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * contra la base de datos PostgreSQL. Esta clase NO conoce la logica de negocio;
 * solo sabe como traducir objetos Java a sentencias SQL y viceversa.
 *
 * Usa el patron Singleton para obtener la conexion compartida:
 *   Singleton.getInstance().getConnection()
 *
 * Maneja herencia de modelos con tablas separadas:
 *   - Tabla 'drones'            : datos comunes a todos los drones
 *   - Tabla 'drones_agricultura': datos exclusivos de Agricultura
 *   - Tabla 'drones_vigilancia' : datos exclusivos de Vigilancia
 */
public class DroneDAO {

    /**
     * Inserta un dron nuevo en la base de datos.
     * Primero inserta los datos comunes en 'drones', luego los especificos
     * en la tabla hija correspondiente (Agricultura o Vigilancia).
     * Si alguna insercion falla, se hace rollback de toda la transaccion.
     *
     * @param drone El objeto Drone (o subclase) a guardar.
     * @return true si se guardo correctamente, false si hubo un error.
     */
    public boolean guardarDrone(Drone drone) {
        Connection conn = Singleton.getInstance().getConnection();
        if (conn == null) {
            System.err.println("No se pudo obtener conexion a la base de datos.");
            return false;
        }

        String insertDrone = "INSERT INTO drones (id, serial, modelo, fabricante, peso) VALUES (?, ?, ?, ?, ?)";
        try {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(insertDrone)) {
                ps.setString(1, drone.getId());
                ps.setString(2, drone.getSerial());
                ps.setString(3, drone.getModelo());
                ps.setString(4, drone.getFabricante());
                ps.setDouble(5, drone.getPeso());
                ps.executeUpdate();
            }

            if (drone instanceof Agricultura) {
                String insertAgr = "INSERT INTO drones_agricultura (id_drone, capacidad_tanque) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertAgr)) {
                    ps.setString(1, drone.getId());
                    ps.setDouble(2, ((Agricultura) drone).getCapacidadTanque());
                    ps.executeUpdate();
                }
            } else if (drone instanceof Vigilancia) {
                String insertVig = "INSERT INTO drones_vigilancia (id_drone, deteccion_termica) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertVig)) {
                    ps.setString(1, drone.getId());
                    ps.setBoolean(2, ((Vigilancia) drone).isDeteccionTermica());
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al guardar el dron: " + e.getMessage());
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * Retorna la lista completa de todos los drones almacenados en la BD.
     * Usa LEFT JOIN para unir los datos comunes con los especificos de cada subtipo,
     * y determina el tipo correcto (Agricultura o Vigilancia) segun que columna tenga valor.
     *
     * @return Lista de objetos Drone (pueden ser Agricultura o Vigilancia).
     */
    public List<Drone> listarDrones() {
        Connection conn = Singleton.getInstance().getConnection();
        List<Drone> lista = new ArrayList<>();
        if (conn == null) {
            System.err.println("No hay conexion, retornando lista vacia.");
            return lista;
        }

        String query = "SELECT d.id, d.serial, d.modelo, d.fabricante, d.peso, " +
                       "a.capacidad_tanque, v.deteccion_termica " +
                       "FROM drones d " +
                       "LEFT JOIN drones_agricultura a ON d.id = a.id_drone " +
                       "LEFT JOIN drones_vigilancia v ON d.id = v.id_drone";

        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                String serial = rs.getString("serial");
                String modelo = rs.getString("modelo");
                String fabricante = rs.getString("fabricante");
                double peso = rs.getDouble("peso");

                rs.getDouble("capacidad_tanque");
                boolean isAgr = !rs.wasNull();

                if (isAgr) {
                    lista.add(new Agricultura(id, serial, modelo, fabricante, peso, rs.getDouble("capacidad_tanque")));
                } else {
                    rs.getBoolean("deteccion_termica");
                    if (!rs.wasNull()) {
                        lista.add(new Vigilancia(id, serial, modelo, fabricante, peso, rs.getBoolean("deteccion_termica")));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar drones: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Actualiza los datos de un dron existente identificado por su ID.
     * Actualiza primero la tabla comun 'drones' y luego la tabla hija correspondiente.
     *
     * El dron con los datos nuevos. Su ID debe corresponder a un registro existente.
     * @return true si se actualizo correctamente, false si hubo un error.
     */
    public boolean actualizarDrone(Drone drone) {
        Connection conn = Singleton.getInstance().getConnection();
        if (conn == null) return false;

        String updateDrone = "UPDATE drones SET serial=?, modelo=?, fabricante=?, peso=? WHERE id=?";
        try {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(updateDrone)) {
                ps.setString(1, drone.getSerial());
                ps.setString(2, drone.getModelo());
                ps.setString(3, drone.getFabricante());
                ps.setDouble(4, drone.getPeso());
                ps.setString(5, drone.getId());
                ps.executeUpdate();
            }

            if (drone instanceof Agricultura) {
                String updateAgr = "UPDATE drones_agricultura SET capacidad_tanque=? WHERE id_drone=?";
                try (PreparedStatement ps = conn.prepareStatement(updateAgr)) {
                    ps.setDouble(1, ((Agricultura) drone).getCapacidadTanque());
                    ps.setString(2, drone.getId());
                    ps.executeUpdate();
                }
            } else if (drone instanceof Vigilancia) {
                String updateVig = "UPDATE drones_vigilancia SET deteccion_termica=? WHERE id_drone=?";
                try (PreparedStatement ps = conn.prepareStatement(updateVig)) {
                    ps.setBoolean(1, ((Vigilancia) drone).isDeteccionTermica());
                    ps.setString(2, drone.getId());
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar dron: " + e.getMessage());
            try { conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    /**
     * Elimina un dron de la base de datos por su ID.
     * Las tablas hijas (drones_agricultura, drones_vigilancia) se borran
     * automaticamente gracias a la restriccion ON DELETE CASCADE definida en la BD.
     *
     * @param id El ID del dron a eliminar.
     * @return true si se elimino correctamente, false si hubo un error.
     */
    public boolean eliminarDrone(String id) {
        Connection conn = Singleton.getInstance().getConnection();
        if (conn == null) return false;

        String deleteSQL = "DELETE FROM drones WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(deleteSQL)) {
            ps.setString(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar dron: " + e.getMessage());
            return false;
        }
    }
}
