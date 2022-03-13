package com.example.uploadingfiles

import com.example.uploadingfiles.storage.*
import org.springframework.core.io.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.*
import org.springframework.web.servlet.mvc.method.annotation.*
import org.springframework.web.servlet.mvc.support.*
import java.util.stream.*

@Controller
class FileUploadController(private val storageService: StorageService) {
    @GetMapping("/")
    @Throws(Exception::class)
    fun listUploadedFiles(model: Model): String {
        model.addAttribute("files", storageService.loadAll().map {
            MvcUriComponentsBuilder.fromMethodName(
                FileUploadController::class.java, "serveFile",
                it.fileName.toString()).build().toUri().toString()
        }.collect(Collectors.toList()))
        return "uploadForm"
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    fun serveFile(@PathVariable filename: String): ResponseEntity<Resource> {
        val file = storageService.loadAsResource(filename)
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + file.filename + "\"").body(file)
    }

    @Suppress("SpringMVCViewInspection")
    @PostMapping("/")
    fun handleFileUpload(@RequestParam("file") file: MultipartFile, redirectAttributes: RedirectAttributes): String {
        storageService.store(file)
        redirectAttributes.addFlashAttribute("message",
            "You successfully uploaded ${file.originalFilename }!")
        return "redirect:/"
    }

    @ExceptionHandler(StorageFileNotFoundException::class)
    fun handleStorageFileNotFound(exc: StorageFileNotFoundException): ResponseEntity<*> {
        return ResponseEntity.notFound().build<Any>()
    }
}