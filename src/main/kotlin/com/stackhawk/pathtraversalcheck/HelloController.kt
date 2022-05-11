package com.stackhawk.pathtraversalcheck

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {
    @GetMapping("/")
    fun index(): String = "Greetings from Spring Boot!"

    @GetMapping("/file")
    fun fileTraversal(
        filePath: String
    ): String = String(Files.readAllBytes(File(filePath).toPath())) // Don't ever do this

    @GetMapping("/file-check")
    fun fileTraversalCheck(
        filePath: String
    ): String = getContent(filePath)

    companion object {
        const val BASE_PATH = "/Users/topher"
        val VALID_PATHS = setOf("/.vimrc")

        val logger = LoggerFactory.getLogger(HelloController::class.java)
    }

    @Throws(IOException::class)
    fun getContent(path: String): String {
        val normalizedPath: String = Paths.get(path).normalize().toString()
        logger.info("Normalized Path $normalizedPath")

        return if (VALID_PATHS.contains(normalizedPath)) {
            val file = File(BASE_PATH, normalizedPath)
            logger.info("Canonical Path ${file.canonicalPath}")

            if (file.canonicalPath.startsWith(BASE_PATH)) {
                String(Files.readAllBytes(file.toPath()))
            } else {
                "Access Error"
            }
        } else {
            "Access Error"
        }
    }
}