package why.supermanmusic.utils;


import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;

import why.supermanmusic.Base.BaseApplication;

/**
 * @创建者	 Administrator
 * @创时间 	 2015-10-20 上午10:16:38
 * @描述	     和ui相关的类
 *
 * @版本       $Rev: 11 $
 * @更新者     $Author: admin $
 * @更新时间    $Date: 2015-10-20 17:13:37 +0800 (星期二, 20 十月 2015) $
 * @更新描述    TODO
 */
public class UIUtils {
	/**得到上下文*/
	public static Context getContext() {
		return BaseApplication.getContext();
	}

	/**得到Resource对象*/
	public static Resources getResources() {
		return getContext().getResources();
	}

	/**得到string.xml中的字符*/
	public static String getString(int resId) {
		return getResources().getString(resId);
	}

	/**得到string.xml中的字符数组*/
	public static String[] getStringArr(int resId) {
		return getResources().getStringArray(resId);
	}

	/**得到color.xml中的颜色值*/
	public static int getColor(int resId) {
		return getResources().getColor(resId);
	}

	/**得到应用程序的包名*/
	public static String getPackageName() {
		return getContext().getPackageName();
	}

	/**得到主线程的Id*/
	public static long getMainThreadId() {
		return BaseApplication.getMainThreadId();
	}

	/**得到主线程的hanlder*/
	public static Handler getMainThreadHandler() {
		return BaseApplication.getHandler();
	}

	/**安全的执行一个task
	 * @return */
	public static void postTaskSafely(Runnable task) {
		// 当前线程==子线程,通过消息机制,把任务交给主线程的Handler去执行
		// 当前线程==主线程,直接执行任务
		int curThreadId = android.os.Process.myTid();
		long mainThreadId = getMainThreadId();
		if (curThreadId == mainThreadId) {
			task.run();
		} else {
			getMainThreadHandler().post(task);
		}
	}
}
