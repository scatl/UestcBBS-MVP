package com.scatl.uestcbbs.custom.imageview;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatImageView;

import com.scatl.uestcbbs.R;

/**
 * author: sca_tl
 * description: from:https://blog.csdn.net/chy555chy/article/details/54800086
 * date: 2019/07/20 16:41
 */
public class CircleImageView extends AppCompatImageView {
    private Paint paint = null;
    private PaintFlagsDrawFilter pfdf = null;
    private Path path = null;

    public CircleImageView(Context context) {
        super(context);
        init(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context contexts) {
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        if (path == null) {
            path = new Path();
            path.addCircle(width / 2f, height / 2f, Math.min(width / 2f, height / 2f), Path.Direction.CCW);
            path.close();
        }
        int saveCount = canvas.save();
        canvas.setDrawFilter(pfdf);
        canvas.clipPath(path, Region.Op.INTERSECT);
        super.onDraw(canvas);
        canvas.restoreToCount(saveCount);
    }

}
