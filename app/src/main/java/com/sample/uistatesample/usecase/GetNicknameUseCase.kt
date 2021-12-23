package com.sample.uistatesample.usecase

import com.sample.uistatesample.api.MyApi
import com.sample.uistatesample.data.ApiResult
import com.sample.uistatesample.data.Dog
import javax.inject.Inject

class GetNicknameUseCase @Inject constructor(
    private val api: MyApi
) {
    suspend operator fun invoke(): ApiResult<String> {
        return try {
            ApiResult.Success(api.getNickname())
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}
