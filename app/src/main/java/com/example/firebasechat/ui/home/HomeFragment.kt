package com.example.firebasechat.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasechat.R
import com.example.firebasechat.data.entity.ChatMessage
import com.example.firebasechat.data.util.ResultDataWrapper
import com.example.firebasechat.databinding.FragmentHomeBinding
import com.example.firebasechat.ui.adapter.ChatAdapter
import com.example.firebasechat.util.VerticalSpaceItemDecoration
import com.example.firebasechat.util.closeKeyboard
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {

    @set:Inject
    lateinit var firebaseAuth: FirebaseAuth

    @set:Inject
    lateinit var firebaseFirestore: FirebaseFirestore

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var chatListAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentHomeBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_home,
            container,
            false
        )
        return fragmentHomeBinding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragmentHomeBinding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = homeViewModel
        }

        homeViewModel.sendSuccessfully.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            when (it.status) {
                ResultDataWrapper.Status.SUCCESS -> {
                    fragmentHomeBinding.messageInput.text?.clear()
                    closeKeyboard(requireActivity())
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

        //Initialize PagedList Configuration
        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(5)
            .setPageSize(5)
            .build()

        val firestoreQuery: Query =
            firebaseFirestore.collection("messages").orderBy("date", Query.Direction.DESCENDING)

        // Initialize FirestorePagingOptions
        val databasePagingOptions =
            FirestorePagingOptions.Builder<ChatMessage>()
                .setLifecycleOwner(viewLifecycleOwner)
                .setQuery(
                    firestoreQuery,
                    pagedListConfig,
                    ChatMessage::class.java
                )
                .build()

        chatListAdapter = ChatAdapter(databasePagingOptions)
            .withSwipeToRefreshLayout(fragmentHomeBinding.swipeRefreshLayout)
            .withFirebaseAuth(firebaseAuth)

        chatListAdapter.updateOptions(databasePagingOptions)

        fragmentHomeBinding.messageRecyclerView.apply {
            // Item Decoration
            addItemDecoration(
                VerticalSpaceItemDecoration(
                    requireContext(), 10f, 18f, 18f
                )
            )
            // Init list layout manager and adapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            (layoutManager as LinearLayoutManager).reverseLayout = true

            adapter = chatListAdapter
        }

        fragmentHomeBinding.swipeRefreshLayout.setOnRefreshListener {
            chatListAdapter.refresh()
        }
    }

    private fun normalState() {
        fragmentHomeBinding.apply {
            errorLayout.root.visibility = View.GONE
            swipeRefreshLayout.visibility = View.VISIBLE
            loadingLayout.root.visibility = View.GONE
        }
    }

    private fun loadingState() {
        fragmentHomeBinding.apply {
            swipeRefreshLayout.visibility = View.GONE
            loadingLayout.root.visibility = View.VISIBLE
        }
    }

    private fun errorState(errorMessage: String?) {
        fragmentHomeBinding.apply {
            swipeRefreshLayout.visibility = View.GONE
            errorLayout.errorText.text = errorMessage
            swipeRefreshLayout.visibility = View.GONE
        }
    }
}