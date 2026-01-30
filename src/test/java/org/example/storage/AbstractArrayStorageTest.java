package org.example.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.example.exception.ResumeAlreadyExistsException;
import org.example.exception.ResumeNotFoundException;
import org.example.exception.StorageOverflowException;
import org.example.model.Resume;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

abstract class AbstractArrayStorageTest {
    private static final Resume R1 = new Resume("uuid1");
    private static final Resume R2 = new Resume("uuid2");
    private static final Resume R3 = new Resume("uuid3");

    private final Storage storage;

    protected AbstractArrayStorageTest(Storage storage) {
        this.storage = storage;
    }

    @BeforeEach
    void setUp() {
        storage.clear();
        storage.save(R1);
        storage.save(R2);
        storage.save(R3);
    }

    @Test
    void save_whenEmpty_saves() {
        storage.clear();

        storage.save(R1);

        assertArrayEquals(new Resume[]{R1}, storage.getAll());
    }

    @Test
    void save_whenExists_throwsResumeAlreadyExistsException() {
        assertThrows(ResumeAlreadyExistsException.class, () -> storage.save(R1));

        assertArrayEquals(new Resume[]{R1, R2, R3}, storage.getAll());
    }

    @Test
    void save_whenFull_throwsStorageOverflowException() {
        storage.clear();
        int limit = AbstractArrayStorage.STORAGE_LIMIT;

        for (int i = 0; i < limit; i++) {
            try {
                storage.save(new Resume("uuid" + i));
            } catch (StorageOverflowException e) {
                fail("Overflow happened too early at i: " + i);
            }
        }

        assertEquals(limit, storage.size());

        assertThrows(StorageOverflowException.class, () -> storage.save(new Resume("overflow")));
        assertEquals(limit, storage.size());
    }

    @Test
    void get_whenFound_returns() {
        Resume actual = storage.get(R1.getUuid());

        assertEquals(actual, R1);
        assertArrayEquals(new Resume[]{R1, R2, R3}, storage.getAll());
    }

    @Test
    void get_whenNotFound_throwsResumeNotFoundException() {
        assertEquals(3, storage.size());
        assertArrayEquals(new Resume[]{R1, R2, R3}, storage.getAll());

        assertThrows(ResumeNotFoundException.class, () -> storage.get("missing-uuid"));

        assertEquals(3, storage.size());
        assertArrayEquals(new Resume[]{R1, R2, R3}, storage.getAll());
    }

    @Test
    void update_whenFound_updated() {
        assertEquals(3, storage.size());
        assertArrayEquals(new Resume[]{R1, R2, R3}, storage.getAll());
        Resume updated = new Resume(R1.getUuid());

        storage.update(updated);

        assertEquals(3, storage.size());
        assertEquals(updated, storage.get(R1.getUuid()));
        assertArrayEquals(new Resume[]{updated, R2, R3}, storage.getAll());
    }

    @Test
    void update_whenNotFound_throwsResumeNotFoundException() {
        Resume updated = new Resume(R1.getUuid());

        storage.update(updated);

        Resume actualResume = storage.get(R1.getUuid());

        assertSame(updated, actualResume);
    }

    @Test
    void delete_whenFound_deleted() {
        storage.delete(R1.getUuid());

        assertThrows(ResumeNotFoundException.class, () -> storage.get(R1.getUuid()));
    }

    @Test
    void delete_whenNotFound_throwsResumeNotFoundException() {
        assertThrows(ResumeNotFoundException.class, () -> storage.delete("missing-uuid"));
    }

    @Test
    void size_whenNotEmpty_returns() {
        assertEquals(3, storage.size());
    }

    @Test
    void getAll_whenNotEmpty_returns() {
        assertArrayEquals(new Resume[]{R1, R2, R3}, storage.getAll());
    }

    @Test
    void clear_whenNotEmpty_clears() {
        storage.clear();

        assertArrayEquals(new Resume[0], storage.getAll());
    }
}
