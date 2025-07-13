package com.example.maintainer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MaintainerApplication

fun main(args: Array<String>) {
    runApplication<MaintainerApplication>(*args)
}
