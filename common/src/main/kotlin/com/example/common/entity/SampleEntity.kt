package com.example.common.entity

import jakarta.validation.constraints.NotBlank
import lombok.AllArgsConstructor
import lombok.Data

@AllArgsConstructor
@Data
class SampleEntity {
    @NotBlank
    private val id: Long = 0
    @NotBlank
    private val text: String = "text"
    @NotBlank
    private val description: String = "description"
}