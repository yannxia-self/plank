package android.yannxia.info.plank;

import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class PlankActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plank);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final PlankRing plankRing = (PlankRing) findViewById(R.id.plank_ring);
        setSupportActionBar(toolbar);
        Button startButton = (Button) findViewById(R.id.start_button);
        final Timer timer = new Timer(true);

        if (plankRing != null && startButton != null) {
            plankRing.setTotalSec(60);
            plankRing.setCurSec(0);

            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlankRingTimerTask
                            plankRingTimerTask = new PlankRingTimerTask(new PlankRingHandle(plankRing), timer, plankRing);

                    timer.schedule(plankRingTimerTask, TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(1));
                }
            });
        }
    }

    class PlankRingTimerTask extends TimerTask {

        private PlankRingHandle plankRingHandle;
        private Timer timer;
        private PlankRing plankRing;

        public PlankRingTimerTask(PlankRingHandle plankRingHandle, Timer timer, PlankRing plankRing) {
            this.plankRingHandle = plankRingHandle;
            this.timer = timer;
            this.plankRing = plankRing;
        }

        @Override
        public void run() {
            Bundle bundle = new Bundle();
            bundle.putInt("curSec", plankRing.getCurSec() + 1);
            Message message = new Message();
            message.what = 1;
            message.setData(bundle);
            plankRingHandle.sendMessage(message);

            if (plankRing.getTotalSec() < plankRing.getCurSec()) {
                timer.cancel();
            }
        }
    }

    class PlankRingHandle extends Handler {

        private PlankRing plankRing;

        public PlankRingHandle(PlankRing plankRing) {
            this.plankRing = plankRing;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int curSec = msg.getData().getInt("curSec");
                    plankRing.setCurSec(curSec);
                    plankRing.invalidate();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plank, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
