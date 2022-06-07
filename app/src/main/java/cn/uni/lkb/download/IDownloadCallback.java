package cn.uni.lkb.download;


public interface IDownloadCallback {

    void downloadSuccess(String path);

    void downloadFail();

    void downloadComplete(String path);

}
