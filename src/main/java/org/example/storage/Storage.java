package org.example.storage;

import org.example.model.Resume;

public interface Storage {
    // CRUD
    void save(Resume r);

    Resume get(String uuid);

    void update(Resume r);

    void delete(String uuid);

    // Queries
    int size();

    Resume[] getAll();

    // Maintenance
    void clear();
}
