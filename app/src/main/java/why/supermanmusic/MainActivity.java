package why.supermanmusic;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ringdroid.RingdroidSelectActivity;
import com.umeng.analytics.MobclickAgent;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import why.supermanmusic.Base.BaseApplication;
import why.supermanmusic.ResideMenu.ResideMenu;
import why.supermanmusic.ResideMenu.ResideMenuItem;
import why.supermanmusic.fragment.HomeFragment;
import why.supermanmusic.fragment.MeFragment;
import why.supermanmusic.fragment.ScanFragment;
import why.supermanmusic.service.AudioPlayService;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ResideMenu resideMenu;
    private MainActivity mContext;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemScan;
    private ResideMenuItem itemSleep;
    private ResideMenuItem itemShare;
    private ResideMenuItem itemExit;
    private ResideMenuItem itemMe;
    private ResideMenuItem itemCut;
    private TextView mTitleBarName;
    private long exitTime;

    public static final String ALARM_CLOCK_BROADCAST = "alarm_clock_broadcast";
    private int mScreenWidth;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        exitTime = 0;

        mContext = this;
        setUpMenu();

        initView();
        initData();

        if (savedInstanceState == null)
            changeFragment(new HomeFragment());
    }

    private void initData() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;

        //注册定时睡眠的广播接收者
        IntentFilter filter = new IntentFilter();
        filter.addAction(ALARM_CLOCK_BROADCAST);
        registerReceiver(mAlarmReceiver, filter);
    }

    private void initView() {
        mTitleBarName = (TextView) findViewById(R.id.title_bar_name);

    }

    private void setUpMenu() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setUse3D(true);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.8f);

        // create menu items;
        itemHome = new ResideMenuItem(this, R.drawable.home, "主页");
        itemScan = new ResideMenuItem(this, R.drawable.scan, "歌曲扫描");
        itemSleep = new ResideMenuItem(this, R.drawable.sleep, "开启睡眠");
        itemCut = new ResideMenuItem(this, R.drawable.cut, "铃音剪辑");
        itemShare = new ResideMenuItem(this, R.drawable.share, "低调分享");
        itemExit = new ResideMenuItem(this, R.drawable.exit, "退出");
        itemMe = new ResideMenuItem(this, R.drawable.me, "关于我");

        itemHome.setOnClickListener(this);
        itemScan.setOnClickListener(this);
        itemSleep.setOnClickListener(this);
        itemShare.setOnClickListener(this);
        itemCut.setOnClickListener(this);
        itemExit.setOnClickListener(this);
        itemMe.setOnClickListener(this);

        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemScan, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemSleep, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemCut, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemShare, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemMe, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemExit, ResideMenu.DIRECTION_LEFT);

        // You can disable a direction by setting ->
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);

        findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });

    }

    @Override
    public void onClick(View view) {

        if (view == itemScan) {
            changeFragment(new ScanFragment());
            mTitleBarName.setText("歌曲扫描");
        } else if (view == itemSleep) {
            //            changeFragment(new SleepFragment());
            //            mTitleBarName.setText("开启睡眠");
            showSleepDialog();
        } else if (view == itemShare) {
            //            changeFragment(new ShareFragment());
            //            mTitleBarName.setText("低调分享");
            showShare();
        } else if (view == itemExit) {//退出
//            Toast.makeText(this, "已安全退出, 欢迎再次光临!", Toast.LENGTH_SHORT).show();
//            System.exit(0);
            openOptionsDialog();
        } else if (view == itemMe) {
            changeFragment(new MeFragment());
            mTitleBarName.setText("关于我");
        } else if (view == itemCut) {//铃音剪辑
//            changeFragment(new CutMusicFragment());
//            mTitleBarName.setText("铃音剪辑");
            Intent intent = new Intent(this, RingdroidSelectActivity.class);
            intent.setAction("android.intent.action.GET_CONTENT");
            intent.addCategory("android.intent.category.OPENABLE");
            startActivity(intent);

        } else if (view == itemHome) {
            changeFragment(new HomeFragment());

            mTitleBarName.setText("我的音乐");
        }

        resideMenu.closeMenu();
    }


    private void openOptionsDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("是否残忍离开?")
                .setCancelable(false)
                .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //友盟保存数据
                        MobclickAgent.onKillProcess(MainActivity.this);
                        //杀死进程
                        android.os.Process.killProcess(android.os.Process.myPid());
                        //停止服务
                        Intent intent = new Intent(MainActivity.this, AudioPlayService.class);
                        stopService(intent);
                    }
                })
                .setNegativeButton("不离开", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showSleepDialog() {
        if (BaseApplication.mIsSleepClockSetting) {
            cancleSleepClock();
            return;
        }

        View view = View.inflate(this, R.layout.sleep_time, null);
        final Dialog dialog = new Dialog(this, R.style.lrc_dialog);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        // lp.x = 100; // 新位置X坐标
        // lp.y = 100; // 新位置Y坐标
        lp.width = (int) (mScreenWidth * 0.7); // 宽度
        // lp.height = 400; // 高度

        // 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
        // dialog.onWindowAttributesChanged(lp);
        dialogWindow.setAttributes(lp);

        dialog.show();

        final Button cancleBtn = (Button) view.findViewById(R.id.cancle_btn);
        final Button okBtn = (Button) view.findViewById(R.id.ok_btn);
        final EditText timeEt = (EditText) view.findViewById(R.id.time_et);
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v == cancleBtn) {
                    dialog.dismiss();
                } else if (v == okBtn) {
                    String timeS = timeEt.getText().toString();
                    if (TextUtils.isEmpty(timeS)
                            || Integer.parseInt(timeS) == 0) {
                        Toast.makeText(getApplicationContext(), "输入无效！",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    setSleepClock(timeS);
                    dialog.dismiss();
                }
            }
        };

        cancleBtn.setOnClickListener(listener);
        okBtn.setOnClickListener(listener);
    }

    /**
     * 设置睡眠闹钟
     *
     * @param timeS
     */
    private void setSleepClock(String timeS) {
        Intent intent = new Intent(ALARM_CLOCK_BROADCAST);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, 0, intent, 0);
        // 设置time时间之后退出程序
        int time = Integer.parseInt(timeS);
        long longTime = time * 60 * 1000L;
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC, System.currentTimeMillis() + longTime,
                pendingIntent);
        BaseApplication.mIsSleepClockSetting = true;
        Toast.makeText(this, "程序将在" + timeS + "分钟后退出!", Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * 取消睡眠闹钟
     */
    private void cancleSleepClock() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("友情提示")
                .setMessage("是否取消睡眠模式?")
                .setNegativeButton("不, 继续开启!", null)
                .setPositiveButton("是的, 我要起床", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ALARM_CLOCK_BROADCAST);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                MainActivity.this, 0, intent, 0);
                        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                        am.cancel(pendingIntent);
                        BaseApplication.mIsSleepClockSetting = false;
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "已取消睡眠模式!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        builder.show();

    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            //            Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void closeMenu() {
            //            Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT).show();
        }
    };

    private void changeFragment(Fragment targetFragment) {
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    // What good method is to access resideMenu？
    public ResideMenu getResideMenu() {
        return resideMenu;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("分享超人音乐到---");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("超人音乐,一款超好用的本地音乐播放器, 由王浩宇开发, 推荐你来下载使用! ");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }

    private BroadcastReceiver mAlarmReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //退出程序
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mAlarmReceiver);

        cancleSleepClock();

        MobclickAgent.onKillProcess(this);
        System.exit(0);
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
