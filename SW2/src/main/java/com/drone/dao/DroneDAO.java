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
 * Clase encargada de gestionar el acceso a datos (Data Access Object - DAO).
 * 
 * Centraliza y abstrae todas las operaciones a la base de datos PostgreSQL (CRUD).
 * Utiliza el patrón Singleton (`Singleton.getInstance().getConnection()`) para obtener
 * y compartir una única conexión de red, previniendo saturación en el servidor de base de datos.
 * Maneja transacciones SQL (`conn.setAutoCommit(false)`) y la lógica de inserción
 * jerárquica para objetos con herencia (Agricultura y Vigilancia).
 */
public class DroneDAO {

    public boolean guardarDrone(Drone drone) {
        Connection conn = Singleton.getInstance().getConnection();
        if (conn == null) {
            System.err.println("No se pudo obtener conexin a la base de datos.");
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
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error al restaurar autoCommit: " + e.getMessage());
            }
        }
    }

    public List<Drone> listarDrones() {
        Connection conn = Singleton.getInstance().getConnection();
        List<Drone> lista = new ArrayList<>();
        if (conn == null) {
            System.err.println("No hay conexin, retornando lista vaca.");
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
                    Agricultura a = new Agricultura(id, serial, modelo, fabricante, peso, rs.getDouble("capacidad_tanque"));
                    lista.add(a);
                } else {
                    rs.getBoolean("deteccion_termica");
                    boolean isVig = !rs.wasNull();
                    if (isVig) {
                        Vigilancia v = new Vigilancia(id, serial, modelo, fabricante, peso, rs.getBoolean("deteccion_termica"));
                        lista.add(v);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar drones: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Actualiza los datos de un dron existente.
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
     * (Debido al ON DELETE CASCADE en la BD, se borran tambin los registros hijos)
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
