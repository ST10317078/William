package com.example.insy7315_wil_.ui.widget

import androidx.annotation.DrawableRes

data class NavItem(
    val route: String,
    val label: String,
    @DrawableRes val iconRes: Int = 0,
)
