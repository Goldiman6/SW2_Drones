package com.drone.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Patrn Singleton aplicado a la conexin de Base de Datos.
 * Garantiza que a lo largo de toda la ejecucin del programa exista una NICA conexin (Connection) compartida,
 * optimizando recursos de memoria y puerto local.
 */
public class Singleton {
    private static Singleton instancia;
    private Connection conexion;
    
    // Credenciales y URL
    private String URL = "jdbc:postgresql://localhost:5432/drones";
    private String usuario = "postgres";
    private String clave = "Yoyito";

    /**
     * Constructor privado. Evita que la clase sea instanciada mediante 'new'.
     */
    private Singleton() {
        try {
            conexion = DriverManager.getConnection(URL, usuario, clave);
            System.out.println("Conexin a base de datos (PostgreSQL) establecida exitosamente.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al conectar a la base de datos.");
        }
    }

    /**
     * Mtodo de acceso global para obtener la instancia Singleton.
     * @return La nica instancia de la clase.
     */
    public static Singleton getInstance() {
        if (instancia == null) {
            instancia = new Singleton();
        }
        return instancia;
    }

    /**
     * Obtiene el objeto de conexin JDBC.
     * @return Connection
     */
    public Connection getConnection() {
        return conexion;
    }
}
