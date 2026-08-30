package com.drone.test;

import com.drone.model.Agricultura;
import com.drone.model.Drone;
import com.drone.model.Vigilancia;
import com.drone.servicios.CrearAgricultura;
import com.drone.servicios.CrearVigilancia;
import com.drone.servicios.FactoryCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba del Patron Factory Method.
 * Verifica que cada fabrica concreta crea el subtipo correcto de Drone.
 */
@DisplayName("Pruebas - Patron Factory Method")
public class FactoryMethodTest {

    @Test
    @DisplayName("La fabrica CrearAgricultura debe producir un Drone de tipo Agricultura")
    public void testFactoryProduceAgricultura() throws Exception {
        FactoryCreator factory = new CrearAgricultura();
        String rnd = "FA-" + (int)(Math.random() * 10000);
        Drone dron = factory.crearYGuardar(rnd, "SER-" + rnd, "AgriX", "FabTech", 10.0, 80.0, false);

        assertNotNull(dron, "ERROR: La fabrica devolvio null.");
        assertInstanceOf(Agricultura.class, dron,
            "ERROR: Se esperaba un objeto de tipo Agricultura pero se obtuvo: " + dron.getClass().getSimpleName());
    }

    @Test
    @DisplayName("La fabrica CrearVigilancia debe producir un Drone de tipo Vigilancia")
    public void testFactoryProduceVigilancia() throws Exception {
        FactoryCreator factory = new CrearVigilancia();
        String rnd = "FV-" + (int)(Math.random() * 10000);
        Drone dron = factory.crearYGuardar(rnd, "SER-" + rnd, "SkyEye", "DroneVision", 5.0, 0.0, true);

        assertNotNull(dron, "ERROR: La fabrica devolvio null.");
        assertInstanceOf(Vigilancia.class, dron,
            "ERROR: Se esperaba un objeto de tipo Vigilancia pero se obtuvo: " + dron.getClass().getSimpleName());
    }

    @Test
    @DisplayName("La fabrica concreta correcta mantiene los datos que le pasas")
    public void testDatosCorrectos() throws Exception {
        FactoryCreator factory = new CrearAgricultura();
        String rnd = "FD-" + (int)(Math.random() * 10000);
        Drone dron = factory.crearYGuardar(rnd, "SER-" + rnd, "ModeloTest", "FabTest", 15.5, 100.0, false);

        assertEquals("SER-" + rnd, dron.getSerial(), "El serial guardado no coincide.");
        assertEquals("ModeloTest", dron.getModelo(), "El modelo guardado no coincide.");
        assertEquals(15.5, dron.getPeso(), "El peso guardado no coincide.");
    }
}
