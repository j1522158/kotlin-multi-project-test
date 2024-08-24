package com.example.api.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SampleController {
    @GetMapping("/")
    fun index(): String {
        return "root"
    }

    @GetMapping("/sample")
    fun home(): String {
        return "Sample Text Hello World"
    }
}