package com.example.insy7315_wil_.ui.screens.quiz

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.insy7315_wil_.R
import com.example.insy7315_wil_.databinding.FragmentQuizResultBinding

class QuizResultFragment : Fragment(R.layout.fragment_quiz_result) {

    private var _binding: FragmentQuizResultBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentQuizResultBinding.bind(view)

        val answer = requireArguments().getString(ARG_SELECTED_ANSWER).orEmpty()
        val category = requireArguments().getString(ARG_RECOMMENDED_CATEGORY).orEmpty()
        val broadcast = requireArguments().getString(ARG_RECOMMENDED_BROADCAST).orEmpty()
        val playerTitle = requireArguments().getString(ARG_PLAYER_TITLE).orEmpty()
        val playerSubtitle = requireArguments().getString(ARG_PLAYER_SUBTITLE).orEmpty()
        val points = requireArguments().getInt(ARG_POINTS_EARNED, QUIZ_POINTS_EARNED)
        val resolvedPlayerTitle = if (playerTitle.isNotBlank()) playerTitle else "Calm reset"

        binding.quizResultAnswer.text = "You chose: $answer"
        binding.quizResultCategory.text = "Recommended meditation category: $category"
        binding.quizResultBroadcast.text = "Broadcast: $broadcast"
        binding.quizOpenPlayerButton.text = "Open $resolvedPlayerTitle player"
        binding.quizOpenPlayerButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_quizResultFragment_to_playerFragment,
                bundleOf(
                    ARG_SELECTED_ANSWER to answer,
                    ARG_RECOMMENDED_CATEGORY to category,
                    ARG_RECOMMENDED_BROADCAST to broadcast,
                    ARG_PLAYER_TITLE to resolvedPlayerTitle,
                    ARG_PLAYER_SUBTITLE to playerSubtitle,
                    ARG_POINTS_EARNED to points,
                ),
            )
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
