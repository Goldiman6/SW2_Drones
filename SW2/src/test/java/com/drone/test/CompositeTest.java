package com.drone.test;

import com.drone.servicios.ComponenteSensor;
import com.drone.servicios.GeneradorCompositeSensores;
import com.drone.servicios.GrupoSensor;
import com.drone.servicios.SensorEstatico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas - Patrón Composite (Sensores)")
public class CompositeTest {

    @Test
    @DisplayName("1. SensorEstatico (Hoja) devuelve su nombre correctamente y no tiene hijos")
    void testSensorEstaticoPropiedades() {
        ComponenteSensor sensor = new SensorEstatico("Sensor de Prueba");
        
        assertEquals("Sensor de Prueba", sensor.getNombre(), "El nombre del sensor debe coincidir.");
        assertNotNull(sensor.getHijos(), "La lista de hijos no debe ser nula.");
        assertTrue(sensor.getHijos().isEmpty(), "Un SensorEstatico no debe tener hijos.");
    }

    @Test
    @DisplayName("2. SensorEstatico lanza excepción al intentar agregar o eliminar hijos")
    void testSensorEstaticoExcepciones() {
        ComponenteSensor hoja = new SensorEstatico("Sensor Hoja");
        ComponenteSensor otro = new SensorEstatico("Otro Sensor");

        assertThrows(UnsupportedOperationException.class, () -> hoja.agregar(otro),
                "Debe lanzar UnsupportedOperationException al agregar a una hoja.");
        
        assertThrows(UnsupportedOperationException.class, () -> hoja.eliminar(otro),
                "Debe lanzar UnsupportedOperationException al eliminar de una hoja.");
    }

    @Test
    @DisplayName("3. GrupoSensor (Composite) puede agregar y eliminar hijos correctamente")
    void testGrupoSensorManejoHijos() {
        ComponenteSensor grupo = new GrupoSensor("Grupo Principal");
        ComponenteSensor hoja1 = new SensorEstatico("Hoja 1");
        ComponenteSensor hoja2 = new SensorEstatico("Hoja 2");
        ComponenteSensor subGrupo = new GrupoSensor("Subgrupo");

        // Agregar
        grupo.agregar(hoja1);
        grupo.agregar(hoja2);
        grupo.agregar(subGrupo);

        List<ComponenteSensor> hijos = grupo.getHijos();
        assertEquals(3, hijos.size(), "El grupo debe tener 3 hijos.");
        assertTrue(hijos.contains(hoja1));
        assertTrue(hijos.contains(subGrupo));

        // Eliminar
        grupo.eliminar(hoja2);
        assertEquals(2, grupo.getHijos().size(), "El grupo debe tener 2 hijos tras eliminar uno.");
        assertFalse(grupo.getHijos().contains(hoja2));
    }

    @Test
    @DisplayName("4. Generador construye el árbol estático exacto según el diagrama")
    void testGeneradorJerarquiaExacta() {
        ComponenteSensor raiz = GeneradorCompositeSensores.crearArbolSensores();

        // 1. Validar la Raíz
        assertEquals("Sensor General", raiz.getNombre());
        List<ComponenteSensor> hijosRaiz = raiz.getHijos();
        assertEquals(4, hijosRaiz.size(), "La raíz debe tener 4 hijos directos.");

        // Encontrar sub-grupos
        ComponenteSensor temp = null, camara = null, sonido = null, inteligente = null;
        for (ComponenteSensor h : hijosRaiz) {
            switch (h.getNombre()) {
                case "Sensor Temperatura": temp = h; break;
                case "Sensor Cámara": camara = h; break;
                case "Sensor Sonido": sonido = h; break;
                case "Sensor Inteligente": inteligente = h; break;
            }
        }

        assertNotNull(temp, "Debe existir Sensor Temperatura");
        assertNotNull(camara, "Debe existir Sensor Cámara");
        assertNotNull(sonido, "Debe existir Sensor Sonido");
        assertNotNull(inteligente, "Debe existir Sensor Inteligente");

        // 2. Validar que Sensor Inteligente sea una hoja sin hijos
        assertTrue(inteligente instanceof SensorEstatico);
        assertTrue(inteligente.getHijos().isEmpty());

        // 3. Validar Grupo Temperatura (Infrarrojo, RTD)
        assertEquals(2, temp.getHijos().size());
        assertEquals("Sensor Infrarrojo", temp.getHijos().get(0).getNombre());
        assertEquals("RTD", temp.getHijos().get(1).getNombre());

        // 4. Validar Grupo Cámara (CMOS, CCD)
        assertEquals(2, camara.getHijos().size());
        assertEquals("Sensor CMOS", camara.getHijos().get(0).getNombre());
        assertEquals("Sensor CCD", camara.getHijos().get(1).getNombre());

        // 5. Validar Grupo Sonido y su Subgrupo Digital
        assertEquals(2, sonido.getHijos().size(), "Sensor Sonido debe tener Analógico y el grupo Digital");
        assertEquals("Sensor Analógico", sonido.getHijos().get(0).getNombre());
        
        ComponenteSensor digital = sonido.getHijos().get(1);
        assertEquals("Sensor Digital", digital.getNombre());
        assertEquals(2, digital.getHijos().size(), "Sensor Digital debe tener SPI y UART");
        assertEquals("SPI", digital.getHijos().get(0).getNombre());
        assertEquals("UART", digital.getHijos().get(1).getNombre());
    }
}
