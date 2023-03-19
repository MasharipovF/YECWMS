package com.example.yecwms.presentation.businesspartners.bplist

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yecwms.R
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.businesspartners.BusinessPartners
import com.example.yecwms.databinding.FragmentBpListBinding
import com.example.yecwms.presentation.businesspartners.BpActivity
import com.example.yecwms.presentation.businesspartners.adapter.BpListAdapter
import com.example.yecwms.presentation.businesspartners.bpadd.BpAddActivity
import com.example.yecwms.presentation.businesspartners.bpinfo.BpInfoFragment
import com.example.yecwms.util.Utils


class BpListFragment : Fragment() {

    private lateinit var mViewModel: BpListViewModel
    private lateinit var binding: FragmentBpListBinding
    private lateinit var adapter: BpListAdapter

    companion object {
        val TAG = BpListFragment::class.java.simpleName

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bp_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBpListBinding.bind(view)

        mViewModel = ViewModelProvider(this).get(BpListViewModel::class.java)

        adapter = BpListAdapter(object :
            BpListAdapter.BpClickListener {

            override fun onClick(bp: BusinessPartners) {
                (activity as BpActivity).replaceFragment(
                    R.id.bpFragmentContainer,
                    BpInfoFragment.newInstance(bp.CardCode!!),
                    BpInfoFragment.TAG,
                    backStack = true
                )
            }

            override fun loadMore(lastItemIndex: Int) {
                mViewModel.getMoreBp(lastItemIndex)
            }

        })

        binding.rvBpList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBpList.adapter = adapter

        binding.imgShowOnlyBpDebts.setOnClickListener {
            mViewModel.onlyWithDebts.value = !mViewModel.onlyWithDebts.value!!
        }

        mViewModel.connectionError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), mViewModel.errorString, Toast.LENGTH_SHORT).show()
        }


        mViewModel.totalDebtByShop.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.tvTotalDebtUSD.text =
                    Utils.getNumberWithThousandSeparator(it) + " ${Preferences.localCurrency}"
            }
        }

        mViewModel.errorDebtLoading.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(
                    requireContext(),
                    "Ошибка при загрузке общего долга: ${it}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        mViewModel.filterString.observe(viewLifecycleOwner) {
            mViewModel.getBp()
        }

        mViewModel.onlyWithDebts.observe(viewLifecycleOwner) {
            if (it) {
                binding.imgShowOnlyBpDebts.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_baseline_visibility_24,
                        null
                    )
                )
            } else {
                binding.imgShowOnlyBpDebts.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_baseline_visibility_off_24,
                        null
                    )
                )
            }

            mViewModel.getBp()
        }

        mViewModel.listToDraw.observe(viewLifecycleOwner) {
            adapter.list = it
        }

        mViewModel.errorLoading.observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                it,
                Toast.LENGTH_SHORT
            ).show()
        }

        mViewModel.loading.observe(viewLifecycleOwner) {
            if (it) {
                binding.loader.visibility = View.VISIBLE
                binding.rvBpList.visibility = View.GONE
            } else {
                binding.loader.visibility = View.GONE
                binding.rvBpList.visibility = View.VISIBLE
            }
        }


        binding.fabAddBp.setOnClickListener {
            val intent = Intent(requireContext(), BpAddActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        val listItem = menu.findItem(R.id.menu_list)
        val searchItem = menu.findItem(R.id.menu_search)

        listItem.isVisible = false

        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(string: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(string: String?): Boolean {
                    if (string != null) {
                        mViewModel.setFilter(string)
                    }
                    return true
                }
            })
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

}