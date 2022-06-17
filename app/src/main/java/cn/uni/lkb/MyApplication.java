package cn.uni.lkb;

import android.app.Application;

import com.tencent.mmkv.MMKV;

import cn.uni.lkb.utils.MMKVTools;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MMKVTools.getInstance().initMMKV(this);
    }
}
