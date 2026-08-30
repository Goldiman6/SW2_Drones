package com.drone.test;

import com.drone.dao.Singleton;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba del Patron Singleton aplicado a la conexion de Base de Datos.
 * Verifica que siempre se retorne la misma instancia y la misma conexion.
 */
@DisplayName("Pruebas - Patron Singleton (Conexion BD)")
public class SingletonTest {

    @Test
    @DisplayName("La instancia Singleton siempre debe ser la misma")
    public void testInstanciaUnica() {
        Singleton s1 = Singleton.getInstance();
        Singleton s2 = Singleton.getInstance();
        // Comprueba que los dos son exactamente el mismo objeto en memoria
        assertSame(s1, s2, "ERROR: getInstance() devolvio dos instancias diferentes.");
    }

    @Test
    @DisplayName("El hashCode del Singleton debe ser identico en ambas referencias")
    public void testHashCodeIgual() {
        Singleton s1 = Singleton.getInstance();
        Singleton s2 = Singleton.getInstance();
        assertEquals(s1.hashCode(), s2.hashCode(),
            "ERROR: Los hashCode son diferentes, no es un Singleton real.");
    }

    @Test
    @DisplayName("La conexion a la BD no debe ser null (PostgreSQL activo)")
    public void testConexionActiva() {
        Singleton s = Singleton.getInstance();
        assertNotNull(s.getConnection(),
            "ERROR: La conexion a la BD es null. Verifica que PostgreSQL este encendido.");
    }

    @Test
    @DisplayName("La conexion compartida entre dos referencias es identica")
    public void testMismaConexion() {
        Singleton s1 = Singleton.getInstance();
        Singleton s2 = Singleton.getInstance();
        assertSame(s1.getConnection(), s2.getConnection(),
            "ERROR: Las dos referencias tienen conexiones diferentes.");
    }
}
