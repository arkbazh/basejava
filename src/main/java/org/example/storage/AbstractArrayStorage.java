package org.example.storage;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.example.model.Resume;

/**
 * Array-based storage for Resumes.
 */
public abstract class AbstractArrayStorage implements Storage {
    protected static final int STORAGE_LIMIT = 100_000;

    protected Resume[] storage = new Resume[STORAGE_LIMIT];
    protected int size = 0;

    @Override
    public final void save(Resume r) {
        Objects.requireNonNull(r, "Resume must not be null");

        if (size >= STORAGE_LIMIT) {
            throw new IllegalStateException("Storage overflow");
        }

        String uuid = r.getUuid();

        int index = findIndex(uuid);
        if (index >= 0) {
            throw new IllegalArgumentException("Resume already exists: " + r.getUuid());
        }

        insertResume(r, index);
        size++;
    }

    @Override
    public final Resume get(String uuid) {
        Objects.requireNonNull(uuid, "Uuid must not be null");

        return storage[getExistingIndex(uuid)];
    }

    @Override
    public final void update(Resume r) {
        Objects.requireNonNull(r, "Resume must not be null");

        storage[getExistingIndex(r.getUuid())] = r;
    }

    @Override
    public final void delete(String uuid) {
        Objects.requireNonNull(uuid, "Uuid must not be null");

        deleteResume(getExistingIndex(uuid));

        storage[size] = null;
    }

    @Override
    public final int size() {
        return size;
    }

    @Override
    public final Resume[] getAll() {
        return Arrays.copyOfRange(storage, 0, size);
    }

    @Override
    public final void clear() {
        Arrays.fill(storage, 0, size, null);
        size = 0;
    }

    private int getExistingIndex(String uuid) {
        int index = findIndex(uuid);
        if (index < 0) {
            throw new NoSuchElementException("Resume not found");
        }
        return index;
    }

    protected abstract int findIndex(String uuid);

    protected abstract void insertResume(Resume r, int index);

    protected abstract void deleteResume(int index);
}
