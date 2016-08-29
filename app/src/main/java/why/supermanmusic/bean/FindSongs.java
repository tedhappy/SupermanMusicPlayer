package why.supermanmusic.bean;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import why.supermanmusic.adapter.AlbumListAdapter;

/**
 * 创建者     Ted
 * 创建时间   2016/6/11 23:05
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class FindSongs implements Serializable {

    public List<Mp3Info> getMp3Infos(ContentResolver contentResolver) {
        Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
        for (int i = 0; i < cursor.getCount(); i++) {
            //新建一个歌曲对象,将从cursor里读出的信息存放进去,直到取完cursor里面的内容为止.
            Mp3Info mp3Info = new Mp3Info();
            cursor.moveToNext();


            long id = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));   //音乐id

            String title = cursor.getString((cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));//音乐标题

            String artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));//艺术家

            long duration = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));//时长

            long size = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE));  //文件大小

            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));  //文件路径

            String album = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ALBUM)); //专辑名

            long album_id = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)); //唱片图片ID

            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐

            String track = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.TRACK)); //track

            if (isMusic != 0 && duration / (1000 * 60) >= 1) {     //只把1分钟以上的音乐添加到集合当中
                mp3Info.setId(id);
                mp3Info.setTitle(title);
                mp3Info.setArtist(artist);
                mp3Info.setDuration(duration);
                mp3Info.setSize(size);
                mp3Info.setUrl(url);
                mp3Info.setAlbum(album);
                mp3Info.setAlbum_id(album_id);
                mp3Info.setTrack(track);
                mp3Infos.add(mp3Info);
            }
        }
        cursor.close();
        return mp3Infos;
    }

   /* //给显示全部歌曲的listv设置适配器
    public void setListAdpter(Context context,List<Mp3Info> mp3Infos,ListView mMusicList) {

        List<HashMap<String, String>> mp3list = new ArrayList<HashMap<String, String>>();
        MusicListAdapter mAdapter = new MusicListAdapter(context,mp3Infos);
        mMusicList.setAdapter(mAdapter);
    }*/

    //给显示专辑的listview设置适配器
    public void setAlbumAdpter(Context context,List<Mp3Info> mp3Infos,ListView mMusicList) {

        List<HashMap<String, String>> mp3list = new ArrayList<HashMap<String, String>>();
        AlbumListAdapter mAdapter = new AlbumListAdapter(context,mp3Infos);
        mMusicList.setAdapter(mAdapter);
    }
}
