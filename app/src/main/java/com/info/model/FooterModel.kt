package com.info.model

import android.view.ViewGroup
import androidx.databinding.BindingAdapter

/**
 * Created by Amit Gupta on 21,July,2021
 */
data class FooterModel(
    var isHome: Boolean = false,
    var isChat: Boolean = false,
    var isServices: Boolean = false,
    var isNotification: Boolean = false,
    var isMore: Boolean = false
)
