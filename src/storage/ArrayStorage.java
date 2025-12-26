package storage;

import java.util.Arrays;
import java.util.Objects;

import model.Resume;

public class ArrayStorage {
    private static final int CAPACITY = 10000;

    private final Resume[] storage = new Resume[CAPACITY];
    private int size = 0;

    public void save(Resume r) {
        Objects.requireNonNull(r, "Resume must not be null");

        if (size >= CAPACITY) {
            throw new IllegalStateException("Storage overflow: capacity = " + CAPACITY);
        }

        if (getIndex(r) > 0) {
            throw new IllegalArgumentException("Resume with UUID " + r.toString() + " already exists");
        }

        storage[size] = r;
        size++;
    }

    public Resume get(String uuid) {
        Objects.requireNonNull(uuid, "uuid must not be null");

        for (int i = 0; i < size; i++) {
            if (uuid.equals(storage[i].getUuid())) {
                return storage[i];
            }
        }
        return null;
    }

    public void update(Resume r) {
        Objects.requireNonNull(r, "Resume must not be null");

        if (getIndex(r) < 0) {
            throw new IllegalArgumentException("Resume with UUID " + r.toString() + " not exists");
        }

        storage[getIndex(r)] = r;

    }

    public void delete(String uuid) {
        Objects.requireNonNull(uuid, "uuid must not be null");

        for (int i = 0; i < size; i++) {
            if (uuid.equals(storage[i].toString())) {
                storage[i] = storage[size - 1];
                storage[size - 1] = null;
                size--;
                break;
            }
        }
    }

    public int size() {
        return size;
    }

    public Resume[] getAll() {
        return Arrays.copyOf(storage, size);
    }

    public void clear() {
        Arrays.fill(storage, 0, size, null);
        size = 0;
    }

    private int getIndex(Resume r) {
        Objects.requireNonNull(r, "Resume must not be null");

        String uuid = r.toString();

        for (int i = 0; i < size; i++) {
            if (uuid.equals(storage[i].toString())) {
                return i;
            }
        }
        return -1;
    }
}
