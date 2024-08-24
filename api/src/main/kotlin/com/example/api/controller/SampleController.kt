package com.example.api.controller

import com.example.common.model.SampleModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class SampleController {
    @GetMapping("/")
    fun index(): String {
        return "root"
    }

    @GetMapping("/sample")
    fun sample(): String {
        return "Sample Text Hello World"
    }

    @GetMapping("/sample/param")
    fun sampleParam(): SampleModel {
        return SampleModel(
            sampleStr = "sampleStr",
            sampleNumber = BigDecimal.ZERO.toLong()
        )
    }
}