package com.example.insy7315_wil_.ui.gallery

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.insy7315_wil_.R
import com.example.insy7315_wil_.ui.widget.SgulaModal
import com.google.android.material.button.MaterialButton

class GalleryFragment : Fragment(R.layout.fragment_gallery) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<MaterialButton>(R.id.gallery_show_modal).setOnClickListener {
            SgulaModal.show(
                context = requireContext(),
                title = getString(R.string.sgula_demo_modal_title),
                body = getString(R.string.sgula_demo_modal_body),
                confirmText = getString(R.string.sgula_delete),
                onConfirm = {},
                dismissText = getString(R.string.sgula_cancel),
                destructive = true,
            )
        }
    }
}
