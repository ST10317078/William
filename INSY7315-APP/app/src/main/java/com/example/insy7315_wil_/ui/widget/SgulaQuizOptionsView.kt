package com.example.insy7315_wil_.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.use
import com.example.insy7315_wil_.R

class SgulaQuizOptionsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val itemViews = mutableListOf<View>()

    var onSelect: ((Int) -> Unit)? = null

    var selectedIndex: Int? = null
        set(value) {
            field = value
            itemViews.forEachIndexed { index, view -> view.isSelected = index == value }
        }

    init {
        orientation = VERTICAL

        var options: List<String> = emptyList()
        var initial = NO_SELECTION

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SgulaQuizOptionsView,
            defStyleAttr,
            0,
        ).use { a ->
            val entriesId = a.getResourceId(R.styleable.SgulaQuizOptionsView_sgulaEntries, 0)
            if (entriesId != 0) options = resources.getStringArray(entriesId).toList()
            initial = a.getInt(R.styleable.SgulaQuizOptionsView_sgulaSelectedIndex, NO_SELECTION)
        }

        if (options.isNotEmpty()) {
            setOptions(options, if (initial in options.indices) initial else null)
        }
    }

    fun setOptions(options: List<String>, selected: Int? = null) {
        removeAllViews()
        itemViews.clear()

        val inflater = LayoutInflater.from(context)
        val gap = resources.getDimensionPixelSize(R.dimen.sgula_space_3)

        options.forEachIndexed { index, option ->
            val item = inflater.inflate(R.layout.view_quiz_option, this, false)
            val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            if (index > 0) params.topMargin = gap
            item.layoutParams = params

            item.findViewById<TextView>(R.id.quiz_text).text = option
            item.setOnClickListener {
                selectedIndex = index
                onSelect?.invoke(index)
            }

            addView(item)
            itemViews += item
        }

        selectedIndex = selected
    }

    private companion object {
        const val NO_SELECTION = -1
    }
}
