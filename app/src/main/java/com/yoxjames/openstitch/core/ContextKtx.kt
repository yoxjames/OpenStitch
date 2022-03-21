package com.yoxjames.openstitch.core

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import com.yoxjames.openstitch.OpenStitchActivity

fun <A : ComponentActivity> Context.getActivity(): A = when (this) {
    is ComponentActivity -> this as? A
            ?: throw IllegalStateException("Cannot cast ${this::class} to specified AppCompatActivity")
    is ContextWrapper -> baseContext.getActivity() as? A
            ?: throw IllegalStateException("Cannot cast ${this::class} to specified AppCompatActivity")
    else -> throw IllegalStateException("Context cannot be null")
}

val Context.openStitchActivity: OpenStitchActivity get() = getActivity()