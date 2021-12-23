package com.sample.uistatesample.usecase

import com.sample.uistatesample.api.MyApi
import com.sample.uistatesample.data.ApiResult
import com.sample.uistatesample.data.OrderId
import javax.inject.Inject

class CancelOrderUseCase @Inject constructor(
    private val api: MyApi
) {
    suspend operator fun invoke(orderId: OrderId): ApiResult<Unit> {
        return try {
            ApiResult.Success(api.cancelOrder(orderId))
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}
