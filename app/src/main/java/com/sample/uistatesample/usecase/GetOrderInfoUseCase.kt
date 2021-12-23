package com.sample.uistatesample.usecase

import com.sample.uistatesample.api.MyApi
import com.sample.uistatesample.data.ApiResult
import com.sample.uistatesample.data.OrderId
import com.sample.uistatesample.data.OrderInfo
import javax.inject.Inject

class GetOrderInfoUseCase @Inject constructor(
    private val api: MyApi
) {
    suspend operator fun invoke(orderId: OrderId): ApiResult<OrderInfo> {
        return try {
            ApiResult.Success(api.getOrderInfo(orderId))
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}
