package com.example.yecwms.presentation.login

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.yecwms.R
import com.example.yecwms.core.BaseActivity
import com.example.yecwms.data.Preferences
import com.example.yecwms.databinding.ActivityLoginBinding
import com.example.yecwms.presentation.main.MainActivity


class LoginActivity : BaseActivity() {

    private lateinit var mViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding


    override fun init(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

/*
        BarCodePrinterHelper.printBarcodes(
            arrayListOf(
                DocumentLines(
                    ItemCode = "0200000012",
                    ItemName = "SOME VERY LONG NAME OF FLOWER SOME VERY LONG NAME OF FLOWER"
                ),
                DocumentLines(
                    ItemCode = "0200000022",
                    ItemName = "SOME VERY LONG NAME OF FLOWER SOME VERY LONG NAME OF FLOWER"
                )
            ), this
        )*/


        mViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)


        mViewModel.connectionError.observe(this) {
            Toast.makeText(this, "Connection error: " + mViewModel.errorString, Toast.LENGTH_SHORT)
                .show()
            binding.textView.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            binding.cardView.isClickable = true
            binding.cardView.isFocusable = true
        }

        mViewModel.loading.observe(this) {
            if (it) {
                binding.textView.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                binding.cardView.isClickable = false
                binding.cardView.isFocusable = false
            } else {
                binding.textView.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                binding.cardView.isClickable = true
                binding.cardView.isFocusable = true
            }
        }

        mViewModel.logged.observe(this) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        mViewModel.loginerror.observe(this) {
            Toast.makeText(this, "Ошибка " + it, Toast.LENGTH_SHORT)
                .show()
        }

        binding.cardView.setOnClickListener {

            if (!isIpAddressWritten()) {
                showIpAddressDialog(this)
                return@setOnClickListener
            }

            val username = binding.etvLogin.text.toString()
            val password = binding.etvPassword.text.toString()

            if (username == "") {
                binding.tilLogin.error = getString(R.string.askUsername)
                return@setOnClickListener
            } else binding.tilLogin.error = null

            if (password == "") {
                binding.tilPassword.error = getString(R.string.askPassword)
                return@setOnClickListener
            } else binding.tilPassword.error = null

            Log.wtf("CREDENTIALS", "COMPANY ${Preferences.companyDB}")
            Log.wtf("CREDENTIALS", "IP ADDRESS ${Preferences.ipAddress}")
            Log.wtf("CREDENTIALS", "PORT ${Preferences.portNumber}")

            mViewModel.requestLogin(Preferences.companyDB!!, username, password)

        }


        binding.etvPassword.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.cardView.performClick()
                true
            }
            false
        }

        binding.etvLogin.setText(Preferences.userName)

        binding.imgBtnSettings.setOnClickListener {
            showIpAddressDialog(this)
        }


        //TODO REMOVE ON RELEASE
        tempLogin()
    }


    private fun tempLogin() {
        binding.etvPassword.setText("1234")
        val username = binding.etvLogin.text.toString()
        val password = binding.etvPassword.text.toString()

        if (username == "") {
            binding.tilLogin.error = getString(R.string.askUsername)
            return
        } else binding.tilLogin.error = null

        if (password == "") {
            binding.tilPassword.error = getString(R.string.askPassword)
            return
        } else binding.tilPassword.error = null

        mViewModel.requestLogin(Preferences.companyDB!!, username, password)
    }

    private fun isIpAddressWritten(): Boolean {
        if (Preferences.ipAddress.isNullOrEmpty()) return false
        if (Preferences.portNumber.isNullOrEmpty()) return false
        if (Preferences.companyDB.isNullOrEmpty()) return false
        return true
    }

    private fun showIpAddressDialog(activity: Activity) {
        val dialog = Dialog(activity)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_database_ip)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val btnSubmit = dialog.findViewById<Button>(R.id.btnSubmit)
        val etvIp = dialog.findViewById<EditText>(R.id.etvipAddress)
        val etvPort = dialog.findViewById<EditText>(R.id.etvPortNumber)
        val etvDbName = dialog.findViewById<EditText>(R.id.etvCompanyDb)

        etvIp.setText(Preferences.ipAddress.toString())
        etvPort.setText(Preferences.portNumber.toString())
        etvDbName.setText(Preferences.companyDB.toString())

        Log.d(
            "LOGINREPREFERENCES",
            "${Preferences.ipAddress}      ${Preferences.portNumber}      ${Preferences.companyDB}      "
        )

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSubmit.setOnClickListener {
            Preferences.ipAddress = etvIp.text.toString()
            Preferences.portNumber = etvPort.text.toString()
            Preferences.companyDB = etvDbName.text.toString()
            dialog.dismiss()
            Log.d(
                "LOGINREPREFERENCES",
                "${Preferences.ipAddress}      ${Preferences.portNumber}      ${Preferences.companyDB}      "
            )
        }
        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }


}