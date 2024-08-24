package com.example.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "com.example.common",
        "com.example.api"
    ]
)
@ConfigurationPropertiesScan(
    basePackages = [
        "com.example.common",
        "com.example.api"
    ]
)
class ApiApplication {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            runApplication<ApiApplication>(*args)
        }
    }
}