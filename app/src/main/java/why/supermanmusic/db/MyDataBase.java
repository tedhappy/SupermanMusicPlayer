package why.supermanmusic.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import why.supermanmusic.R;
import why.supermanmusic.adapter.AudioListAdapter;

public class MyDataBase {

    private int dataCount;
    private static Context context;
    public static ListAdapter adapter;

    public final static String DATABASE_NAME   = "MyMusicDataBase";
    public final static String TABLE_NAME      = "RecentlyPlayTable1";
    public final static String TABLE_NAME_FAVORITE = "FavoriteTable";
    public final static String TABLE_MUSIC_ID  = "_id";
    public final static String TABLE_MUSIC_TITLE = "music_title";
    public final static String TABLE_ARTIST = "artist";
    public final static String TABLE_DURATION = "druation";
    public final static String TABLE_MUSIC_URL = "music_url";
    public final static String TABLE_ALBUM_ID = "album_id";
    public final static String TABLE_CURRENT_TIME = "current_time";
    public final static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" + TABLE_MUSIC_ID + " INTEGER PRIMARY KEY,"
            + TABLE_MUSIC_TITLE + " STRING," + TABLE_ARTIST + " STRING,"
            + TABLE_DURATION + " LONG," + TABLE_MUSIC_URL + " STRING,"
            + TABLE_ALBUM_ID + " INTEGER," + TABLE_CURRENT_TIME + " LONG)";

    public final static String CREATE_FAVORITE_TABLE =  "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME_FAVORITE + " (" + TABLE_MUSIC_ID + " INTEGER PRIMARY KEY,"
            + TABLE_MUSIC_TITLE + " STRING," + TABLE_ARTIST + " STRING,"
            + TABLE_DURATION + " LONG," + TABLE_MUSIC_URL + " STRING,"
            + TABLE_ALBUM_ID + " INTEGER," + TABLE_CURRENT_TIME + " LONG)";

    public static SQLiteDatabase recentPlaySQLiteDatabase = null;                //最近播放的数据库


    public MyDataBase(Context context,ListView listView){
        this.context = context;
        this.dataCount = 0;
        this.adapter = null;
    }

    public static void CreateDataBase(){
        recentPlaySQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME,Context.MODE_PRIVATE,null);
//        recentPlaySQLiteDatabase.delete(TABLE_NAME,null,null);
//        recentPlaySQLiteDatabase.delete(TABLE_NAME_FAVORITE,null,null);
        recentPlaySQLiteDatabase.execSQL(CREATE_TABLE);
        recentPlaySQLiteDatabase.execSQL(CREATE_FAVORITE_TABLE);
    }

    public static void AddData(String table_name,int music_id,String music_title,
                        String music_artist,long music_duration,
                        String music_url,int album_id,long current_time){
        ContentValues cv = new ContentValues();
        cv.put(TABLE_MUSIC_ID,music_id);
        cv.put(TABLE_MUSIC_TITLE,music_title);
        cv.put(TABLE_ARTIST,music_artist);
        cv.put(TABLE_DURATION, AudioListAdapter.formatTime(music_duration));
        cv.put(TABLE_MUSIC_URL,music_url);
        cv.put(TABLE_ALBUM_ID,album_id);
        cv.put(TABLE_CURRENT_TIME,current_time);

        recentPlaySQLiteDatabase.insert(table_name,null,cv);
    }

    public static void AddData(String table_name,int music_id,String music_title,
                               String music_artist,long music_duration,
                               String music_url,int album_id){
        ContentValues cv = new ContentValues();
        cv.put(TABLE_MUSIC_ID,music_id);
        cv.put(TABLE_MUSIC_TITLE,music_title);
        cv.put(TABLE_ARTIST,music_artist);
        cv.put(TABLE_DURATION,AudioListAdapter.formatTime(music_duration));
        cv.put(TABLE_MUSIC_URL,music_url);
        cv.put(TABLE_ALBUM_ID,album_id);

        recentPlaySQLiteDatabase.insert(table_name,null,cv);
    }

    public static void UpdateData(String table_name,int music_id,long current_time){

        String str = "UPDATE " + table_name + " SET " + TABLE_CURRENT_TIME + "=" + current_time + " WHERE "
                + TABLE_MUSIC_ID + "=" + music_id;

        recentPlaySQLiteDatabase.execSQL(str);
    }

    public static boolean IsExistData(String table_name,int music_id){

        String str = "SELECT * FROM " + table_name + " WHERE " + TABLE_MUSIC_ID + "=" +music_id;

        if(recentPlaySQLiteDatabase.rawQuery(str,null).getCount() == 0)
            return false;
        else
            return true;
    }

    public static String FindMusicUrl(int position){
        String url = null;
        String str = "SELECT " + MyDataBase.TABLE_MUSIC_URL
                + " FROM " + MyDataBase.TABLE_NAME;

        Cursor cur = MyDataBase.recentPlaySQLiteDatabase.rawQuery(str,null);

        while(cur.moveToNext()){
            int i = 0;
            System.out.println(cur.getString(i));
            if(i == position-1){
                url = cur.getString(i);
            }
            i++;
        }
        cur.close();
        return url;
    }

    public static void UpdateAdapter(){
        try{
        Cursor cur = recentPlaySQLiteDatabase.
                query(TABLE_NAME,new String[] {
                        TABLE_MUSIC_ID,
                        TABLE_MUSIC_TITLE,
                        TABLE_ARTIST,
                        TABLE_DURATION,
                        TABLE_MUSIC_URL,
                        TABLE_ALBUM_ID}
                        ,null,null,null,null,null);

        if(cur !=null && cur.getCount() >= 0){
            adapter = new SimpleCursorAdapter(
                    context,
                    R.layout.recently_play_item_layout,
                    cur,
                    new String[] {
                            TABLE_MUSIC_TITLE,
                            TABLE_ARTIST,
                            TABLE_DURATION},
                    new int[] {R.id.recently_play_music_title,
                            R.id.recently_play_music_Artist,
                            R.id.recently_play_music_duration}
            );
        }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void DeleteData(String table_name,int music_id){

        String str = "DELETE FROM " + MyDataBase.TABLE_NAME_FAVORITE +
                " WHERE " + MyDataBase.TABLE_MUSIC_ID + "=" +music_id;

        recentPlaySQLiteDatabase.execSQL(str);
        System.out.println("-------------------数据删除成功-------------");
    }

    public static void SetAdapter(ListView listView){

        listView.setAdapter(adapter);
    }

    public static void Update_adapter(){
        try{
            Cursor cur = recentPlaySQLiteDatabase.
                    query(TABLE_NAME_FAVORITE,new String[] {
                            TABLE_MUSIC_ID,
                            TABLE_MUSIC_TITLE,
                            TABLE_ARTIST,
                            TABLE_DURATION,
                            TABLE_MUSIC_URL,
                            TABLE_ALBUM_ID}
                            ,null,null,null,null,null);

            if(cur !=null && cur.getCount() >= 0){
                adapter = new SimpleCursorAdapter(
                        context,
                        R.layout.recently_play_item_layout,
                        cur,
                        new String[] {
                                TABLE_MUSIC_TITLE,
                                TABLE_ARTIST,
                                TABLE_DURATION},
                        new int[] {R.id.recently_play_music_title,
                                R.id.recently_play_music_Artist,
                                R.id.recently_play_music_duration}
                );
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


}
