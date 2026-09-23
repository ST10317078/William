package com.example.insy7315_wil_.ui.widget

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.use
import com.example.insy7315_wil_.R

class SgulaMoodScaleView @JvmOverloads constructor(
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
        orientation = HORIZONTAL

        var labels = resources.getStringArray(R.array.sgula_mood_labels).toList()
        var initial = NO_SELECTION

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SgulaMoodScaleView,
            defStyleAttr,
            0,
        ).use { a ->
            val entriesId = a.getResourceId(R.styleable.SgulaMoodScaleView_sgulaEntries, 0)
            if (entriesId != 0) labels = resources.getStringArray(entriesId).toList()
            initial = a.getInt(R.styleable.SgulaMoodScaleView_sgulaSelectedIndex, NO_SELECTION)
        }

        buildItems(labels)
        selectedIndex = if (initial in labels.indices) initial else null
    }

    private fun buildItems(labels: List<String>) {
        val inflater = LayoutInflater.from(context)
        val tones = resources.obtainTypedArray(R.array.sgula_mood_tones)
        val gap = resources.getDimensionPixelSize(R.dimen.sgula_space_2)

        labels.forEachIndexed { index, label ->
            val item = inflater.inflate(R.layout.view_mood_item, this, false)
            val params = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
            if (index > 0) params.marginStart = gap
            item.layoutParams = params

            item.findViewById<TextView>(R.id.mood_label).text = label

            val toneIndex = index.coerceIn(0, tones.length() - 1)
            item.findViewById<View>(R.id.mood_dot).backgroundTintList =
                ColorStateList.valueOf(tones.getColor(toneIndex, 0))

            item.setOnClickListener {
                selectedIndex = index
                onSelect?.invoke(index)
            }

            addView(item)
            itemViews += item
        }
        tones.recycle()
    }

    private companion object {
        const val NO_SELECTION = -1
    }
}
