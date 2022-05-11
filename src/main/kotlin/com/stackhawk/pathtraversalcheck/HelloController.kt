package com.stackhawk.pathtraversalcheck

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    companion object {
        const val BASE_PATH = "/private/tmp/user_content"
        val VALID_PATHS = setOf("/hello")

        val logger = LoggerFactory.getLogger(HelloController::class.java)
    }

    @GetMapping("/")
    fun index(): String = "Greetings from Spring Boot!"

    @GetMapping("/file")
    fun fileTraversal(
        filePath: String
    ): String = String(Files.readAllBytes(File(filePath).toPath())) // Don't ever do this

    @GetMapping("/file-check")
    fun fileTraversalCheck(
        filePath: String
    ): ResponseEntity<String> = getContent(filePath)

    @Throws(IOException::class)
    fun getContent(path: String): ResponseEntity<String> {
        val normalizedPath: String = Paths.get(path).normalize().toString()
        logger.info("Normalized Path $normalizedPath")

        return if (VALID_PATHS.contains(normalizedPath)) {
            val file = File(BASE_PATH, normalizedPath)
            logger.info("Canonical Path ${file.canonicalPath}")

            if (file.canonicalPath.startsWith(BASE_PATH)) {
                ResponseEntity.ok(String(Files.readAllBytes(file.toPath())))
            } else {
                logger.info("Canonical path is not valid")
                ResponseEntity.internalServerError().body("Access Error")
            }
        } else {
            logger.info("Normalized Path is not valid")
            ResponseEntity.internalServerError().body("Access Error")
        }
    }
}