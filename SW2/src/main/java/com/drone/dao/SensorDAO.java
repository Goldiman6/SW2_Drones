package com.drone.dao;

import com.drone.model.Sensor;
import java.util.List;

public interface SensorDAO {
    void create(Sensor sensor);
    Sensor read(int id);
    List<Sensor> readAll();
    void update(Sensor sensor);
    void delete(int id);
}
