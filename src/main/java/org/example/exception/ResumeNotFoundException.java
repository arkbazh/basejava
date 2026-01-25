package org.example.exception;

public class ResumeNotFoundException extends StorageException {
    public ResumeNotFoundException(String uuid) {
        super("Resume not found: " + uuid);
    }
}
