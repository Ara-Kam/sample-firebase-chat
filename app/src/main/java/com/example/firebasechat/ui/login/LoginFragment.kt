package com.example.firebasechat.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.firebasechat.data.util.ResultDataWrapper
import com.example.firebasechat.databinding.FragmentLoginBinding
import com.example.firebasechat.util.closeKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by viewModels()
    lateinit var loginBinding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loginBinding = FragmentLoginBinding.inflate(inflater, container, false)

        return loginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loginBinding.viewModel = loginViewModel
        loginBinding.lifecycleOwner = viewLifecycleOwner

        loginViewModel.fireBaseUser.observe(viewLifecycleOwner, Observer {
            closeKeyboard(requireActivity())
            when (it?.status) {
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

        loginBinding.errorLayout.retry.setOnClickListener { normalState() }
    }

    override fun onPause() {
        super.onPause()
        normalState()
    }

    private fun normalState() {
        loginBinding.apply {
            errorLayout.root.visibility = View.GONE
            loginContainer.visibility = View.VISIBLE
        }
    }

    private fun loadingState() {
        loginBinding.apply {
            loginContainer.visibility = View.GONE
        }
    }

    private fun errorState(errorMessage: String?) {
        loginBinding.apply {
            loginContainer.visibility = View.GONE
            errorLayout.errorText.text = errorMessage
        }
    }
}