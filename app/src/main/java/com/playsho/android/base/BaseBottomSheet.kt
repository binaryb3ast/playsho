package com.playsho.android.base

import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.playsho.android.R
import com.playsho.android.db.SessionStorage

abstract class BaseBottomSheet<B : ViewDataBinding> : BottomSheetDialogFragment() {

    protected lateinit var binding: B
    private var origin: String? = null
    protected var bottomSheetStatusCallback: BottomSheetStatusCallback? = null
    protected var bottomSheetResultCallback: BottomSheetResultCallback? = null

    interface BottomSheetStatusCallback {
        fun onBottomSheetShow()
        fun onBottomSheetDismiss()
    }

    interface BottomSheetResultCallback {
        fun onBottomSheetProcessSuccess(data: String)
        fun onBottomSheetProcessFail(data: String)
    }

    protected abstract fun getLayoutResourceId(): Int
    protected abstract fun initView()

    init {
        val parentFragment = parentFragment
        setOrigin(parentFragment?.javaClass?.simpleName ?: activity?.javaClass?.simpleName ?: "")
    }

    fun setStatusCallback(bottomSheetCallback: BottomSheetStatusCallback) {
        this.bottomSheetStatusCallback = bottomSheetCallback
    }

    fun setResultCallback(callback: BottomSheetResultCallback) {
        this.bottomSheetResultCallback = callback
    }

    protected fun setOrigin(origin: String) {
        this.origin = origin
    }

    protected fun getOrigin(): String? {
        return this.origin
    }

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutResourceId(), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    protected fun getSessionStorage(): SessionStorage {
        return ApplicationLoader.getSessionStorage()
    }

    fun show() {
        show(parentFragmentManager, getOrigin())
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        bottomSheetStatusCallback?.onBottomSheetDismiss()
    }
}