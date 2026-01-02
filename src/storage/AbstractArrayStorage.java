package storage;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import model.Resume;

/**
 * Array-based storage for Resumes.
 */
public abstract class AbstractArrayStorage implements Storage {
    protected static final int STORAGE_LIMIT = 100_000;

    protected Resume[] storage = new Resume[STORAGE_LIMIT];
    protected int size = 0;

    @Override
    public void save(Resume r) {
        Objects.requireNonNull(r, "resume must not be null");

        if (size == STORAGE_LIMIT) {
            throw new IllegalStateException("storage overflow");
        }

        String uuid = r.getUuid();
        for (int i = 0; i < size; i++) {
            if (uuid.equals(storage[i].getUuid())) {
                throw new IllegalArgumentException("resume already exists: " + r);
            }
        }

        storage[size++] = r;
    }

    @Override
    public Resume get(String uuid) {
        Objects.requireNonNull(uuid, "resume must not be null");

        int index = getIndex(uuid);
        if (index == -1) {
            throw new NoSuchElementException("resume not found");
        }
        return storage[index];
    }

    @Override
    public void update(Resume r) {
        Objects.requireNonNull(r, "resume must not be null");

        int index = getIndex(r.getUuid());
        if (index == -1) {
            throw new NoSuchElementException("resume not found");
        }

        storage[index] = r;
    }

    @Override
    public void delete(String uuid) {
        Objects.requireNonNull(uuid, "uuid must not be null");

        int index = getIndex(uuid);
        if (index == -1) {
            throw new NoSuchElementException("resume not found");
        }

        storage[index] = storage[--size];
        storage[size] = null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        Arrays.fill(storage, 0, size, null);
        size = 0;
    }

    @Override
    public Resume[] getAll() {
        return Arrays.copyOfRange(storage, 0, size);
    }

    protected int getIndex(String uuid) {
        for (int i = 0; i < size; i++) {
            if (uuid.equals(storage[i].getUuid())) {
                return i;
            }
        }
        return -1;
    }
}
