package com.example.insy7315_wil_.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.insy7315_wil_.R

class SgulaTopNavView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val brandView: TextView
    private val itemsContainer: LinearLayout
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
        setBackgroundColor(ContextCompat.getColor(context, R.color.sgula_surface))
        elevation = resources.getDimension(R.dimen.sgula_elevation_sm)
        outlineAmbientShadowColor = ContextCompat.getColor(context, R.color.sgula_shadow)
        outlineSpotShadowColor = ContextCompat.getColor(context, R.color.sgula_shadow)

        inflate(context, R.layout.view_top_nav, this)
        brandView = findViewById(R.id.top_nav_brand)
        itemsContainer = findViewById(R.id.top_nav_items)
    }

    fun setBrand(text: CharSequence?) {
        brandView.text = text
    }

    fun setItems(navItems: List<NavItem>, selected: String? = null) {
        items = navItems
        itemsContainer.removeAllViews()
        itemViews.clear()

        val inflater = LayoutInflater.from(context)
        val gap = resources.getDimensionPixelSize(R.dimen.sgula_space_6)

        navItems.forEachIndexed { index, item ->
            val view = inflater.inflate(R.layout.view_top_nav_item, itemsContainer, false)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
            if (index > 0) params.marginStart = gap
            view.layoutParams = params

            view.findViewById<TextView>(R.id.top_nav_label).text = item.label
            view.setOnClickListener { onNavigate?.invoke(item.route) }

            itemsContainer.addView(view)
            itemViews += view
        }

        selectedRoute = selected ?: navItems.firstOrNull()?.route
    }
}
