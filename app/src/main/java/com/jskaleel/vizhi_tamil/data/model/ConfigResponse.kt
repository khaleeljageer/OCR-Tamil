package com.jskaleel.vizhi_tamil.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ConfigResponse(
    @SerialName("update_data")
    val updateData: Boolean,
    val revision: Int
)