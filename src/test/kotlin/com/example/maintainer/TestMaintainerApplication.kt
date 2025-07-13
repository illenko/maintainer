package com.example.maintainer

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<MaintainerApplication>().with(TestcontainersConfiguration::class).run(*args)
}
