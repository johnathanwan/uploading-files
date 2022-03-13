package com.example.uploadingfiles


import com.example.uploadingfiles.storage.*
import org.hamcrest.*
import org.junit.jupiter.api.*
import org.mockito.BDDMockito.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.test.autoconfigure.web.servlet.*
import org.springframework.boot.test.context.*
import org.springframework.boot.test.mock.mockito.*
import org.springframework.mock.web.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.nio.file.*
import java.util.stream.*

@AutoConfigureMockMvc
@SpringBootTest
class FileUploadTests {
    @Autowired
    private val mvc: MockMvc? = null

    @MockBean
    private val storageService: StorageService? = null

    @Test
    fun `should list all files`() {
        given(storageService!!.loadAll()).willReturn(
            Stream.of(Paths.get("first.txt"), Paths.get("second.txt")))
        mvc!!.perform(get("/")).andExpect(status().isOk)
            .andExpect(
                model().attribute("files",
                Matchers.contains("http://localhost/files/first.txt", "http://localhost/files/second.txt")))
    }

    @Test
    fun `should save uploaded file`() {
        val multipartFile = MockMultipartFile(
            "file", "test.txt", "text/plain", "Spring Framework".toByteArray())
        mvc!!.perform(multipart("/").file(multipartFile))
            .andExpect(status().isFound)
            .andExpect(header().string("Location", "/"))
        then(storageService).should()?.store(multipartFile)
    }

    @Test
    fun `should 404 when missing file`() {
        given(storageService!!.loadAsResource("test.txt")).willThrow(
            StorageFileNotFoundException::class.java)
        mvc!!.perform(get("/files/test.txt"))
            .andExpect(status().isNotFound)
    }
}