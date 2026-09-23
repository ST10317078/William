package com.example.insy7315_wil_.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import com.example.insy7315_wil_.R

class AffirmationCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val cornerRadius = resources.getDimension(R.dimen.sgula_radius_lg)
    private val gradientStart = ContextCompat.getColor(context, R.color.sgula_plum_100)
    private val gradientEnd = ContextCompat.getColor(context, R.color.sgula_accent_100)

    private val eyebrowView: TextView
    private val affirmationView: TextView

    init {
        inflate(context, R.layout.view_affirmation_card, this)
        eyebrowView = findViewById(R.id.affirmation_eyebrow)
        affirmationView = findViewById(R.id.affirmation_text)

        setWillNotDraw(false)

        elevation = resources.getDimension(R.dimen.sgula_elevation_md)
        outlineAmbientShadowColor = ContextCompat.getColor(context, R.color.sgula_shadow)
        outlineSpotShadowColor = ContextCompat.getColor(context, R.color.sgula_shadow)

        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, cornerRadius)
            }
        }
        clipToOutline = true

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AffirmationCardView,
            defStyleAttr,
            0,
        ).use { a ->
            eyebrowView.text = a.getString(R.styleable.AffirmationCardView_sgulaEyebrow)
                ?: context.getString(R.string.sgula_affirmation_eyebrow)
            affirmationView.text = a.getString(R.styleable.AffirmationCardView_sgulaAffirmation)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w == 0 || h == 0) return
        paint.shader = LinearGradient(
            0f,
            0f,
            w * 0.36f,
            h.toFloat(),
            gradientStart,
            gradientEnd,
            Shader.TileMode.CLAMP,
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

    fun setAffirmation(text: CharSequence?) {
        affirmationView.text = text
    }

    fun setEyebrow(text: CharSequence?) {
        eyebrowView.text = text
    }
}
