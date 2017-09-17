package com.example.wenhai.listenall.widget

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.extension.hide
import com.example.wenhai.listenall.extension.show
import com.example.wenhai.listenall.extension.showToast
import com.example.wenhai.listenall.moudle.artist.detail.ArtistDetailFragment
import com.example.wenhai.listenall.moudle.detail.DetailContract
import com.example.wenhai.listenall.moudle.detail.DetailFragment
import com.example.wenhai.listenall.moudle.main.MainActivity
import com.example.wenhai.listenall.moudle.play.service.PlayProxy
import com.example.wenhai.listenall.utils.FragmentUtil

class SongOpsDialog(context: Context, val song: Song, private val activity: FragmentActivity) : BaseBottomDialog(context) {
    private var playProxy: PlayProxy? = null
    private var isMainActivity = false

    init {
        playProxy = if (activity is PlayProxy) {
            activity
        } else {
            null
        }
        isMainActivity = activity is MainActivity
    }

    @BindView(R.id.song_name)
    lateinit var songNameTv: TextView
    @BindView(R.id.add_play_next)
    lateinit var playNext: LinearLayout
    @BindView(R.id.add_to_collect)
    lateinit var addToCollect: LinearLayout
    @BindView(R.id.download)
    lateinit var downLoad: LinearLayout
    @BindView(R.id.save_cover)
    lateinit var saveCover: LinearLayout
    @BindView(R.id.artist_detail)
    lateinit var artistDetail: LinearLayout
    @BindView(R.id.artist_name)
    lateinit var artistName: TextView
    @BindView(R.id.album_detail)
    lateinit var albumDetail: LinearLayout
    @BindView(R.id.album_name)
    lateinit var albumName: TextView
    @BindView(R.id.delete)
    lateinit var deleteSong: LinearLayout

    var deleteListener: View.OnClickListener? = null


    var showAlbum = true
    var showArtist = true
    var showDelete = false
    var canSaveCover = false


    override fun getLayoutResId(): Int = R.layout.dialog_song_ops

    override fun initView() {
        songNameTv.text = song.name
        artistName.text = song.artistName

        if (showAlbum) {
            albumName.text = song.albumName
        } else {
            albumDetail.hide()
        }

        if (showDelete && deleteListener != null) {
            deleteSong.show()
        } else {
            deleteSong.hide()
        }

        if (! showArtist) {
            artistDetail.hide()
        }

        if (! canSaveCover) {
            saveCover.hide()
        }
    }

    @OnClick(R.id.add_play_next, R.id.delete, R.id.artist_detail, R.id.album_detail)
    fun onClick(view: View) {
        when (view.id) {
            R.id.add_play_next -> {
                if (playProxy !!.setNextSong(song)) {
                    context.showToast(R.string.play_has_set_to_next)
                } else {
                    context.showToast(context.getString(R.string.song_already_in_playlist))
                }
                dismiss()
            }
            R.id.delete -> {
                deleteListener?.onClick(view)
            }
            R.id.artist_detail -> {
                showArtistDetail()
                dismiss()
            }
            R.id.album_detail -> {
                showAlbumDetail()
                dismiss()
            }
        }
    }

    private fun showAlbumDetail() {
        val data = Bundle()
        data.putLong(DetailContract.ARGS_ID, song.albumId)
        data.putSerializable(DetailContract.ARGS_LOAD_TYPE, DetailContract.LoadType.ALBUM)
        if (isMainActivity) {
            val detailFragment = DetailFragment()
            detailFragment.arguments = data
            FragmentUtil.addFragmentToMainView(activity.supportFragmentManager, detailFragment)
        } else {
            val intent = Intent()
            intent.putExtras(data)
            activity.setResult(MainActivity.RESULT_SHOW_ALBUM, intent)
            activity.finish()
        }
    }

    private fun showArtistDetail() {
        val data = Bundle()
        data.putParcelable("artist", song.artist)
        if (isMainActivity) {
            val artistDetailFragment = ArtistDetailFragment()
            artistDetailFragment.arguments = data
            FragmentUtil.addFragmentToMainView(activity.supportFragmentManager, artistDetailFragment)
        } else {
            val intent = Intent()
            intent.putExtras(data)
            activity.setResult(MainActivity.RESULT_SHOW_ARTIST, intent)
            activity.finish()
        }
    }

}