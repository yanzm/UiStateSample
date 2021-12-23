package com.sample.uistatesample.usecase

import com.sample.uistatesample.api.MyApi
import com.sample.uistatesample.data.ApiResult
import javax.inject.Inject

class UpdateNicknameUseCase @Inject constructor(
    private val api: MyApi
) {
    suspend operator fun invoke(nickname: String): ApiResult<Unit> {
        return try {
            ApiResult.Success(api.updateNickname(nickname))
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}
