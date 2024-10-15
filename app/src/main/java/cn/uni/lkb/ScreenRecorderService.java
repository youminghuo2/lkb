package cn.uni.lkb;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.IOException;

import cn.uni.lkb.utils.MMKVTools;

public class ScreenRecorderService extends Service {
    private MediaProjection mediaProjection;
    private MediaRecorder mediaRecorder;
    private VirtualDisplay virtualDisplay;

    private int width;
    private int height;
    private int dpi;

    private String videoPath = "";

    //标志，判断是否正在录屏
    private boolean running;

    private final String NOTIFICATION_CHANNEL_ID = "cn.uni.lkb.ScreenRecordService";
    private final String NOTIFICATION_CHANNEL_NAME = "cn.uni.lkb";
    private final String NOTIFICATION_CHANNEL_DESC = "cn.uni.lkb.channel_desc";


    @Override
    public void onCreate() {
        super.onCreate();
        startNotification();
    }

    public void startNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent notificationIntent = new Intent(this, ScreenRecorderService.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground))
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Starting Service")
                    .setContentText("Starting monitoring service")
                    .setContentIntent(pendingIntent);
            Notification notification = notificationBuilder.build();
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(NOTIFICATION_CHANNEL_DESC);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            startForeground(1, notification); //必须使用此方法显示通知，不能使
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class ScreenRecordBinder extends Binder {
        public ScreenRecorderService getScreenRecordService() {
            return ScreenRecorderService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ScreenRecordBinder();
    }

    //设置录屏工具MediaProjection
    public void setMediaProjection(MediaProjection projection) {
        mediaProjection = projection;
    }

    //设置需要录制的屏幕参数
    public void setConfig(int width, int height, int dpi) {
        this.width = width;
        this.height = height;
        this.dpi = dpi;
    }

    //返回判断，判断其是否在录屏
    public boolean isRunning() {
        return running;
    }

    //开始录屏
    public boolean startRecord() {
        //首先判断是否有录屏工具以及是否在录屏
        if (mediaProjection == null || running) {
            return false;
        }
        //初始化录像机，录音机Recorder
        initRecord();
        //根据获取的屏幕参数创建虚拟的录屏屏幕
        createVirtualDisplay();

        try {
            mediaRecorder.start();
            running = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "开启失败,没有开始录屏", Toast.LENGTH_LONG).show();
            running = false;
            return false;
        }
    }

    //停止录屏
    public boolean stopRecord() {
        if (!running) {
            //没有录屏，无法停止
            return false;
        }
        //无论设备是否还原或者有异常，但是确实录屏结束，修改标志位为未录屏
        running = false;
        try {
            mediaRecorder.stop();
            mediaRecorder.reset();
            virtualDisplay.release();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "录屏出现异常，视频保存失败！", Toast.LENGTH_SHORT).show();
            return false;
        }
        //无异常，保存成功
        Toast.makeText(this, "录屏结束，保存成功！", Toast.LENGTH_SHORT).show();
        return true;
    }

    public void initRecord() {
        //新建Recorder
        mediaRecorder = new MediaRecorder();
        //设置录像机的一系列参数
        //设置音频来源
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置视频来源
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        //设置视频格式为mp4
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //保存在该位置
        mediaRecorder.setOutputFile(videoPath);
        //设置视频大小
        mediaRecorder.setVideoSize(width, height);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //设置视频存储地址，返回的文件夹下的命名为当前系统事件的文件

        File externalFilesDir = getExternalFilesDir(null);

        File outputFile = new File(externalFilesDir,  System.currentTimeMillis() +"screen_recording.mp4");
        mediaRecorder.setOutputFile(outputFile.getAbsolutePath());
//        String VideoEncoding = MMKVTools.getInstance().getString("VideoEncoding", "H264");
//        switch (VideoEncoding) {
//            case "H264":
//                //设置视频编码为H.264
//                mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//                break;
//            case "H263":
//                mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);
//                break;
//            case "MPEG_4":
//                mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
//                break;
//        }
//
//        String AudioEncoding=MMKVTools.getInstance().getString("AudioEncoding","AMR_NB");
//        switch (AudioEncoding) {
//            case "default":
//                //设置音频编码
//                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//                break;
//            case "AMR_NB":
//                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                break;
//            case "AAC":
//                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//                break;
//        }
//
//        String frameRate=MMKVTools.getInstance().getString("frameRate","rate_15");
//        switch (frameRate) {
//            case "rate_15":
//                mediaRecorder.setVideoFrameRate(15);
//                break;
//            case "rate_20":
//                mediaRecorder.setVideoFrameRate(20);
//                break;
//            case "rate_25":
//                mediaRecorder.setVideoFrameRate(25);
//                break;
//        }

        //设置视频码率
        mediaRecorder.setVideoEncodingBitRate(2 * 1920 * 1080);
        mediaRecorder.setVideoFrameRate(25);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Recorder录像机prepare失败，无法使用，请重新初始化！",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void createVirtualDisplay() {
        //虚拟屏幕通过MediaProjection获取，传入一系列传过来的参数
        try {
            virtualDisplay = mediaProjection.createVirtualDisplay("VirtualScreen", width, height, dpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), null, null);
        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(this, "virtualDisplay创建录屏异常，请退出重试！", Toast.LENGTH_SHORT).show();
        }
    }

    //获取存储文件夹的位置
    public String getSaveDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //如果确认为视频类型，设置根目录，绝对路径下的自定义文件夹中
            String rootDir = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/" + "录屏文件" + "/";
            //创建该文件夹
            File file = new File(rootDir);
            if (!file.exists()) {
                //如果该文件夹不存在
                if (!file.mkdirs()) {
                    //如果没有创建成功
                    return null;
                }
            }
            //创建成功了，返回该目录
            return rootDir;
        } else {
            //不是音视频文件，不保存，无路径
            return null;
        }
    }

}