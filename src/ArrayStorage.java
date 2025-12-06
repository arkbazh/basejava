import java.util.Arrays;
import java.util.Objects;

/**
 * Array, based storage for Resumes.
 */
public class ArrayStorage {
    Resume[] storage = new Resume[10000];

    void save(Resume r) {
        for (int i = 0; i < storage.length; i++) {
            if (storage[i] == null) {
                storage[i] = r;
                return;
            }
        }
    }

    Resume get(String uuid) {
        Objects.requireNonNull(uuid, "uuid must not be null");

        for (Resume r : storage) {
            if (r != null && uuid.equals(r.uuid)) {
                return r;
            }
        }
        return null;
    }

    void delete(String uuid) {
        Objects.requireNonNull(uuid, "uuid must not be null");

        for (int i = 0; i < storage.length; i++) {
            Resume r = storage[i];

            if (r != null && uuid.equals(r.uuid)) {
                for (int j = i; j < storage.length - 1; j++) {
                    storage[j] = storage[j + 1];
                }
                storage[storage.length - 1] = null;
                return;
            }
        }
    }

    int size() {
        int count = 0;
        for (Resume r : storage) {
            if (r == null) {
                return count;
            }
            count++;
        }
        return count;
    }

    /**
     * Retrieves all stored resumes in the form of an array.
     * The array contains only non-null elements up to the current size of the storage.
     *
     * @return an array of {@link Resume} objects currently stored in the storage
     */
    Resume[] getAll() {
        int size = size();
        return Arrays.copyOf(storage, size);
    }

    void clear() {
        int size = size();
        Arrays.fill(storage, 0, size, null);
    }
}

