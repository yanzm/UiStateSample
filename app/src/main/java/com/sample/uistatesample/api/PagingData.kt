package com.sample.uistatesample.api

data class PagingData<T>(val list: List<T>, val hasNext: Boolean)
