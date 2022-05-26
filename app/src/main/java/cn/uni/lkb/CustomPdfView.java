package cn.uni.lkb;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class CustomPdfView extends View {
    private int view_width;
    private int view_height;
    private float preX;
    private float preY;

    private Path path;
    private Paint paint;
    private Paint mBitmapPaint;
    Bitmap cacheBitmap = null;
    Canvas cacheCanvas = null;

    private static List<DrawPath> savePath;
    private DrawPath dp;

    int name = 100;

    public class DrawPath {
        public Path path;
        public Paint paint;
    }

    public CustomPdfView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        view_width = context.getResources().getDisplayMetrics().widthPixels;
        view_height = context.getResources().getDisplayMetrics().heightPixels;
//        cacheBitmap = Bitmap.createBitmap(view_width, view_height, Bitmap.Config.ARGB_8888);
        cacheBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.aa);

        cacheCanvas = new Canvas(cacheBitmap);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        paint = new Paint();
        paint.setColor(Color.BLACK);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);
        paint.setDither(true);

        savePath = new ArrayList<DrawPath>();
    }

    public void clear() {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint.setStrokeWidth(25);
    }

    //清空画布
    public void clearAll() {
        cacheBitmap = Bitmap.createBitmap(view_width, view_height, Bitmap.Config.ARGB_8888);
        cacheCanvas.setBitmap(cacheBitmap);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(cacheBitmap, 0, 0, mBitmapPaint);

        if (path != null) {
            canvas.drawPath(path, paint);
        }
    }

    public void reset() {
        cacheBitmap = Bitmap.createBitmap(view_width, view_height, Bitmap.Config.ARGB_8888);
        cacheCanvas.setBitmap(cacheBitmap);

        if (savePath != null && savePath.size() > 0) {
            savePath.remove(savePath.size() - 1);
            Iterator<DrawPath> iter = savePath.iterator();
            while (iter.hasNext()) {
                DrawPath drawPath = iter.next();
                cacheCanvas.drawPath(drawPath.path, drawPath.paint);
            }
            invalidate();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path = new Path();
                dp = new DrawPath();
                dp.path = path;
                dp.paint = paint;
                path.moveTo(x, y);
                preX = x;
                preY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - preX);
                float dy = Math.abs(preY - y);
                if (dx >= 4 || dy >= 4) {
                    path.quadTo(preX, preY, (x + preX) / 2, (y + preY) / 2);
                    preX = x;
                    preY = y;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                path.lineTo(preX,preY);
                cacheCanvas.drawPath(path,paint);
                savePath.add(dp);
                path=null;
                invalidate();
                break;
        }
        return true;
    }

    public void save(){
        try {
            saveBitmap("myPicture");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void saveBitmap(String fileName)throws IOException{
        if (cacheBitmap!=null){
            String path= Environment.getExternalStorageDirectory().getPath();
            name+=1;
            File file = new File(path+"/Pictures/",name+".png");
            file.createNewFile();//创建一个新文件
            try {
                FileOutputStream bos= new FileOutputStream(file);
                cacheBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                bos.flush();
                bos.close();
                Toast.makeText(getContext(), "保存成功！", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
