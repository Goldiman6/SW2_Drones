package com.drone.model;

import java.util.Date;

public class Mision {
    private int id;
    private String mision;
    private String ubicacion;
    private Date fecha;

    public Mision() {}

    public Mision(int id, String mision, String ubicacion, Date fecha) {
        this.id = id;
        this.mision = mision;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMision() { return mision; }
    public void setMision(String mision) { this.mision = mision; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
}
