package com.example.insy7315_wil_.ui.screens.audio

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.insy7315_wil_.R
import com.example.insy7315_wil_.databinding.FragmentAudioLibraryBinding
import com.example.insy7315_wil_.ui.widget.SgulaTrackItemView

private data class Track(val title: String, val duration: String, val favourite: Boolean = false)

private val categories = listOf("White noise", "Nature sounds", "Guided meditation")

// Order has to match sgula_audio_tabs
private val tracks = listOf(
    listOf(
        Track("Soft static", "45:00", favourite = true),
        Track("Fan hum", "60:00"),
        Track("Distant traffic", "30:00"),
    ),
    listOf(
        Track("Rain on leaves", "14:00", favourite = true),
        Track("Ocean drift", "20:00"),
        Track("Forest morning", "18:00"),
    ),
    listOf(
        Track("Body scan", "12:00"),
        Track("Letting go of tension", "10:00", favourite = true),
        Track("Breathing for sleep", "15:00"),
    ),
)

class AudioLibraryFragment : Fragment(R.layout.fragment_audio_library) {

    private var _binding: FragmentAudioLibraryBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAudioLibraryBinding.bind(view)

        binding.audioTabs.onSelect = { index -> showCategory(index) }
        showCategory(binding.audioTabs.selectedIndex)
    }

    private fun showCategory(index: Int) {
        binding.audioCategoryLabel.text = categories[index]
        binding.audioTrackList.removeAllViews()

        val gap = resources.getDimensionPixelSize(R.dimen.sgula_space_3)

        tracks[index].forEach { track ->
            val item = SgulaTrackItemView(requireContext()).apply {
                setTitle(track.title)
                setDuration(track.duration)
                isFavourite = track.favourite
                setOnClickListener { openPlayer(track, categories[index]) }
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
            if (binding.audioTrackList.childCount > 0) params.topMargin = gap
            binding.audioTrackList.addView(item, params)
        }
    }

    private fun openPlayer(track: Track, category: String) {
        findNavController().navigate(
            R.id.action_audioLibraryFragment_to_playerFragment,
            Bundle().apply {
                putString(ARG_TRACK_TITLE, track.title)
                putString(ARG_TRACK_SUBTITLE, category)
                putString(ARG_TRACK_DURATION, track.duration)
            },
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}