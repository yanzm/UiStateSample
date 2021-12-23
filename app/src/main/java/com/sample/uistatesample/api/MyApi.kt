package com.sample.uistatesample.api

import com.sample.uistatesample.data.*

interface MyApi {

    suspend fun getDogs(): List<Dog>

    suspend fun getDogs(lastId: DogId?): PagingData<Dog>

    suspend fun getOrderInfo(orderId: OrderId): OrderInfo

    suspend fun cancelOrder(orderId: OrderId)

    suspend fun getSettingInfo(): List<SettingInfo>

    suspend fun updateSetting(id: SettingId, checked: Boolean)

    suspend fun addNote(note: String)

    suspend fun getNickname(): String

    suspend fun updateNickname(nickname: String)
}
