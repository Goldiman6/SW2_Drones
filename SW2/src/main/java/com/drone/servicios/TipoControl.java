package com.drone.servicios;

import com.drone.model.Drone;

/**
 * Interfaz del Patron Bridge - Abstraccion del Tipo de Control.
 *
 *
 * Las implementaciones (ControlBasico, ControlAutonomo) son
 * placebo: no modifican el modelo ni persisten datos en BD.
 * Solo generan una descripcion textual para visualizacion en UI.
 */
public interface TipoControl {

    /**
     * Configura (de forma simulada/placebo) el modo de control del dron dado.
     *
     * @param dron El dron al que se le asigna el modo de control.
     * @return     Texto descriptivo del modo de control asignado, para mostrar en UI.
     */
    String configurar(Drone dron);
}
