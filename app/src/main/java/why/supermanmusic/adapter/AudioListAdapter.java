package why.supermanmusic.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import why.supermanmusic.R;
import why.supermanmusic.bean.Mp3Info;
import why.supermanmusic.utils.UIUtils;


/**
 * 创建者     Ted
 * 创建时间   2016/6/17 0:17
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class AudioListAdapter extends CursorAdapter {

    public final static String DATABASE_NAME = "MyMusicDataBase";
    public final static String TABLE_NAME = "RecentlyPlayTable1";
    public final static String TABLE_NAME_FAVORITE = "FavoriteTable";
    public final static String TABLE_MUSIC_ID = "_id";
    public final static String TABLE_MUSIC_TITLE = "music_title";
    public final static String TABLE_ARTIST = "artist";
    public final static String TABLE_DURATION = "druation";
    public final static String TABLE_MUSIC_URL = "music_url";
    public final static String TABLE_ALBUM_ID = "album_id";
    public final static String TABLE_CURRENT_TIME = "current_time";


    private Mp3Info mMp3Info;


    public AudioListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public AudioListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public AudioListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = View.inflate(context, R.layout.musiclist_item, null);
        ViewHolder holder = new ViewHolder(view);

        //设置不同条目的背景颜色
        if (cursor.getPosition() % 2 == 0) {
            view.setBackgroundResource(R.drawable.listview_item1_selector);
        } else {
            view.setBackgroundResource(R.drawable.listview_item2_selector);
        }

        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //获取holder
        final ViewHolder holder = (ViewHolder) view.getTag();
        //获取当前条目上的audioitem
        mMp3Info = Mp3Info.getMp3Info(cursor);
        //View的初始化
        holder.music_artist.setText(mMp3Info.getArtist());
        holder.music_title.setText(mMp3Info.getTitle());
        holder.music_duration.setText(String.valueOf(formatTime(mMp3Info.getDuration())));


        //初始化红心的状态
        if (mMp3Info.isFavorite()) {
            holder.music_favorite.setImageResource(R.drawable.icon_favourite_checked);
        } else {
            holder.music_favorite.setImageResource(R.drawable.icon_favourite_normal);
        }


    }

    class ViewHolder {
        public TextView music_title;
        public TextView music_artist;
        public TextView music_duration;
        public ImageView music_favorite;

        public ViewHolder(View view) {
            music_title = (TextView) view.findViewById(R.id.musicname_tv);
            music_artist = (TextView) view.findViewById(R.id.artist_tv);
            music_duration = (TextView) view.findViewById(R.id.duration_tv);
            music_favorite = (ImageView) view.findViewById(R.id.favorite_iv);
        }
    }

    //将歌曲的时间转换为分秒的制度
    public static String formatTime(Long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";

        if (min.length() < 2)
            min = "0" + min;
        switch (sec.length()) {
            case 4:
                sec = "0" + sec;
                break;
            case 3:
                sec = "00" + sec;
                break;
            case 2:
                sec = "000" + sec;
                break;
            case 1:
                sec = "0000" + sec;
                break;
        }
        return min + ":" + sec.trim().substring(0, 2);
    }

    @NonNull
    private ArrayList<Mp3Info> getFavoriteSongsData() {
        SQLiteDatabase recentPlaySQLiteDatabase = UIUtils.getContext().openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        Cursor cursor = recentPlaySQLiteDatabase.
                query(TABLE_NAME_FAVORITE, new String[]{
                        TABLE_MUSIC_ID,
                        TABLE_MUSIC_TITLE,
                        TABLE_ARTIST,
                        TABLE_DURATION,
                        TABLE_MUSIC_URL,
                        TABLE_ALBUM_ID}
                        , null, null, null, null, null);
        ArrayList<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();

        while (cursor.moveToNext()) {
            Mp3Info info = new Mp3Info();
            int musicId = cursor.getInt(cursor.getColumnIndex(TABLE_MUSIC_ID));
            String title = cursor.getString(cursor.getColumnIndex(TABLE_MUSIC_TITLE));
            String artist = cursor.getString(cursor.getColumnIndex(TABLE_ARTIST));
            long duration = cursor.getLong(cursor.getColumnIndex(TABLE_DURATION));
            String path = cursor.getString(cursor.getColumnIndex(TABLE_MUSIC_URL));
            int album_id = cursor.getInt(cursor.getColumnIndex(TABLE_ALBUM_ID));

            //添加数据
            info.setAlbum_id(album_id);
            info.setPath(path);
            info.setTitle(title);
            info.setId(musicId);
            info.setArtist(artist);
            info.setDuration(duration);

            //添加到集合
            mp3Infos.add(info);
        }
        cursor.close();
        return mp3Infos;
    }
}
