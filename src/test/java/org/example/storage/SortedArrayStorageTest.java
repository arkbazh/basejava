package org.example.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.example.exception.ResumeAlreadyExistsException;
import org.example.exception.ResumeNotFoundException;
import org.example.model.Resume;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SortedArrayStorageTest {
    private static final String UUID_1 = "uuid1";
    private static final String UUID_2 = "uuid2";
    private static final String UUID_3 = "uuid3";
    private static final String UUID_4 = "uuid4";
    private static final String UUID_5 = "uuid5";

    private Storage storage;

    @BeforeEach
    void setUp() {
        storage = new SortedArrayStorage();
    }

    @Test
    void save_whenInsertAtBeginning_keepsSortedOrder() {
        Resume r1 = new Resume(UUID_1);
        Resume r2 = new Resume(UUID_2);
        Resume r3 = new Resume(UUID_3);
        seed(r2, r3);

        int sizeBefore = storage.size();

        storage.save(r1);

        Resume[] after = storage.getAll();
        assertEquals(sizeBefore + 1, after.length);
        assertArrayEquals(new Resume[]{r1, r2, r3}, after);
        assertEquals(storage.size(), after.length);
    }

    @Test
    void save_whenInsertInMiddle_keepsSortedOrder() {
        Resume r1 = new Resume(UUID_1);
        Resume r2 = new Resume(UUID_2);
        Resume r3 = new Resume(UUID_3);
        seed(r1, r3);

        int sizeBefore = storage.size();

        storage.save(r2);

        Resume[] after = storage.getAll();
        assertEquals(sizeBefore + 1, after.length);
        assertArrayEquals(new Resume[]{r1, r2, r3}, after);
        assertEquals(storage.size(), after.length);
    }

    @Test
    void save_whenInsertAtEnd_keepsSortedOrder() {
        Resume r1 = new Resume(UUID_1);
        Resume r2 = new Resume(UUID_2);
        Resume r3 = new Resume(UUID_3);
        seed(r1, r2);

        int sizeBefore = storage.size();

        storage.save(r3);

        Resume[] after = storage.getAll();
        assertEquals(sizeBefore + 1, after.length);
        assertArrayEquals(new Resume[]{r1, r2, r3}, after);
        assertEquals(storage.size(), after.length);
    }

    @Test
    void save_whenMultipleInserts_randomOrder_resultsSorted() {
        Resume r1 = new Resume(UUID_1);
        Resume r2 = new Resume(UUID_2);
        Resume r3 = new Resume(UUID_3);
        Resume r4 = new Resume(UUID_4);
        Resume r5 = new Resume(UUID_5);
        seed(r4, r2, r5, r1, r3);

        Resume[] after = storage.getAll();
        assertArrayEquals(new Resume[]{r1, r2, r3, r4, r5}, after);
        assertEquals(storage.size(), after.length);
    }

    @Test
    void delete_whenOnlyElementDeleted_becomesEmpty() {
        storage.save(new Resume(UUID_1));

        storage.delete(UUID_1);

        assertThrows(ResumeNotFoundException.class, () -> storage.get(UUID_1));

        Resume[] after = storage.getAll();
        assertEquals(0, after.length);
        assertEquals(storage.size(), after.length);
    }

    @Test
    void delete_whenDeleteFirst_shiftsLeftAndKeepsSorted() {
        Resume r1 = new Resume(UUID_1);
        Resume r2 = new Resume(UUID_2);
        Resume r3 = new Resume(UUID_3);
        seed(r1, r2, r3);

        int sizeBefore = storage.size();

        storage.delete(UUID_1);

        assertThrows(ResumeNotFoundException.class, () -> storage.get(UUID_1));

        Resume[] after = storage.getAll();
        assertEquals(sizeBefore - 1, after.length);
        assertArrayEquals(new Resume[]{r2, r3}, after);
        assertEquals(storage.size(), after.length);
    }

    @Test
    void delete_whenDeleteMiddle_shiftsLeftAndKeepsSorted() {
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
    void delete_whenDeleteLast_keepsSorted() {
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

    private void seed(Resume... rs) {
        for (Resume r : rs) storage.save(r);
    }
}
