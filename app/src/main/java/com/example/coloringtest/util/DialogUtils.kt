package com.example.coloringtest.util

import android.content.Context
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

object DialogUtils {


    fun showNumberInputDialog(context: Context, title: String, message: String, positiveButtonText: String, negativeButtonText: String, listener: NumberInputDialogListener) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        // EditText를 추가하여 숫자 입력 받기
        val input = EditText(context)
        builder.setView(input)

        builder.setPositiveButton(positiveButtonText) { dialog, which ->
            val number = input.text.toString().toIntOrNull()
            if (number != null) {
                listener.onNumberInputPositive(number)
            } else {
                listener.onNumberInputError("잘못된 숫자 형식입니다.")
            }
        }

        builder.setNegativeButton(negativeButtonText) { dialog, which ->
            listener.onNumberInputNegative()
        }

        builder.setOnCancelListener { dialog ->
            listener.onNumberInputNegative()
        }

        builder.show()
    }

    interface NumberInputDialogListener {
        fun onNumberInputPositive(number: Int)
        fun onNumberInputError(errorMessage: String)
        fun onNumberInputNegative()
    }

}