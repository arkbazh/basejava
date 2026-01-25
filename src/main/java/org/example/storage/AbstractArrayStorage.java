package org.example.storage;

import java.util.Arrays;
import java.util.Objects;
import org.example.exception.ResumeAlreadyExistsException;
import org.example.exception.ResumeNotFoundException;
import org.example.exception.StorageOverflowException;
import org.example.model.Resume;

/**
 * Array-based storage for Resumes.
 */
public abstract class AbstractArrayStorage implements Storage {
    protected static final int STORAGE_LIMIT = 100_000;

    protected Resume[] storage = new Resume[STORAGE_LIMIT];
    protected int size = 0;

    @Override
    public final void save(final Resume r) {
        Objects.requireNonNull(r, "Resume must not be null");

        if (size >= STORAGE_LIMIT) {
            throw new StorageOverflowException();
        }

        final String uuid = r.getUuid();

        final int index = findIndex(uuid);
        if (index >= 0) {
            throw new ResumeAlreadyExistsException(uuid);
        }

        insertResume(r, index);
        size++;
    }

    @Override
    public final Resume get(final String uuid) {
        Objects.requireNonNull(uuid, "Uuid must not be null");

        return storage[getExistingIndex(uuid)];
    }

    @Override
    public final void update(final Resume r) {
        Objects.requireNonNull(r, "Resume must not be null");

        storage[getExistingIndex(r.getUuid())] = r;
    }

    @Override
    public final void delete(final String uuid) {
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

    protected abstract int findIndex(String uuid);

    protected abstract void insertResume(Resume r, int index);

    protected abstract void deleteResume(int index);

    private int getExistingIndex(final String uuid) {
        final int index = findIndex(uuid);
        if (index < 0) {
            throw new ResumeNotFoundException(uuid);
        }
        return index;
    }
}
