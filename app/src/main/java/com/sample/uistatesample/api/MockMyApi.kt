package com.sample.uistatesample.api

import com.sample.uistatesample.data.*
import kotlinx.coroutines.delay
import java.io.IOException
import java.util.*
import kotlin.random.Random

private const val successFetchDog = true
private const val successFetchDogPagingInitial = true
private const val successFetchDogPagingNext = true
private const val successFetchOrderInfo = true
private const val successCancelOrder = true
private const val successFetchSettingInfo = true
private const val successAddNote = true
private const val successFetchNickname = true
private const val successUpdateNickname = true

class MockMyApi : MyApi {

    override suspend fun getDogs(): List<Dog> {
        delay(1000)
        if (successFetchDog) {
            return buildList {
                repeat(20) {
                    add(
                        Dog(
                            id = DogId(UUID.randomUUID().toString())
                        )
                    )
                }
            }
        } else {
            throw IOException()
        }
    }

    override suspend fun getDogs(lastId: DogId?): PagingData<Dog> {
        delay(1000)
        if (lastId == null && successFetchDogPagingInitial || lastId != null && successFetchDogPagingNext) {
            if (Random.nextBoolean()) {
                return PagingData(
                    list = buildList {
                        repeat(20) {
                            add(
                                Dog(
                                    id = DogId(UUID.randomUUID().toString())
                                )
                            )
                        }
                    },
                    hasNext = true
                )
            } else {
                return PagingData(
                    list = buildList {
                        repeat(5) {
                            add(
                                Dog(
                                    id = DogId(UUID.randomUUID().toString())
                                )
                            )
                        }
                    },
                    hasNext = false
                )
            }
        } else {
            throw IOException()
        }
    }

    override suspend fun getOrderInfo(orderId: OrderId): OrderInfo {
        delay(1000)
        if (successFetchOrderInfo) {
            return OrderInfo(orderId)
        } else {
            throw IOException()
        }
    }

    override suspend fun cancelOrder(orderId: OrderId) {
        delay(1000)
        if (successCancelOrder) {
            return
        } else {
            throw IOException()
        }
    }

    override suspend fun getSettingInfo(): List<SettingInfo> {
        delay(1000)
        if (successFetchSettingInfo) {
            return listOf(settingA, settingB, settingC)
        } else {
            throw IOException()
        }
    }

    override suspend fun updateSetting(id: SettingId, checked: Boolean) {
        delay(1000)
        when (id) {
            SettingId("1") -> {
                settingA = settingA.copy(checked = checked)
            }
            SettingId("2") -> {
                settingB = settingB.copy(checked = checked)
            }
            SettingId("3") -> {
                settingC = settingC.copy(checked = checked)
            }
        }
    }

    override suspend fun addNote(note: String) {
        delay(1000)
        if (successAddNote) {
            return
        } else {
            throw IOException()
        }
    }

    override suspend fun getNickname(): String {
        delay(1000)
        if (successFetchNickname) {
            return nickname
        } else {
            throw IOException()
        }
    }

    override suspend fun updateNickname(name: String) {
        delay(1000)
        if (successUpdateNickname) {
            nickname = name
            return
        } else {
            throw IOException()
        }
    }
}

private var settingA = SettingInfo(id = SettingId("1"), name = "設定 A", checked = true)
private var settingB = SettingInfo(id = SettingId("2"), name = "設定 B", checked = false)
private var settingC = SettingInfo(id = SettingId("3"), name = "設定 C", checked = true)

private var nickname = "Compose"
