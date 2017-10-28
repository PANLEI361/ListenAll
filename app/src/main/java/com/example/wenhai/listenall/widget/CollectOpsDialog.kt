package com.example.wenhai.listenall.widget

import android.content.Context
import butterknife.OnClick
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.data.bean.JoinCollectsWithSongsDao
import com.example.wenhai.listenall.utils.DAOUtil
import java.io.Serializable

class CollectOpsDialog(context: Context) : BaseBottomDialog(context) {

    var collectId: Long = 0
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
        val collectDao = DAOUtil.getSession(context).collectDao
        collectDao.deleteByKey(collectId)
        val relationDao = DAOUtil.getSession(context).joinCollectsWithSongsDao
        val existRelations = relationDao.queryBuilder().where(JoinCollectsWithSongsDao.Properties.CollectId.eq(collectId)).list()
        relationDao.deleteInTx(existRelations)
        dismiss()
        onCollectOperationListener?.onDelete()
    }

    fun setCollectId(collectId: Long): CollectOpsDialog {
        this.collectId = collectId
        return this
    }

    interface OnCollectOperationListener : Serializable {
        fun onUpdate()
        fun onDelete()
    }
}