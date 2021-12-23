package com.sample.uistatesample.usecase

import com.sample.uistatesample.api.MyApi
import com.sample.uistatesample.api.PagingData
import com.sample.uistatesample.data.ApiResult
import com.sample.uistatesample.data.Dog
import com.sample.uistatesample.data.DogId
import javax.inject.Inject

class GetDogListPagingUseCase @Inject constructor(
    private val api: MyApi
) {
    suspend operator fun invoke(lastId: DogId?): ApiResult<PagingData<Dog>> {
        return try {
            ApiResult.Success(api.getDogs(lastId))
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}
