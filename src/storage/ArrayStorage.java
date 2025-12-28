package storage;

import java.util.Arrays;
import java.util.Objects;

import model.Resume;

public class ArrayStorage {
    private static final int CAPACITY = 10_000;

    private final Resume[] storage = new Resume[CAPACITY];
    private int size = 0;

    public void save(Resume r) {
        Objects.requireNonNull(r, "resume must not be null");

        String uuid = r.getUuid();

        if (size >= CAPACITY) {
            throw new IllegalStateException("storage overflow: capacity: " + CAPACITY);
        }

        if (findIndex(uuid) >= 0) {
            throw new IllegalArgumentException("resume already exists: " + uuid);
        }

        storage[size++] = r;
    }

    public Resume get(String uuid) {
        Objects.requireNonNull(uuid, "uuid must not be null");

        int index = findIndex(uuid);

        if (index < 0) {
            throw new IllegalArgumentException("resume not found: " + uuid);
        }

        return storage[index];
    }

    public void update(Resume r) {
        Objects.requireNonNull(r, "resume must not be null");

        String uuid = r.getUuid();
        int index = findIndex(uuid);

        if (index < 0) {
            throw new IllegalArgumentException("resume not found: " + uuid);
        }

        storage[index] = r;
    }

    public void delete(String uuid) {
        Objects.requireNonNull(uuid, "uuid must not be null");

        int index = findIndex(uuid);

        if (index < 0) {
            throw new IllegalArgumentException("resume not found: " + uuid);
        }

        storage[index] = storage[--size];
        storage[size] = null;
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

    private int findIndex(String uuid) {
        Objects.requireNonNull(uuid, "uuid must not be null");

        for (int i = 0; i < size; i++) {
            if (uuid.equals(storage[i].getUuid())) {
                return i;
            }
        }
        return -1;
    }
}
