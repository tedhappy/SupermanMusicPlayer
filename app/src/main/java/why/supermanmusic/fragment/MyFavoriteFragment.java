package why.supermanmusic.fragment;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import why.supermanmusic.Base.BaseApplication;
import why.supermanmusic.R;
import why.supermanmusic.activity.SongsPlayActivity;
import why.supermanmusic.bean.Mp3Info;
import why.supermanmusic.utils.UIUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFavoriteFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {


    @InjectView(R.id.list_favorite)
    ListView mListFavorite;
    @InjectView(R.id.srl_favorite_refresh)
    SwipeRefreshLayout mSrlFavoriteRefresh;
    @InjectView(R.id.myfavorite_nodata)
    TextView mMyfavoriteNodata;

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

    private static final int FROM_CONTENT = 6;

    public MyFavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_favorite, container, false);
        ButterKnife.inject(this, view);

        initData();

        initialize(view);//自定义的初始化函数

        initEvent();

        return view;
    }

    private void initialize(View view) {
        //初始化listview
        SongsPlayActivity.myDataBase.Update_adapter();
        SongsPlayActivity.myDataBase.SetAdapter((ListView) view.findViewById(R.id.list_favorite));
    }

    private void initEvent() {
        mListFavorite.setOnItemClickListener(this);
    }

    private void initData() {
        //初始化下拉刷新
        mSrlFavoriteRefresh.setRefreshing(false);
        mSrlFavoriteRefresh.setColorSchemeColors(Color.RED, Color.BLUE, Color.YELLOW);
        mSrlFavoriteRefresh.setOnRefreshListener(this);

        //判断是否有数据
        ArrayList<Mp3Info> recentSongsData = getFavoriteSongsData();
        if (recentSongsData.size() == 0) {
            mMyfavoriteNodata.setVisibility(View.VISIBLE);
            mListFavorite.setVisibility(View.GONE);
        } else {
            mMyfavoriteNodata.setVisibility(View.GONE);
            mListFavorite.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onRefresh() {
        BaseApplication.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSrlFavoriteRefresh.setRefreshing(false);
                Toast.makeText(UIUtils.getContext(), "刷新成功!", Toast.LENGTH_SHORT).show();
            }
        }, 1000);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ArrayList<Mp3Info> mp3Infos = getFavoriteSongsData();

        //点击后将position和歌曲list集合传到Activity中
        Intent intent = new Intent(UIUtils.getContext(), SongsPlayActivity.class);
        intent.putExtra("list", mp3Infos);
        intent.putExtra("position", position);
        startActivity(intent);
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
