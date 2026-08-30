package com.drone.model;

import java.util.List;

public class Mision {
    private String id;
    private String nombre;
    private String ubicacion;
    private String fecha;
    private List<Drone> drones;

    public Mision() {}

    public Mision(String id, String nombre, String ubicacion, String fecha, List<Drone> drones) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
        this.drones = drones;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public List<Drone> getDrones() { return drones; }
    public void setDrones(List<Drone> drones) { this.drones = drones; }
}
