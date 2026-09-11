package com.drone.servicios;

/**
 * Decorador Concreto del Patron Decorator.
 * 
 * Envuelve a un DroneComponent (que puede ser un DroneBasico u otro decorador)
 * y anade unicamente el modulo de bateria adicional a la descripcion, sin alterar
 * la clase Drone original.
 */
public class BateriaDecorator implements DroneComponent {
    
    private DroneComponent wrappee;
    
    public BateriaDecorator(DroneComponent wrappee) {
        this.wrappee = wrappee;
    }
    
    @Override
    public String getDescription() {
        // Ejecuta el metodo del componente envuelto y concatena su propia caracteristica
        return wrappee.getDescription() + 
               "\n[DECORADOR] + Bateria de Alta Capacidad Anadida (Extra)";
    }
}
