package com.example.roomviewmodel.viewpager2demo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

const val A_INDEX = 1
const val B_INDEX = 2
class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        A_INDEX to { AFragment.newInstance() },
        B_INDEX to { BFragment.newInstance() }
    )
    override fun getItemCount(): Int {
        return tabFragmentsCreators.size
    }

    override fun createFragment(position: Int): Fragment {
        if(position == 1){
            return AFragment.newInstance()
        } else{
            return BFragment.newInstance()
        }
    }
}
