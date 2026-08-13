package com.drone.dao;

import com.drone.model.Piloto;
import java.util.List;

public interface PilotoDAO {
    void create(Piloto piloto);
    Piloto read(int id);
    List<Piloto> readAll();
    void update(Piloto piloto);
    void delete(int id);
}
