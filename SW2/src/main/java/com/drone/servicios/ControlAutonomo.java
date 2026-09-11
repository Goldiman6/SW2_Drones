package com.drone.servicios;

import com.drone.model.Drone;

/**
 * Implementacion concreta del Patron Bridge - Control Autonomo.
 *
 *
 * Esta implementacion es placebo: no modifica el modelo ni
 * la base de datos. Unicamente genera una descripcion textual
 * del modo de control para mostrar en la interfaz grafica.
 */
public class ControlAutonomo implements TipoControl {

    /**
     * Genera la descripcion del modo de Control Autonomo para el dron dado.
     *
     * @param dron El dron al que se le asigna este modo de control.
     * @return     Texto descriptivo del modo de control asignado.
     */
    @Override
    public String configurar(Drone dron) {
        return "\n--- Modo de Control Asignado ---\n" +
               "Tipo:    Control Autonomo (IA)\n" +
               "Dron:    " + dron.getModelo() + " [" + dron.getId() + "]\n" +
               "Sistema: Planificacion de rutas con GPS + Vision computacional\n" +
               "Piloto:  No requerido (operacion automatizada)\n" +
               "Alcance: Mas alla de linea de vision (BVLoS)\n" +
               "Estado:  Configuracion simulada aplicada correctamente.";
    }
}
