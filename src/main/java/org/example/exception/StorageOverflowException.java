package org.example.exception;

public class StorageOverflowException extends StorageException {
    public StorageOverflowException() {
        super("Storage overflow");
    }
}
