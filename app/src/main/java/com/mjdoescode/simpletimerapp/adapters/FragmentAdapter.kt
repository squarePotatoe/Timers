package com.mjdoescode.simpletimerapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mjdoescode.simpletimerapp.fragments.CountdownFragment
import com.mjdoescode.simpletimerapp.fragments.StopwatchFragment

class FragmentAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> CountdownFragment()
            1 -> StopwatchFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}