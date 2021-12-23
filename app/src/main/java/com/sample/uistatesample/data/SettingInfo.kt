package com.sample.uistatesample.data

@JvmInline
value class SettingId(val value: String)

data class SettingInfo(val id: SettingId, val name: String, val checked: Boolean)
