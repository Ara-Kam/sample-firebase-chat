package com.example.firebasechat.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.firebasechat.R
import com.example.firebasechat.data.entity.ChatMessage
import com.example.firebasechat.util.timeStampDateTimeConverter
import com.firebase.ui.database.paging.DatabasePagingOptions
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter
import com.firebase.ui.database.paging.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class ChatAdapter(databasePagingOptions: DatabasePagingOptions<ChatMessage>) :
    FirebaseRecyclerPagingAdapter<ChatMessage, RecyclerView.ViewHolder>(databasePagingOptions) {

    companion object {
        private const val TYPE_OWNER = 1
        private const val TYPE_OTHERS = 2
    }

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mFirebaseAuth: FirebaseAuth

    override fun onBindViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        position: Int,
        model: ChatMessage
    ) {
        if (viewHolder.itemViewType == TYPE_OWNER) {
            (viewHolder as OwnerViewHolder).bindOwner(position)
        } else
            (viewHolder as OthersViewHolder).bindOthers(position)
    }

    override fun onLoadingStateChanged(state: LoadingState) {
        when (state) {
            LoadingState.LOADING_INITIAL, LoadingState.LOADING_MORE ->
                swipeRefreshLayout.isRefreshing = true
            LoadingState.LOADED ->
                swipeRefreshLayout.isRefreshing = false
            LoadingState.FINISHED ->
                swipeRefreshLayout.isRefreshing = false
            LoadingState.ERROR -> retry()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_OWNER) {
            return OwnerViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.owner_chat_item, parent, false)
            )
        }
        return OthersViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.others_chat_item, parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        val chatItem: DataSnapshot? = getItem(position)
        var userType: Int = TYPE_OTHERS
        (chatItem?.value as HashMap<*, *>).entries.forEach lit@{
            if (it.key == "uid") {
                userType =
                    if (it.value == mFirebaseAuth.currentUser?.uid) TYPE_OWNER else TYPE_OTHERS
            }
            return@lit
        }
        return userType
    }

    inner class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var ownerMsgTextView = itemView.findViewById<TextView>(R.id.owner_text)
        private var ownerName = itemView.findViewById<TextView>(R.id.sender_name)
        private var sendDate = itemView.findViewById<TextView>(R.id.date)
        fun bindOwner(position: Int) {
            val chatItem: DataSnapshot = getItem(position)!!
            (chatItem.value as HashMap<*, *>).entries.forEach {
                when (it.key) {
                    "text" -> ownerMsgTextView.text = it.value.toString()
                    "sender_name" -> ownerName.text = it.value.toString()
                    "date" -> sendDate.text = timeStampDateTimeConverter(it.value.toString())
                }

            }
        }
    }

    inner class OthersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var othersMsgTextView = itemView.findViewById<TextView>(R.id.others_text)
        private var othersName = itemView.findViewById<TextView>(R.id.sender_name)
        private var sendDate = itemView.findViewById<TextView>(R.id.date)
        fun bindOthers(position: Int) {
            val chatItem: DataSnapshot = getItem(position)!!
            (chatItem.value as HashMap<*, *>).entries.forEach {
                when (it.key) {
                    "text" -> othersMsgTextView.text = it.value.toString()
                    "sender_name" -> othersName.text = it.value.toString()
                    "date" -> sendDate.text = timeStampDateTimeConverter(it.value.toString())
                }

            }
        }
    }

    fun withSwipeToRefreshLayout(swipeRefreshLayout: SwipeRefreshLayout): ChatAdapter {
        this.swipeRefreshLayout = swipeRefreshLayout
        return this
    }

    fun withFirebaseAuth(firebaseAuth: FirebaseAuth): ChatAdapter {
        mFirebaseAuth = firebaseAuth
        return this
    }

    override fun onError(databaseError: DatabaseError) {
        super.onError(databaseError)
        swipeRefreshLayout.isRefreshing = false
        databaseError.toException().printStackTrace()
    }
}

