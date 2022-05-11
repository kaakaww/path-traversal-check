package com.stackhawk.pathtraversalcheck

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PathTraversalCheckApplication

fun main(args: Array<String>) {
	runApplication<PathTraversalCheckApplication>(*args)
}
