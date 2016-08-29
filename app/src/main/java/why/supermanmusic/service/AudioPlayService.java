package why.supermanmusic.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.Random;

import de.greenrobot.event.EventBus;
import why.supermanmusic.R;
import why.supermanmusic.activity.SongsPlayActivity;
import why.supermanmusic.bean.Mp3Info;
import why.supermanmusic.utils.MediaUtil;

public class AudioPlayService extends Service {
    public static final int PLAYMODE_ALL = 0;
    public static final int PLAYMODE_RANDOM = 1;
    public static final int PLAYMODE_SINGLE = 2;
    private static final int FROM_NEXT = 3;
    private static final int FROM_PRE = 4;
    private static final int FROM_PLAY = 5;
    public static final int FROM_CONTENT = 6;
    private int PLAYMODE;
    private ArrayList<Mp3Info> audioItems;
    private int position = -1;
    private AudioBinder audioBinder;
    private MediaPlayer mediaPlayer;
    private SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        audioBinder = new AudioBinder();
        //sp
        sp = getSharedPreferences("audioservice", MODE_PRIVATE);
        //取出播放模式
        PLAYMODE = sp.getInt("play_mode", PLAYMODE_ALL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {

            //onstartcommand在多次start会执行多次  保证歌曲播放
            int from = intent.getIntExtra("from", -1);
            switch (from) {
                case FROM_CONTENT://通知栏
                    EventBus.getDefault().post(audioItems.get(position));
                    break;
                case FROM_PRE://上一曲
                    audioBinder.playPre();
                    break;
                case FROM_NEXT://下一曲
                    audioBinder.playNext();
                    break;
                case FROM_PLAY://正在播放, 暂停
                    audioBinder.pause();
                    break;
                default:
                    //onstartcommand在多次start会执行多次  保证歌曲播放

                    int pos = intent.getIntExtra("position", -1);
                    if (pos == position) {
                        //通知界面更新
                        EventBus.getDefault().post(audioItems.get(position));
                    } else {
                        position = pos;
                        //获取当前的播放列表和播放位置
                        audioItems = (ArrayList<Mp3Info>) intent.getSerializableExtra("list");

                        audioBinder.playItem();
                    }
                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return audioBinder;
    }

    public class AudioBinder extends Binder {
        private NotificationManager manager;

        public void playItem() {
            //播放当前条目
            //创建mediaplayer
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            /*mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(new AudioOnprepareListener());
            mediaPlayer.setOnCompletionListener(new AudioOncomPletionListener());
            //设置播放路径
            try {
                mediaPlayer.setDataSource(audioItems.get(position).getPath());
                //准备(加载进内存)
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            mediaPlayer = MediaPlayer.create(AudioPlayService.this, Uri.parse(audioItems.get(position).getPath()));
            mediaPlayer.setOnPreparedListener(new AudioOnprepareListener());
            mediaPlayer.setOnCompletionListener(new AudioOncomPletionListener());
        }

        //获取当前的进度
        public int getProgress() {
            return mediaPlayer.getCurrentPosition();
        }

        //获取音乐总时长
        public int getDuration() {
            return mediaPlayer.getDuration();
        }

        //跳转到指定位置播放
        public void seekTo(int progress) {
            mediaPlayer.seekTo(progress);
        }

        //获取当前的播放状态  true 播放 false  暂停
        public boolean isPlaying() {
            return mediaPlayer.isPlaying();
        }

        //暂停播放
        public void pause() {
            mediaPlayer.pause();
            hideNotification();
        }

        //开启播放
        public void start() {
            mediaPlayer.start();
            showNotification();
        }

        public int getPlayMode() {
            return PLAYMODE;
        }

        //设置播放模式
        public void setPlayMode(int playmode) {
            PLAYMODE = playmode;
            //保存播放模式
            sp.edit().putInt("play_mode", PLAYMODE).commit();
        }

        //播放上一曲
        public void playPre() {
            switch (PLAYMODE) {
                case PLAYMODE_RANDOM:
                    position = new Random().nextInt(audioItems.size());
                    break;
                default:
                    if (position == 0) {
                        position = audioItems.size() - 1;
                    } else {
                        position--;
                    }
                    break;
            }
            playItem();
        }

        //播放下一曲
        public void playNext() {
            switch (PLAYMODE) {
                case PLAYMODE_RANDOM:
                    position = new Random().nextInt(audioItems.size());
                    break;
                default:
                    position = (position + 1) % audioItems.size();
                    break;
            }
            playItem();
        }

        //播放当前条目歌曲
        public void playPosition(int position) {
            AudioPlayService.this.position = position;
            playItem();
        }

        //播放完成监听
        class AudioOncomPletionListener implements MediaPlayer.OnCompletionListener {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //播放完成 自动播放下一曲
                autoPlayNext();
            }
        }

        //隐藏通知
        private void hideNotification() {
            manager.cancel(0);
        }

        //自动播放下一曲
        private void autoPlayNext() {
            switch (PLAYMODE) {
                case PLAYMODE_ALL:
                    position = (position + 1) % audioItems.size();
                    break;
                case PLAYMODE_RANDOM:
                    position = new Random().nextInt(audioItems.size());
                    break;
                case PLAYMODE_SINGLE:

                    break;
            }
            playItem();
        }

        //mediaplayer准备监听
        class AudioOnprepareListener implements MediaPlayer.OnPreparedListener {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //开启播放
                mediaPlayer.start();

                if (mediaPlayer.isPlaying()) {
                    //通知界面更新
                    EventBus.getDefault().post(audioItems.get(position));

                    //显示通知
                    showNotification();
                }
            }
        }

        //显示通知
        private void showNotification() {
            Notification notification = getPersonalNotification();
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(0, notification);
        }

        //自定义notification
        private Notification getPersonalNotification() {
            Notification.Builder builder = new Notification.Builder(AudioPlayService.this);
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker("正在播放歌曲: " + audioItems.get(position).getTitle())
                    .setWhen(System.currentTimeMillis())
                    .setContent(getRemoteViews())//自定义通知
                    .setOngoing(true)//设置为true 通知不能移除
                    .setContentIntent(getPendingIntent());
            return builder.getNotification();
        }

        //自定义通知
        private RemoteViews getRemoteViews() {
            RemoteViews remoteviews = new RemoteViews(getPackageName(), R.layout.notification);
            remoteviews.setOnClickPendingIntent(R.id.notification_pre, getPrePendingIntent());
            remoteviews.setOnClickPendingIntent(R.id.notification_next, getNextPendingIntent());
            remoteviews.setOnClickPendingIntent(R.id.notification_play, getPlayPendingIntent());
            remoteviews.setTextViewText(R.id.notification_name, audioItems.get(position).getTitle());
            remoteviews.setTextViewText(R.id.notification_artist, audioItems.get(position).getArtist());

            //设置专辑图片
            Bitmap bitmap = MediaUtil.getArtwork(AudioPlayService.this, audioItems.get(position).getId(), audioItems.get(position).getAlbum_id(), true, true);
            remoteviews.setImageViewBitmap(R.id.notification_album, bitmap);

            return remoteviews;
        }

        //点击播放或暂停
        private PendingIntent getPlayPendingIntent() {
            Intent intent = new Intent(AudioPlayService.this, AudioPlayService.class);
            intent.putExtra("from", FROM_PLAY);
            PendingIntent pendingIntent = PendingIntent.getService(AudioPlayService.this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntent;
        }

        //下一曲点击事件
        private PendingIntent getNextPendingIntent() {
            Intent intent = new Intent(AudioPlayService.this, AudioPlayService.class);
            intent.putExtra("from", FROM_NEXT);
            PendingIntent pendingIntent = PendingIntent.getService(AudioPlayService.this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntent;
        }

        //上一曲点击事件
        private PendingIntent getPrePendingIntent() {
            Intent intent = new Intent(AudioPlayService.this, AudioPlayService.class);
            intent.putExtra("from", FROM_PRE);
            PendingIntent pendingIntent = PendingIntent.getService(AudioPlayService.this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntent;
        }

        //通知栏点击事件
        private PendingIntent getPendingIntent() {
            Intent intent = new Intent(AudioPlayService.this, SongsPlayActivity.class);
            intent.putExtra("from", FROM_CONTENT);
            intent.putExtra("list", audioItems);
            PendingIntent pendingIntent = PendingIntent.getActivity(AudioPlayService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntent;
        }
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
