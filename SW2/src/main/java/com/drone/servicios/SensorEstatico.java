package com.drone.servicios;

import java.util.ArrayList;
import java.util.List;

/**
 * Hoja (Leaf) - Patrón Composite
 * Representa un sensor individual estático (sin hijos) en memoria.
 * Solo almacena un nombre String.
 */
public class SensorEstatico implements ComponenteSensor {
    
    private String nombre;
    
    public SensorEstatico(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public List<ComponenteSensor> getHijos() {
        // Una hoja no tiene hijos
        return new ArrayList<>();
    }

    @Override
    public void agregar(ComponenteSensor componente) {
        throw new UnsupportedOperationException("No se pueden agregar hijos a un SensorEstatico (Hoja).");
    }

    @Override
    public void eliminar(ComponenteSensor componente) {
        throw new UnsupportedOperationException("No se pueden eliminar hijos de un SensorEstatico (Hoja).");
    }
}
