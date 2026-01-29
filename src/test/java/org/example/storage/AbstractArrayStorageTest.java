package org.example.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.example.exception.ResumeAlreadyExistsException;
import org.example.exception.ResumeNotFoundException;
import org.example.exception.StorageOverflowException;
import org.example.model.Resume;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

abstract class AbstractArrayStorageTest {
    protected static final Resume R1 = new Resume("uuid1");
    protected static final Resume R2 = new Resume("uuid2");
    protected static final Resume R3 = new Resume("uuid3");

    protected final Storage storage;

    protected AbstractArrayStorageTest(Storage storage) {
        this.storage = storage;
    }

    @BeforeEach
    void setUp() {
        storage.clear();
    }

    @Test
    void save_whenEmpty_saves() {
        assertEquals(0, storage.size());

        storage.save(R1);

        assertEquals(1, storage.size());
        assertEquals(R1, storage.get(R1.getUuid()));
        assertArrayEquals(new Resume[]{R1}, storage.getAll());
    }

    @Test
    void save_whenExists_throwsResumeAlreadyExistsException() {
        storage.save(R1);
        assertEquals(1, storage.size());

        assertThrows(ResumeAlreadyExistsException.class, () -> storage.save(R1));

        assertEquals(1, storage.size());
        assertEquals(R1, storage.get(R1.getUuid()));
        assertArrayEquals(new Resume[]{R1}, storage.getAll());
    }

    @Test
    void save_whenFull_throwsStorageOverflowException() {
        fillStorageToCapacity();

        int limit = AbstractArrayStorage.STORAGE_LIMIT;
        assertEquals(limit, storage.size());

        Resume[] before = storage.getAll().clone();

        assertThrows(StorageOverflowException.class, () -> storage.save(new Resume("overflow")));

        assertEquals(limit, storage.size());
        assertArrayEquals(before, storage.getAll());
    }

    @Test
    void get_whenFound_returns() {
        storage.save(R1);

        Resume actual = storage.get(R1.getUuid());

        assertEquals(R1, actual);
        assertEquals(1, storage.size());
        assertArrayEquals(new Resume[]{R1}, storage.getAll());
    }

    @Test
    void get_whenNotFound_throwsResumeNotFoundException() {
        assertEquals(0, storage.size());
        assertArrayEquals(new Resume[0], storage.getAll());

        assertThrows(ResumeNotFoundException.class, () -> storage.get("notfound"));

        assertEquals(0, storage.size());
        assertArrayEquals(new Resume[0], storage.getAll());
    }

    @Test
    void update_whenFound_updated() {
        storage.save(R1);

        Resume updated = new Resume(R1.getUuid());

        storage.update(updated);

        assertEquals(1, storage.size());
        assertEquals(updated, storage.get(R1.getUuid()));
        assertArrayEquals(new Resume[]{updated}, storage.getAll());
    }

    @Test
    void update_whenNotFound_throwsResumeNotFoundException() {
        assertEquals(0, storage.size());
        assertArrayEquals(new Resume[0], storage.getAll());

        assertThrows(ResumeNotFoundException.class, () -> storage.update(R1));

        assertEquals(0, storage.size());
        assertArrayEquals(new Resume[0], storage.getAll());
    }

    @Test
    void delete_whenFound_deleted() {
        storage.save(R1);

        assertEquals(1, storage.size());
        assertArrayEquals(new Resume[]{R1}, storage.getAll());

        String uuid = R1.getUuid();
        storage.delete(uuid);

        assertThrows(ResumeNotFoundException.class, () -> storage.get(uuid));
        assertEquals(0, storage.size());
        assertArrayEquals(new Resume[0], storage.getAll());
    }

    @Test
    void delete_whenNotFound_throwsResumeNotFoundException() {
        assertEquals(0, storage.size());
        assertArrayEquals(new Resume[0], storage.getAll());

        assertThrows(ResumeNotFoundException.class, () -> storage.delete("missing-uuid"));

        assertEquals(0, storage.size());
        assertArrayEquals(new Resume[0], storage.getAll());
    }

    @Test
    void size_whenNotEmpty_returns() {
        storage.save(R1);
        storage.save(R2);
        storage.save(R3);

        assertEquals(3, storage.size());
    }

    @Test
    void getAll_whenNotEmpty_returns() {
        storage.save(R1);
        storage.save(R2);
        storage.save(R3);

        Resume[] actual = storage.getAll();

        assertEquals(3, storage.size());
        assertEquals(storage.size(), actual.length);
    }

    @Test
    void clear_whenNotEmpty_clears() {
        storage.save(R1);
        storage.save(R2);
        storage.save(R3);

        storage.clear();

        assertEquals(0, storage.size());
        assertArrayEquals(new Resume[0], storage.getAll());
    }

    protected Resume createResume(int id) {
        return new Resume("uuid" + id);
    }

    protected void fillStorageToCapacity() {
        storage.clear();
        for (int i = 1; i <= AbstractArrayStorage.STORAGE_LIMIT; i++) {
            storage.save(createResume(i));
        }
    }
}
