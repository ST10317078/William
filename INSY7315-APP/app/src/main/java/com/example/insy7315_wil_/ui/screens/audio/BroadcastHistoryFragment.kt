package com.example.insy7315_wil_.ui.screens.audio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.insy7315_wil_.R
import com.example.insy7315_wil_.databinding.FragmentBroadcastHistoryBinding
import com.example.insy7315_wil_.databinding.ItemBroadcastBinding
import androidx.core.view.isNotEmpty

private data class PastBroadcast(
    val date: String,
    val topic: String,
    val duration: String,
    val description: String,
)

private val pastBroadcasts = listOf(
    PastBroadcast("4 August", "Sleeping Through Worry", "07:45", "Winding the body down when the mind will not."),
    PastBroadcast("3 August", "When Motivation Dips", "09:10", "Working with low days instead of against them."),
    PastBroadcast("2 August", "Setting Boundaries", "06:20", "Saying no without carrying it around afterwards."),
    PastBroadcast("1 August", "The Comparison Trap", "08:05", "Why other people's highlight reels feel like evidence."),
)

class BroadcastHistoryFragment : Fragment(R.layout.fragment_broadcast_history) {

    private var _binding: FragmentBroadcastHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBroadcastHistoryBinding.bind(view)

        val inflater = LayoutInflater.from(requireContext())
        val gap = resources.getDimensionPixelSize(R.dimen.sgula_space_3)

        pastBroadcasts.forEach { broadcast ->
            val item = ItemBroadcastBinding.inflate(inflater, binding.broadcastHistoryList, false)
            item.broadcastItemDate.text = broadcast.date
            item.broadcastItemDuration.text = broadcast.duration
            item.broadcastItemTopic.text = broadcast.topic
            item.broadcastItemDescription.text = broadcast.description
            item.root.setOnClickListener { openPlayer(broadcast) }

            val params = item.root.layoutParams as LinearLayout.LayoutParams
            if (binding.broadcastHistoryList.isNotEmpty()) params.topMargin = gap
            binding.broadcastHistoryList.addView(item.root, params)
        }
    }

    private fun openPlayer(broadcast: PastBroadcast) {
        findNavController().navigate(
            R.id.action_broadcastHistoryFragment_to_playerFragment,
            Bundle().apply {
                putString(ARG_TRACK_TITLE, broadcast.topic)
                putString(ARG_TRACK_SUBTITLE, broadcast.date)
                putString(ARG_TRACK_DURATION, broadcast.duration)
            },
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}