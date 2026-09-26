package com.example.insy7315_wil_.ui.screens.audio

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.insy7315_wil_.R
import com.example.insy7315_wil_.databinding.FragmentPlayerBinding
import com.example.insy7315_wil_.data.`Data classes`.AudioSession
import com.example.insy7315_wil_.data.`Data classes`.FirebaseWellnessRepository
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

import com.example.insy7315_wil_.ui.screens.quiz.ARG_PLAYER_SUBTITLE
import com.example.insy7315_wil_.ui.screens.quiz.ARG_PLAYER_TITLE
import com.example.insy7315_wil_.ui.screens.quiz.ARG_POINTS_EARNED
import com.example.insy7315_wil_.ui.screens.quiz.ARG_RECOMMENDED_BROADCAST
import com.example.insy7315_wil_.ui.screens.quiz.ARG_RECOMMENDED_CATEGORY
import com.example.insy7315_wil_.ui.screens.quiz.ARG_SELECTED_ANSWER

internal const val ARG_TRACK_TITLE = "track_title"
internal const val ARG_TRACK_SUBTITLE = "track_subtitle"
internal const val ARG_TRACK_DURATION = "track_duration"

internal const val ARG_AUDIO_ID = "audio_id"
internal const val ARG_AUDIO_URL = "audio_url"
internal const val ARG_AUDIO_STORAGE_PATH = "audio_storage_path"

