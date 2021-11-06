package com.jskaleel.vizhi_tamil.model

sealed class ContributorsResponse {
    data class Success(val contributors: List<Contributors>) : ContributorsResponse()

    data class Error(val message: String) : ContributorsResponse()
}
