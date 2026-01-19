package org.example.exception;

public class NotExistStorageException extends StorageException {
    public NotExistStorageException(String uuid) {
        super("Resume not found: " + uuid);
    }
}
