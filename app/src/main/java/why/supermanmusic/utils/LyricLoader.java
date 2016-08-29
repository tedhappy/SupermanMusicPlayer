package why.supermanmusic.utils;

import android.os.Environment;

import java.io.File;

/**
 * 加载歌词文件
 * Created by ThinkPad on 2016/6/14.
 */
public class LyricLoader {
    private static final File dirFile1 = new File(Environment.getExternalStorageDirectory(), "/Musiclrc");
    private static final File dirFile2 = new File(Environment.getExternalStorageDirectory(), "/netease/cloudmusic/download/lyric");
    private static final File dirFile3 = new File(Environment.getExternalStorageDirectory(), "/qqmusic");
    private static final File dirFile4 = new File(Environment.getExternalStorageDirectory(), "/kgmusic/download");

    public static File loadLyric(String title) {
        //创建歌词file
        if (dirFile1.exists() && dirFile1.length() != 0) {
            File file1 = new File(dirFile1, title + ".lrc");

            return file1;
        }
        if (dirFile2.exists() && dirFile2.length() != 0) {
            File file2 = new File(dirFile2, title + ".lrc");

            return file2;
        }
        if (dirFile3.exists() && dirFile3.length() != 0) {
            File file3 = new File(dirFile3, title + ".lrc");

            return file3;
        }
        if (dirFile4.exists() && dirFile4.length() != 0) {
            File file4 = new File(dirFile4, title + ".lrc");

            return file4;
        }

        File file = new File(dirFile1, title + ".lrc");
        return file;

    }
}
