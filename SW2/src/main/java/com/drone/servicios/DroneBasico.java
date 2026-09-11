package com.drone.servicios;

import com.drone.model.Agricultura;
import com.drone.model.Drone;
import com.drone.model.Vigilancia;

/**
 * Componente Concreto del Patron Decorator.
 * 
 * Es la clase base que envuelve a la entidad Drone de nuestra base de datos.
 * Su proposito es extraer los datos originales del dron recien creado
 * y presentarlos en un formato base, para que luego puedan ser decorados.
 */
public class DroneBasico implements DroneComponent {
    
    private Drone dron;
    
    public DroneBasico(Drone dron) {
        this.dron = dron;
    }
    
    @Override
    public String getDescription() {
        String base = "--- ESPECIFICACIONES ACTUALES ---\n" +
                      "- ID de Fabrica: " + dron.getId() + "\n" +
                      "- Modelo: " + dron.getModelo() + "\n" +
                      "- Fabricante: " + dron.getFabricante() + "\n" +
                      "- Peso Base: " + dron.getPeso() + " kg";
        
        // Agregar atributos especiales segun la instancia
        if (dron instanceof Agricultura) {
            base += "\n- Capacidad de Tanque: " + ((Agricultura)dron).getCapacidadTanque() + " L";
        } else if (dron instanceof Vigilancia) {
            base += "\n- Deteccion Termica: " + (((Vigilancia)dron).isDeteccionTermica() ? "SI" : "NO");
        }
        
        return base;
    }
}
