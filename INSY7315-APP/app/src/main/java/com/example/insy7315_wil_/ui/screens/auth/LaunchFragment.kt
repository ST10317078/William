package com.example.insy7315_wil_.ui.screens.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.insy7315_wil_.R
import com.example.insy7315_wil_.data.SessionManager

class LaunchFragment : Fragment(R.layout.fragment_launch) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val session = SessionManager(requireContext())
        if (session.isLoggedIn) {
            findNavController().navigate(R.id.action_launchFragment_to_homeFragment)
            return
        }
        if (session.isGuest) {
            findNavController().navigate(R.id.action_launchFragment_to_guestLandingFragment)
            return
        }

        view.findViewById<View>(R.id.launch_login).setOnClickListener {
            findNavController().navigate(R.id.action_launchFragment_to_loginFragment)
        }
        view.findViewById<View>(R.id.launch_guest).setOnClickListener {
            session.continueAsGuest()
            findNavController().navigate(R.id.action_launchFragment_to_guestLandingFragment)
        }
    }
}
