package com.example.yecwms.presentation.businesspartners.bpadress

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.data.entity.businesspartners.BPAddresses
import com.example.yecwms.R
import com.example.yecwms.databinding.FragmentBpAddressesBinding
import com.example.yecwms.presentation.businesspartners.adapter.BpAddressesAdapter
import java.util.*

class BpAddressesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var bpAddresses: List<BPAddresses>? = null
    private lateinit var mViewModel: BpAddressViewModel
    private lateinit var binding: FragmentBpAddressesBinding
    private lateinit var adapter: BpAddressesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bpAddresses = it.getParcelableArrayList(GeneralConsts.PASSED_BP_ADDRESSES)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bp_addresses, container, false)
    }

    companion object {

        val TAG = BpAddressesFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(bpAddresses: List<BPAddresses>?) =
            BpAddressesFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(
                        GeneralConsts.PASSED_BP_ADDRESSES,
                        bpAddresses as ArrayList<out Parcelable>
                    )
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBpAddressesBinding.bind(view)

        mViewModel = ViewModelProvider(this).get(BpAddressViewModel::class.java)

        adapter = BpAddressesAdapter()
        binding.rvBpAddresses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBpAddresses.adapter = adapter

        mViewModel.setAddresses(bpAddresses)
        mViewModel.listToDraw.observe(viewLifecycleOwner, {
            adapter.list = it
        })

    }
}