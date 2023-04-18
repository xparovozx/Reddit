package com.example.reddit.ui.fragments.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reddit.databinding.ItemOnboardingBinding

class ScreenAdapter(private val onBoardingScreens : List<OnBoardingScreen>) : RecyclerView.Adapter<ScreenAdapter.OnBoardingViewHolder> () {
    var onTextPassed: ((textView: TextView) -> Unit)? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScreenAdapter.OnBoardingViewHolder {return OnBoardingViewHolder( ItemOnboardingBinding.inflate(
        LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(
        holder: ScreenAdapter.OnBoardingViewHolder,
        position: Int
    ) {
        holder.bind(onBoardingScreens[position])
    }

    override fun getItemCount(): Int {
        return onBoardingScreens.size
    }

    inner class OnBoardingViewHolder(private val binding: ItemOnboardingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(onBoardingScreen : OnBoardingScreen) {
            with(binding) {
                onBoardingTitle.text = onBoardingScreen.title
                onBoardingDescription.text = onBoardingScreen.description
                Glide.with(onBoardingIcon)
                    .load(onBoardingScreen.icon)
                    .into(onBoardingIcon)
                onTextPassed?.invoke(onBoardingTitle)
            }
        }
    }
}