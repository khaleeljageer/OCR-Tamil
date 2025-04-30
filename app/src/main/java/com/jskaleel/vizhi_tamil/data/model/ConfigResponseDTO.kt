package com.jskaleel.vizhi_tamil.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ConfigResponseDTO(
    @SerialName("update_data")
    val updateData: Boolean,
    val revision: Int
)