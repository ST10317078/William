package com.example.insy7315_wil_.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListPopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import androidx.core.view.isVisible
import com.example.insy7315_wil_.R

class SgulaDropdownFieldView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val labelView: TextView
    private val anchorView: LinearLayout
    private val valueView: TextView
    private val chevronView: ImageView
    private val errorView: TextView

    private var options: List<String> = emptyList()
    private var popup: ListPopupWindow? = null

    var onSelect: ((String) -> Unit)? = null

    var selection: String?
        get() = valueView.text?.toString()
        set(value) {
            valueView.text = value
        }

    var error: CharSequence? = null
        set(value) {
            field = value
            applyError()
        }

    init {
        orientation = VERTICAL
        inflate(context, R.layout.view_dropdown_field, this)
        labelView = findViewById(R.id.dropdown_label)
        anchorView = findViewById(R.id.dropdown_anchor)
        valueView = findViewById(R.id.dropdown_value)
        chevronView = findViewById(R.id.dropdown_chevron)
        errorView = findViewById(R.id.dropdown_error)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SgulaDropdownFieldView,
            defStyleAttr,
            0,
        ).use { a ->
            val label = a.getString(R.styleable.SgulaDropdownFieldView_sgulaLabel)
            labelView.text = label
            labelView.isVisible = !label.isNullOrEmpty()

            val entriesId = a.getResourceId(R.styleable.SgulaDropdownFieldView_sgulaEntries, 0)
            if (entriesId != 0) {
                options = resources.getStringArray(entriesId).toList()
            }

            valueView.text = a.getString(R.styleable.SgulaDropdownFieldView_sgulaSelection)
                ?: options.firstOrNull()

            error = a.getString(R.styleable.SgulaDropdownFieldView_sgulaError)
        }

        anchorView.setOnClickListener { showPopup() }
    }

    fun setOptions(values: List<String>, selected: String? = null) {
        options = values
        valueView.text = selected ?: values.firstOrNull()
    }

    private fun showPopup() {
        if (options.isEmpty()) return

        val window = ListPopupWindow(context).apply {
            anchorView = this@SgulaDropdownFieldView.anchorView
            width = this@SgulaDropdownFieldView.anchorView.width
            isModal = true
            setBackgroundDrawable(
                ContextCompat.getDrawable(context, R.drawable.sgula_popup_background)
            )
            setAdapter(ArrayAdapter(context, R.layout.item_dropdown, options))
            setOnItemClickListener { _, _, position, _ ->
                val picked = options[position]
                valueView.text = picked
                onSelect?.invoke(picked)
                dismiss()
            }
        }
        popup = window
        window.show()
    }

    private fun applyError() {
        val message = error
        val hasError = !message.isNullOrEmpty()

        val left = anchorView.paddingLeft
        val top = anchorView.paddingTop
        val right = anchorView.paddingRight
        val bottom = anchorView.paddingBottom
        anchorView.setBackgroundResource(
            if (hasError) R.drawable.sgula_field_background_error
            else R.drawable.sgula_field_background
        )
        anchorView.setPadding(left, top, right, bottom)

        errorView.text = message
        errorView.isVisible = hasError
    }
}
