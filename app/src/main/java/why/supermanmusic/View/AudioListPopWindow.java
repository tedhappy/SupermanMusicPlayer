package why.supermanmusic.View;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import why.supermanmusic.R;
import why.supermanmusic.adapter.AudioPopAdapter;

public class AudioListPopWindow extends PopupWindow {

    public AudioListPopWindow(Context context, AudioPopAdapter adapter, AdapterView.OnItemClickListener onItemClickListener){
        View view = View.inflate(context, R.layout.audio_popwindow,null);
        //初始化listview
        final ListView audio_popwindow_list = (ListView) view.findViewById(R.id.audio_popwindow_list);
        //适配
        audio_popwindow_list.setAdapter(adapter);
        //条目点击事件
        audio_popwindow_list.setOnItemClickListener(onItemClickListener);
        //设置popwindow的布局
        setContentView(view);

        //设置可获取焦点
        setFocusable(true);
        //设置背景 可响应后退按键
        setBackgroundDrawable(new BitmapDrawable());

        //设置popwindow的宽度和高度
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        //设置进入和退出的动画
        setAnimationStyle(R.style.audio_pop);
        //设置触摸事件
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //当手指触摸上部  隐藏popwindow
                if(event.getY()<audio_popwindow_list.getTop()){
                    dismiss();
                }
                return false;
            }
        });
    }
}
