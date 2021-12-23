package com.sample.uistatesample.usecase

import com.sample.uistatesample.api.MyApi
import com.sample.uistatesample.data.ApiResult
import javax.inject.Inject

class AddNoteUseCase @Inject constructor(
    private val api: MyApi
) {
    suspend operator fun invoke(note: String): ApiResult<Unit> {
        return try {
            ApiResult.Success(api.addNote(note))
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}
