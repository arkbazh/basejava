package org.example.exception;

public class ResumeAlreadyExistsException extends StorageException {
    public ResumeAlreadyExistsException(String uuid) {
        super("Resume already exists: " + uuid);
    }
}
