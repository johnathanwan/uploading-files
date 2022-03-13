package com.example.uploadingfiles.storage

open class StorageException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : RuntimeException()