package com.example.insy7315_wil_.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.use
import com.example.insy7315_wil_.R

class SgulaStreakChipView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val daysView: TextView

    var days: Int = 0
        set(value) {
            field = value
            daysView.text = value.toString()
        }

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setBackgroundResource(R.drawable.sgula_chip_accent_soft)
        val h = resources.getDimensionPixelSize(R.dimen.sgula_space_4)
        val v = resources.getDimensionPixelSize(R.dimen.sgula_space_2)
        setPadding(h, v, h, v)

        inflate(context, R.layout.view_streak_chip, this)
        daysView = findViewById(R.id.streak_days)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SgulaStreakChipView,
            defStyleAttr,
            0,
        ).use { a ->
            days = a.getInt(R.styleable.SgulaStreakChipView_sgulaDays, 0)
        }
    }
}
