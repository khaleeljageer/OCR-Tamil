package com.jskaleel.ocr_tamil.ui.contrib

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jskaleel.ocr_tamil.R
import com.jskaleel.ocr_tamil.model.Contributors
import com.jskaleel.ocr_tamil.model.ContributorsResponse
import com.jskaleel.ocr_tamil.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.*
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class ContributorsViewModel @Inject constructor(private val okHttpClient: OkHttpClient) :
    ViewModel() {

    private val _contributorsResponse = MutableLiveData<ContributorsResponse>()
    val contributorsResponse: MutableLiveData<ContributorsResponse> =
        _contributorsResponse

    private val _showProgress = MutableLiveData<Boolean>()
    val showProgress: MutableLiveData<Boolean> = _showProgress

    fun getContributors(context: Context) {
        _showProgress.value = true
        val request =
            Request.Builder()
                .url(Constants.GITHUB_BASE_API + Constants.CONTRIBUTORS_PATH)
                .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                _contributorsResponse.postValue(
                    ContributorsResponse.Error(
                        context.getString(
                            R.string.error_string
                        )
                    )
                )
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful && response.body != null) {
                    val body = response.body
                    if (body != null) {
                        val listType = object : TypeToken<List<Contributors>>() {}.type
                        val contributors: List<Contributors> =
                            Gson().fromJson(body.string(), listType)
                        _contributorsResponse.postValue(ContributorsResponse.Success(contributors))
                        _showProgress.postValue(false)
                    } else {
                        _contributorsResponse.postValue(
                            ContributorsResponse.Error(
                                context.getString(
                                    R.string.error_string
                                )
                            )
                        )
                    }
                } else {
                    _contributorsResponse.postValue(
                        ContributorsResponse.Error(
                            context.getString(
                                R.string.error_string
                            )
                        )
                    )
                }
            }
        })
    }
}