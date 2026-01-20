package org.example.storage;

import org.example.model.Resume;

/**
 * Array-based storage for Resumes.
 */
public class ArrayStorage extends AbstractArrayStorage {
    @Override
    protected int findIndex(String uuid) {
        for (int i = 0; i < size; i++) {
            if (uuid.equals(storage[i].getUuid())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void insertResume(Resume r, int index) {
        storage[size] = r;
    }

    @Override
    protected void deleteResume(int index) {
        size--;
        storage[index] = storage[size];
    }
}
