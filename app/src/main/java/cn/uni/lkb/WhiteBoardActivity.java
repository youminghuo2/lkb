package cn.uni.lkb;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import cn.uni.lkb.databinding.ActivityWhiteBoardBinding;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;


public class WhiteBoardActivity extends AppCompatActivity {
    private DoodleView mDoodleView;
    private AlertDialog mColorDialog;
    private AlertDialog mPaintDialog;
    private AlertDialog mShapeDialog;
    private ActivityWhiteBoardBinding binding;
    private int Pen = 1;
    private int Eraser = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWhiteBoardBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mDoodleView=binding.doodleDoodleview;

        binding.doodleDoodleview.setSize(dip2px(5));

        binding.pdfView.getDoodleView(binding.doodleDoodleview);
        binding.pdfView.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDoodleView.getVisibility()==View.VISIBLE){
            return mDoodleView.onTouchEvent(event);
        }
       return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.main_color:
                mDoodleView.setMode(Pen);
                showColorDialog();
                break;
            case R.id.main_size:
                showSizeDialog();
                break;
            case R.id.main_action:
                showShapeDialog();
                break;
            case R.id.main_reset:
                if (mDoodleView.canReset()){
                    mDoodleView.reset();
                }else {
                  if (binding.pdfView.getVisibility()==View.VISIBLE){
                      binding.pdfView.clearPageOnePdf();
                  }
                }
                break;
            case R.id.main_save:
                String path = mDoodleView.saveBitmap(mDoodleView);
                Log.d(TAG, "onOptionsItemSelected: " + path);
                Toast.makeText(this, "保存图片的路径为：" + path,  Toast.LENGTH_SHORT).show();
                break;
            case R.id.rubber_bt:
//                mDoodleView.setColor("#ffffff");
                mDoodleView.setMode(Eraser);
                break;
            case R.id.reset_bt:
                mDoodleView.back();
                break;
            case R.id.add_pdf:
                mDoodleView.reset();
                binding.pdfView.setVisibility(View.VISIBLE);
                addPdf();
                break;
            case R.id.add_white:
                mDoodleView.reset();
                binding.pdfView.setVisibility(View.INVISIBLE);
                break;
        }
        return true;
    }

    private void addPdf() {
        mDoodleView.transColor();
        binding.pdfView.loadPdf("file:///android_assets/aabb.pdf");
    }

    /**
     * 显示选择画笔颜色的对话框
     */
    private void showColorDialog() {
        if(mColorDialog == null){
            mColorDialog = new AlertDialog.Builder(this)
                    .setTitle("选择颜色")
                    .setSingleChoiceItems(new String[]{"蓝色", "红色", "黑色"}, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case 0:
                                            mDoodleView.setColor("#0000ff");
                                            break;
                                        case 1:
                                            mDoodleView.setColor("#ff0000");
                                            break;
                                        case 2:
                                            mDoodleView.setColor("#272822");
                                            break;
                                        default:break;
                                    }
                                    dialog.dismiss();
                                }
                            }).create();
        }
        mColorDialog.show();
    }

    /**
     * 显示选择画笔粗细的对话框
     */
    private void showSizeDialog(){
        if(mPaintDialog == null){
            mPaintDialog = new AlertDialog.Builder(this)
                    .setTitle("选择画笔粗细")
                    .setSingleChoiceItems(new String[]{"细", "中", "粗"}, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case 0:
                                            mDoodleView.setSize(dip2px(5));
                                            break;
                                        case 1:
                                            mDoodleView.setSize(dip2px(10));
                                            break;
                                        case 2:
                                            mDoodleView.setSize(dip2px(15));
                                            break;
                                        default:
                                            break;
                                    }
                                    dialog.dismiss();
                                }
                            }).create();
        }
        mPaintDialog.show();
    }

    /**
     * 显示选择画笔形状的对话框
     */
    private void showShapeDialog(){
        if(mShapeDialog == null){
            mShapeDialog = new AlertDialog.Builder(this)
                    .setTitle("选择形状")
                    .setSingleChoiceItems(new String[]{"路径", "直线", "矩形", "圆形","实心矩形", "实心圆"}, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case 0:
                                            mDoodleView.setType(DoodleView.ActionType.Path);
                                            break;
                                        case 1:
                                            mDoodleView.setType(DoodleView.ActionType.Line);
                                            break;
                                        case 2:
                                            mDoodleView.setType(DoodleView.ActionType.Rect);
                                            break;
                                        case 3:
                                            mDoodleView.setType(DoodleView.ActionType.Circle);
                                            break;
                                        case 4:
                                            mDoodleView.setType(DoodleView.ActionType.FillEcRect);
                                            break;
                                        case 5:
                                            mDoodleView.setType(DoodleView.ActionType.FilledCircle);
                                            break;
                                        default:
                                            break;
                                    }
                                    dialog.dismiss();
                                }
                            }).create();
        }
        mShapeDialog.show();
    }

    private int dip2px(float dpValue){
        final float scale = getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }

}