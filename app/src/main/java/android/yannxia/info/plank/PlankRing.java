package android.yannxia.info.plank;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Timer;

/**
 * Created by yann on 16/7/28.
 */
public class PlankRing extends View {


    static class PlankRingConfig {
        Integer plankSec;
        Integer restSec;
        Integer ringCycle;
    }

    enum PlankRingStatus {
        SOON_START, PLANKING, RESTING;

        public PlankRingStatus nextStatus() {
            if (this.equals(SOON_START)) return PLANKING;
            if (this.equals(PLANKING)) return RESTING;
            if (this.equals(RESTING)) return PLANKING;

            return null;
        }
    }


    private RectF rectF;
    private Integer alarmSec = 3;
    private Integer ringCycle;
    private Paint plankRing;
    private Paint plankSec;
    private Integer totalSec = 3;
    private Integer curSec = 0;
    private PlankRingStatus plankRingStatus = PlankRingStatus.SOON_START;
    private PlankRingConfig plankRingConfig;


    public PlankRing(Context context) {
        super(context);
        init();
    }

    public PlankRing(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void plusSec() {
        this.curSec++;
    }

    public void setPlankRingConfig(PlankRingConfig plankRingConfig) {
        if (PlankRingStatus.SOON_START.equals(this.plankRingStatus)) {
            this.plankRingConfig = plankRingConfig;
//            this.totalSec = plankRingConfig.plankSec;
            this.ringCycle = plankRingConfig.ringCycle;
        }
    }

    public void switchStatus() {

        this.plankRingStatus = this.plankRingStatus.nextStatus();
        if (PlankRingStatus.PLANKING.equals(this.plankRingStatus)) {
            this.ringCycle--;
            this.totalSec = plankRingConfig.plankSec;
        } else if (PlankRingStatus.RESTING.equals(this.plankRingStatus)) {
            this.totalSec = plankRingConfig.restSec;
        }

        this.curSec = -1;
        switchPaint();
    }

    private void switchPaint() {
        switch (plankRingStatus) {
            case SOON_START:
                plankRing.setColor(Color.YELLOW);
                break;
            case PLANKING:
                plankRing.setColor(Color.RED);
                break;
            case RESTING:
                plankRing.setColor(Color.DKGRAY);
                break;
        }

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


        if (this.totalSec.equals(this.curSec)) {
            switchStatus();
        }
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

    public Integer getTotalSec() {
        return totalSec;
    }

    public Integer getCurSec() {
        return curSec;
    }

    public Integer getRingCycle() {
        return ringCycle;
    }

    public PlankRingStatus getPlankRingStatus() {
        return plankRingStatus;
    }
}
