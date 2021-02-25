package com.example.firebasechat.ui.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.firebasechat.data.util.ResultDataWrapper
import com.example.firebasechat.databinding.FragmentRegistrationBinding
import com.example.firebasechat.util.closeKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private val registrationViewModel: RegistrationViewModel by viewModels()
    private lateinit var registrationBinding: FragmentRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        registrationBinding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return registrationBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        registrationBinding.also {
            it.viewModel = registrationViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        registrationViewModel.fireBaseUser.observe(viewLifecycleOwner, Observer {
            closeKeyboard(requireActivity())
            when (it.status) {
                ResultDataWrapper.Status.SUCCESS -> {
                    if (it.data == null) return@Observer

                    normalState()
                }

                ResultDataWrapper.Status.ERROR, ResultDataWrapper.Status.CANCELED -> {
                    errorState(it.message)
                }

                ResultDataWrapper.Status.LOADING -> {
                    loadingState()
                }
            }
        })

        registrationBinding.errorLayout.retry.setOnClickListener { normalState() }
    }

    override fun onPause() {
        super.onPause()
        normalState()
    }

    private fun normalState() {
        registrationBinding.apply {
            errorLayout.root.visibility = View.GONE
            signUpContainer.visibility = View.VISIBLE
        }
    }

    private fun loadingState() {
        registrationBinding.apply {
            signUpContainer.visibility = View.GONE
        }
    }

    private fun errorState(errorMessage: String?) {
        registrationBinding.apply {
            signUpContainer.visibility = View.GONE
            errorLayout.errorText.text = errorMessage
        }
    }
}