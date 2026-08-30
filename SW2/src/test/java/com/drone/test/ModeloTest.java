package com.drone.test;

import com.drone.model.Agricultura;
import com.drone.model.Drone;
import com.drone.model.IPrototype;
import com.drone.model.Vigilancia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba de los Modelos (herencia y polimorfismo).
 * Verifica que Agricultura y Vigilancia se comportan correctamente como subtipos de Drone.
 */
@DisplayName("Pruebas - Modelos (Herencia y Polimorfismo)")
public class ModeloTest {

    @Test
    @DisplayName("Agricultura debe ser un subtipo valido de Drone")
    public void testAgriculturaesDrone() {
        Drone d = new Agricultura("1", "SER-A", "AgriX", "FabA", 10.0, 80.0);
        assertInstanceOf(Drone.class, d);
    }

    @Test
    @DisplayName("Vigilancia debe ser un subtipo valido de Drone")
    public void testVigilanciaEsDrone() {
        Drone d = new Vigilancia("2", "SER-V", "EyeX", "FabV", 5.0, true);
        assertInstanceOf(Drone.class, d);
    }

    @Test
    @DisplayName("El metodo clonar() de Agricultura debe generar un objeto diferente con los mismos datos")
    public void testClonacionAgricultura() {
        Agricultura original = new Agricultura("3", "SER-OR", "ModeloA", "FabA", 12.0, 60.0);
        IPrototype clon = original.clonar();

        assertNotSame(original, clon, "ERROR: clonar() devolvio el mismo objeto.");
        assertInstanceOf(Agricultura.class, clon);
        Agricultura clonAgr = (Agricultura) clon;
        assertEquals(original.getModelo(), clonAgr.getModelo());
        assertEquals(original.getCapacidadTanque(), clonAgr.getCapacidadTanque());
    }

    @Test
    @DisplayName("El metodo clonar() de Vigilancia debe generar un objeto diferente con los mismos datos")
    public void testClonacionVigilancia() {
        Vigilancia original = new Vigilancia("4", "SER-VG", "VigX", "FabV", 7.5, true);
        IPrototype clon = original.clonar();

        assertNotSame(original, clon, "ERROR: clonar() devolvio el mismo objeto.");
        assertInstanceOf(Vigilancia.class, clon);
        Vigilancia clonVig = (Vigilancia) clon;
        assertEquals(original.isDeteccionTermica(), clonVig.isDeteccionTermica());
    }
}
