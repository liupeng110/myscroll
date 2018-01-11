package com.andlp.myscroll.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.orhanobut.logger.Logger;

public class ChildViewPager extends ViewPager {//demo中第三层03   这个viewpager包含四个图片

    public ChildViewPager(Context context) {  super(context);  }
    public ChildViewPager(Context context, AttributeSet attrs) {  super(context, attrs);  }

    @Override  public boolean dispatchTouchEvent(MotionEvent ev) {
        Logger.i("进入 dispatchTouchEvent ");
        int curPosition;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Logger.i("进入 dispatchTouchEvent 按下 保证子view能够接收Touch事件");
                getParent().requestDisallowInterceptTouchEvent(true);  break;  //首先保证所有子view可以接收到ACTION_DOWM事件
            case MotionEvent.ACTION_MOVE:                                                      //然后判断move时候  哪里处理
                curPosition = this.getCurrentItem();                                                //获取当前viewpager是第几个
                    int count = this.getAdapter().getCount();
                Logger.i("进入 dispatchTouchEvent 移动时候 当前为第一/最后页 结果 ："+(curPosition == count - 1 || curPosition == 0));
                if (curPosition == count - 1 || curPosition == 0) {                    // 当前页面在最后一页和第0页的时候 ,由上层拦截触摸事件
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true);     //其他页面由子控件拦截  (如果子控件不拦截 就自己拦截)
                }
        }

        return super.dispatchTouchEvent(ev);
    }


}
