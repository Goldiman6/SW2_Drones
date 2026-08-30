package com.drone.model;

/**
 * Interfaz que establece el comportamiento para clonar objetos 
 * requerimiento principal del patrn Prototype.
 */
public interface IPrototype {
    /**
     * @return Una copia exacta del objeto que lo implementa, ocupando un espacio en memoria distinto.
     */
    IPrototype clonar();
}
