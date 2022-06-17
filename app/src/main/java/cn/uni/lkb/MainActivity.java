package cn.uni.lkb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.anim.DefaultAnimator;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.enums.SidePattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.permissionx.guolindev.request.ForwardScope;

import java.util.List;

import cn.uni.lkb.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    //录屏工具
    MediaProjectionManager mediaProjectionManager;
    MediaProjection mediaProjection;
    //获取录屏范围参数
    DisplayMetrics metrics;
    //录屏服务
    ScreenRecorderService screenRecordService;
    //请求码
    private final static int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        initPermission();
        connectService();
        initClick();
    }

    private void initPermission() {
        PermissionX.init(this)
                .permissions(Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                .onExplainRequestReason(new ExplainReasonCallback() {
                    @Override
                    public void onExplainReason(@NonNull ExplainScope scope, @NonNull List<String> deniedList) {
                        scope.showRequestReasonDialog(deniedList, "即将申请的权限是程序必须依赖的权限", "我已明白");
                    }
                })
                .explainReasonBeforeRequest()
                .onForwardToSettings(new ForwardToSettingsCallback() {
                    @Override
                    public void onForwardToSettings(@NonNull ForwardScope scope, @NonNull List<String> deniedList) {
                        scope.showForwardToSettingsDialog(deniedList, "您需要去应用程序中手动开启权限", "我已明白");
                    }
                })
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                        if (allGranted) {
                            Toast.makeText(MainActivity.this, "所有申请的权限都已通过", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "您拒绝了如下权限：" + deniedList, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    private void initClick() {
        binding.startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PermissionX.init(MainActivity.this)
                        .permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                        .onExplainRequestReason(new ExplainReasonCallback() {
                            @Override
                            public void onExplainReason(@NonNull ExplainScope scope, @NonNull List<String> deniedList) {
                                scope.showRequestReasonDialog(deniedList,"不授予悬浮权限，可能导致视频录制无法在桌面上进行保存，建议授权","确定","取消");
                            }
                        })
                        .explainReasonBeforeRequest()
                        .request(new RequestCallback() {
                            @Override
                            public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                                if (allGranted) {
                                    Toast.makeText(MainActivity.this, "授权成功,开始录制", Toast.LENGTH_SHORT).show();
                                    initEasyFloat();
                                } else {
                                    Toast.makeText(MainActivity.this, "授权失败，开始录制" + deniedList, Toast.LENGTH_SHORT).show();
                                }
//                                screenRecordService.startRecord();
                            }
                        });



//                if(screenRecordService != null && screenRecordService.isRunning()){
//                    Toast.makeText(MainActivity.this,"当前正在录屏，请不要重复点击哦！",Toast.LENGTH_SHORT).show();
//                } else if(screenRecordService != null && !screenRecordService.isRunning()){
//                    //没有录制，就开始录制，弹出提示，返回主界面开始录制
//                } else if(screenRecordService == null){
//                    connectService();
//                }
            }
        });

        binding.stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(screenRecordService != null && !screenRecordService.isRunning()){

                    //没有在录屏，无法停止，弹出提示
                    Toast.makeText(MainActivity.this,"您还没有录屏",Toast.LENGTH_SHORT).show();
                }else if(screenRecordService != null && screenRecordService.isRunning()){
                    //正在录屏，点击停止，停止录屏
                    screenRecordService.stopRecord();
                }
            }
        });

        binding.whiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,WhiteBoardActivity.class));
            }
        });

        binding.settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initEasyFloat() {
        EasyFloat.with(this)
                .setLayout(R.layout.dialog_round)
                .setShowPattern(ShowPattern.ALL_TIME)
                .setSidePattern(SidePattern.TOP)
                .setDragEnable(true)
                .setAnimator(new DefaultAnimator())
                .registerCallbacks(new OnFloatCallbacks() {
                    @Override
                    public void createdResult(boolean b, @Nullable String s, @Nullable View view) {

                    }

                    @Override
                    public void show(@NonNull View view) {
                        Glide.with(MainActivity.this).asGif().load(R.drawable.red_round).into((ImageView) view.findViewById(R.id.red_img));
                    }

                    @Override
                    public void hide(@NonNull View view) {

                    }

                    @Override
                    public void dismiss() {

                    }

                    @Override
                    public void touchEvent(@NonNull View view, @NonNull MotionEvent motionEvent) {
                        view.findViewById(R.id.pause).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                screenRecordService.stopRecord();
                                Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                                EasyFloat.dismiss();
                            }
                        });
                    }

                    @Override
                    public void drag(@NonNull View view, @NonNull MotionEvent motionEvent) {

                    }

                    @Override
                    public void dragEnd(@NonNull View view) {

                    }
                })
                .show();
    }

    private void connectService() {
        //通过intent为中介绑定Service，会自动create
        Intent intent = new Intent(this, ScreenRecorderService.class);
        //绑定过程连接，选择绑定模式
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //获取Binder
            ScreenRecorderService.ScreenRecordBinder binder = (ScreenRecorderService.ScreenRecordBinder) iBinder;
            //通过Binder获取Service
            screenRecordService = binder.getScreenRecordService();
            //获取到服务，初始化录屏管理者
            mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            //通过管理者，创建录屏请求，通过Intent
            Intent captureIntent = mediaProjectionManager.createScreenCaptureIntent();
            //将请求码作为标识一起发送，调用该接口，需有返回方法
            startActivityForResult(captureIntent, REQUEST_CODE);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Toast.makeText(MainActivity.this, "录屏服务未连接成功，请重试！", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    //返回方法，获取返回的信息
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        //首先判断请求码是否一致，结果是否ok
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            //录屏请求成功，使用工具MediaProjection录屏
            //从发送获得的数据和结果中获取该工具
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
            //将该工具给Service，并一起传过去需要录制的屏幕范围的参数
            if (screenRecordService != null) {
                screenRecordService.setMediaProjection(mediaProjection);
                //获取录屏屏幕范围参数
                metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                screenRecordService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}