package com.andlp.myscroll.fragment;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 717219917@qq.com      2018/1/11  14:33
 */

public class MyListview extends ListView{
    public MyListview(Context context) { super(context);  }
    public MyListview(Context context, AttributeSet attrs) {  super(context, attrs); }
    public MyListview(Context context, AttributeSet attrs,  int defStyle) { super(context, attrs, defStyle);  }

    //解决scrollview嵌套listview  listview显示不完整    (recycleview没有这个问题)
    @Override   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