private const val COMPLETION_POINTS = 20
private const val PROGRESS_UPDATE_MS = 250L

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val repository = FirebaseWellnessRepository()

    private var mediaPlayer: MediaPlayer? = null

    private val handler = Handler(Looper.getMainLooper())

    private var audioId = ""
    private var audioStoragePath = ""
    private var audioUrl = ""

    // Unique ID for this playback session
    private var sessionId = ""

    private var totalSeconds = 0
    private var sessionCompleted = false

    private var sleepTimerRunnable: Runnable? = null

    private val progressUpdater = object : Runnable {
        override fun run() {

            val player = mediaPlayer ?: return

            if (player.isPlaying) {

                updateProgress(player)

                handler.postDelayed(
                    this,
                    PROGRESS_UPDATE_MS
                )
            }
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentPlayerBinding.bind(view)

        // Create a unique ID for this audio session
        sessionId = UUID.randomUUID().toString()

        val args = arguments

        /*
         * ------------------------------------------------------------
         * Quiz recommendation information
         * ------------------------------------------------------------
         */

        val category =
            args?.getString(ARG_RECOMMENDED_CATEGORY).orEmpty()

        val broadcast =
            args?.getString(ARG_RECOMMENDED_BROADCAST).orEmpty()

        val answer =
            args?.getString(ARG_SELECTED_ANSWER).orEmpty()

        val fromQuiz =
            category.isNotBlank() ||
                    broadcast.isNotBlank() ||
                    answer.isNotBlank()

        binding.playerRecommendationCard.isVisible = fromQuiz

        if (fromQuiz) {

            val points =
                args?.getInt(ARG_POINTS_EARNED, 15) ?: 15

            binding.playerRecommendationSubtitle.text =
                "Your matched meditation is loaded. You earned +$points points."

            binding.playerCategoryLabel.text =
                if (category.isNotBlank()) {
                    "Meditation category: $category"
                } else {
                    "Meditation category: Calm reset"
                }

            binding.playerBroadcastLabel.text =
                if (broadcast.isNotBlank()) {
                    "Broadcast: $broadcast"
                } else {
                    "Broadcast: David's soft breath broadcast"
                }

            binding.playerAnswerLabel.text =
                if (answer.isNotBlank()) {
                    "Matched from: $answer"
                } else {
                    "Matched from: Calm"
                }
        }

        /*
         * ------------------------------------------------------------
         * Track information
         * ------------------------------------------------------------
         */

        val quizTitle =
            args?.getString(ARG_PLAYER_TITLE).orEmpty()

        val quizSubtitle =
            args?.getString(ARG_PLAYER_SUBTITLE).orEmpty()

        val trackTitle =
            args?.getString(ARG_TRACK_TITLE).orEmpty()

        val trackSubtitle =
            args?.getString(ARG_TRACK_SUBTITLE).orEmpty()

        val duration =
            args?.getString(ARG_TRACK_DURATION).orEmpty()

        audioId =
            args?.getString(ARG_AUDIO_ID).orEmpty()

        audioUrl =
            args?.getString(ARG_AUDIO_URL).orEmpty()

        audioStoragePath =
            args?.getString(ARG_AUDIO_STORAGE_PATH).orEmpty()

        val title =
            listOf(trackTitle, quizTitle)
                .firstOrNull { it.isNotBlank() }
                ?: "Calm reset"

        val subtitle =
            listOf(trackSubtitle, quizSubtitle)
                .firstOrNull { it.isNotBlank() }
                ?: "Guided meditation"

        binding.playerTrackTitle.text = title
        binding.playerTrackSubtitle.text = subtitle

        binding.playerBar.setTitle(title)
        binding.playerBar.setSubtitle(subtitle)

        /*
         * We initially use the duration passed from the library.
         * Once MediaPlayer prepares, the actual duration replaces it.
         */

        totalSeconds =
            parseDuration(duration) ?: 0

        if (totalSeconds > 0) {

            binding.playerBar.setTotal(
                formatTime(totalSeconds)
            )
        }

        binding.playerBar.setElapsed("0:00")
        binding.playerBar.playbackProgress = 0f

        /*
         * ------------------------------------------------------------
         * Player controls
         * ------------------------------------------------------------
         */

        binding.playerBar.onPlayPause = { playing ->

            if (playing) {
                playAudio()
            } else {
                pauseAudio()
            }
        }

        binding.playerBar.onToggleLoop = { looping ->

            mediaPlayer?.isLooping = looping
        }

        binding.playerBar.onToggleFavourite = { favourite ->

            // Favourite persistence can be connected to Firestore later.
            // The player currently maintains the UI state.
        }

        binding.playerBar.onSeek = { progress ->

            val player = mediaPlayer

            if (player != null && player.duration > 0) {

                val targetPosition =
                    ((progress / 100f) * player.duration)
                        .toInt()

                player.seekTo(targetPosition)

                updateProgress(player)
            }
        }

        /*
         * ------------------------------------------------------------
         * Sleep timer
         * ------------------------------------------------------------
         */

        binding.playerSleepTimer.onSelect = { choice ->

            setupSleepTimer(choice)

            binding.playerSleepTimerNote.text =
                if (choice == "Off") {
                    "Playback stops on its own when the timer runs out."
                } else {
                    "Playback will stop after $choice."
                }
        }

        /*
         * ------------------------------------------------------------
         * Start loading the audio
         * ------------------------------------------------------------
         */

        loadAudio()
    }

    /*
     * ------------------------------------------------------------
     * LOAD AUDIO
     * ------------------------------------------------------------
     */

    private fun loadAudio() {

        showLoading(true)
        showError(false)

        /*
         * Prefer the download URL already stored in AudioContent.
         */

        if (audioUrl.isNotBlank()) {

            preparePlayer(audioUrl)

            return
        }

        /*
         * Older Firestore documents may only have storagePath.
         */

        if (audioStoragePath.isNotBlank()) {

            repository.getAudioDownloadUrl(
                storagePath = audioStoragePath,

                onSuccess = { url ->

                    if (isAdded) {

                        audioUrl = url

                        preparePlayer(url)
                    }
                },

                onError = { error ->

                    if (isAdded) {

                        showLoading(false)

                        showError(
                            true,
                            "We couldn't load this audio: ${
                                error.message ?: "Unknown error"
                            }"
                        )
                    }
                }
            )

            return
        }

        showLoading(false)

        showError(
            true,
            "This audio does not have a playable file."
        )
    }

    /*
     * ------------------------------------------------------------
     * PREPARE MEDIA PLAYER
     * ------------------------------------------------------------
     */

    private fun preparePlayer(url: String) {

        releasePlayer()

        showLoading(true)
        showError(false)

        try {

            val player = MediaPlayer()

            mediaPlayer = player

            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(
                        AudioAttributes.CONTENT_TYPE_MUSIC
                    )
                    .setUsage(
                        AudioAttributes.USAGE_MEDIA
                    )
                    .build()
            )

            player.setDataSource(url)

            player.setOnPreparedListener {

                if (isAdded) {

                    totalSeconds =
                        (it.duration / 1000)
                            .coerceAtLeast(0)

                    binding.playerBar.setTotal(
                        formatTime(totalSeconds)
                    )

                    binding.playerBar.setElapsed("0:00")

                    binding.playerBar.playbackProgress = 0f

                    showLoading(false)

                    showError(false)

                    it.isLooping =
                        binding.playerBar.isLooping

                    binding.playerBar.isPlaying = false
                }
            }

            player.setOnCompletionListener {

                if (!binding.playerBar.isLooping) {

                    finishTrack()
                }
            }

            player.setOnErrorListener { _, _, _ ->

                if (isAdded) {

                    binding.playerBar.isPlaying = false

                    showLoading(false)

                    showError(
                        true,
                        "Unable to play this audio. Please check your connection and try again."
                    )
                }

                true
            }

            player.prepareAsync()

        } catch (error: Exception) {

            showLoading(false)

            showError(
                true,
                "Unable to prepare this audio: ${
                    error.message ?: "Unknown error"
                }"
            )
        }
    }

    /*
     * ------------------------------------------------------------
     * PLAY
     * ------------------------------------------------------------
     */

    private fun playAudio() {

        val player = mediaPlayer

        if (player == null) {

            loadAudio()

            return
        }

        try {

            if (!player.isPlaying) {

                player.start()

                binding.playerBar.isPlaying = true

                showError(false)

                startProgressUpdates()
            }

        } catch (error: Exception) {

            binding.playerBar.isPlaying = false

            showError(
                true,
                "Unable to start playback: ${
                    error.message ?: "Unknown error"
                }"
            )
        }
    }

    /*
     * ------------------------------------------------------------
     * PAUSE
     * ------------------------------------------------------------
     */

    private fun pauseAudio() {

        try {

            mediaPlayer?.let {

                if (it.isPlaying) {
                    it.pause()
                }
            }

        } catch (_: Exception) {
        }

        binding.playerBar.isPlaying = false

        stopProgressUpdates()
    }

    /*
     * ------------------------------------------------------------
     * PROGRESS
     * ------------------------------------------------------------
     */

    private fun startProgressUpdates() {

        handler.removeCallbacks(progressUpdater)

        handler.post(progressUpdater)
    }

    private fun stopProgressUpdates() {

        handler.removeCallbacks(progressUpdater)
    }

    private fun updateProgress(player: MediaPlayer) {

        if (player.duration <= 0) return

        val elapsed =
            player.currentPosition / 1000

        val duration =
            player.duration / 1000

        totalSeconds = duration

        binding.playerBar.setElapsed(
            formatTime(elapsed)
        )

        binding.playerBar.setTotal(
            formatTime(duration)
        )

        binding.playerBar.playbackProgress =
            player.currentPosition.toFloat() /
                    player.duration.toFloat()
    }

    /*
     * ------------------------------------------------------------
     * FINISH TRACK
     * ------------------------------------------------------------
     */

    private fun finishTrack() {

        stopProgressUpdates()

        binding.playerBar.isPlaying = false

        binding.playerBar.playbackProgress = 1f

        binding.playerBar.setElapsed(
            formatTime(totalSeconds)
        )

        if (sessionCompleted) return

        val userId =
            FirebaseAuth.getInstance()
                .currentUser
                ?.uid
                ?: return

        /*
         * Prevent duplicate completion events.
         */

        sessionCompleted = true

        binding.playerPointsToast.text =
            getString(
                R.string.sgula_points_toast,
                COMPLETION_POINTS
            )

        binding.playerPointsToast.isVisible = true

        /*
         * Save the Firebase AudioContent ID.
         *
         * The Firebase Function awards the actual +20 points.
         */

        repository.saveAudioSession(
            AudioSession(
                sessionId = sessionId,
                userId = userId,
                audioId = audioId,
                completed = true,
                durationSeconds = totalSeconds,
                pointsAwarded = false
            )
        ).addOnFailureListener {

            /*
             * Do NOT award points locally.
             *
             * Firebase Functions remains the source of truth
             * for the +20 points.
             */
        }
    }

    /*
     * ------------------------------------------------------------
     * SLEEP TIMER
     * ------------------------------------------------------------
     */

    private fun setupSleepTimer(choice: String) {

        /*
         * Cancel previous timer.
         */

        sleepTimerRunnable?.let {

            handler.removeCallbacks(it)
        }

        sleepTimerRunnable = null

        val minutes =
            when (choice) {

                "15 minutes" -> 15

                "30 minutes" -> 30

                "45 minutes" -> 45

                "1 hour" -> 60

                else -> null
            }

        if (minutes == null) return

        val runnable = Runnable {

            pauseAudio()

            binding.playerSleepTimerNote.text =
                "Sleep timer finished. Playback has stopped."
        }

        sleepTimerRunnable = runnable

        handler.postDelayed(
            runnable,
            minutes * 60_000L
        )
    }

    /*
     * ------------------------------------------------------------
     * LOADING / ERROR UI
     * ------------------------------------------------------------
     */

    private fun showLoading(show: Boolean) {

        if (_binding == null) return

        binding.playerLoading.isVisible = show

        if (show) {
            binding.playerError.isVisible = false
        }
    }

    private fun showError(
        show: Boolean,
        message: String = "Unable to play this audio."
    ) {

        if (_binding == null) return

        binding.playerError.text = message

        binding.playerError.isVisible = show

        if (show) {
            binding.playerLoading.isVisible = false
        }
    }

    /*
     * ------------------------------------------------------------
     * RELEASE MEDIA PLAYER
     * ------------------------------------------------------------
     */

    private fun releasePlayer() {

        stopProgressUpdates()

        try {
            mediaPlayer?.stop()
        } catch (_: Exception) {
        }

        try {
            mediaPlayer?.reset()
        } catch (_: Exception) {
        }

        try {
            mediaPlayer?.release()
        } catch (_: Exception) {
        }

        mediaPlayer = null
    }

    /*
     * ------------------------------------------------------------
     * DURATION HELPERS
     * ------------------------------------------------------------
     */

    private fun parseDuration(value: String): Int? {

        val parts = value.split(":")

        if (parts.size != 2) return null

        val minutes =
            parts[0].toIntOrNull()
                ?: return null

        val seconds =
            parts[1].toIntOrNull()
                ?: return null

        return minutes * 60 + seconds
    }

    private fun formatTime(seconds: Int): String {

        val safeSeconds =
            seconds.coerceAtLeast(0)

        return "%d:%02d".format(
            safeSeconds / 60,
            safeSeconds % 60
        )
    }

    /*
     * ------------------------------------------------------------
     * LIFECYCLE
     * ------------------------------------------------------------
     */

    override fun onPause() {

        super.onPause()

        /*
         * Stop playback when leaving the screen.
         */

        pauseAudio()
    }

    override fun onDestroyView() {

        sleepTimerRunnable?.let {

            handler.removeCallbacks(it)
        }

        sleepTimerRunnable = null

        releasePlayer()

        _binding = null

        super.onDestroyView()
    }
}