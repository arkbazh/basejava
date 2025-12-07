import java.util.Arrays;
import java.util.Objects;

public class ArrayStorage {
    private static final int CAPACITY = 10000;

    private final Resume[] storage = new Resume[CAPACITY];
    private int size = 0;

    void save(Resume r) {
        Objects.requireNonNull(r, "resume must not be null");

        if (size >= CAPACITY) {
            throw new IllegalStateException("storage overflow: capacity = " + CAPACITY);
        }
        storage[size] = r;
        size++;
    }

    Resume get(String uuid) {
        Objects.requireNonNull(uuid, "uuid must not be null");

        for (int i = 0; i < size; i++) {
            if (uuid.equals(storage[i].uuid)) {
                return storage[i];
            }
        }
        return null;
    }

    void delete(String uuid) {
        Objects.requireNonNull(uuid, "uuid must not be null");

        for (int i = 0; i < size; i++) {
            if (uuid.equals(storage[i].uuid)) {
                storage[i] = storage[size - 1];
                storage[size - 1] = null;
                size--;
                break;
            }
        }
    }

    int size() {
        return size;
    }

    Resume[] getAll() {
        return Arrays.copyOf(storage, size);
    }

    void clear() {
        Arrays.fill(storage, 0, size, null);
        size = 0;
    }
}