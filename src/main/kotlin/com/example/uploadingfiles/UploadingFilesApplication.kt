package com.example.uploadingfiles

import com.example.uploadingfiles.storage.*
import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.boot.context.properties.*
import org.springframework.context.annotation.*

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties::class)
class UploadingFilesApplication {
    @Bean
    fun init(storageService: StorageService): CommandLineRunner {
        return CommandLineRunner {
            storageService.deleteAll()
            storageService.init()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<UploadingFilesApplication>(*args)
}