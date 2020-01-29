package com.greendream.photocollagemaker;

import android.app.Application;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.StrictMode;
import android.os.StrictMode.VmPolicy.Builder;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.facebook.ads.AudienceNetworkAds;

public class MyApplication extends Application {
    static Context context;

    public void onCreate() {
        super.onCreate();

        // get unique id
//        TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
//        String m_deviceId = TelephonyMgr.getDeviceId();
        String m_androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Glob.DATABASE_CART = m_androidId;

        AudienceNetworkAds.initialize(this);

        context = getApplicationContext();
        if (VERSION.SDK_INT >= 24) {
            Builder builder = new Builder();
            builder.detectFileUriExposure();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    public static Context getContext() {
        return context;
    }
}
