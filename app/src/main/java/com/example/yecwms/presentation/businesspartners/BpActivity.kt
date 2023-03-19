package com.example.yecwms.presentation.businesspartners

import android.os.Bundle
import com.example.yecwms.R
import com.example.yecwms.core.BaseActivity
import com.example.yecwms.presentation.businesspartners.bplist.BpListFragment

class BpActivity : BaseActivity() {

    override fun init(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_bp_master)

        if (savedInstanceState == null)
            addFragment(
                R.id.bpFragmentContainer,
                BpListFragment(),
                BpListFragment.TAG,
                backStack = true
            )
    }

}