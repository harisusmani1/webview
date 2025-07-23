package com.just.agentweb.sample.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;

import com.just.agentweb.AgentWebCompat;
import com.just.agentweb.sample.service.WebService;
import com.queue.library.GlobalQueue;

/**
 * Created by cenxiaozhong on 2017/5/23.
 * Source code: https://github.com/Justson/AgentWeb
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Enable StrictMode for debugging
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        }

        /**
         * Note: WebView initialization takes about 250ms.
         * Pre-initializing WebView can improve page initialization speed and reduce white screen time.
         * The downside is that it slows down App cold start speed. If WebView is used with VasSonic,
         * it is recommended not to pre-initialize WebView here.
         */
//        WebView mWebView=new WebView(new MutableContextWrapper(this));

//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
        // Normal app init code...

        // implementation 'com.github.Justson:dispatch-queue:v1.0.5'
        GlobalQueue.getMainQueue().postRunnableInIdleRunning(new Runnable() {
            @Override
            public void run() {
                try {
                    startService(new Intent(App.this, WebService.class));
                } catch (Throwable throwable) {

                }
            }
        });
    }

    public static Context mContext;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mContext = base;
        AgentWebCompat.setDataDirectorySuffix(base);
    }


}
