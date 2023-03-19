package com.example.yecwms.util

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import com.example.yecwms.R
import com.example.yecwms.databinding.BottomsheetCalculatorBinding
import com.example.yecwms.util.Utils.getIntOrDoubleNumberString
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CalculatorBottomSheet(val listener: CalculatorListener) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomsheetCalculatorBinding
    private var currentAction = CalcActions.NO_ACTION
    private var isTyping = true
    private var valueInMemory: String = ""
    private var valueCurrent: String = ""
    var errorMessage: String = "Ошибка"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.bottomsheet_calculator, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = BottomsheetCalculatorBinding.bind(view)
        if (binding.layoutCalculator.viewTreeObserver?.isAlive!!) {
            binding.layoutCalculator.viewTreeObserver
                .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        binding.btn1.height = binding.btn1.measuredWidth
                        binding.btn2.height = binding.btn2.measuredWidth
                        binding.btn3.height = binding.btn3.measuredWidth
                        binding.btn4.height = binding.btn4.measuredWidth
                        binding.btn5.height = binding.btn5.measuredWidth
                        binding.btn6.height = binding.btn6.measuredWidth
                        binding.btn7.height = binding.btn7.measuredWidth
                        binding.btn8.height = binding.btn8.measuredWidth
                        binding.btn9.height = binding.btn9.measuredWidth
                        binding.btn0.height = binding.btn0.measuredWidth
                        binding.btnDot.height = binding.btnDot.measuredWidth

                        binding.layoutCalculator.viewTreeObserver.removeOnGlobalLayoutListener(this);
                    }
                })
        }

        binding.btn1.setOnClickListener { listener.onEdit(onNumberPress("1")) }
        binding.btn2.setOnClickListener { listener.onEdit(onNumberPress("2")) }
        binding.btn3.setOnClickListener { listener.onEdit(onNumberPress("3")) }
        binding.btn4.setOnClickListener { listener.onEdit(onNumberPress("4")) }
        binding.btn5.setOnClickListener { listener.onEdit(onNumberPress("5")) }
        binding.btn6.setOnClickListener { listener.onEdit(onNumberPress("6")) }
        binding.btn7.setOnClickListener { listener.onEdit(onNumberPress("7")) }
        binding.btn8.setOnClickListener { listener.onEdit(onNumberPress("8")) }
        binding.btn9.setOnClickListener { listener.onEdit(onNumberPress("9")) }
        binding.btn0.setOnClickListener { listener.onEdit(onNumberPress("0")) }

        binding.btnDot.setOnClickListener {
            if (valueCurrent.isEmpty()) {
                valueCurrent += "0."
                listener.onEdit(valueCurrent)
                return@setOnClickListener
            }

            if (valueCurrent.contains('.')) {
                return@setOnClickListener
            }

            valueCurrent += "."
            listener.onEdit(valueCurrent)
        }

        binding.btnDivide.setOnClickListener {
            calculate()
            listener.onEdit(valueCurrent)
            currentAction = CalcActions.DIVIDE
            isTyping = false
        }
        binding.btnMultiply.setOnClickListener {
            calculate()
            listener.onEdit(valueCurrent)
            currentAction = CalcActions.MULTIPLY
            isTyping = false
        }
        binding.btnSubtract.setOnClickListener {
            calculate()
            listener.onEdit(valueCurrent)
            currentAction = CalcActions.SUBTRACT
            isTyping = false
        }
        binding.btnAdd.setOnClickListener {
            calculate()
            listener.onEdit(valueCurrent)
            currentAction = CalcActions.ADD
            isTyping = false
        }

        binding.btnEqual.setOnClickListener {
            calculate()
            listener.onEdit(valueCurrent)
            currentAction = CalcActions.NO_ACTION
            isTyping = false
        }

        binding.btnClear.setOnClickListener {
            if (valueCurrent == errorMessage) {
                clearAll()
                return@setOnClickListener
            }

            if (valueCurrent.isEmpty()) {
                clearAll()
                return@setOnClickListener
            }

            if (valueCurrent == "0.") {
                valueCurrent = ""
                listener.onEdit(valueCurrent)
                return@setOnClickListener
            }

            valueCurrent = valueCurrent.dropLast(1)
            listener.onEdit(valueCurrent)
        }

        binding.btnSubmit.setOnClickListener {
            calculate()
            if (valueCurrent.isNotEmpty())
                listener.onSubmit(valueCurrent.toDouble())
            else listener.onSubmit(null)
            dismiss()
        }
        binding.btnCancel.setOnClickListener {
            if (valueCurrent.isNotEmpty())
                listener.onSubmit(valueCurrent.toDouble())
            else listener.onSubmit(null)
            dismiss()
        }

        super.onViewCreated(view, savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        if (valueCurrent.isNotEmpty())
            listener.onSubmit(valueCurrent.toDouble())
        else listener.onSubmit(null)
    }

    private fun calculate() {
        if (valueInMemory.isEmpty()) return

        val firstVal = if (valueInMemory.last() == '.') valueInMemory.dropLast(1)
            .toDouble() else valueInMemory.toDouble()
        val secondVal = if (valueCurrent.last() == '.') valueCurrent.dropLast(1)
            .toDouble() else valueCurrent.toDouble()

        valueCurrent = when (currentAction) {
            CalcActions.ADD -> {
                getIntOrDoubleNumberString(firstVal + secondVal)
            }
            CalcActions.SUBTRACT -> {
                getIntOrDoubleNumberString(firstVal - secondVal)
            }
            CalcActions.DIVIDE -> {
                if (firstVal == 0.0) {
                    errorMessage
                } else
                    getIntOrDoubleNumberString(firstVal / secondVal)
            }
            CalcActions.MULTIPLY -> {
                getIntOrDoubleNumberString(firstVal * secondVal)
            }
            else -> return
        }
    }



    fun clearAll() {
        valueCurrent = ""
        valueInMemory = ""
        isTyping = true
        currentAction = CalcActions.NO_ACTION
    }

    fun onNumberPress(numberPressed: String): String {
        if (valueCurrent == errorMessage) clearAll()

        if (!isTyping) {
            valueInMemory = valueCurrent
            valueCurrent = ""
            isTyping = true
        }

        if (valueCurrent == "0" && numberPressed == "0") return valueCurrent

        valueCurrent += numberPressed
        return valueCurrent
    }


    interface CalculatorListener {
        fun onEdit(number: String)
        fun onSubmit(number: Double?)
    }

    internal enum class CalcActions {
        ADD,
        SUBTRACT,
        DIVIDE,
        MULTIPLY,
        NO_ACTION
    }
}