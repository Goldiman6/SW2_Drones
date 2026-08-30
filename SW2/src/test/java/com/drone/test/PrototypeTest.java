package com.drone.test;

import com.drone.model.Vigilancia;
import com.drone.servicios.Prototype;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba del Patron Prototype.
 * Verifica que el clon es un objeto diferente en memoria
 * pero con los mismos datos que el original.
 */
@DisplayName("Pruebas - Patron Prototype")
public class PrototypeTest {

    private Prototype manager;
    private Vigilancia original;

    @BeforeEach
    public void setUp() {
        manager = new Prototype();
        original = new Vigilancia("T-ORIG-01", "SER-ORIG", "EagleEye", "SkyCorp", 7.0, true);
        manager.addPrototipo("EAGLE", original);
    }

    @Test
    @DisplayName("El clon debe ser un objeto DIFERENTE en memoria al original")
    public void testClonEsObjetoDistinto() throws Exception {
        String rnd = "C1-" + (int)(Math.random() * 10000);
        Vigilancia clon = manager.cloneAndSave("EAGLE", rnd, "SER-" + rnd);

        assertNotNull(clon, "ERROR: El clon es null.");
        assertNotSame(original, clon,
            "ERROR: El clon es el MISMO objeto que el original (deberia ser una copia).");
    }

    @Test
    @DisplayName("El clon debe conservar los datos del original (modelo, fabricante, peso)")
    public void testClonConservaDatos() throws Exception {
        String rnd = "C2-" + (int)(Math.random() * 10000);
        Vigilancia clon = manager.cloneAndSave("EAGLE", rnd, "SER-" + rnd);

        assertEquals(original.getModelo(), clon.getModelo(),
            "ERROR: El modelo del clon no coincide con el original.");
        assertEquals(original.getFabricante(), clon.getFabricante(),
            "ERROR: El fabricante del clon no coincide con el original.");
        assertEquals(original.getPeso(), clon.getPeso(),
            "ERROR: El peso del clon no coincide con el original.");
        assertEquals(original.isDeteccionTermica(), clon.isDeteccionTermica(),
            "ERROR: La deteccion termica del clon no coincide con el original.");
    }

    @Test
    @DisplayName("El clon debe tener su propio ID y Serial nuevos")
    public void testClonTieneIdNuevo() throws Exception {
        String rnd = "C3-" + (int)(Math.random() * 10000);
        Vigilancia clon = manager.cloneAndSave("EAGLE", rnd, "SER-" + rnd);

        assertNotEquals(original.getId(), clon.getId(),
            "ERROR: El clon tiene el mismo ID que el original.");
        assertNotEquals(original.getSerial(), clon.getSerial(),
            "ERROR: El clon tiene el mismo serial que el original.");
    }

    @Test
    @DisplayName("Se pueden hacer multiples clones del mismo original")
    public void testMultiplesClones() throws Exception {
        String rnd1 = "C4-" + (int)(Math.random() * 10000);
        String rnd2 = "C5-" + (int)(Math.random() * 10000);
        Vigilancia clon1 = manager.cloneAndSave("EAGLE", rnd1, "SER-" + rnd1);
        Vigilancia clon2 = manager.cloneAndSave("EAGLE", rnd2, "SER-" + rnd2);

        assertNotNull(clon1, "ERROR: El primer clon es null.");
        assertNotNull(clon2, "ERROR: El segundo clon es null.");
        assertNotSame(clon1, clon2, "ERROR: Los dos clones son el mismo objeto.");
        assertNotEquals(clon1.getId(), clon2.getId(), "ERROR: Los dos clones tienen el mismo ID.");
    }
}
