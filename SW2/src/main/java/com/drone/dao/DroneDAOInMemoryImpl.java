package com.drone.dao;

import com.drone.model.Drone;
import java.util.ArrayList;
import java.util.List;

public class DroneDAOInMemoryImpl implements DroneDAO {
    private List<Drone> drones = new ArrayList<>();
    private int currentId = 1;

    @Override
    public void create(Drone drone) {
        drone.setId(currentId++);
        drones.add(drone);
    }

    @Override
    public Drone read(int id) {
        return drones.stream().filter(d -> d.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<Drone> readAll() {
        return new ArrayList<>(drones);
    }

    @Override
    public void update(Drone drone) {
        for (int i = 0; i < drones.size(); i++) {
            if (drones.get(i).getId() == drone.getId()) {
                drones.set(i, drone);
                return;
            }
        }
    }

    @Override
    public void delete(int id) {
        drones.removeIf(d -> d.getId() == id);
    }
}
