package com.lowelltyner.rates

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(info = Info(title = "Rate REST API"))
class RateApplication

fun main(args: Array<String>) {
	runApplication<RateApplication>(*args)
}
