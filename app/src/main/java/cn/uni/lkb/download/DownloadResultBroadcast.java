package cn.uni.lkb.download;

import static cn.uni.lkb.constants.Constants.DOWNLOAD_RESULT;
import static cn.uni.lkb.constants.Constants.DOWNLOAD_STATE;
import static cn.uni.lkb.constants.Constants.DownloadState.COMPLETE;
import static cn.uni.lkb.constants.Constants.DownloadState.FAIL;
import static cn.uni.lkb.constants.Constants.DownloadState.SUCCESS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DownloadResultBroadcast extends BroadcastReceiver {

    IDownloadCallback mCallback;

    public void setResultCallback(IDownloadCallback callback) {
        mCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int state = intent.getIntExtra(DOWNLOAD_STATE, COMPLETE);
        String resultPath = intent.getStringExtra(DOWNLOAD_RESULT);
        if (mCallback != null) {
           switch (state) {
               case SUCCESS:
                   mCallback.downloadSuccess(resultPath);
                   break;
               case FAIL:
                   mCallback.downloadFail();
                   break;
               case COMPLETE:
                   mCallback.downloadComplete(resultPath);
                   break;
           }
        }
    }

}
