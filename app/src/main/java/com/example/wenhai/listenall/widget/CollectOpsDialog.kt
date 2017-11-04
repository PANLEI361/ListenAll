package com.example.wenhai.listenall.widget

import android.content.Context
import butterknife.OnClick
import com.example.wenhai.listenall.R
import java.io.Serializable

class CollectOpsDialog(context: Context) : BaseBottomDialog(context) {

    var onCollectOperationListener: OnCollectOperationListener? = null

    override fun getLayoutResId(): Int {
        return R.layout.dialog_collect_opreation
    }

    override fun initView() {
    }

    @OnClick(R.id.edit_collect)
    fun editCollect() {
        onCollectOperationListener?.onUpdate()
        dismiss()
    }

    @OnClick(R.id.delete_collect)
    fun deleteCollect() {
        onCollectOperationListener?.onDelete()
        dismiss()
    }

    interface OnCollectOperationListener : Serializable {
        fun onUpdate()
        fun onDelete()
    }
}