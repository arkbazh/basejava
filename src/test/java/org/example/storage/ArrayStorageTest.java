package org.example.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.example.exception.ResumeNotFoundException;
import org.example.model.Resume;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArrayStorageTest {
    private static final String UUID_1 = "uuid1";
    private static final String UUID_2 = "uuid2";
    private static final String UUID_3 = "uuid3";

    private Storage storage;

    @BeforeEach
    void setUp() {
        storage = new ArrayStorage();
    }

    @Test
    void delete_whenFirstDeleted_swapsLastIntoDeletedSlot() {
        Resume r1 = new Resume(UUID_1);
        Resume r2 = new Resume(UUID_2);
        Resume r3 = new Resume(UUID_3);
        seed(r1, r2, r3);

        int sizeBefore = storage.size();

        storage.delete(UUID_1);

        assertThrows(ResumeNotFoundException.class, () -> storage.get(UUID_1));
        Resume[] after = storage.getAll();
        assertEquals(sizeBefore - 1, after.length);
        assertArrayEquals(new Resume[]{r3, r2}, after);
        assertEquals(storage.size(), after.length);
    }

    @Test
    void delete_whenMiddleDeleted_swapsLastIntoDeletedSlot() {
        Resume r1 = new Resume(UUID_1);
        Resume r2 = new Resume(UUID_2);
        Resume r3 = new Resume(UUID_3);
        seed(r1, r2, r3);

        int sizeBefore = storage.size();

        storage.delete(UUID_2);

        assertThrows(ResumeNotFoundException.class, () -> storage.get(UUID_2));
        Resume[] after = storage.getAll();
        assertEquals(sizeBefore - 1, after.length);
        assertArrayEquals(new Resume[]{r1, r3}, after);
        assertEquals(storage.size(), after.length);
    }

    @Test
    void delete_whenLastDeleted_removesLast_prefixOrderUnchanged() {
        Resume r1 = new Resume(UUID_1);
        Resume r2 = new Resume(UUID_2);
        Resume r3 = new Resume(UUID_3);
        seed(r1, r2, r3);

        int sizeBefore = storage.size();

        storage.delete(UUID_3);

        assertThrows(ResumeNotFoundException.class, () -> storage.get(UUID_3));
        Resume[] after = storage.getAll();
        assertEquals(sizeBefore - 1, after.length);
        assertArrayEquals(new Resume[]{r1, r2}, after);
        assertEquals(storage.size(), after.length);
    }

    @Test
    void delete_whenOnlyElementDeleted_becomesEmpty() {
        Resume r1 = new Resume(UUID_1);
        seed(r1);

        int sizeBefore = storage.size();

        storage.delete(UUID_1);

        assertThrows(ResumeNotFoundException.class, () -> storage.get(UUID_1));
        Resume[] after = storage.getAll();
        assertEquals(sizeBefore - 1, after.length);
        assertArrayEquals(new Resume[0], after);
        assertEquals(storage.size(), after.length);
    }

    private void seed(Resume... rs) {
        for (Resume r : rs) storage.save(r);
    }
}
