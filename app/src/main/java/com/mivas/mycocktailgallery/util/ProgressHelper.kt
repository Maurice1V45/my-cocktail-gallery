package com.brainfactory.buffl.view

import android.app.ProgressDialog
import android.content.Context

class ProgressHelper(private val context: Context) {
    private var progressDialog: ProgressDialog? = null

    fun show(message: String) {
        progressDialog = ProgressDialog(context).apply {
            setMessage(message)
            isIndeterminate = true
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    fun show(message: Int) {
        progressDialog = ProgressDialog(context).apply {
            setMessage(context.getString(message))
            isIndeterminate = true
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    fun hide() = progressDialog?.dismiss()
}
