package cn.uni.lkb;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.tencent.mmkv.MMKV;

import cn.uni.lkb.databinding.ActivitySettingAcivityBinding;
import cn.uni.lkb.utils.MMKVTools;

public class SettingActivity extends AppCompatActivity {
    private ActivitySettingAcivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingAcivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        binding.videoGp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (binding.H264.getId() == i) {
                    MMKVTools.getInstance().setString("VideoEncoding", "H264");
                }

                if (binding.H263.getId() == i) {
                    MMKVTools.getInstance().setString("VideoEncoding", "H263");
                }

                if (binding.MPEG4.getId() == i) {
                    MMKVTools.getInstance().setString("VideoEncoding", "MPEG_4");
                }
            }
        });

        binding.audioGp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (binding.defaultAudio.getId() == i) {
                    MMKVTools.getInstance().setString("AudioEncoding", "default");
                }

                if (binding.AMRNB.getId() == i) {
                    MMKVTools.getInstance().setString("AudioEncoding", "AMR_NB");
                }

                if (binding.AAC.getId() == i) {
                    MMKVTools.getInstance().setString("AudioEncoding", "AAC");
                }
            }
        });

        binding.frameRateGp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (binding.rate15.getId() == i) {
                    MMKVTools.getInstance().setString("frameRate", "rate_15");
                }

                if (binding.rate20.getId() == i) {
                    MMKVTools.getInstance().setString("frameRate", "rate_20");
                }

                if (binding.rate15.getId() == i) {
                    MMKVTools.getInstance().setString("frameRate", "rate_25");
                }
            }
        });



    }
}