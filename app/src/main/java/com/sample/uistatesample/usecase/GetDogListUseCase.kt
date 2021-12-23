package com.sample.uistatesample.usecase

import com.sample.uistatesample.api.MyApi
import com.sample.uistatesample.data.ApiResult
import com.sample.uistatesample.data.Dog
import javax.inject.Inject

class GetDogListUseCase @Inject constructor(
    private val api: MyApi
) {
    suspend operator fun invoke(): ApiResult<List<Dog>> {
        return try {
            ApiResult.Success(api.getDogs())
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}
