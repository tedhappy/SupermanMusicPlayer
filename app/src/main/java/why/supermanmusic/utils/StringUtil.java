package why.supermanmusic.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ThinkPad on 2016/6/10.
 */
public class StringUtil {
    private static final int HOUR = 60 * 60 * 1000;
    private static final int MIN = 60 * 1000;
    private static final int SEC = 1000;

    public static String parseDuration(int duration) {
        int hour = duration / HOUR;
        int min = duration % HOUR / MIN;
        int sec = duration % HOUR % MIN / SEC;
        if (hour == 0) {
            return String.format("%02d:%02d", min, sec);//%02d %结果为字面值  0如果结果不足给定的位数(2位)，用0补齐  2：最多2位  d：格式化为十进制整数
        } else {
            return String.format("%02d:%02d:%02d", hour, min, sec);//%02d %结果为字面值  0如果结果不足给定的位数(2位)，用0补齐  2：最多2位  d：格式化为十进制整数
        }
    }
    public static String psrseTime(){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }
}
