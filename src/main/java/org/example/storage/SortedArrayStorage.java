package org.example.storage;

import java.util.Arrays;
import org.example.model.Resume;

public class SortedArrayStorage extends AbstractArrayStorage {
    @Override
    protected int findIndex(String uuid) {
        Resume r = new Resume(uuid);
        return Arrays.binarySearch(storage, 0, size, r);
    }

    @Override
    protected void insertResume(Resume r, int index) {
        int insertPos = ~index;
        System.arraycopy(storage, insertPos, storage, insertPos + 1, size - insertPos);
        storage[insertPos] = r;
    }

    @Override
    protected void deleteResume(int index) {
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(storage, index + 1, storage, index, numMoved);
        }
        size--;
    }
}
