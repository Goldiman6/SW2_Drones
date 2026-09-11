package com.drone.servicios;

/**
 * Interfaz Base del Patron Decorator.
 * 
 * Define el contrato comun tanto para el componente base (DroneBasico)
 * como para los decoradores adicionales (BateriaDecorator).
 */
public interface DroneComponent {
    /**
     * Obtiene la descripcion y atributos actuales del componente.
     * @return Texto descriptivo.
     */
    String getDescription();
}
