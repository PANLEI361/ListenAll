package com.example.wenhai.listenall.moudle.albumlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wenhai.listenall.R;
import com.example.wenhai.listenall.data.bean.Album;
import com.example.wenhai.listenall.utils.GlideApp;

import java.util.List;


public class NewAlbumAdapter extends BaseAdapter {
    private List<Album> albumList;
    private Context context;

    NewAlbumAdapter(Context context, List<Album> albumList) {
        this.albumList = albumList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return albumList.size();
    }

    @Override
    public Object getItem(int i) {
        return albumList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_album_list, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.cover = convertView.findViewById(R.id.album_cover);
            viewHolder.title = convertView.findViewById(R.id.album_title);
            viewHolder.artist = convertView.findViewById(R.id.album_artist);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        Album album = albumList.get(position);
        viewHolder.title.setText(album.getTitle());
        viewHolder.artist.setText(album.getArtist());
        GlideApp.with(context)
                .load(album.getCoverUrl())
                .placeholder(R.drawable.ic_main_all_music)
                .into(viewHolder.cover);

        return convertView;
    }

    private class ViewHolder {
        private ImageView cover;
        private TextView title;
        private TextView artist;
    }
}
