package com.masuwes.messagingapp.ui.adapter

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.masuwes.messagingapp.R
import com.masuwes.messagingapp.ui.fragment.ChatFragment
import com.masuwes.messagingapp.ui.fragment.SearchFragment
import com.masuwes.messagingapp.ui.fragment.ProfileFragment

class SectionPagerAdapter(private val context: Context, fragmentManager: FragmentManager):
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    @StringRes
    private val tabTitles = intArrayOf(
        R.string.tab_chat,
        R.string.tab_search,
        R.string.tab_profile

    )

    override fun getItem(position: Int): Fragment {
        var fragment : Fragment? = null
        when(position) {
            0 -> fragment = ChatFragment()
            1 -> fragment = SearchFragment()
            2 -> fragment = ProfileFragment()
        }
        return fragment as Fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(tabTitles[position])
    }


    override fun getCount(): Int = 3



}












