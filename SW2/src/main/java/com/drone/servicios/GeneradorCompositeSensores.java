package com.drone.servicios;

/**
 * Generador Estático del árbol Composite.
 * Construye y devuelve la raíz de la jerarquía según la nueva especificación.
 */
public class GeneradorCompositeSensores {

    public static ComponenteSensor crearArbolSensores() {
        // --- 1. Crear la Raíz ---
        ComponenteSensor sensorGeneral = new GrupoSensor("Sensor General");
        
        // --- 2. Crear los grupos principales ---
        ComponenteSensor sensorTemperatura = new GrupoSensor("Sensor Temperatura");
        ComponenteSensor sensorCamara = new GrupoSensor("Sensor Cámara");
        ComponenteSensor sensorSonido = new GrupoSensor("Sensor Sonido");
        ComponenteSensor sensorInteligente = new GrupoSensor("Sensor Inteligente");
        
        // --- 3. Crear hojas del grupo Temperatura ---
        sensorTemperatura.agregar(new SensorEstatico("Sensor Infrarrojo"));
        sensorTemperatura.agregar(new SensorEstatico("RTD"));
        
        
        // --- 4. Crear hojas del grupo Cámara ---
        sensorCamara.agregar(new SensorEstatico("Sensor CMOS"));
        sensorCamara.agregar(new SensorEstatico("Sensor CCD"));
        
        // --- 5. Grupo Sonido 
        
        // --- 6. Inteligente 
        ComponenteSensor sensorAnalogico = new SensorEstatico("Sensor Analógico");
        ComponenteSensor sensorDigital = new GrupoSensor("Sensor Digital");
        
        // Digital hijos
        sensorDigital.agregar(new SensorEstatico("SPI"));
        sensorDigital.agregar(new SensorEstatico("UART"));
        
        sensorInteligente.agregar(sensorAnalogico);
        sensorInteligente.agregar(sensorDigital);
        
        // --- 7. Añadir los grupos principales a la Raíz ---
        sensorGeneral.agregar(sensorTemperatura);
        sensorGeneral.agregar(sensorCamara);
        sensorGeneral.agregar(sensorSonido);
        sensorGeneral.agregar(sensorInteligente);
        
        return sensorGeneral;
    }
}
