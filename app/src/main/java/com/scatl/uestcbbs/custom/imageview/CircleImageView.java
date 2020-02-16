package com.scatl.uestcbbs.custom.imageview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * author: sca_tl
 * description: from:https://blog.csdn.net/chy555chy/article/details/54800086
 * date: 2019/07/20 16:41
 */
public class CircleImageView extends AppCompatImageView {
    private Paint paint = null;
    // 设置画布抗锯齿(毛边过滤)
    private PaintFlagsDrawFilter pfdf = null;
    private Path path = null;

    public CircleImageView(Context context) {
        super(context);
        //init(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init(context, attrs);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        paint = new Paint();
        // 透明度: 00%=FF（不透明） 100%=00（透明）
        paint.setColor(Color.WHITE);
        // paint.setColor(Color.parseColor("ffffffff"));
        paint.setStyle(Paint.Style.STROKE);
        // 解决图片拉伸后出现锯齿的两种办法: 1.画笔上设置抗锯齿 2.画布上设置抗锯齿
        // http://labs.easymobi.cn/?p=3819
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        int clearBits = 0;
        int setBits = Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG;
        pfdf = new PaintFlagsDrawFilter(clearBits, setBits);
        //由于imageview有默认底色,如黑色,设置背景为透明是为了第一次setImageBitmap时不显示圆以外方型的默认背景色
        //但是这样在中兴nubia手机上还会首先显示正方形黑色背景,然后才变圆(解决办法,先裁成圆再setImageBitmap)
        setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        // CCW: CounterClockwise(逆时针)
        // CW: Clockwise(顺时针)
        if (path == null) {
            path = new Path();
            path.addCircle(width / 2f, height / 2f, Math.min(width / 2f, height / 2f), Path.Direction.CCW);
            path.close();
        }
//      canvas.drawCircle(width / 2f, height / 2f, Math.min(width / 2f, height / 2f), paint);
        // super.onDraw里面也可能有多个canvas.save
        int saveCount = canvas.save();
        canvas.setDrawFilter(pfdf);
        // Region.Op.REPLACE 是显示第二次的
//      canvas.clipPath(path, Region.Op.REPLACE);
        canvas.clipPath(path, Region.Op.INTERSECT);
        super.onDraw(canvas);
        canvas.restoreToCount(saveCount);
    }

}
