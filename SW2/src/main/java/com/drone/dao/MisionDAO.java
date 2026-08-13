package com.drone.dao;

import com.drone.model.Mision;
import java.util.List;

public interface MisionDAO {
    void create(Mision mision);
    Mision read(int id);
    List<Mision> readAll();
    void update(Mision mision);
    void delete(int id);
}
