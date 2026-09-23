package com.example.insy7315_wil_.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.use
import com.example.insy7315_wil_.R

class SgulaTabsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val tabViews = mutableListOf<TextView>()

    var onSelect: ((Int) -> Unit)? = null

    var selectedIndex: Int = 0
        set(value) {
            field = value
            tabViews.forEachIndexed { index, tab -> tab.isSelected = index == value }
        }

    init {
        orientation = HORIZONTAL
        setBackgroundResource(R.drawable.sgula_tabs_track)
        val pad = resources.getDimensionPixelSize(R.dimen.sgula_space_1)
        setPadding(pad, pad, pad, pad)
        clipToPadding = false
        clipChildren = false

        var tabs: List<String> = emptyList()
        var initial = 0

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SgulaTabsView,
            defStyleAttr,
            0,
        ).use { a ->
            val entriesId = a.getResourceId(R.styleable.SgulaTabsView_sgulaEntries, 0)
            if (entriesId != 0) tabs = resources.getStringArray(entriesId).toList()
            initial = a.getInt(R.styleable.SgulaTabsView_sgulaSelectedIndex, 0)
        }

        if (tabs.isNotEmpty()) setTabs(tabs, initial)
    }

    fun setTabs(tabs: List<String>, selected: Int = 0) {
        removeAllViews()
        tabViews.clear()

        val inflater = LayoutInflater.from(context)
        val gap = resources.getDimensionPixelSize(R.dimen.sgula_space_1)

        tabs.forEachIndexed { index, label ->
            val tab = inflater.inflate(R.layout.view_tab_item, this, false) as TextView
            val params = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
            if (index > 0) params.marginStart = gap
            tab.layoutParams = params
            tab.text = label
            tab.setOnClickListener {
                selectedIndex = index
                onSelect?.invoke(index)
            }
            addView(tab)
            tabViews += tab
        }

        selectedIndex = selected.coerceIn(0, tabs.lastIndex)
    }
}
