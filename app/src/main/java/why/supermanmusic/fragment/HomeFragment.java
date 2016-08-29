package why.supermanmusic.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import why.supermanmusic.R;
import why.supermanmusic.View.ChildViewPager;
import why.supermanmusic.View.FloatingActionButton;
import why.supermanmusic.View.ZoomOutPageTransformer;
import why.supermanmusic.activity.SongsPlayActivity;
import why.supermanmusic.bean.FindSongs;
import why.supermanmusic.bean.Mp3Info;
import why.supermanmusic.service.AudioPlayService;
import why.supermanmusic.utils.ServiceUtils;
import why.supermanmusic.utils.UIUtils;

/**
 * 创建者     Ted
 * 创建时间   2016/6/8 19:27
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    @InjectView(R.id.viewpagertab)
    SmartTabLayout mViewpagertab;
    @InjectView(R.id.home_viewpager)
    ChildViewPager mHomeViewpager;
    @InjectView(R.id.pink_icon)
    FloatingActionButton mPinkIcon;

    private View parentView;
    private FragmentPagerItemAdapter mAdapter;

    private String[] tabs = {
            "全部歌曲", "我的最爱", "最近播放", "专辑唱片"
    };
    private ArrayList<Mp3Info> mMp3Infos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.home, container, false);
        ButterKnife.inject(this, parentView);

        initData();
        initEvent();

        return parentView;
    }

    private void initEvent() {
        mPinkIcon.setOnClickListener(this);
    }

    private void initData() {
        //设置页签的数据
        FragmentPagerItems pages = new FragmentPagerItems(getActivity());

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), FragmentPagerItems.with(getActivity())
                .add(tabs[0], AllSongsFragment.class)
                .add(tabs[1], MyFavoriteFragment.class)
                .add(tabs[2], RecentPlayedFragment.class)
                .add(tabs[3], AlbumsRecordsFragment.class)
                .create());

        mHomeViewpager.setAdapter(adapter);
        //设置切换动画效果
        mHomeViewpager.setPageTransformer(true, new ZoomOutPageTransformer());

        mViewpagertab.setViewPager(mHomeViewpager);

        //获取数据
        getSongsData();

    }

    private void getSongsData() {
        new Thread() {
            @Override
            public void run() {
                FindSongs findSongs = new FindSongs();
                mMp3Infos = (ArrayList<Mp3Info>) findSongs.getMp3Infos(getContext().getContentResolver());
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        if (ServiceUtils.isServiceOpen(getContext(), "why.supermanmusic.service.AudioPlayService")) {

            Intent intent = new Intent(getContext(), SongsPlayActivity.class);
            intent.putExtra("from", AudioPlayService.FROM_CONTENT);
            intent.putExtra("list", mMp3Infos);
            startActivity(intent);
        } else {
            Toast.makeText(UIUtils.getContext(), "请先播放歌曲再进入", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("HomeFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("HomeFragment");
    }
}
