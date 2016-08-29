package why.supermanmusic.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.commit451.nativestackblur.NativeStackBlur;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import de.greenrobot.event.EventBus;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import why.supermanmusic.R;
import why.supermanmusic.View.AudioListPopWindow;
import why.supermanmusic.View.CircleImageView;
import why.supermanmusic.View.LyricTextView;
import why.supermanmusic.adapter.AudioPopAdapter;
import why.supermanmusic.bean.Mp3Info;
import why.supermanmusic.db.MyDataBase;
import why.supermanmusic.service.AudioPlayService;
import why.supermanmusic.utils.LyricLoader;
import why.supermanmusic.utils.MediaUtil;
import why.supermanmusic.utils.StringUtil;
import why.supermanmusic.utils.UIUtils;

/**
 * 创建者     Ted
 * 创建时间   2016/6/17 14:20
 * 描述	      ${TODO}$
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}$
 */

public class SongsPlayActivity extends SwipeBackActivity implements View.OnClickListener {

    private static final int MSG_PLAY_LYRIC = 1;
    @InjectView(R.id.back)
    ImageView mBack;
    @InjectView(R.id.audio_player_title)
    TextView mAudioPlayerTitle;
    @InjectView(R.id.audio_player_anim)
    ImageView mAudioPlayerAnim;
    @InjectView(R.id.audio_player_artist)
    TextView mAudioPlayerArtist;
    @InjectView(R.id.audio_player_lyric)
    LyricTextView mAudioPlayerLyric;
    @InjectView(R.id.audio_player_progress)
    TextView mAudioPlayerProgress;
    @InjectView(R.id.audio_player_progress_sk)
    SeekBar mAudioPlayerProgressSk;
    @InjectView(R.id.audio_player_playmode)
    ImageView mAudioPlayerPlaymode;
    @InjectView(R.id.audio_player_pre)
    ImageView mAudioPlayerPre;
    @InjectView(R.id.audio_player_playstatre)
    ImageView mAudioPlayerPlaystatre;
    @InjectView(R.id.audio_player_next)
    ImageView mAudioPlayerNext;
    @InjectView(R.id.audio_player_playlist)
    ImageView mAudioPlayerPlaylist;

    private static final int MSG_UPDATE_PROGRESS = 0;
    @InjectView(R.id.audio_player_root)
    LinearLayout mAudioPlayerRoot;
    @InjectView(R.id.audio_player_duration)
    TextView mAudioPlayerDuration;
    @InjectView(R.id.audio_player_album)
    CircleImageView mAudioPlayerAlbum;
    @InjectView(R.id.playactivity_share)
    ImageView mPlayactivityShare;
    @InjectView(R.id.playactivity_favorite)
    ImageView mPlayactivityFavorite;
    private Mp3Info audioItem;
    private AudioPlayService.AudioBinder audioBinder;
    public ArrayList<Mp3Info> audioItems;
    private AudioServiceConnection mConnection;
    private int mPosition;

    public static boolean isFavorite = false;
    public static MyDataBase myDataBase;
    public static final int FROM_CONTENT = 6;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        ButterKnife.inject(this);

        initFavoritePlayed();

