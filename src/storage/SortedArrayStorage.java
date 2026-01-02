package storage;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import model.Resume;

public class SortedArrayStorage extends AbstractArrayStorage {
    @Override
    public void save(Resume r) {
        Objects.requireNonNull(r, "resume must not be null");

        if (size == STORAGE_LIMIT) {
            throw new IllegalStateException("storage overflow");
        }

        int searchResult = Arrays.binarySearch(storage, 0, size, r);
        if (searchResult >= 0) {
            throw new IllegalArgumentException("resume already exists: " + r.getUuid());
        }

        int insertPos = ~searchResult;
        System.arraycopy(storage, insertPos, storage, insertPos + 1, size - insertPos);
        storage[insertPos] = r;
        size++;
    }

    @Override
    public Resume get(String uuid) {
        Objects.requireNonNull(uuid, "uuid must not be null");

        int index = getIndex(uuid);
        if (index < 0) {
            throw new NoSuchElementException("resume not found: " + uuid);
        }
        return storage[index];
    }

    @Override
    public void update(Resume r) {
        Objects.requireNonNull(r, "resume must not be null");

        String uuid = r.getUuid();
        int index = getIndex(uuid);

        if (index < 0) {
            throw new NoSuchElementException("resume not found: " + uuid);
        }

        storage[index] = r;
    }

    @Override
    public void delete(String uuid) {
        Objects.requireNonNull(uuid, "uuid must not be null");

        int index = getIndex(uuid);
        if (index < 0) {
            throw new NoSuchElementException("resume not found: " + uuid);
        }

        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(storage, index + 1, storage, index, numMoved);
        }

        storage[--size] = null;
    }

    @Override
    protected int getIndex(String uuid) {
        Resume searchKey = new Resume();
        searchKey.setUuid(uuid);
        return Arrays.binarySearch(storage, 0, size, searchKey);
    }
}
