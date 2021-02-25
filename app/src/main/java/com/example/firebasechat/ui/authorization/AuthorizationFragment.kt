package com.example.firebasechat.ui.authorization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation.findNavController
import com.example.firebasechat.R
import com.example.firebasechat.databinding.FragmentAuthorizationBinding
import com.example.firebasechat.ui.adapter.AuthorizationPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthorizationFragment : Fragment() {

    lateinit var authorizationBinding: FragmentAuthorizationBinding
    private var authorizationPagerAdapter: AuthorizationPagerAdapter? = null
    private val authViewModel: AuthorizationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        authorizationBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_authorization,
            container,
            false
        )

        authorizationPagerAdapter = AuthorizationPagerAdapter(childFragmentManager, lifecycle)
        authorizationBinding.authorizationViewPager.adapter = authorizationPagerAdapter

        TabLayoutMediator(
            authorizationBinding.tabControl,
            authorizationBinding.authorizationViewPager
        ) { tab, position ->
            if (position == 0) {
                tab.text = getString(R.string.login_btn_txt)
            } else {
                tab.text = getString(R.string.signup_btn_txt)
            }
        }.attach()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    isEnabled = false
                    activity?.finish()
                }
            })

        return authorizationBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authViewModel.getUserLiveData().observe(viewLifecycleOwner, Observer {
            if (it.uid != null) {
                // Navigate to Home screen
                val mainNavController =
                    findNavController(requireActivity(), R.id.nav_host_fragment)
                mainNavController.navigate(AuthorizationFragmentDirections.actionAuthorizationFragmentToNavHome())
            }
        })
    }
}