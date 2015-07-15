package com.hankkin.PCall.application;

import android.app.Application;
import android.view.WindowManager;

/**
 * Created by Hankkin on 2015/7/14.
 *
 */
public class PCallApplication extends Application{
    //need小助手悬浮窗需要用的权限
    private WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
    public WindowManager.LayoutParams getWindowParams() {
        return windowParams;
    }
}
