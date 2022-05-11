package com.stackhawk.pathtraversalcheck

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import com.stackhawk.pathtraversalcheck.HelloController.Companion.BASE_PATH
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class PathTraversalCheckApplicationTests(@Autowired val mockMvc: MockMvc) {

    @BeforeEach
    fun beforeEach() {
        val basePath = Paths.get(BASE_PATH)
        val backup = Paths.get("$basePath.bak")
        val file = "$basePath/hello"
        Files.deleteIfExists(Paths.get(file))
        Files.deleteIfExists(basePath)
        Files.deleteIfExists(backup)

        Files.createDirectory(basePath)
        File(file).writeText("Hello World")
        Files.copy(basePath, backup)
    }

    @Test
    fun `test paths`() {
        getPath("/hello", HttpStatus.OK)
        getPath("/../usr_content.bak/hello", HttpStatus.INTERNAL_SERVER_ERROR)
        getPath("/usr_content.bak/hello", HttpStatus.INTERNAL_SERVER_ERROR)
        getPath("/../usr_content/hello", HttpStatus.INTERNAL_SERVER_ERROR)
        getPath("hello", HttpStatus.INTERNAL_SERVER_ERROR)
    }

    fun getPath(path: String, status: HttpStatus) {
        mockMvc.perform(MockMvcRequestBuilders.get("/file-check?filePath=$path"))
            .andExpect(MockMvcResultMatchers.status().`is`(status.value()))
    }
}
