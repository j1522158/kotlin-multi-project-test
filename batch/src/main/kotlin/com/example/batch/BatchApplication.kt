package com.example.batch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "com.example.common",
        "com.example.batch"
    ]
)
@ConfigurationPropertiesScan(
    basePackages = [
        "com.example.common",
        "com.example.batch"
    ]
)
class BatchApplication {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            runApplication<BatchApplication>(*args)
        }
    }
}