package why.supermanmusic.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import why.supermanmusic.R;
import why.supermanmusic.bean.LyricBean;
import why.supermanmusic.utils.LyricParser;



public class LyricTextView extends TextView{

    private Paint paint;
    private int green;
    private float bigSize;
    private int viewW;
    private int viewH;
    private ArrayList<LyricBean> lyricBeens;
    private int centerLine;
    private int lineHeight;
    private float smallSize;
    private int white;
    private int mProgress;
    private int mDuration;

    public LyricTextView(Context context) {
        super(context);
        initView();
    }

    public LyricTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LyricTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public LyricTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }
    //初始化操作
    private void initView() {
        paint = new Paint();
        green = getResources().getColor(R.color.pbcolor);
        white = getResources().getColor(R.color.white);
        bigSize = getResources().getDimension(R.dimen.bigSize);
        smallSize = getResources().getDimension(R.dimen.smallSize);
        //行高
        lineHeight = getResources().getDimensionPixelOffset(R.dimen.lineHeight);
//        //创建歌词集合
//        lyricBeens = new ArrayList<LyricBean>();
//        for (int i = 0; i < 30; i++) {
//            lyricBeens.add(new LyricBean(i*2000,"正在播放第"+i+"行歌词"));
//        }
//        //指定中间行位置
//        centerLine = 10;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewW = w;
        viewH = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        drawSingleLine(canvas);
        drawMultipleLine(canvas);
    }

    private void drawMultipleLine(Canvas canvas) {
////行可用时间
        int lineTime;
        if(lyricBeens==null) return;
        if(centerLine==lyricBeens.size()-1){
//        当中间行为最后一行时：
//        行可用时间=总时长-最后一行开始时间
            lineTime = mDuration-lyricBeens.get(centerLine).getStartTime();
        }else{
//        当中间行不为最后一行时：
//        行可用时间=下一行开始时间-中间行开始时间
            lineTime = lyricBeens.get(centerLine+1).getStartTime()-lyricBeens.get(centerLine).getStartTime();
        }
//        偏移进度=进度-中间行开始时间
        int progressOffset = mProgress-lyricBeens.get(centerLine).getStartTime();
//        偏移百分比=偏移进度/行可用时间
        float offsetPercent = progressOffset/(float)lineTime;
//        偏移y=进度偏移百分比*行高
        float offsetY = offsetPercent*lineHeight;


        String text = lyricBeens.get(centerLine).getContent();
        paint.setTextSize(bigSize);
        paint.setColor(green);
        Rect bounds = new Rect();
        paint.getTextBounds(text,0,text.length(),bounds);
//        float textW = bounds.width();
        float textH = bounds.height();
//        x=viewW/2-textW/2
//        float x = viewW/2-textW/2;
//        y=viewH/2+textH/2
//        中间行最终y=原来中间行y-偏移y
        float y = viewH/2+textH/2-offsetY;


        for (int i = 0; i < lyricBeens.size(); i++) {
            //判断为中间行 字体大小和颜色变化
            if(i==centerLine){
                paint.setTextSize(bigSize);
                paint.setColor(green);
            }else {
                paint.setTextSize(smallSize);
                paint.setColor(white);
            }
//        x=viewW/2-textW/2
            String currentText = lyricBeens.get(i).getContent();

//            paint.getTextBounds(currentText,0,currentText.length(),bounds);
//            float currentW = bounds.width();
            //用另外的方式求text的宽度
            float currentW = paint.measureText(currentText,0,currentText.length());
            float currentX = viewW/2-currentW/2;
//        y=centerY+(position-centerPosition)*行高
            float currentY = y+(i-centerLine)*lineHeight;
             canvas.drawText(currentText,0,currentText.length(),currentX,currentY,paint);

        }


    }

    private void drawSingleLine(Canvas canvas) {
        String text = "正在加载歌词...";
        paint.setTextSize(bigSize);
        paint.setColor(green);
        Rect bounds = new Rect();
        paint.getTextBounds(text,0,text.length(),bounds);
        float textW = bounds.width();
        float textH = bounds.height();
//        x=viewW/2-textW/2
        float x = viewW/2-textW/2;
//        y=viewH/2+textH/2
        float y = viewH/2+textH/2;
        canvas.drawText(text,0,text.length(),x,y,paint);
    }
    public void rollText(int progress,int duration){
        mProgress = progress;
        mDuration = duration;
//        播放进度>=最后一行的开始时间 最后一行是中间行
            if(progress>=lyricBeens.get(lyricBeens.size()-1).getStartTime()){
                centerLine = lyricBeens.size()-1;
            }else {
                //其他行是中间行
//        播放进度>=中间行的开始时间
//        播放进度<下一行开始时间
                for (int i = 0; i < lyricBeens.size()-1; i++) {
                    if(progress>=lyricBeens.get(i).getStartTime()&&progress<lyricBeens.get(i+1).getStartTime()){
                        centerLine = i;
                    }
                }
            }
        //刷新
        invalidate();
    }
    //加载歌词
    public void setFile(File file){
        lyricBeens = LyricParser.parseLyric(file);
        //中间行设置为0
        centerLine = 0;
    }
}
