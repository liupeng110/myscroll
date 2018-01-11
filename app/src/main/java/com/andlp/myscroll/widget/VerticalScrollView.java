package com.andlp.myscroll.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.orhanobut.logger.Logger;

 //只拦截垂直滑动事件,用来解决 ScrollView里面嵌套ViewPager      gdutxiaoxu@163.com
public class VerticalScrollView extends ScrollView {   //demo中这是最外层01

    public VerticalScrollView(Context context) {  super(context);  }
    public VerticalScrollView(Context context, AttributeSet attrs) {  super(context, attrs);  }
    public VerticalScrollView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr);  }
    @TargetApi(21) public VerticalScrollView(Context context, AttributeSet attrs, int defStyleAttr, int  defStyleRes) {  super(context, attrs, defStyleAttr, defStyleRes); }

    private float mDownPosX = 0;
    private float mDownPosY = 0;//当前按下的x y坐标

    @Override public boolean onInterceptTouchEvent(MotionEvent ev) { //判断是否拦截触摸事件
        Logger.i("进入 onInterceptTouchEvent ");
        final float x = ev.getX();
        final float y = ev.getY();
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Logger.i("进入 onInterceptTouchEvent 的down 不做其他处理");
                mDownPosX = x;     //记录按下时的坐标位置
                mDownPosY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaX = Math.abs(x - mDownPosX);     //绝对值判断方向
                final float deltaY = Math.abs(y - mDownPosY);
                if (deltaX > deltaY) { //这里根据屏幕坐标系 判断结果为横向滑动
                    Logger.i("进入 onInterceptTouchEvent 判断是否左右滑动  : "+ (deltaX > deltaY));
                    return false;   // 事件传递给子控件  (因该scroll中有viewpager            等左右滑动的控件所以这里需要传递下去)
                }                         //其他的(垂直滑动)  自己处理(不传递给子控件)               明显的效果就是该scroll里面的viewpager等  可以上下滑动
        }
        Logger.i("进入 onInterceptTouchEvent 返回onInterceptTouchEvent事件的super中");
        return super.onInterceptTouchEvent(ev);
    }
}
