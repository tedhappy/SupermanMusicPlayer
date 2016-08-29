package why.supermanmusic.fragment;

import android.content.ContentResolver;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import why.supermanmusic.Base.BaseApplication;
import why.supermanmusic.R;
import why.supermanmusic.utils.UIUtils;

/**
 * User: special
 * Date: 13-12-22
 * Time: 下午1:31
 * Mail: specialcyci@gmail.com
 */
public class ScanFragment extends Fragment implements View.OnClickListener {

    @InjectView(R.id.iv_scan)
    ImageView mIvScan;
    @InjectView(R.id.tv_start_scan)
    TextView mTvStartScan;
    private ContentResolver mResolver;
    private AnimationDrawable mDrawable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        ButterKnife.inject(this, view);

        initData();
        initEvent();

        return view;
    }

    private void initEvent() {
        mTvStartScan.setOnClickListener(this);
    }

    private void initData() {
        mResolver = getContext().getContentResolver();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(UIUtils.getContext(), "正在扫描, 请稍后...", Toast.LENGTH_SHORT).show();
        //动画
        startScanAnim();
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(3000);
                BaseApplication.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        mDrawable.stop();
                        Toast.makeText(UIUtils.getContext(), "扫描完成", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();
    }

    private void startScanAnim() {
        mDrawable = (AnimationDrawable) mIvScan.getBackground();
        mDrawable.start();
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ScanFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ScanFragment");
    }
}
