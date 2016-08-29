package why.supermanmusic.utils;

import android.database.Cursor;

/**
 * Created by ThinkPad on 2016/6/10.
 */
public class CursorUtil {
    public static void cursorLog(Cursor cursor){
        //判断cursor是否为空
        if(cursor==null||cursor.getCount()==0) return;
        //打印
        while (cursor.moveToNext()){
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                LogUtils.d("logCursor","key:"+cursor.getColumnName(i)+",value:"+cursor.getString(i));
            }
        }
    }
}
