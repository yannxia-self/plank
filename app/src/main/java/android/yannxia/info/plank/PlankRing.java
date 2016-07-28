package android.yannxia.info.plank;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by yann on 16/7/28.
 */
public class PlankRing extends View {

    private Paint plankRing;
    private RectF rectF;
    private Paint plankSec;
    private Integer totalSec;
    private Integer curSec;

    public PlankRing(Context context) {
        super(context);
        init();
    }

    public PlankRing(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int circleXCenter = displayMetrics.widthPixels / 2;
        int circleYCenter = displayMetrics.heightPixels / 3;
        int radius = (int) (circleYCenter / 1.5);

        //draw arc
        rectF.set(circleXCenter - radius, circleYCenter - radius, circleXCenter + radius, circleYCenter + radius);

        float sweepAngle = 360 / totalSec * curSec;
        sweepAngle = sweepAngle < 1 ? 1 : sweepAngle;
        canvas.drawArc(rectF, -90, sweepAngle, false, plankRing);

        //draw text
        float textSize = radius / 3;
        plankSec.setTextSize(textSize);
        canvas.drawText(String.valueOf(totalSec - curSec), rectF.centerX(), rectF.centerY() - ((plankSec.descent() + plankSec.ascent()) / 2), plankSec);
    }


    private void init() {
        rectF = new RectF();
        plankRing = new Paint();
        plankRing.setColor(Color.BLUE);
        plankRing.setStyle(Paint.Style.STROKE);
        plankRing.setStrokeWidth(30);
        plankRing.setAntiAlias(true);

        plankSec = new Paint(Paint.ANTI_ALIAS_FLAG);
        plankSec.setColor(Color.RED);
        plankSec.setStyle(Paint.Style.FILL);
        plankSec.setAntiAlias(true);
        plankSec.setTextAlign(Paint.Align.CENTER);
    }


    public Paint getPlankRing() {
        return plankRing;
    }

    public void setPlankRing(Paint plankRing) {
        this.plankRing = plankRing;
    }

    public RectF getRectF() {
        return rectF;
    }

    public void setRectF(RectF rectF) {
        this.rectF = rectF;
    }

    public Paint getPlankSec() {
        return plankSec;
    }

    public void setPlankSec(Paint plankSec) {
        this.plankSec = plankSec;
    }

    public Integer getTotalSec() {
        return totalSec;
    }

    public void setTotalSec(Integer totalSec) {
        this.totalSec = totalSec;
    }

    public Integer getCurSec() {
        return curSec;
    }

    public void setCurSec(Integer curSec) {
        this.curSec = curSec;
    }
}
