package com.sample.uistatesample.usecase

import com.sample.uistatesample.api.MyApi
import com.sample.uistatesample.data.ApiResult
import com.sample.uistatesample.data.SettingInfo
import javax.inject.Inject

class GetSettingInfoUseCase @Inject constructor(
    private val api: MyApi
) {
    suspend operator fun invoke(): ApiResult<List<SettingInfo>> {
        return try {
            ApiResult.Success(api.getSettingInfo())
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}
