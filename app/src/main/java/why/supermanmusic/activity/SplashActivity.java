package why.supermanmusic.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;

import com.umeng.analytics.MobclickAgent;

import why.supermanmusic.MainActivity;
import why.supermanmusic.R;
import why.supermanmusic.config.Constants;
import why.supermanmusic.utils.SpTools;

/**
 * Splash界面
 */
public class SplashActivity extends Activity implements AnimationListener {

	private RelativeLayout mIv_splash;
	private AlphaAnimation mAa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initView();
		initEvent();
	}

	private void initEvent() {
		//开启动画
		startAnimation();
		
		//动画的监听
		mAa.setAnimationListener(this);
	}

	private void startAnimation() {
		//透明度动画
		mAa = new AlphaAnimation(0, 1);
		
		//持续时间
		mAa.setDuration(1000);
		
		mIv_splash.startAnimation(mAa);
	}

	private void initView() {
		setContentView(R.layout.activity_splash);
		mIv_splash = (RelativeLayout) findViewById(R.id.iv_splash);
	}

	@Override
	public void onAnimationStart(Animation animation) {
		
	}

	//在动画播放结束的时候,跳转
	@Override
	public void onAnimationEnd(Animation animation) {
		//判断是否已经进入过向导界面
		if(SpTools.getBoolean(this, Constants.ISGUIDE, false)){
			//进入主界面
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}else{
			//进入向导界面
			Intent intent = new Intent(this, GuideActivity.class);
			startActivity(intent);
		}
		finish();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
}
