package com.example.uploadingfiles.storage

import org.springframework.core.io.*
import org.springframework.stereotype.*
import org.springframework.util.*
import org.springframework.web.multipart.*
import java.io.*
import java.net.*
import java.nio.file.*
import java.util.stream.*

@Service
class FileSystemStorageService(properties: StorageProperties) : StorageService {
    private val rootLocation = Paths.get(properties.location)

    override fun store(file: MultipartFile) {
        try {
            if (file.isEmpty) {
                throw StorageException("Failed to store empty file.")
            }
            val destinationFile = rootLocation.resolve(Paths.get(file.originalFilename!!)).normalize().toAbsolutePath()
            if (destinationFile.parent != rootLocation.toAbsolutePath()) {
                throw StorageException("Cannot store file outside current directory.")
            }
            /**
             * We can invoke the use function on any object which implements AutoCloseable or Closeable, just as with
             * try-with-resources in Java.
             * The method takes a lambda expression, executes it, and disposes of the resource of
             * (by calling close() on it) whenever execution leaves the block, either normally or with an exception.
             * So, in this case, after use, the InputStream is no longer usable, because Kotlin has automatically
             * closed it.
             */
            file.inputStream.use { Files.copy(it, destinationFile, StandardCopyOption.REPLACE_EXISTING) }
        } catch (e: IOException) {
            throw StorageException("Failed to store file.", e)
        }
    }

    override fun loadAll(): Stream<Path> {
        return try {
            Files.walk(rootLocation, 1)
                .filter { it != rootLocation }
                .map { rootLocation.relativize(it) }
        } catch (e: IOException) {
            throw StorageException("Failed to read stored files", e)
        }
    }

    override fun load(filename: String): Path {
        return rootLocation.resolve(filename)
    }

    override fun loadAsResource(filename: String): Resource {
        return try {
            val file = load(filename)
            val resource: Resource = UrlResource(file.toUri())
            if (resource.exists() || resource.isReadable) {
                resource
            } else {
                throw StorageFileNotFoundException("Could not read file: $filename")
            }
        } catch (e: MalformedURLException) {
            throw StorageFileNotFoundException("Could not read file: $filename", e)
        }
    }

    override fun deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile())
    }

    override fun init() {
        try {
            Files.createDirectory(rootLocation)
        } catch (e: IOException) {
            throw StorageException("Could not initialize storage", e)
        }
    }
}