package com.sample.uistatesample.data

@JvmInline
value class DogId(val value: String)

data class Dog(val id: DogId)
