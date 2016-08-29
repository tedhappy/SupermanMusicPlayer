package why.supermanmusic.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 创建者     Ted
 * 创建时间   2016/6/11 22:44
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class Mp3Info implements Serializable {

    private long id;
    private long album_id;
    private String title;
    private String artist;
    private long duration;
    private long size;
    private String url;
    private String album;
    private String track;
    private int isMusic;
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private boolean isFavorite;

    public static ArrayList<Mp3Info> getMp3Infos(Cursor cursor){
        //创建列表集合
        ArrayList<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
        //判断cursor是否为空
        if(cursor==null||cursor.getCount()==0) return mp3Infos;
        //解析cursor
        //移动游标到-1位
        cursor.moveToPosition(-1);

        while (cursor.moveToNext()){
            //解析当前游标的cursor获取Mp3Info
            Mp3Info Mp3Info = getMp3Info(cursor);
            //添加到集合中
            mp3Infos.add(Mp3Info);
        }
        //返回集合
        return mp3Infos;
    }
    public static Mp3Info getMp3Info(Cursor cursor){
        //创建Mp3Info
        Mp3Info mp3Info = new Mp3Info();
        //判断cursor是否为空
        if(cursor==null||cursor.getCount()==0) return mp3Info;
        //解析cursor
        mp3Info.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
        mp3Info.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
        mp3Info.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));

        mp3Info.setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));

        mp3Info.setId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
        mp3Info.setAlbum_id(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));

        //返回Mp3Info
        return mp3Info;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(long album_id) {
        this.album_id = album_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getIsMusic() {
        return isMusic;
    }

    public void setIsMusic(int isMusic) {
        this.isMusic = isMusic;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    @Override
    public String toString() {
        return "Mp3Info{" +
                "id=" + id +
                ", album_id=" + album_id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", url='" + url + '\'' +
                ", album='" + album + '\'' +
                ", track='" + track + '\'' +
                ", isMusic=" + isMusic +
                ", isFavorite=" + isFavorite +
                '}';
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }
}
