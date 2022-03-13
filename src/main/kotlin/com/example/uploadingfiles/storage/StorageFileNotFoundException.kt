package com.example.uploadingfiles.storage

class StorageFileNotFoundException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : StorageException()