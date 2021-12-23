package com.sample.uistatesample.data

@JvmInline
value class OrderId(val value: String)

data class OrderInfo(
    val id: OrderId
)
