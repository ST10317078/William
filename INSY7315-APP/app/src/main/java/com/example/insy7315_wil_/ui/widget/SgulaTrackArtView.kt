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
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import com.example.insy7315_wil_.R

class SgulaTrackArtView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val cornerRadius = resources.getDimension(R.dimen.sgula_radius_sm)
    private val stripeLight = ContextCompat.getColor(context, R.color.sgula_plum_100)
    private val stripeDark = ContextCompat.getColor(context, R.color.sgula_plum_200)

    private var artSize = resources.getDimensionPixelSize(R.dimen.sgula_track_art)

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SgulaTrackArtView,
            defStyleAttr,
            0,
        ).use { a ->
            artSize = a.getDimensionPixelSize(R.styleable.SgulaTrackArtView_sgulaArtSize, artSize)
        }

        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, cornerRadius)
            }
        }
        clipToOutline = true

        val period = 12f * resources.displayMetrics.density / 1.4142f
        paint.shader = LinearGradient(
            0f,
            0f,
            period,
            period,
            intArrayOf(stripeDark, stripeDark, stripeLight, stripeLight),
            floatArrayOf(0f, 0.5f, 0.5f, 1f),
            Shader.TileMode.REPEAT,
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(artSize, artSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }
}
