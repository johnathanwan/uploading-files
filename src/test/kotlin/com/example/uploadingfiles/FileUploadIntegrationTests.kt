package com.example.uploadingfiles

import com.example.uploadingfiles.storage.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.mockito.*
import org.mockito.BDDMockito.*
import org.mockito.Mockito.validateMockitoUsage
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.test.context.*
import org.springframework.boot.test.mock.mockito.*
import org.springframework.boot.test.web.client.*
import org.springframework.boot.web.server.*
import org.springframework.core.io.*
import org.springframework.http.*
import org.springframework.util.*
import org.springframework.web.multipart.*


@Suppress("SpellCheckingInspection")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileUploadIntegrationTests {

    @Autowired
    private val restTemplate: TestRestTemplate? = null

    @MockBean
    private var storageService: StorageService? = null

    @LocalServerPort
    private val port: Int? = null

    /**
     * Check the following link for why this was written:
     * https://stackoverflow.com/questions/59230041/argumentmatchers-any-must-not-be-null
     */
    private fun <T> any(type: Class<T>): T = Mockito.any(type)

    @Test
    fun `should upload file`() {

        val resource = ClassPathResource("testupload.txt")
        val map = LinkedMultiValueMap<String, Any>()
        map.add("file", resource)
        val response: ResponseEntity<String> = restTemplate!!.postForEntity("/", map, String::class)

        assertThat(response.statusCode).isEqualByComparingTo(HttpStatus.FOUND)
        assertThat(response.headers.location.toString()).startsWith("http://localhost:$port/")
        then(storageService).should()!!.store(any(MultipartFile::class.java))


    }

    @Test
    fun `should download file`() {
        val resource = ClassPathResource("testupload.txt")
        given(storageService!!.loadAsResource("testupload.txt")).willReturn(resource)

        val response: ResponseEntity<String> = restTemplate!!
            .getForEntity("/files/{filename}", String::class.java, "testupload.txt")

        assertThat(response.statusCodeValue).isEqualTo(200)
        assertThat(response.headers.getFirst(HttpHeaders.CONTENT_DISPOSITION))
            .isEqualTo("attachment; filename=\"testupload.txt\"")
        assertThat(response.body).isEqualTo("Spring Framework")
    }

    /**
     * Mockito reports errors in the next Mockito method call which may not be in the same test method.
     * And the simpler the tests the more likely an error is to show up in the next test!
     * Here is an easy fix that will ensure errors appear in the correct test method:
     * Check the following link for more details:
     * https://stackoverflow.com/questions/15904584/mockito-gives-unfinishedverificationexception-when-it-seems-ok
     */
    @AfterAll
    fun validate() {
        validateMockitoUsage()
    }
}