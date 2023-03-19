package com.example.yecwms.presentation.businesspartners.bpinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.yecwms.R
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.data.Preferences
import com.example.yecwms.databinding.FragmentBpInfoBinding
import com.example.yecwms.presentation.businesspartners.BpActivity
import com.example.yecwms.presentation.businesspartners.bpadress.BpAddressesFragment
import com.example.yecwms.util.Utils

class BpInfoFragment : Fragment() {
    private var bpCode: String? = null
    private lateinit var mViewModel: BpInfoViewModel
    private lateinit var binding: FragmentBpInfoBinding


    companion object {
        val TAG = BpInfoFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(bpCode: String) =
            BpInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(GeneralConsts.PASSED_CARD_CODE, bpCode)
                }
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bpCode = it.getString(GeneralConsts.PASSED_CARD_CODE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bp_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBpInfoBinding.bind(view)

        mViewModel = ViewModelProvider(this).get(BpInfoViewModel::class.java)

        mViewModel.getBpInfo(bpCode!!)
        if (Preferences.defaultWhs == null) {
            mViewModel.currentWhsCode.value = ""
        } else {
            mViewModel.currentWhsCode.value = Preferences.defaultWhs
        }


        mViewModel.bpInfo.observe(viewLifecycleOwner) {
            binding.tvCardName.text = it.CardName
            binding.tvAddress.text = it.Address
            binding.tvPhone.text = it.Phone1
            binding.tvBalance.text = Utils.getNumberWithThousandSeparator(it.Balance!!)

            if (it.CardType == GeneralConsts.BP_TYPE_CUSTOMER) {
                binding.tvLimit.text = Utils.getNumberWithThousandSeparator(it.CreditLimit!!)
                if (it.CreditLimit!! > 0) binding.tvLimitCurrency.text =
                    GeneralConsts.DOC_CURRENCY_USD
                binding.tvCardType.text = getString(R.string.cardtype_customer)
            } else if (it.CardType == GeneralConsts.BP_TYPE_SUPPLIER) {
                binding.tvLimit.text = Utils.getNumberWithThousandSeparator(it.MaxCommitment!!)
                if (it.MaxCommitment!! > 0) binding.tvLimitCurrency.text =
                    GeneralConsts.DOC_CURRENCY_USD
                binding.tvCardType.text = getString(R.string.cardtype_supplier)
            }

            binding.tvCardGroup.text = it.GroupName
            binding.tvPriceList.text = it.PriceListName

            if (it.BPAddresses?.size!! > 1) {
                binding.imgAddrArrow.visibility = View.VISIBLE
                binding.layoutAddress.isClickable = true
            } else {
                binding.imgAddrArrow.visibility = View.GONE
                binding.layoutAddress.isClickable = false
            }
        }

        mViewModel.bpDebtByShop.observe(viewLifecycleOwner, {
            binding.tvBalanceByShop.text = Utils.getNumberWithThousandSeparator(it)
        })

        mViewModel.loading.observe(viewLifecycleOwner, {
            if (it) {
                binding.loader.visibility = View.VISIBLE
                binding.cardMainInfo.visibility = View.GONE
            } else {
                binding.loader.visibility = View.GONE
                binding.cardMainInfo.visibility = View.VISIBLE
            }
        })

        mViewModel.errorLoading.observe(viewLifecycleOwner, {
            Toast.makeText(
                requireContext(),
                it,
                Toast.LENGTH_SHORT
            ).show()
        })

        mViewModel.connectionError.observe(viewLifecycleOwner, {
            Toast.makeText(requireContext(), mViewModel.errorString, Toast.LENGTH_SHORT).show()
        })

        binding.layoutAddress.setOnClickListener {
            (activity as BpActivity).replaceFragment(
                R.id.bpFragmentContainer,
                BpAddressesFragment.newInstance(mViewModel.getBpAddresses()),
                BpAddressesFragment.TAG,
                backStack = true
            )
        }


    }

}