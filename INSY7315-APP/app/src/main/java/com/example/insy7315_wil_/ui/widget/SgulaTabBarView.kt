package com.example.insy7315_wil_.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.insy7315_wil_.R

class SgulaTabBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val itemViews = mutableListOf<View>()
    private var items: List<NavItem> = emptyList()

    var onNavigate: ((String) -> Unit)? = null

    var selectedRoute: String? = null
        set(value) {
            field = value
            itemViews.forEachIndexed { index, view ->
                view.isSelected = items.getOrNull(index)?.route == value
            }
        }

    init {
        orientation = HORIZONTAL
        setBackgroundColor(ContextCompat.getColor(context, R.color.sgula_surface))
        elevation = resources.getDimension(R.dimen.sgula_elevation_md)
        outlineAmbientShadowColor = ContextCompat.getColor(context, R.color.sgula_shadow)
        outlineSpotShadowColor = ContextCompat.getColor(context, R.color.sgula_shadow)
    }

    fun setItems(navItems: List<NavItem>, selected: String? = null) {
        items = navItems
        removeAllViews()
        itemViews.clear()

        val inflater = LayoutInflater.from(context)
        navItems.forEach { item ->
            val view = inflater.inflate(R.layout.view_tab_bar_item, this, false)
            view.layoutParams = LayoutParams(0, LayoutParams.MATCH_PARENT, 1f)

            view.findViewById<TextView>(R.id.tab_bar_label).text = item.label

            val icon = view.findViewById<ImageView>(R.id.tab_bar_icon)
            val dot = view.findViewById<View>(R.id.tab_bar_dot)
            if (item.iconRes != 0) {
                icon.setImageResource(item.iconRes)
                icon.isVisible = true
                dot.isVisible = false
            }

            view.setOnClickListener { onNavigate?.invoke(item.route) }

            addView(view)
            itemViews += view
        }

        selectedRoute = selected ?: navItems.firstOrNull()?.route
    }
}
