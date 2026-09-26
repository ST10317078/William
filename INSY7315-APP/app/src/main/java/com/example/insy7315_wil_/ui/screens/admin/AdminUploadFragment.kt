package com.example.insy7315_wil_.ui.screens.admin

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast

import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

import com.example.insy7315_wil_.R
import com.example.insy7315_wil_.data.`Data classes`.FirebaseWellnessRepository
import com.example.insy7315_wil_.data.`Data classes`.MeditationCategory
import com.example.insy7315_wil_.ui.screens.redirectNonAdminFromAdminContent
import com.example.insy7315_wil_.ui.widget.SgulaDropdownFieldView
import com.example.insy7315_wil_.ui.widget.SgulaTextFieldView

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AdminUploadFragment : Fragment(R.layout.fragment_admin_upload) {

    private var selectedAudioUri: Uri? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val repository = FirebaseWellnessRepository()

        repository.checkAdmin(
            onResult = { isAdmin ->

                if (!isAdmin) {
                    redirectNonAdminFromAdminContent()
                } else {
                    setupUploadScreen(view)
                }
            },
            onError = {
                redirectNonAdminFromAdminContent()
            }
        )
    }

    /*
     * AUDIO FILE PICKER
     */
    private val audioPicker =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            selectedAudioUri = uri

            view?.findViewById<TextView>(
                R.id.admin_upload_file_name
            )?.text =
                uri?.lastPathSegment ?: "No file selected"
        }

    /*
     * SETUP ADMIN UPLOAD SCREEN
     */
    private fun setupUploadScreen(view: View) {

        /*
         * CHOOSE AUDIO BUTTON
         */
        val chooseButton =
            view.findViewById<MaterialButton>(
                R.id.admin_upload_choose_file
            )

        chooseButton.setOnClickListener {
            audioPicker.launch("audio/*")
        }

        /*
         * TITLE
         */
        val titleField =
            view.findViewById<SgulaTextFieldView>(
                R.id.admin_upload_title
            )

        /*
         * MAIN CATEGORY
         */
        val categoryField =
            view.findViewById<SgulaDropdownFieldView>(
                R.id.admin_upload_category
            )

        /*
         * WELLNESS CATEGORY
         */
        val wellnessField =
            view.findViewById<SgulaDropdownFieldView>(
                R.id.admin_upload_wellness_category
            )

        /*
         * BROADCAST DATE
         */
        val broadcastDateField =
            view.findViewById<TextInputEditText>(
                R.id.admin_upload_broadcast_date
            )

        /*
         * LOAD WELLNESS CATEGORIES
         */
        val repository = FirebaseWellnessRepository()

        var categories: List<MeditationCategory> = emptyList()

        wellnessField.onSelect = {
            wellnessField.error = null
        }

        repository.loadMeditationCategories()
            .addOnSuccessListener { snapshot ->

                categories = snapshot.documents
                    .map(repository::toMeditationCategory)
                    .sortedBy { it.name }

                wellnessField.setOptions(
                    categories.map { it.name }
                )
            }
            .addOnFailureListener {

                wellnessField.error =
                    "Couldn't load the categories"
            }

        /*
         * CALENDAR DATE PICKER
         */
        broadcastDateField.setOnClickListener {

            val calendar = Calendar.getInstance()

            val datePicker =
                DatePickerDialog(
                    requireContext(),

                    { _, year, month, dayOfMonth ->

                        val selectedDate =
                            Calendar.getInstance().apply {

                                set(
                                    year,
                                    month,
                                    dayOfMonth,
                                    0,
                                    0,
                                    0
                                )

                                set(
                                    Calendar.MILLISECOND,
                                    0
                                )
                            }

                        val dateFormat =
                            SimpleDateFormat(
                                "dd/MM/yyyy",
                                Locale.getDefault()
                            )

                        broadcastDateField.setText(
                            dateFormat.format(
                                selectedDate.time
                            )
                        )
                    },

                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

            datePicker.show()
        }

        /*
         * UPLOAD BUTTON
         */
        val uploadButton =
            view.findViewById<MaterialButton>(
                R.id.admin_upload_button
            )

        uploadButton.setOnClickListener {

            /*
             * AUDIO FILE VALIDATION
             */
            val uri = selectedAudioUri

            if (uri == null) {

                Toast.makeText(
                    requireContext(),
                    "Please choose an audio file",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            /*
             * TITLE VALIDATION
             */
            if (titleField.text.isBlank()) {

                titleField.error =
                    "Title is required"

                return@setOnClickListener
            }

            /*
             * MAIN CATEGORY VALIDATION
             */
            if (categoryField.selection.isNullOrBlank()) {

                categoryField.error =
                    "Category is required"

                return@setOnClickListener
            }

            /*
             * WELLNESS CATEGORY VALIDATION
             */
            val wellnessCategory =
                categories.firstOrNull {
                    it.name == wellnessField.selection
                }

            if (wellnessCategory == null) {

                wellnessField.error =
                    "Wellness category is required"

                return@setOnClickListener
            }

            /*
             * BROADCAST DATE VALIDATION
             */
            if (broadcastDateField.text
                    ?.toString()
                    ?.isBlank() != false
            ) {

                broadcastDateField.error =
                    "Broadcast date is required"

                return@setOnClickListener
            }

            /*
             * DISABLE BUTTON WHILE UPLOADING
             */
            uploadButton.isEnabled = false

            /*
             * UPLOAD AUDIO
             */
            repository.uploadAudioContent(

                uri = uri,

                title = titleField.text.trim(),

                category = categoryField.selection
                    .orEmpty(),

                categoryId = wellnessCategory.categoryId,

                broadcastDate =
                    broadcastDateField.text
                        ?.toString()
                        .orEmpty(),

                onSuccess = {

                    uploadButton.isEnabled = true

                    Toast.makeText(
                        requireContext(),
                        "Audio uploaded successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    /*
                     * CLEAR FORM
                     */
                    titleField.text = ""

                    selectedAudioUri = null

                    broadcastDateField.setText("")

                    view.findViewById<TextView>(
                        R.id.admin_upload_file_name
                    ).text =
                        "No file selected"
                },

                onError = { error ->

                    uploadButton.isEnabled = true

                    Toast.makeText(
                        requireContext(),
                        "Upload failed: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }
}