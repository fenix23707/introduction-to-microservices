package com.epam.resource.exception.storage;

public class FileStorageDeleteException extends FileStorageException{

    public FileStorageDeleteException(String bucket, String key) {
        super(String.format("Failed to delete from file storage: %s/%s", bucket, key));
    }
}
