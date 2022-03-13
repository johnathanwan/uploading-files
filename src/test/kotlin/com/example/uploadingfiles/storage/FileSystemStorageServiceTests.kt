package com.example.uploadingfiles.storage

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.condition.*
import org.springframework.http.MediaType.*
import org.springframework.mock.web.*
import java.nio.file.*
import java.util.*
import kotlin.math.*


class FileSystemStorageServiceTests {

    private val properties = StorageProperties()
    private lateinit var service: FileSystemStorageService

    @BeforeEach
    fun init() {
        properties.location = "target/files/${abs(Random().nextLong())}"
        service = FileSystemStorageService(properties)
        service.init()
    }

    @Test
    fun `load non existent`() {
        assertThat(service.load("foo.txt")).doesNotExist()
    }

    @Test
    fun `save and load`() {
        service.store(MockMultipartFile("foo", "foo.txt", TEXT_PLAIN_VALUE,
            "Hello, World".toByteArray()))
        assertThat(service.load("foo.txt")).exists()
    }

    @Test
    fun `save relative path not permitted`() {
        assertThrows<StorageException> {
            service.store(MockMultipartFile("foo", "../foo.txt", TEXT_PLAIN_VALUE,
                "Hello, World".toByteArray()))
        }
    }

    @Test
    fun `save absolute path not permitted`() {
        assertThrows<StorageException> {
            service.store(MockMultipartFile("foo", "/etc/passwd", TEXT_PLAIN_VALUE,
                "Hello, World".toByteArray()))
        }
    }

    @Test
    @EnabledOnOs(OS.MAC)
    fun `save absolute path in file name permitted`() {
        val fileName = "\\etc\\passwd"
        service.store(MockMultipartFile(fileName, fileName, TEXT_PLAIN_VALUE, "Hello, World".toByteArray()))
        assertTrue(Files.exists(Paths.get(properties.location).resolve(Paths.get(fileName))))
    }

    @Test
    fun `save permitted`() {
        service.store(MockMultipartFile("foo", "bar/../foo.txt", TEXT_PLAIN_VALUE,
            "Hello, World".toByteArray()))
    }
}