package com.drone.servicios;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite - Patrón Composite
 * Representa una agrupación de sensores (puede contener hojas u otros grupos).
 */
public class GrupoSensor implements ComponenteSensor {
    
    private String nombre;
    private List<ComponenteSensor> hijos;

    public GrupoSensor(String nombre) {
        this.nombre = nombre;
        this.hijos = new ArrayList<>();
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public List<ComponenteSensor> getHijos() {
        return hijos;
    }

    @Override
    public void agregar(ComponenteSensor componente) {
        hijos.add(componente);
    }

    @Override
    public void eliminar(ComponenteSensor componente) {
        hijos.remove(componente);
    }
}
