package why.supermanmusic.Base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.umeng.analytics.MobclickAgent;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import why.supermanmusic.R;

/**
 * @创建者	 Administrator
 * @创时间 	 2015-9-17 上午11:08:40
 * @描述	     Application是一个单例,全局的盒子,变量,方法
 *
 * @版本       $Rev: 3 $
 * @更新者     $Author: admin $
 * @更新时间    $Date: 2015-09-17 11:28:39 +0800 (星期四, 17 九月 2015) $
 * @更新描述    TODO
 */

@ReportsCrashes(formUri = "http://yourserver.com/yourscript",
		mode = ReportingInteractionMode.TOAST,
		forceCloseDialogAfterToast = false, // optional, default false
		resToastText = R.string.crash_toast_text)
public class BaseApplication extends Application {

	private static Context	mContext;
	private static Handler	mHandler;
	private static long		mMainThreadId;

	public static boolean mIsSleepClockSetting = false;

	public static Context getContext() {
		return mContext;
	}

	public static Handler getHandler() {
		return mHandler;
	}

	public static long getMainThreadId() {
		return mMainThreadId;
	}

	@Override
	public void onCreate() {// 程序的入口方法

		ACRA.init(this);

		// 1.上下文
		mContext = getApplicationContext();

		// 2.创建一个主线程中的hanlder
		mHandler = new Handler();

		// 3.得到主线程的id
		mMainThreadId = android.os.Process.myTid();

		initUmeng(getApplicationContext());

		super.onCreate();
	}

	private void initUmeng(Context context) {
		MobclickAgent.setScenarioType(context, MobclickAgent.EScenarioType.E_UM_NORMAL);

		MobclickAgent.openActivityDurationTrack(false);

	}

}
