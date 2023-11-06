package com.marzie.nourmohammadi.lib.model

import android.graphics.Bitmap

data class LuckyItem(
    var id: Int,
    var text: String? = null,
    var icon: Bitmap? = null,
    var color: Int = 0,
)