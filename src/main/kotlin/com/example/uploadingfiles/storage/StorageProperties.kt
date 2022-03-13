package com.example.uploadingfiles.storage

import org.springframework.boot.context.properties.*

@ConfigurationProperties("storage")
class StorageProperties {
    /**
     * Folder location for storing files
     */
    var location = "upload-dir"
}