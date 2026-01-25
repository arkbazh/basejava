package org.example.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.example.exception.ResumeAlreadyExistsException;
import org.example.exception.ResumeNotFoundException;
import org.example.exception.StorageOverflowException;
import org.example.model.Resume;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractArrayStorageTest {
    private Storage storage;

    @BeforeEach
    void setUp() {
        storage = new ArrayStorage();
    }

    @Test
    void save_whenEmpty_addResume() {
        // Arrange SUT + preconditions + inputs
        Resume newResume = new Resume("uuid1");

        // Arrange BEFORE
        int sizeBefore = storage.size();

        // Act
        storage.save(newResume);

        // Assert: outcome visible effect
        assertEquals(newResume, storage.get(newResume.getUuid()));

        // Assert: AFTER
        assertEquals(sizeBefore + 1, storage.size());
        Resume[] after = storage.getAll();
        assertArrayEquals(new Resume[]{newResume}, after);

        // Assert: invariants AFTER
        assertEquals(storage.size(), after.length);
    }

    @Test
    void save_whenNonEmpty_addResume() {
        Resume r1 = new Resume("uuid1");
        Resume r2 = new Resume("uuid2");
        storage.save(r1);

        final int sizeBefore = storage.size();
        assertArrayEquals(new Resume[]{r1}, storage.getAll());

        storage.save(r2);

        assertEquals(r2, storage.get(r2.getUuid()));
        assertEquals(sizeBefore + 1, storage.size());

        Resume[] after = storage.getAll();
        assertArrayEquals(new Resume[]{r1, r2}, after);
        assertEquals(storage.size(), after.length);
    }

    @Test
    void save_whenLastSlot_addResume() {
        fillStorageToLastSlot();
        assertEquals(AbstractArrayStorage.STORAGE_LIMIT - 1, storage.size());

        Resume r = new Resume("uuid" + (AbstractArrayStorage.STORAGE_LIMIT - 1));

        storage.save(r);

        assertEquals(r, storage.get(r.getUuid()));

        Resume[] after = storage.getAll();
        assertEquals(AbstractArrayStorage.STORAGE_LIMIT, after.length);
        assertEquals(r, after[after.length - 1]);

        assertEquals(storage.size(), after.length);
    }

    @Test
    void save_whenFull_throwsStorageOverflowException() {
        fillStorageToFull();

        Resume r = new Resume("overflow");

        Resume[] before = storage.getAll().clone();
        int sizeBefore = storage.size();

        assertThrows(StorageOverflowException.class, () -> storage.save(r));

        assertEquals(sizeBefore, storage.size());
        assertArrayEquals(before, storage.getAll());
    }

    @Test
    void save_whenNull_throwsNullPointerException() {
        Resume[] before = storage.getAll().clone();
        int sizeBefore = storage.size();

        assertThrows(NullPointerException.class, () -> storage.save(null));

        assertEquals(sizeBefore, storage.size());
        assertArrayEquals(before, storage.getAll());
    }

    @Test
    void save_whenDuplicate_throwsExistStorageException() {
        Resume existing = new Resume("uuid1");
        storage.save(existing);

        Resume duplicate = new Resume("uuid1");

        Resume[] before = storage.getAll().clone();
        int sizeBefore = storage.size();

        assertThrows(ResumeAlreadyExistsException.class, () -> storage.save(duplicate));

        assertArrayEquals(before, storage.getAll());
        assertEquals(sizeBefore, storage.size());
        assertEquals(existing, storage.get(existing.getUuid()));
    }

    @Test
    void get_whenFound_returnResume() {
        Resume expected = new Resume("uuid1");
        storage.save(expected);

        Resume actual = storage.get(expected.getUuid());

        assertEquals(expected, actual);
        assertEquals(1, storage.size());
        assertEquals(storage.size(), storage.getAll().length);
        assertArrayEquals(new Resume[]{expected}, storage.getAll());
    }

    @Test
    void get_whenNotFound_throwsResumeNotFoundException() {
        String missingUuid = "uuid1";
        assertThrows(ResumeNotFoundException.class, () -> storage.get(missingUuid));

        assertEquals(0, storage.size());
        assertEquals(storage.size(), storage.getAll().length);
    }

    @Test
    void get_whenNullUuid_throwsNullPointerException() {
        int sizeBefore = storage.size();
        Resume[] before = storage.getAll().clone();

        assertThrows(NullPointerException.class, () -> storage.get(null));

        assertEquals(sizeBefore, storage.size());
        assertArrayEquals(before, storage.getAll());
        assertEquals(storage.size(), storage.getAll().length);
    }

    @Test
    void update_whenFound_replacesResume() {
        Resume existing = new Resume("uuid1");
        storage.save(existing);

        Resume updated = new Resume("uuid1");

        int sizeBefore = storage.size();

        storage.update(updated);

        assertEquals(sizeBefore, storage.size());
        assertEquals(updated, storage.get("uuid1"));
        assertEquals(storage.size(), storage.getAll().length);
    }

    @Test
    void update_whenNotFound_throwsNotExistStorageException() {
        Resume existing = new Resume("uuid1");
        storage.save(existing);

        Resume missing = new Resume("uuid-missing");

        int sizeBefore = storage.size();
        Resume[] before = storage.getAll().clone();

        assertThrows(ResumeNotFoundException.class, () -> storage.update(missing));

        assertEquals(sizeBefore, storage.size());

        Resume[] after = storage.getAll();
        assertArrayEquals(before, after);

        assertEquals(existing, storage.get(existing.getUuid()));
        assertEquals(storage.size(), after.length);
    }

    @Test
    void update_whenNullResume_throwsNullPointerException() {
        Resume[] before = storage.getAll().clone();
        int sizeBefore = storage.size();

        assertThrows(NullPointerException.class, () -> storage.update(null));

        assertArrayEquals(before, storage.getAll());
        assertEquals(sizeBefore, storage.size());

        assertEquals(storage.size(), storage.getAll().length);
    }

    @Test
    void delete_whenNullUuid_throwsNullPointerException() {
        int sizeBefore = storage.size();
        final Resume[] before = storage.getAll().clone();

        assertThrows(NullPointerException.class, () -> storage.delete(null));

        assertEquals(sizeBefore, storage.size());

        Resume[] after = storage.getAll();
        assertEquals(storage.size(), after.length);
        assertArrayEquals(before, after);
    }

    @Test
    void delete_whenExists_deleteResume() {
        Resume existing = new Resume("uuid1");
        storage.save(existing);

        int sizeBefore = storage.size();

        storage.delete(existing.getUuid());

        assertThrows(ResumeNotFoundException.class, () -> storage.get(existing.getUuid()));

        assertEquals(sizeBefore - 1, storage.size());
        assertArrayEquals(new Resume[0], storage.getAll());
        assertEquals(storage.size(), storage.getAll().length);
    }

    @Test
    void delete_whenNotExists_throwsResumeNotFoundException() {
        Resume[] before = storage.getAll().clone();
        int sizeBefore = storage.size();

        assertThrows(ResumeNotFoundException.class, () -> storage.delete("uuid1"));

        assertEquals(sizeBefore, storage.size());
        assertArrayEquals(before, storage.getAll());
        assertEquals(storage.size(), storage.getAll().length);
    }

    @Test
    void size_whenEmpty_returns0() {
        assertEquals(0, storage.size());
        assertEquals(0, storage.getAll().length);
        assertEquals(storage.size(), storage.getAll().length);
    }

    @Test
    void getAll_whenEmpty_returnsEmptyArray() {
        Resume[] all = storage.getAll();

        assertArrayEquals(new Resume[0], all);
        assertEquals(0, storage.size());
        assertEquals(storage.size(), all.length);
    }

    @Test
    void clear_whenNonEmpty_becomesEmpty() {
        Resume r1 = new Resume("uuid1");
        Resume r2 = new Resume("uuid2");
        storage.save(r1);
        storage.save(r2);

        storage.clear();

        assertEquals(0, storage.size());
        assertArrayEquals(new Resume[0], storage.getAll());
        assertEquals(storage.size(), storage.getAll().length);
    }

    private static Resume uniqueResume(int i) {
        return new Resume("uuid" + i);
    }

    private void assertArrayBased() {
        assertTrue(storage instanceof AbstractArrayStorage, "Storage must be array-based");
    }

    private void fillStorageToLastSlot() {
        assertArrayBased();
        assertEquals(0, storage.size(), "Storage must be empty before fill");

        for (int i = 0; i < AbstractArrayStorage.STORAGE_LIMIT - 1; i++) {
            storage.save(uniqueResume(i));
        }

        Resume[] all = storage.getAll();
        assertEquals(AbstractArrayStorage.STORAGE_LIMIT - 1, all.length);
    }

    private void fillStorageToFull() {
        assertArrayBased();
        assertEquals(0, storage.size(), "Storage must be empty before fill");

        for (int i = 0; i < AbstractArrayStorage.STORAGE_LIMIT; i++) {
            storage.save(uniqueResume(i));
        }

        Resume[] all = storage.getAll();
        assertEquals(AbstractArrayStorage.STORAGE_LIMIT, all.length);
    }
}