        initData();
        initEvent();


    }

    private void initEvent() {
        mAudioPlayerProgressSk.setOnSeekBarChangeListener(new AudioOnseekbarChangeListener());
        mAudioPlayerPlaystatre.setOnClickListener(this);
        mAudioPlayerPlaymode.setOnClickListener(this);
        mAudioPlayerPre.setOnClickListener(this);
        mAudioPlayerNext.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mAudioPlayerPlaylist.setOnClickListener(this);
        mPlayactivityFavorite.setOnClickListener(this);
        mPlayactivityShare.setOnClickListener(this);
    }

    private void initData() {

        EventBus.getDefault().register(this);

        audioItems = (ArrayList<Mp3Info>) getIntent().getSerializableExtra("list");
        mPosition = getIntent().getIntExtra("position", -1);

        Intent intent = new Intent(getIntent());
        intent.setClass(this, AudioPlayService.class);
        mConnection = new AudioServiceConnection();
        bindService(intent, mConnection, Service.BIND_AUTO_CREATE);
        //再次startservice为了保证退出界面服务不销毁
        startService(intent);

        //滑动退出activity
        SwipeBackLayout swipeBackLayout = getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

    }

    private void initFavoritePlayed() {

        myDataBase = new MyDataBase(UIUtils.getContext(), null);  //实例化数据库类
        myDataBase.CreateDataBase();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_PROGRESS:
                    startUpdateProgress();
                    break;
                case MSG_PLAY_LYRIC:
                    startPlayLyric();
                    break;
            }
        }
    };

    //接收service中界面更新通知
    public void onEventMainThread(Mp3Info item) {
        audioItem = item;
        //更新歌曲名称
        mAudioPlayerTitle.setText(audioItem.getTitle());
        //更新歌手名称
        mAudioPlayerArtist.setText(audioItem.getArtist());
        //开启示波器
        startAnim();
        //更新进度条进度最大值
        mAudioPlayerProgressSk.setMax(audioBinder.getDuration());
        //更新进度
        startUpdateProgress();
        //更新播放状态按钮
        updatePlaystateBtn();
        //更新播放模式图片
        updatePlayModeBtn();
        //设置播放的歌词
        mAudioPlayerLyric.setFile(LyricLoader.loadLyric(audioItem.getTitle()));
        //播放歌词
        startPlayLyric();

        //初始化背景, 专辑和红心
        initAlbum();
    }

    private void initAlbum() {
        //初始化整个背景,模糊效果
        Bitmap bitmap = MediaUtil.getArtwork(this, audioItem.getId(), audioItem.getAlbum_id(), true, false);
        Bitmap blurBitmap = NativeStackBlur.process(bitmap, 20);
        mAudioPlayerRoot.setBackgroundDrawable(new BitmapDrawable(blurBitmap));

        //初始化专辑图片
        mAudioPlayerAlbum.setImageBitmap(bitmap);

        //初始化最喜爱红心
        if (audioItem.isFavorite()) {
            isFavorite = true;
            mPlayactivityFavorite.setImageResource(R.drawable.icon_favourite_checked);
        } else {
            isFavorite = false;
            mPlayactivityFavorite.setImageResource(R.drawable.icon_favourite_normal);
        }
    }

    //开始播放歌词
    private void startPlayLyric() {
        //获取当前进度
        int progress = audioBinder.getProgress();
        //播放歌词
        mAudioPlayerLyric.rollText(progress, audioBinder.getDuration());
        //循环获取当前进度
        handler.sendEmptyMessage(MSG_PLAY_LYRIC);
    }

    //开启示波器
    private void startAnim() {
        //        AnimationDrawable drawable = audio_player_anim.getBackground();
        AnimationDrawable drawable = (AnimationDrawable) mAudioPlayerAnim.getDrawable();
        drawable.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.audio_player_playstatre:
                updatePlayState();
                break;
            case R.id.audio_player_playmode:
                switchPlayMode();
                break;
            case R.id.audio_player_pre:
                audioBinder.playPre();
                break;
            case R.id.audio_player_next:
                audioBinder.playNext();
                break;
            case R.id.back:
                finish();
                break;
            case R.id.audio_player_playlist:
                showDialog();
                break;
            case R.id.playactivity_favorite:
                add2Favorite();
                break;
            case R.id.playactivity_share:
                showShare();
                break;
        }
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("分享超人音乐到---");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("超人音乐,一款超好用的本地音乐播放器, 由王浩宇开发, 推荐你来下载使用! ");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }

    private void add2Favorite() {
        if (audioItem.isFavorite()) {//取消最喜爱
            mPlayactivityFavorite.setImageResource(R.drawable.icon_favourite_normal);
            isFavorite = false;
            audioItem.setIsFavorite(false);
            myDataBase.DeleteData(
                    MyDataBase.TABLE_NAME_FAVORITE,
                    (int) audioItem.getId());
            Toast.makeText(UIUtils.getContext(), "已取消最喜爱", Toast.LENGTH_SHORT).show();
        } else {//添加到最喜爱
            mPlayactivityFavorite.setImageResource(R.drawable.icon_favourite_checked);
            isFavorite = true;
            audioItem.setIsFavorite(true);
            myDataBase.AddData(
                    MyDataBase.TABLE_NAME_FAVORITE,
                    (int) audioItem.getId(),
                    audioItem.getTitle(),
                    audioItem.getArtist(),
                    audioItem.getDuration(),
                    audioItem.getPath(),
                    (int) audioItem.getAlbum_id()
            );
            Toast.makeText(UIUtils.getContext(), "已添加到最喜爱", Toast.LENGTH_SHORT).show();
        }
    }

    //弹出歌曲列表的对话框
    private void showDialog() {

        //创建adapter
        AudioPopAdapter adapter = new AudioPopAdapter(this, audioItems);
        //创建条目点击事件
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                audioBinder.playPosition(position);
            }
        };
        //创建popwindow
        AudioListPopWindow popwindow = new AudioListPopWindow(this, adapter, onItemClickListener);
        //显示
        popwindow.showAtLocation(mAudioPlayerRoot, Gravity.CENTER, 0, 0);
    }

    class AudioServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            audioBinder = (AudioPlayService.AudioBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    //开启更新进度
    private void startUpdateProgress() {
        //获取当前进度
        int progress = audioBinder.getProgress();
        //设置进度
        updateProgress(progress);
        //定时更新进度
        handler.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS, 500);
    }

    //根据当前的进度数值设置界面进度
    private void updateProgress(int progress) {
        //获取音乐总时长
        int duration = audioBinder.getDuration();
        //设置进度
        mAudioPlayerProgress.setText(StringUtil.parseDuration(progress));
        mAudioPlayerDuration.setText(StringUtil.parseDuration(duration));
        //设置进度条进度
        mAudioPlayerProgressSk.setProgress(progress);
    }

    //切换播放模式 全部循环 单曲循环  随机播放
    private void switchPlayMode() {
        //获取当前播放模式
        int playmode = audioBinder.getPlayMode();
        //根据当前播放模式切换播放模式
        switch (playmode) {
            case AudioPlayService.PLAYMODE_ALL:
                Toast.makeText(getApplicationContext(), "单曲循环", Toast.LENGTH_SHORT).show();
                audioBinder.setPlayMode(AudioPlayService.PLAYMODE_SINGLE);
                break;
            case AudioPlayService.PLAYMODE_SINGLE:
                Toast.makeText(getApplicationContext(), "随机播放", Toast.LENGTH_SHORT).show();
                audioBinder.setPlayMode(AudioPlayService.PLAYMODE_RANDOM);
                break;
            case AudioPlayService.PLAYMODE_RANDOM:
                Toast.makeText(getApplicationContext(), "顺序播放", Toast.LENGTH_SHORT).show();
                audioBinder.setPlayMode(AudioPlayService.PLAYMODE_ALL);
                break;
        }
        //更新播放模式图片
        updatePlayModeBtn();
    }

    //根据当前的播放模式设置图片
    private void updatePlayModeBtn() {
        //获取当前播放模式
        int playmode = audioBinder.getPlayMode();
        //根据当前播放模式切换播放模式
        switch (playmode) {
            case AudioPlayService.PLAYMODE_ALL:
                mAudioPlayerPlaymode.setImageResource(R.drawable.audio_player_playmode_allrepeat_selector);
                break;
            case AudioPlayService.PLAYMODE_SINGLE:
                mAudioPlayerPlaymode.setImageResource(R.drawable.audio_player_playmode_singlerepeat_selector);
                break;
            case AudioPlayService.PLAYMODE_RANDOM:
                mAudioPlayerPlaymode.setImageResource(R.drawable.audio_player_playmode_randomrepeat_selector);
                break;
        }
    }

    //切换当前播放状态
    private void updatePlayState() {
        //获取当前播放状态
        boolean isPlaying = audioBinder.isPlaying();

        //根据当前播放状态切换
        if (isPlaying) {
            //播放  切换到暂停
            audioBinder.pause();
        } else {
            //暂停 切换到播放
            audioBinder.start();
        }
        //更新播放状态按钮图片
        updatePlaystateBtn();
    }

    //根据当前的播放状态更新播放状态按钮图片
    private void updatePlaystateBtn() {
        //获取当前播放状态
        boolean isPlaying = audioBinder.isPlaying();
        //根据当前播放状态更新图片
        if (isPlaying) {
            mAudioPlayerPlaystatre.setImageResource(R.drawable.audio_player_playstate_play_selector);
        } else {
            mAudioPlayerPlaystatre.setImageResource(R.drawable.audio_player_playstate_pause_selector);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        if (mConnection != null)
            unbindService(mConnection);
    }

    class AudioOnseekbarChangeListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 进度条进度改变
         *
         * @param seekBar
         * @param progress 改变之后的进度
         * @param fromUser true 用户操作 false 代码操作
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //判断是否为用户操作
            if (!fromUser)
                return;
            //跳转到指定进度播放
            audioBinder.seekTo(progress);
            //更新进度
            updateProgress(progress);
        }

        /**
         * 手指触摸
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        /**
         * 手指离开
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
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

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

        MobclickAgent.onPageStart("SongsPlayActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd("SongsPlayActivity");
    }

}
