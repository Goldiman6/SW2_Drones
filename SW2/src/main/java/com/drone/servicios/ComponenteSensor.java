package com.drone.servicios;

import java.util.List;

/**
 * Componente (Interfaz) - Patrón Composite
 * Define el contrato común tanto para las hojas (SensorEstatico) 
 * como para los compuestos (GrupoSensor).
 */
public interface ComponenteSensor {
    
    /**
     * Devuelve el nombre del sensor o del grupo.
     */
    String getNombre();
    
    /**
     * Devuelve los hijos del componente. 
     * Para una hoja, debería retornar una lista vacía o null,
     * pero por conveniencia devolvemos List<ComponenteSensor>.
     */
    List<ComponenteSensor> getHijos();
    
    /**
     * Agrega un componente hijo.
     */
    void agregar(ComponenteSensor componente);
    
    /**
     * Elimina un componente hijo.
     */
    void eliminar(ComponenteSensor componente);
}
