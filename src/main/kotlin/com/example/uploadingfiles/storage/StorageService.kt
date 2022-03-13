package com.example.uploadingfiles.storage

import org.springframework.core.io.*
import org.springframework.web.multipart.*
import java.nio.file.*
import java.util.stream.*

interface StorageService {
    fun init()
    fun store(file: MultipartFile)
    fun loadAll(): Stream<Path>
    fun load(filename: String): Path
    fun loadAsResource(filename: String): Resource
    fun deleteAll()
}