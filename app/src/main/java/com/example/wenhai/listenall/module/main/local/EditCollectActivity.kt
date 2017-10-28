package com.example.wenhai.listenall.module.main.local

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.example.wenhai.listenall.BuildConfig
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.data.bean.Collect
import com.example.wenhai.listenall.data.bean.CollectDao
import com.example.wenhai.listenall.data.bean.JoinCollectsWithSongs
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.data.bean.SongDao
import com.example.wenhai.listenall.extension.showToast
import com.example.wenhai.listenall.module.detail.DetailFragment
import com.example.wenhai.listenall.utils.DAOUtil

class EditCollectActivity : AppCompatActivity() {

    lateinit var mUnbinder: Unbinder

    @BindView(R.id.action_bar_title)
    lateinit var mTitle: TextView
    @BindView(R.id.collect_title)
    lateinit var collectTitle: EditText
    @BindView(R.id.collect_cover)
    lateinit var collectCover: ImageView
    @BindView(R.id.collect_intro)
    lateinit var collectIntro: EditText

    var action: String = ""
    var collectId: Long = 0
    var mCollect: Collect? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_collect)
        action = intent.action
        mUnbinder = ButterKnife.bind(this)
        initView()
    }

    private fun initView() {
        if (action == ACTION_CREATE) {
            mTitle.text = getString(R.string.create_collect)
        } else {
            mTitle.text = getString(R.string.edit_collect)
            collectId = intent.getLongExtra("collectId", 0)
            loadCollect()
        }

    }


    private fun loadCollect() {
        val collectDao = DAOUtil.getSession(this).collectDao
        mCollect = collectDao.queryBuilder().where(CollectDao.Properties.Id.eq(collectId)).unique()
        collectTitle.setText(mCollect?.title)
        collectIntro.setText(mCollect?.desc)
        //todo:显示封面
//        collectCover
    }

    @OnClick(R.id.action_bar_back, R.id.save, R.id.collect_cover)
    fun onClick(view: View) {
        when (view.id) {
            R.id.action_bar_back -> {
                finish()
            }

            R.id.save -> {
                if (collectTitle.text.isEmpty()) {
                    showToast("标题不能为空")
                } else {
                    saveCollect()
                }
            }
            R.id.collect_cover -> {
                //todo:从系统相册选择封面
            }
        }
    }

    private fun saveCollect() {
        val title = collectTitle.text.toString()
        val intro = collectIntro.text.toString()
        val collectDao = DAOUtil.getSession(this).collectDao
        val existCollect = collectDao.queryBuilder().where(CollectDao.Properties.Title.eq(title)).unique()
        //名称不重复时保存歌单
        if (action == ACTION_CREATE && existCollect == null) {
            insertCollect(title, intro, collectDao)
            finish()
        } else if (action == ACTION_UPDATE && (existCollect == null || existCollect.id == mCollect?.id)) {
            updateCollect(title, intro, collectDao)
            setResult(DetailFragment.RESULT_UPDATED)
            finish()
        } else {
            showToast("歌单已存在")
        }
    }

    private fun insertCollect(title: String, intro: String, collectDao: CollectDao) {
        mCollect = Collect()
        mCollect?.isFromUser = true
        mCollect?.createDate = System.currentTimeMillis()
        mCollect?.updateDate = System.currentTimeMillis()
        mCollect?.title = title
        mCollect?.desc = intro
        val collectId = collectDao.insert(mCollect)
        if (collectId > 0) {
            if (intent.hasExtra("song")) {
                addSongToCollect(collectId)
            }
            setResult(LocalFragment.RESULT_COLLECT_CREATED)
            finish()
        } else {
            showToast("新建歌单失败，请重试")
        }
    }

    private fun updateCollect(title: String, intro: String, collectDao: CollectDao) {
        mCollect?.title = title
        mCollect?.desc = intro
        collectDao.update(mCollect)
    }

    private fun addSongToCollect(collectId: Long) {
        val song = intent.getSerializableExtra("song") as Song
        //添加歌曲到数据库，并获取歌曲在数据库中的id
        val songId = saveSongToDB(song)
        //添加关系到数据库
        val dao = DAOUtil.getSession(this).joinCollectsWithSongsDao
        val relation = JoinCollectsWithSongs.newRecord(songId, collectId)
        if (dao.insert(relation) > 0) {
            showToast("添加成功")
            //刷新数据，防止缓存导致的数据不更新
        } else {
            showToast("添加失败")
        }
    }

    private fun saveSongToDB(song: Song?): Long {
        val songDao = DAOUtil.getSession(this).songDao
        val list = songDao.queryBuilder().where(SongDao.Properties.SongId.eq(song?.songId)).list()
        return if (list.isNotEmpty()) {
            list[0].id
        } else {
            songDao.insert(song)
        }
    }

    override fun onResume() {
        super.onResume()
        showSoftInput()
    }

    private fun showSoftInput() {
        collectTitle.clearFocus()
        collectTitle.requestFocus()
        collectTitle.setSelection(collectTitle.length())
        val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(collectTitle, InputMethodManager.SHOW_FORCED)
    }

    override fun onPause() {
        super.onPause()
        hideSoftInput()
    }

    override fun onDestroy() {
        super.onDestroy()
        mUnbinder.unbind()
    }

    private fun hideSoftInput() {
        collectTitle.clearFocus()
        val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(collectTitle.windowToken, 0)
    }

    companion object {
        const val ACTION_UPDATE = "${BuildConfig.APPLICATION_ID}.UPDATE"
        const val ACTION_CREATE = "${BuildConfig.APPLICATION_ID}.CREATE"
    }
}
