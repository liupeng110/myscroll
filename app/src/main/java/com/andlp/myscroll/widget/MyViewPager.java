package com.andlp.myscroll.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.orhanobut.logger.Logger;

//该viewpager用来解决ScrollView里面嵌套zViewPager的 内部解决法的          gdutxiaoxu@163.com
public class MyViewPager extends ViewPager {//demo中这是第二外层 02    这个viewpager包含四个大的item项  我的  首页 等

    int lastX = -1;//最后一次x坐标
    int lastY = -1;//最后一次y坐标

    public MyViewPager(Context context) { super(context);  }
    public MyViewPager(Context context, AttributeSet attrs) { super(context, attrs);  }

    @Override  public boolean dispatchTouchEvent(MotionEvent ev) {
        Logger.i("进入 dispatchTouchEvent ");
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();
        int dealtX = 0;
        int dealtY = 0;

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Logger.i("进入 dispatchTouchEvent -按下  保证子view能够接收Touch事件");
                getParent().requestDisallowInterceptTouchEvent(true);  break;// 按下之后 先要保证子View能够接收到Action_move事件
            case MotionEvent.ACTION_MOVE:
                dealtX += Math.abs(x - lastX);
                dealtY += Math.abs(y - lastY);
                Logger.i("进入 dispatchTouchEvent 判断 滑动方向是否横向："+(dealtX >= dealtY));

                //这里没有判断是否 viewpager的第一页和最后页
                if (dealtX >= dealtY) {  //横向滑动  自己拦截
                    getParent().requestDisallowInterceptTouchEvent(true);//当前view处理
                } else {                            //垂直滑动   交给上层拦截
                    getParent().requestDisallowInterceptTouchEvent(false);//返回上级进行处理
                }
                lastX = x;
                lastY = y;  break;
            case MotionEvent.ACTION_CANCEL:  break;
            case MotionEvent.ACTION_UP: break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
