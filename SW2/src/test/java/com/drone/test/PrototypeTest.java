package com.drone.test;

import com.drone.dao.DroneDAO;
import com.drone.dao.Singleton;
import com.drone.model.Drone;
import com.drone.model.Vigilancia;
import com.drone.servicios.Prototype;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de Integracion del Patron Prototype.
 *
 * Verifica que la clase {@link Prototype} (capa de Servicios) funciona correctamente
 * de extremo a extremo: logica de clonacion + persistencia real en PostgreSQL.
 *
 * Arquitectura verificada:
 *   DroneView -> DroneController -> [Prototype] -> DroneDAO -> Singleton -> PostgreSQL
 *
 * NOTA: Estos tests requieren que la base de datos PostgreSQL este activa
 *       con las credenciales configuradas en {@link Singleton}.
 */
@DisplayName("Pruebas - Patron Prototype ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PrototypeTest {

    private static Prototype manager;
    private static DroneDAO dao;
    private static Vigilancia original;

    // IDs generados dinamicamente para no colisionar entre ejecuciones del test
    private static String idOriginal;
    private static String idClon1;
    private static String idClon2;

    /**
     * Se ejecuta una sola vez antes de todos los tests.
     * 1. Verifica que la conexion Singleton este activa.
     * 2. Persiste el dron original en la BD (necesario para que el Prototype tenga que clonar).
     */
    @BeforeAll
    static void setUpAll() throws Exception {
        manager = new Prototype();
        dao = new DroneDAO();

        // Generar IDs unicos para este ciclo de tests
        long ts = System.currentTimeMillis();
        idOriginal = "PT-OR-" + ts;
        idClon1    = "PT-C1-" + ts;
        idClon2    = "PT-C2-" + ts;

        // --- Verificar conexion Singleton ANTES de todos los tests ---
        Singleton s = Singleton.getInstance();
        Connection conn = s.getConnection();
        assertNotNull(conn,
            "PRECONDICION FALLIDA: La conexion Singleton a PostgreSQL es null. " +
            "Verifica que la BD este activa y las credenciales en Singleton.java sean correctas.");
        assertFalse(conn.isClosed(),
            "PRECONDICION FALLIDA: La conexion Singleton esta cerrada.");

        // Guardar el dron original en la BD para que exista antes de clonar
        original = new Vigilancia(idOriginal, "SER-ORIG-PT", "EagleEye Pro", "SkyCorp", 7.0, true);
        boolean guardado = dao.guardarDrone(original);
        assertTrue(guardado,
            "PRECONDICION FALLIDA: No se pudo guardar el dron original en la BD antes de clonar.");

        // Registrarlo en el cache del Prototype
        manager.addPrototipo("EAGLE_TEST", original);
    }

    /**
     * Al terminar todos los tests, limpiamos los registros creados durante los tests
     * para no contaminar la BD con datos de prueba.
     */
    @AfterAll
    static void tearDownAll() {
        dao.eliminarDrone(idOriginal);
        dao.eliminarDrone(idClon1);
        dao.eliminarDrone(idClon2);
        System.out.println("[PrototypeTest] Limpieza completada: registros de prueba eliminados.");
    }

    // ================================================================
    // BLOQUE 1: Verificacion del Singleton (conexion a la BD)
    // ================================================================

    @Test
    @Order(1)
    @DisplayName("1. [Singleton] Siempre debe retornar la MISMA instancia (un solo objeto)")
    void testSingletonMismaInstancia() {
        Singleton s1 = Singleton.getInstance();
        Singleton s2 = Singleton.getInstance();

        assertSame(s1, s2,
            "Singleton esta roto: getInstance() retorno dos instancias distintas.");
    }

    @Test
    @Order(2)
    @DisplayName("2. [Singleton] La conexion debe apuntar al mismo objeto Connection siempre")
    void testSingletonMismaConexion() throws Exception {
        Connection conn1 = Singleton.getInstance().getConnection();
        Connection conn2 = Singleton.getInstance().getConnection();

        assertSame(conn1, conn2,
            "Singleton esta roto: getConnection() retorno dos conexiones distintas.");
        assertFalse(conn1.isClosed(),
            "La conexion de la BD esta cerrada. Revisa el estado de PostgreSQL.");
    }

    // ================================================================
    // BLOQUE 2: Logica pura de clonacion (sin BD)
    // ================================================================

    @Test
    @Order(3)
    @DisplayName("3. [Prototype-Logica] El clon debe ser un objeto DIFERENTE en memoria al original")
    void testClonEsObjetoDistinto() {
        Vigilancia clon = manager.clonar(original, "TEMP-01", "SER-TEMP-01");

        assertNotNull(clon, "El clon no debe ser null.");
        assertNotSame(original, clon,
            "El clon debe ser una instancia diferente en memoria, no la misma referencia.");
    }

    @Test
    @Order(4)
    @DisplayName("4. [Prototype-Logica] El clon debe conservar los datos de negocio del original")
    void testClonConservaDatos() {
        Vigilancia clon = manager.clonar(original, "TEMP-02", "SER-TEMP-02");

        assertEquals(original.getModelo(),            clon.getModelo(),
            "El modelo del clon debe ser identico al original.");
        assertEquals(original.getFabricante(),        clon.getFabricante(),
            "El fabricante del clon debe ser identico al original.");
        assertEquals(original.getPeso(),              clon.getPeso(), 0.001,
            "El peso del clon debe ser identico al original.");
        assertEquals(original.isDeteccionTermica(),   clon.isDeteccionTermica(),
            "La deteccion termica del clon debe ser identica al original.");
    }

    @Test
    @Order(5)
    @DisplayName("5. [Prototype-Logica] El clon debe tener nuevos identificadores (ID y Serial propios)")
    void testClonTieneIdNuevo() {
        Vigilancia clon = manager.clonar(original, "TEMP-03", "SER-TEMP-03");

        assertNotEquals(original.getId(),    clon.getId(),
            "El ID del clon no puede ser el mismo que el del original.");
        assertNotEquals(original.getSerial(), clon.getSerial(),
            "El Serial del clon no puede ser el mismo que el del original.");
        assertEquals("TEMP-03",     clon.getId(),
            "El ID del clon debe ser el asignado explicitamente.");
        assertEquals("SER-TEMP-03", clon.getSerial(),
            "El Serial del clon debe ser el asignado explicitamente.");
    }

    @Test
    @Order(6)
    @DisplayName("6. [Prototype-Logica] Modificar el clon no debe afectar al objeto original")
    void testModificacionClonNoAfectaOriginal() {
        Vigilancia clon = manager.clonar(original, "TEMP-04", "SER-TEMP-04");
        clon.setModelo("ModeloModificado");
        clon.setPeso(999.0);
        clon.setDeteccionTermica(false);

        assertEquals("EagleEye Pro", original.getModelo(),
            "El modelo del original no debe cambiar al modificar el clon.");
        assertEquals(7.0, original.getPeso(), 0.001,
            "El peso del original no debe cambiar al modificar el clon.");
        assertTrue(original.isDeteccionTermica(),
            "La deteccion termica del original no debe cambiar al modificar el clon.");
    }

    // ================================================================
    // BLOQUE 3: Integracion con la Base de Datos (cloneAndSave)
    // ================================================================

    @Test
    @Order(7)
    @DisplayName("7. [BD] cloneAndSave() debe guardar el clon en PostgreSQL correctamente")
    void testClonGuardadoEnBD() throws Exception {
        Vigilancia clon = manager.cloneAndSave("EAGLE_TEST", idClon1, "SER-" + idClon1);

        assertNotNull(clon,
            "cloneAndSave() no debe retornar null si el prototipo existe.");

        // Verificar que realmente existe en la BD
        List<Drone> todos = dao.listarDrones();
        boolean encontrado = todos.stream().anyMatch(d -> d.getId().equals(idClon1));
        assertTrue(encontrado,
            "El clon con ID [" + idClon1 + "] no se encontro en la BD tras guardar.");
    }

    @Test
    @Order(8)
    @DisplayName("8. [BD] El clon guardado en BD debe tener los mismos datos de negocio que el original")
    void testClonEnBDTieneDatosCorrectos() throws Exception {
        // Recuperar el clon que guardamos en el test anterior
        List<Drone> todos = dao.listarDrones();
        Vigilancia clonRecuperado = (Vigilancia) todos.stream()
                .filter(d -> d.getId().equals(idClon1))
                .findFirst()
                .orElse(null);

        assertNotNull(clonRecuperado,
            "No se encontro el clon en la BD. El test 7 puede haber fallado.");
        assertEquals(original.getModelo(),          clonRecuperado.getModelo(),
            "El modelo del clon en BD no coincide con el original.");
        assertEquals(original.getFabricante(),      clonRecuperado.getFabricante(),
            "El fabricante del clon en BD no coincide con el original.");
        assertEquals(original.getPeso(),            clonRecuperado.getPeso(), 0.001,
            "El peso del clon en BD no coincide con el original.");
        assertEquals(original.isDeteccionTermica(), clonRecuperado.isDeteccionTermica(),
            "La deteccion termica del clon en BD no coincide con el original.");
    }

    @Test
    @Order(9)
    @DisplayName("9. [BD] Se pueden guardar multiples clones independientes del mismo prototipo")
    void testMultiplesClonesPersistidos() throws Exception {
        Vigilancia clon2 = manager.cloneAndSave("EAGLE_TEST", idClon2, "SER-" + idClon2);

        assertNotNull(clon2, "El segundo clon no debe ser null.");

        List<Drone> todos = dao.listarDrones();
        long clones = todos.stream()
                .filter(d -> d.getId().equals(idClon1) || d.getId().equals(idClon2))
                .count();

        assertEquals(2, clones,
            "Deben existir exactamente 2 clones del prototipo en la BD.");
    }

    @Test
    @Order(10)
    @DisplayName("10. [BD] cloneAndSave() con clave inexistente debe retornar null (sin excepcion)")
    void testClaveInexistenteRetornaNull() throws Exception {
        Vigilancia resultado = manager.cloneAndSave("CLAVE_FALSA_XYZ", "NO-ID", "NO-SER");
        assertNull(resultado,
            "cloneAndSave() con una clave que no existe en el cache debe retornar null.");
    }
}
