package com.jskaleel.vizhi_tamil.domain.mapper

import com.jskaleel.vizhi_tamil.core.model.ApiResultMapper
import com.jskaleel.vizhi_tamil.data.model.ConfigResponseDTO

class ConfigMapper : ApiResultMapper<ConfigResponseDTO, Boolean>() {
    override fun onSuccess(input: ConfigResponseDTO): Boolean {
        return input.updateData
    }

    override fun onError(error: String): String {
        return error
    }
}