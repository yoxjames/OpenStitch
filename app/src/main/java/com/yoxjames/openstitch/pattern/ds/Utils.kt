package com.yoxjames.openstitch.pattern.ds

import retrofit2.Response

inline fun <reified T> Response<T>.unwrap(onSuccess: (T) -> Unit, onFailure: () -> Unit) {
    val body = body()
    when (isSuccessful && body != null) {
        true -> onSuccess(body)
        false -> onFailure()
    }
}