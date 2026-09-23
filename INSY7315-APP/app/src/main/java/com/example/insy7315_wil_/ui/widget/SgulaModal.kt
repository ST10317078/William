package com.example.insy7315_wil_.ui.widget

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import com.example.insy7315_wil_.R

object SgulaModal {

    fun show(
        context: Context,
        title: CharSequence,
        body: CharSequence,
        confirmText: CharSequence,
        onConfirm: () -> Unit,
        dismissText: CharSequence? = null,
        onDismiss: (() -> Unit)? = null,
        destructive: Boolean = false,
        icon: CharSequence? = null,
    ): Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_sgula_modal)
        dialog.setCanceledOnTouchOutside(true)

        dialog.window?.apply {
            setBackgroundDrawable(
                ColorDrawable(ContextCompat.getColor(context, R.color.sgula_scrim))
            )
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        dialog.findViewById<TextView>(R.id.modal_title).text = title
        dialog.findViewById<TextView>(R.id.modal_body).text = body

        val iconView = dialog.findViewById<TextView>(R.id.modal_icon)
        iconView.text = icon

        val dismissButton = dialog.findViewById<MaterialButton>(R.id.modal_dismiss)
        if (dismissText != null) {
            dismissButton.text = dismissText
            dismissButton.isVisible = true
            dismissButton.setOnClickListener {
                dialog.dismiss()
                onDismiss?.invoke()
            }
        }

        val confirmId = if (destructive) R.id.modal_confirm_destructive else R.id.modal_confirm
        val hiddenId = if (destructive) R.id.modal_confirm else R.id.modal_confirm_destructive
        dialog.findViewById<MaterialButton>(hiddenId).isVisible = false

        val confirmButton = dialog.findViewById<MaterialButton>(confirmId)
        confirmButton.text = confirmText
        confirmButton.isVisible = true
        confirmButton.setOnClickListener {
            dialog.dismiss()
            onConfirm()
        }

        dialog.findViewById<View>(R.id.modal_scrim).setOnClickListener { dialog.dismiss() }
        dialog.findViewById<View>(R.id.modal_card).setOnClickListener { }

        dialog.show()
        return dialog
    }
}
