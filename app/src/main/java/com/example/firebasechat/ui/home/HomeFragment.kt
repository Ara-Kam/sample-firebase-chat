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
import com.firebase.ui.database.paging.DatabasePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {

    @set:Inject
    lateinit var firebaseAuth: FirebaseAuth

    @set:Inject
    lateinit var firebaseDatabase: FirebaseDatabase

    private val homeViewModel: HomeViewModel by viewModels()
    lateinit var fragmentHomeBinding: FragmentHomeBinding
    lateinit var databaseReference: DatabaseReference
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

        // Initialize Database
        databaseReference = firebaseDatabase.reference

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
            .setPageSize(10)
            .build()

        // Initialize FirebasePagingOptions
        val databasePagingOptions = DatabasePagingOptions.Builder<ChatMessage>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(databaseReference, pagedListConfig, ChatMessage::class.java)
            .build()

        chatListAdapter = ChatAdapter(databasePagingOptions)
            .withSwipeToRefreshLayout(fragmentHomeBinding.swipeRefreshLayout)
            .withFirebaseAuth(firebaseAuth)

        databaseReference.orderByKey().limitToLast(1)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val newQuery = databaseReference
                    val newOptions = DatabasePagingOptions.Builder<ChatMessage>()
                        .setQuery(newQuery, pagedListConfig, ChatMessage::class.java)
                        .build()

                    chatListAdapter.updateOptions(newOptions)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

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
            (layoutManager as LinearLayoutManager).stackFromEnd = true

            // Scroll to the last item
            chatListAdapter.registerAdapterDataObserver(object :
                RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    (layoutManager as LinearLayoutManager).smoothScrollToPosition(
                        fragmentHomeBinding.messageRecyclerView,
                        null,
                        chatListAdapter.getItemCount()
                    )
                }
            })

            adapter = chatListAdapter
        }

        fragmentHomeBinding.swipeRefreshLayout.setOnRefreshListener {
            chatListAdapter.refresh()
        }
    }

    override fun onStart() {
        super.onStart()
        chatListAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        chatListAdapter.stopListening()
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