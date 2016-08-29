package why.supermanmusic.config;


import why.supermanmusic.utils.LogUtils;

/**
 * 创建者     Ted
 * 创建时间   2016/5/3 19:44
 * 描述	      常量类
 * <p/>
 * 更新者     $Author: jhy $
 * 更新时间   $Date: 2016-05-21 22:53:28 +0800 (周六, 21 五月 2016) $
 * 更新描述   ${TODO}
 */
public class Constants {
    /*
    LogUtils.LEVEL_ALL: 开启日志,显示所有的日志输出
    LogUtils.LEVEL_OFF: 关闭日志,屏蔽所有的日志输出
    */
    public static final int DEBUGLEVEL = LogUtils.LEVEL_ALL;//日志的级别


    public static final int GETBINDER = 100;


    public static final int OPENSERVICE = 101;

    public static final int LIST_PLAY = 102;
    public static final int SHOW_PLAY = 103;
    public static final String ISGUIDE = "isguide";
}
