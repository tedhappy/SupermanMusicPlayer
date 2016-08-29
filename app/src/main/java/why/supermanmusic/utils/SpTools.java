package why.supermanmusic.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Sp的工具类
 */
public class SpTools {
	/**
	 * 保存字符串数据
	 * @param context
	 */
	public static void putString(Context context,String key,String value){
		SharedPreferences sp = context.getSharedPreferences("config.xml", 0);
		//在存放的时候需要获取到sp的编辑器
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}
	/**
	 * 获取字符串数据
	 * @param context
	 */
	public static String getString(Context context,String key,String value){
		SharedPreferences sp = context.getSharedPreferences("config.xml", 0);
		String string = sp.getString(key, value);
		return string;
	}
	
	/**
	 * 保存布尔数据
	 * @param context
	 */
	public static void putBoolean(Context context,String key,boolean value){
		SharedPreferences sp = context.getSharedPreferences("config.xml", 0);
		//在存放的时候需要获取到sp的编辑器
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	/**
	 * 获取布尔数据
	 * @param context
	 */
	public static boolean getBoolean(Context context,String key,boolean defValue){
		SharedPreferences sp = context.getSharedPreferences("config.xml", 0);
		boolean string = sp.getBoolean(key, defValue);
		return string;
	}
	/**
	 * 保存int数据
	 * @param context
	 */
	public static void putInt(Context context,String key,int value){
		SharedPreferences sp = context.getSharedPreferences("config.xml", 0);
		//在存放的时候需要获取到sp的编辑器
		Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	/**
	 * 获取int数据
	 * @param context
	 */
	public static int getInt(Context context,String key,int defValue){
		SharedPreferences sp = context.getSharedPreferences("config.xml", 0);
		int string = sp.getInt(key, defValue);
		return string;
	}
}

