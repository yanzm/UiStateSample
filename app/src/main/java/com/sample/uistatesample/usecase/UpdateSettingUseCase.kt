package com.sample.uistatesample.usecase

import com.sample.uistatesample.api.MyApi
import com.sample.uistatesample.data.ApiResult
import com.sample.uistatesample.data.SettingId
import javax.inject.Inject

class UpdateSettingUseCase @Inject constructor(
    private val api: MyApi
) {
    suspend operator fun invoke(id: SettingId, checked: Boolean): ApiResult<Unit> {
        return try {
            ApiResult.Success(api.updateSetting(id, checked))
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}
