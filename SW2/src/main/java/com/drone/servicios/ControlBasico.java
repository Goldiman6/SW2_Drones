package com.drone.servicios;

import com.drone.model.Drone;

/**
 * Implementacion concreta del Patron Bridge - Control Basico.
 *
 * Esta implementacion es placebo: no modifica el modelo ni
 * la base de datos. Unicamente genera una descripcion textual
 * del modo de control para mostrar en la interfaz grafica.
 */
public class ControlBasico implements TipoControl {

    /**
     * Genera la descripcion del modo de Control Basico para el dron dado.
     *
     * @param dron El dron al que se le asigna este modo de control.
     * @return     Texto descriptivo del modo de control asignado.
     */
    @Override
    public String configurar(Drone dron) {
        return "\n--- Modo de Control Asignado ---\n" +
               "Tipo:    Control Basico (Manual)\n" +
               "Dron:    " + dron.getModelo() + " [" + dron.getId() + "]\n" +
               "Sistema: Radiocontrol por frecuencia 2.4 GHz\n" +
               "Piloto:  Requerido (operacion humana directa)\n" +
               "Alcance: Linea de vision directa (LoS)\n" +
               "Estado:  Configuracion simulada aplicada correctamente.";
    }
}
