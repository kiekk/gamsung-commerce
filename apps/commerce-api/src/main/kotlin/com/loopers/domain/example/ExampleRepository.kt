package com.loopers.domain.example

interface ExampleRepository {
    fun find(id: Long): ExampleModel?
}
