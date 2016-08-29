package why.supermanmusic.fragment;


import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import why.supermanmusic.Base.BaseApplication;
import why.supermanmusic.R;
import why.supermanmusic.activity.SongsPlayActivity;
import why.supermanmusic.adapter.AudioListAdapter;
import why.supermanmusic.bean.Mp3Info;
import why.supermanmusic.db.MyDataBase;
import why.supermanmusic.utils.UIUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllSongsFragment extends Fragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.list_allsongs)
    ListView mList;
    @InjectView(R.id.allsongs_progress)
    ProgressBar mAllsongsProgress;
    @InjectView(R.id.srl_allsongs_refresh)
    SwipeRefreshLayout mSrlAllsongsRefresh;
    private AudioListAdapter adapter;
    private ContentResolver mResolver;

    private static final int FROM_CONTENT = 6;

    public static MyDataBase myDataBase;

    public AllSongsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mResolver = getContext().getContentResolver();

        //初始化最近播放
        initRecentPlayed();

    }

    private void initRecentPlayed() {

        myDataBase = new MyDataBase(UIUtils.getContext(),mList);  //实例化数据库类
        myDataBase.CreateDataBase();
    }

    private void querySongs(final ContentResolver resolver) {
        //异步查询数据库
        AsyncQueryHandler handler = new AsyncQueryHandler(resolver) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                //打印结果
                //                CursorUtil.cursorLog(cursor);
                mAllsongsProgress.setVisibility(View.GONE);
                //刷新adapter
                ((AudioListAdapter) cookie).swapCursor(cursor);
            }

        };
        handler.startQuery(0, adapter, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID,
        }, null, null, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_songs, container, false);
        ButterKnife.inject(this, view);

        initListView();
        initEvent();

        return view;
    }

    private void initEvent() {
        mList.setOnItemClickListener(this);

    }

    private void initListView() {
        //初始化下拉刷新
        mSrlAllsongsRefresh.setRefreshing(false);
        mSrlAllsongsRefresh.setColorSchemeColors(Color.RED, Color.BLUE, Color.YELLOW);
        mSrlAllsongsRefresh.setOnRefreshListener(this);

        //设置适配器
        adapter = new AudioListAdapter(getContext(), null);
        mList.setAdapter(adapter);

        //查询歌曲
        querySongs(mResolver);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //获取当前的播放列表
        ArrayList<Mp3Info> mp3Infos = Mp3Info.getMp3Infos((Cursor) parent.getItemAtPosition(position));

        //添加到最近播放列表
        long time = System.currentTimeMillis();
        if(myDataBase.IsExistData(MyDataBase.TABLE_NAME, (int) mp3Infos.get(position).getId())){   //最近播放存在该歌曲就更新播放时间,不存在就加入数据库
            myDataBase.UpdateData(MyDataBase.TABLE_NAME,(int)mp3Infos.get(position).getId()
                    ,time);
        }
        else{
            myDataBase.AddData(
                    MyDataBase.TABLE_NAME,
                    (int)mp3Infos.get(position).getId(),
                    mp3Infos.get(position).getTitle(),
                    mp3Infos.get(position).getArtist(),
                    mp3Infos.get(position).getDuration(),
                    mp3Infos.get(position).getPath(),
                    (int)mp3Infos.get(position).getAlbum_id(),
                    time
            );
        }


        //        EventBus.getDefault().post(mp3Infos.get(position));

        //点击后将position和歌曲list集合传到Activity中
        Intent intent = new Intent(UIUtils.getContext(), SongsPlayActivity.class);
        intent.putExtra("list", mp3Infos);
        intent.putExtra("position", position);
        startActivity(intent);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        //关闭cursor
        adapter.changeCursor(null);
    }

    @Override
    public void onRefresh() {
        BaseApplication.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSrlAllsongsRefresh.setRefreshing(false);
//                querySongs(mResolver);
                Toast.makeText(UIUtils.getContext(), "刷新成功!", Toast.LENGTH_SHORT).show();
            }
        }, 1000);
    }

}
