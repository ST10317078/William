package com.example.insy7315_wil_.ui.screens.audio

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.insy7315_wil_.R
import com.example.insy7315_wil_.data.`Data classes`.FirebaseWellnessRepository
import com.example.insy7315_wil_.databinding.FragmentBroadcastBinding

class BroadcastFragment : Fragment(R.layout.fragment_broadcast) {

    private var _binding: FragmentBroadcastBinding? = null
    private val binding get() = _binding!!

    private val repository = FirebaseWellnessRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentBroadcastBinding.bind(view)

        loadDailyBroadcast()

        binding.broadcastHistory.setOnClickListener {
            findNavController().navigate(
                R.id.action_broadcastFragment_to_broadcastHistoryFragment
            )
        }
    }

    private fun loadDailyBroadcast() {
        binding.broadcastLoading.visibility = View.VISIBLE
        binding.broadcastCard.visibility = View.GONE
        binding.broadcastErrorText.visibility = View.GONE

        repository.loadDailyBroadcast()
            .addOnSuccessListener { broadcast ->

                if (_binding == null) return@addOnSuccessListener

                binding.broadcastLoading.visibility = View.GONE

                if (broadcast == null || broadcast.text.isBlank()) {
                    showBroadcastError("No daily broadcast is available yet.")
                    return@addOnSuccessListener
                }

                binding.broadcastCard.visibility = View.VISIBLE

                binding.broadcastTopic.text = "Daily affirmation"

                binding.broadcastDescription.text = broadcast.text

                // The current DailyBroadcast document stores the date as yyyy-MM-dd.
                if (broadcast.dateDisplayed.isNotBlank()) {
                    binding.broadcastDate.text =
                        formatBroadcastDate(broadcast.dateDisplayed)
                } else {
                    binding.broadcastDate.text = "Today's broadcast"
                }
            }
            .addOnFailureListener { exception ->

                if (_binding == null) return@addOnFailureListener

                binding.broadcastLoading.visibility = View.GONE

                showBroadcastError(
                    "Unable to load today's broadcast. Please try again."
                )
            }
    }

    private fun showBroadcastError(message: String) {
        binding.broadcastCard.visibility = View.GONE
        binding.broadcastErrorText.visibility = View.VISIBLE
        binding.broadcastErrorText.text = message
    }

    private fun formatBroadcastDate(date: String): String {
        return "Daily broadcast · $date"
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}