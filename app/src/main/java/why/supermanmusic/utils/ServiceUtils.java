package why.supermanmusic.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

/**
 * 创建者     Ted
 * 创建时间   2016/6/19 13:30
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ServiceUtils {


    /**
     * 判断服务是否开启的工具类
     */
    public static boolean isServiceOpen(Context context, String serviceName){

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取到手机中所有运行的服务
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(Integer.MAX_VALUE);
        //遍历
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            ComponentName componentName = runningServiceInfo.service;
            String className = componentName.getClassName();
            if(serviceName.equals(className)){
                return true;
            }
        }
        return false;
    }
}
