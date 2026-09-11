package com.drone.test;

import com.drone.model.Agricultura;
import com.drone.model.Drone;
import com.drone.model.Vigilancia;
import com.drone.servicios.BateriaDecorator;
import com.drone.servicios.DroneBasico;
import com.drone.servicios.DroneComponent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests Unitarios del Patron Decorator.
 *
 * Verifica que la cadena de decoracion (DroneComponent -> DroneBasico -> BateriaDecorator)
 * funcione correctamente para cualquier tipo de Drone sin modificar su estructura interna.
 *
 * Arquitectura verificada (desde el diagrama):
 *   <<interface>> DroneComponent
 *       |-- DroneBasico  (Wraps Drone, extrae su descripcion base)
 *       |-- BateriaDecorator  (Wraps DroneComponent, anade la bateria)
 */
@DisplayName("Pruebas - Patron Decorator (BateriaDecorator)")
public class DecoratorTest {

    // ----------------------------------------------------------------
    // 1. DroneBasico debe contener la informacion del dron original
    // ----------------------------------------------------------------
    @Test
    @DisplayName("1. DroneBasico debe generar descripcion con los datos del Drone de Agricultura")
    void testDroneBasicoConAgricultura() {
        Drone dron = new Agricultura("DEC-01", "SER-A01", "AgroHawk", "AgroCorp", 12.0, 30.0);
        DroneComponent base = new DroneBasico(dron);
        String desc = base.getDescription();

        assertNotNull(desc, "La descripcion no debe ser null.");
        assertTrue(desc.contains("DEC-01"),     "Debe contener el ID del dron.");
        assertTrue(desc.contains("AgroHawk"),   "Debe contener el modelo del dron.");
        assertTrue(desc.contains("AgroCorp"),   "Debe contener el fabricante del dron.");
        assertTrue(desc.contains("12.0"),       "Debe contener el peso del dron.");
        assertTrue(desc.contains("30.0"),       "Debe contener la capacidad del tanque.");
    }

    @Test
    @DisplayName("2. DroneBasico debe generar descripcion con los datos del Drone de Vigilancia")
    void testDroneBasicoConVigilancia() {
        Drone dron = new Vigilancia("DEC-02", "SER-V02", "EagleSpy", "SpyCorp", 5.5, true);
        DroneComponent base = new DroneBasico(dron);
        String desc = base.getDescription();

        assertNotNull(desc, "La descripcion no debe ser null.");
        assertTrue(desc.contains("DEC-02"),   "Debe contener el ID del dron.");
        assertTrue(desc.contains("EagleSpy"), "Debe contener el modelo del dron.");
        assertTrue(desc.contains("SI"),       "Debe indicar que tiene deteccion termica.");
    }

    // ----------------------------------------------------------------
    // 2. BateriaDecorator debe agregar el modulo de bateria al base
    // ----------------------------------------------------------------
    @Test
    @DisplayName("3. BateriaDecorator debe agregar la bateria a la descripcion base")
    void testBateriaDecoratorAgregaBateria() {
        Drone dron = new Agricultura("DEC-03", "SER-A03", "HarvestX", "FarmTech", 20.0, 50.0);
        DroneComponent base     = new DroneBasico(dron);
        DroneComponent decorado = new BateriaDecorator(base);
        String desc = decorado.getDescription();

        assertNotNull(desc, "La descripcion decorada no debe ser null.");
        assertTrue(desc.contains("Bateria de Alta Capacidad"),
            "Debe contener el modulo de bateria agregado por el decorador.");
        assertTrue(desc.contains("HarvestX"),
            "Debe conservar el modelo del dron base.");
    }

    // ----------------------------------------------------------------
    // 3. El Drone original NO debe ser modificado tras decorar
    // ----------------------------------------------------------------
    @Test
    @DisplayName("4. El Drone original no debe ser alterado por el Decorator")
    void testDroneOriginalNoModificado() {
        Agricultura dron = new Agricultura("DEC-04", "SER-A04", "CleanAir", "EcoFly", 8.0, 25.0);
        DroneComponent base     = new DroneBasico(dron);
        DroneComponent decorado = new BateriaDecorator(base);

        // Ejecutar el decorador
        decorado.getDescription();

        // Verificar que el dron original sigue intacto
        assertEquals("CleanAir", dron.getModelo(),     "El modelo del dron no debe cambiar.");
        assertEquals(8.0,        dron.getPeso(), 0.001,"El peso del dron no debe cambiar.");
        assertEquals(25.0,       dron.getCapacidadTanque(), 0.001, "La capacidad no debe cambiar.");
    }

    // ----------------------------------------------------------------
    // 4. La descripcion decorada incluye toda la info del base
    // ----------------------------------------------------------------
    @Test
    @DisplayName("5. La descripcion decorada debe incluir TODA la informacion base mas la bateria")
    void testDescripcionDecoradorContieneTodoElBase() {
        Drone dron = new Vigilancia("DEC-05", "SER-V05", "NightOwl", "DarkVision", 6.0, false);
        DroneComponent base     = new DroneBasico(dron);
        DroneComponent decorado = new BateriaDecorator(base);

        String descBase     = base.getDescription();
        String descDecorada = decorado.getDescription();

        // La descripcion decorada debe contener TODO lo del base mas el extra
        assertTrue(descDecorada.contains(descBase),
            "La descripcion decorada debe contener toda la descripcion base.");
        assertTrue(descDecorada.contains("Bateria de Alta Capacidad"),
            "La descripcion decorada debe agregar el modulo de bateria.");
        assertTrue(descDecorada.length() > descBase.length(),
            "La descripcion decorada debe ser mas larga que la base.");
    }

    // ----------------------------------------------------------------
    // 5. Polimorfismo: DroneBasico y BateriaDecorator son DroneComponent
    // ----------------------------------------------------------------
    @Test
    @DisplayName("6. DroneBasico y BateriaDecorator deben ser instancias de DroneComponent")
    void testPolimorfismoDecorator() {
        Drone dron = new Agricultura("DEC-06", "SER-A06", "SkyFarm", "AeroCorp", 15.0, 40.0);
        DroneComponent base     = new DroneBasico(dron);
        DroneComponent decorado = new BateriaDecorator(base);

        assertInstanceOf(DroneComponent.class, base,
            "DroneBasico debe implementar DroneComponent.");
        assertInstanceOf(DroneComponent.class, decorado,
            "BateriaDecorator debe implementar DroneComponent.");
    }
}
