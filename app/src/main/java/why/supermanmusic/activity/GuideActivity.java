package why.supermanmusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.umeng.analytics.MobclickAgent;

import why.supermanmusic.MainActivity;
import why.supermanmusic.R;
import why.supermanmusic.View.SampleSlide;
import why.supermanmusic.config.Constants;
import why.supermanmusic.utils.SpTools;

public class GuideActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(SampleSlide.newInstance(R.layout.intro_2));
        addSlide(SampleSlide.newInstance(R.layout.intro2_2));
        addSlide(SampleSlide.newInstance(R.layout.intro3_2));

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);

        setZoomAnimation();
    }

    private void loadMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        //保存状态到sp
        SpTools.putBoolean(GuideActivity.this, Constants.ISGUIDE, true);

        Toast.makeText(getApplicationContext(), "欢迎来到超人音乐!", Toast.LENGTH_SHORT).show();
        loadMainActivity();
    }

    public void getStarted(View v){
        loadMainActivity();
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
