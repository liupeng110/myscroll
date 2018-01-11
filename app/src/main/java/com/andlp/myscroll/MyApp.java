package com.andlp.myscroll;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;


/**BaseFragmentAdapter
 * 717219917@qq.com      2017/12/26  10:42
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashUtil.getInstance().init(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }


}
