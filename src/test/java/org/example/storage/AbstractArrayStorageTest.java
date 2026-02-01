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
    private static final String UUID_1 = "uuid1";
    private static final String UUID_2 = "uuid2";
    private static final String UUID_3 = "uuid3";
    private static final String UUID_4 = "uuid4";
    private static final String MISSING = "missing";
    private static final String OVERFLOW = "overflow";
    private static final Resume R1 = new Resume(UUID_1);
    private static final Resume R2 = new Resume(UUID_2);
    private static final Resume R3 = new Resume(UUID_3);
    private static final Resume R4 = new Resume(UUID_4);

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
    void save_whenStorageEmpty_savesResume() {
        storage.clear();
        storage.save(R1);
        assertEquals(R1, storage.get(UUID_1));
    }

    @Test
    void save_whenStorageNotEmpty_savesResume() {
        storage.save(R4);
        assertEquals(R4, storage.get(UUID_4));
    }

    @Test
    void save_whenResumeExists_throwsResumeAlreadyExistsException() {
        assertThrows(ResumeAlreadyExistsException.class, () -> storage.save(R1));
    }

    @Test
    void save_whenStorageFull_throwsStorageOverflowException() {
        storage.clear();
        int limit = AbstractArrayStorage.STORAGE_LIMIT;

        for (int i = 0; i < limit; i++) {
            try {
                storage.save(new Resume("uuid" + i));
            } catch (StorageOverflowException e) {
                fail("Overflow happened too early at i: " + i);
            }
        }

        assertThrows(StorageOverflowException.class, () -> storage.save(new Resume(OVERFLOW)));
    }

    @Test
    void get_whenResumeExists_returnResume() {
        Resume actual = storage.get(UUID_1);
        assertEquals(R1, actual);
    }

    @Test
    void get_whenResumeNotExists_throwsResumeNotFoundException() {
        assertThrows(ResumeNotFoundException.class, () -> storage.get(MISSING));
    }

    @Test
    void update_whenResumeExists_updatedResume() {
        Resume updated = new Resume(UUID_1);

        storage.update(updated);

        assertSame(updated, storage.get(UUID_1));
    }

    @Test
    void update_whenResumeNotExists_throwsResumeNotFoundException() {
        Resume updated = new Resume(MISSING);
        assertThrows(ResumeNotFoundException.class, () -> storage.update(updated));
    }

    @Test
    void delete_whenResumeExists_deletedResume() {
        storage.delete(UUID_1);
        assertThrows(ResumeNotFoundException.class, () -> storage.get(UUID_1));
    }

    @Test
    void delete_whenResumeNotExists_throwsResumeNotFoundException() {
        assertThrows(ResumeNotFoundException.class, () -> storage.delete(MISSING));
    }

    @Test
    void size_whenStorageNotEmpty_returnsSize() {
        assertEquals(3, storage.size());
    }

    @Test
    void getAll_whenStorageNotEmpty_returnsAllResumes() {
        assertArrayEquals(new Resume[]{R1, R2, R3}, storage.getAll());
    }

    @Test
    void clear_whenStorageNotEmpty_clearsStorage() {
        assertEquals(3, storage.size());
        storage.clear();
        assertArrayEquals(new Resume[0], storage.getAll());
        assertEquals(0, storage.size());
    }
}
