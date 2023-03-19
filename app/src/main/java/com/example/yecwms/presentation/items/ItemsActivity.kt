package com.example.yecwms.presentation.items

import android.os.Bundle
import com.example.yecwms.R
import com.example.yecwms.core.BaseActivity
import com.example.yecwms.presentation.items.itemslist.ItemsListFragment

class ItemsActivity : BaseActivity() {

    override fun init(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_item_master)

        if (savedInstanceState == null)
            addFragment(
                R.id.itemsFragmentContainer,
                ItemsListFragment(),
                ItemsListFragment.TAG,
                backStack = true
            )
    }


}