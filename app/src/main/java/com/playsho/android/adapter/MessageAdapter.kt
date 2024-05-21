package com.playsho.android.adapter

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.TranslateAnimation
import androidx.recyclerview.widget.RecyclerView
import com.playsho.android.data.Message
import com.playsho.android.databinding.ItemMessageMeBinding
import com.playsho.android.databinding.ItemMessageSenderBinding
import com.playsho.android.databinding.ItemMessageSystemBinding
import com.playsho.android.utils.AnimationHelper
import com.playsho.android.utils.accountmanager.AccountInstance


class MessageAdapter(private val dataSet: MutableList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val SENDER = 2
        const val SYSTEM = 0
        const val ME = 1
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataSet[position].type == "system") {
            SYSTEM
        } else if (dataSet[position].sender.tag == AccountInstance.getUserData("tag")) {//me
            SENDER
        } else {
            SENDER
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SENDER -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemMessageSenderBinding.inflate(inflater, parent, false)
                SenderViewHolder(binding)
            }

            ME -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemMessageMeBinding.inflate(inflater, parent, false)
                MyselfViewHolder(binding)
            }

            else -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemMessageSystemBinding.inflate(inflater, parent, false)
                SystemViewHolder(binding)
            }
        }
    }

    fun addMessage(message: Message) {
        dataSet.add(0, message)
        notifyItemInserted(0)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = dataSet[position]
        AnimationHelper.slideInFromBottom(holder.itemView , AnimationHelper.Duration.SLIDE_IN_FROM_BOTTOM)
        when (holder) {
            is SenderViewHolder -> holder.bind(message)
            is MyselfViewHolder -> holder.bind(message)
            is SystemViewHolder -> holder.bind(message)
        }
        Handler(Looper.getMainLooper()).postDelayed({
            dataSet.remove(message)
            notifyItemRemoved(holder.bindingAdapterPosition)
        }, AnimationHelper.Duration.DISAPPEAR_MESSAGE) // 5 seconds delay
    }


    inner class SenderViewHolder(private val binding: ItemMessageSenderBinding ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.apply {
                message.also {
                    txtMessage.text = message.message
                    txtName.text = message.sender.userName
                    txtName.setTextColor(Color.parseColor(message.sender.color))
                }

            }
        }
    }

    inner class SystemViewHolder(private val binding: ItemMessageSystemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.apply {
                message.also {
                    txtMessage.text = message.message
                }

            }
        }
    }

    inner class MyselfViewHolder(private val binding: ItemMessageMeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.apply {
                message.also {
                    txtMessage.text = message.message
                }
            }
        }
    }

}
