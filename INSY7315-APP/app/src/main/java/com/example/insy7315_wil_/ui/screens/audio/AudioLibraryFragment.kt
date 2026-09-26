package com.example.insy7315_wil_.ui.screens.audio

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.insy7315_wil_.R
import com.example.insy7315_wil_.databinding.FragmentAudioLibraryBinding
import com.example.insy7315_wil_.data.`Data classes`.AudioContent
import com.example.insy7315_wil_.data.`Data classes`.FirebaseWellnessRepository
import com.example.insy7315_wil_.ui.widget.SgulaTrackItemView

private val categories = listOf(
    "White noise",
    "Nature sounds",
    "Guided meditation"
)

class AudioLibraryFragment : Fragment(R.layout.fragment_audio_library) {

    private var _binding: FragmentAudioLibraryBinding? = null
    private val binding get() = _binding!!

    private val repository = FirebaseWellnessRepository()

    private var audioContent = emptyList<AudioContent>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAudioLibraryBinding.bind(view)

        binding.audioTabs.onSelect = { index ->
            showCategory(index)
        }

        loadAudio()
    }

    private fun loadAudio() {
        repository.loadAudioContent()
            .addOnSuccessListener { snapshot ->

                audioContent = snapshot.documents.mapNotNull { document ->

                    try {
                        AudioContent(
                            audioId = document.getString("audioId")
                                ?: document.id,

                            title = document.getString("title")
                                ?: "",

                            description = document.getString("description")
                                ?: "",

                            category = document.getString("category")
                                ?: "",

                            storagePath = document.getString("storagePath")
                                ?: "",

                            downloadUrl = document.getString("downloadUrl")
                                ?: "",

                            durationSeconds =
                                document.getLong("durationSeconds")
                                    ?.toInt()
                                    ?: 0,

                            active = document.getBoolean("active")
                                ?: true
                        )
                    } catch (e: Exception) {
                        null
                    }
                }

                showCategory(binding.audioTabs.selectedIndex)
            }
            .addOnFailureListener { error ->

                Toast.makeText(
                    requireContext(),
                    "Could not load audio: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun showCategory(index: Int) {

        if (index !in categories.indices) return

        val category = categories[index]

        binding.audioCategoryLabel.text = category
        binding.audioTrackList.removeAllViews()

        val categoryTracks = audioContent.filter {
            it.category.equals(category, ignoreCase = true)
        }

        val gap = resources.getDimensionPixelSize(
            R.dimen.sgula_space_3
        )

        if (categoryTracks.isEmpty()) {
            return
        }

        categoryTracks.forEach { audio ->

            val duration = formatDuration(audio.durationSeconds)

            val item = SgulaTrackItemView(requireContext()).apply {

                setTitle(audio.title)

                setDuration(
                    if (audio.durationSeconds > 0) {
                        duration
                    } else {
                        "Audio"
                    }
                )

                setOnClickListener {
                    openPlayer(audio)
                }
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            if (binding.audioTrackList.childCount > 0) {
                params.topMargin = gap
            }

            binding.audioTrackList.addView(item, params)
        }
    }

    private fun openPlayer(audio: AudioContent) {

        findNavController().navigate(
            R.id.action_audioLibraryFragment_to_playerFragment,
            Bundle().apply {

                putString(
                    ARG_TRACK_TITLE,
                    audio.title
                )

                putString(
                    ARG_TRACK_SUBTITLE,
                    audio.category
                )

                putString(
                    ARG_TRACK_DURATION,
                    formatDuration(audio.durationSeconds)
                )

                putString(
                    ARG_AUDIO_ID,
                    audio.audioId
                )

                putString(
                    ARG_AUDIO_URL,
                    audio.downloadUrl
                )

                putString(
                    ARG_AUDIO_STORAGE_PATH,
                    audio.storagePath
                )
            }
        )
    }

    private fun formatDuration(seconds: Int): String {

        if (seconds <= 0) {
            return "0:00"
        }

        val minutes = seconds / 60
        val remainingSeconds = seconds % 60

        return "%d:%02d".format(
            minutes,
            remainingSeconds
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}