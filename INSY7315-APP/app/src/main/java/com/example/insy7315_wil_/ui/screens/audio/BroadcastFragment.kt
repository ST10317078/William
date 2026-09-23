package com.example.insy7315_wil_.ui.screens.audio

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.insy7315_wil_.R
import com.example.insy7315_wil_.databinding.FragmentBroadcastBinding

class BroadcastFragment : Fragment(R.layout.fragment_broadcast) {

    private var _binding: FragmentBroadcastBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBroadcastBinding.bind(view)

        binding.broadcastPlay.setOnClickListener {
            findNavController().navigate(
                R.id.action_broadcastFragment_to_playerFragment,
                Bundle().apply {
                    putString(ARG_TRACK_TITLE, "Managing Anxiety")
                    putString(ARG_TRACK_SUBTITLE, "Anat Casey")
                    putString(ARG_TRACK_DURATION, "08:30")
                },
            )
        }

        binding.broadcastHistory.setOnClickListener {
            findNavController().navigate(R.id.action_broadcastFragment_to_broadcastHistoryFragment)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}