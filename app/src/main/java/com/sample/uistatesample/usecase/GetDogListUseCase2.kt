package com.sample.uistatesample.usecase

import com.sample.uistatesample.api.MyApi
import com.sample.uistatesample.data.Dog
import javax.inject.Inject

class GetDogListUseCase2 @Inject constructor(
    private val api: MyApi
) {
    suspend operator fun invoke(): Result<List<Dog>> {
        return runCatching { api.getDogs() }
    }
}
