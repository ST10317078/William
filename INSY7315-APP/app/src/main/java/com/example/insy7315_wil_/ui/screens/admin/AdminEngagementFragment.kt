package com.example.insy7315_wil_.ui.screens.admin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.insy7315_wil_.R
import com.example.insy7315_wil_.ui.screens.redirectNonAdminFromAdminContent

class AdminEngagementFragment : Fragment(R.layout.fragment_admin_engagement) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = FirebaseWellnessRepository()

        repository.checkAdmin(
            onResult = { isAdmin ->
                if (!isAdmin) {
                    redirectNonAdminFromAdminContent()
                } else {
                    loadEngagement(view)
                }
            },
            onError = {
                redirectNonAdminFromAdminContent()
            }
        )
    }

    private fun loadEngagement(view: View) {

        val repository = FirebaseWellnessRepository()

        val container =
            view.findViewById<LinearLayout>(
                R.id.admin_engagement_container
            )

        repository.loadAllUsers(
            onSuccess = { users ->

                container.removeAllViews()

                users.forEach { user ->

                    loadUserEngagement(
                        user,
                        container
                    )
                }
            },
            onError = {

                Toast.makeText(
                    requireContext(),
                    "Unable to load engagement",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    fun loadAudioSessions(
        userId: String,
        onSuccess: (Int) -> Unit,
        onError: (Exception) -> Unit = {}
    ) {
        firestore.collection(FirestoreCollections.AUDIO_SESSION)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                onSuccess(snapshot.size())
            }
            .addOnFailureListener { error ->
                onError(error)
            }
    }
}
