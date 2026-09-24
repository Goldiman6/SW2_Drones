package com.drone.servicios;

/**
 * Target (Interfaz) - Patrón Adapter
 *
 * Define el contrato que el cliente (Controlador) usa para exportar
 * cualquier entidad a un formato determinado.
 * Al depender de esta interfaz y no de una clase concreta,
 * el sistema cumple con el Principio de Inversión de Dependencias (DIP).
 */
public interface ExportadorFormato {

    /**
     * Exporta la entidad adaptada al formato de destino
     * y retorna un mensaje descriptivo del resultado.
     *
     * @return Mensaje de resultado de la exportación.
     */
    String exportar();
}
