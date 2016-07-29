package android.yannxia.info.plank;

import android.content.Intent;
import android.content.SharedPreferences;
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

import org.apache.commons.lang3.ObjectUtils;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class PlankActivity extends AppCompatActivity {

    private PlankRing plankRing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plank);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        plankRing = (PlankRing) findViewById(R.id.plank_ring);
        setSupportActionBar(toolbar);
        Button startButton = (Button) findViewById(R.id.start_button);
        final Timer timer = new Timer(true);

        if (plankRing != null && startButton != null) {
            final PlankRing.PlankRingConfig plankRingConfig = getPlankRingConfig();
            plankRing.setPlankRingConfig(getPlankRingConfig());
            plankRing.invalidate();;
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlankRingTimerTask
                            plankRingTimerTask = new PlankRingTimerTask(new PlankRingHandle(plankRing), timer, plankRing, plankRingConfig);
                    timer.schedule(plankRingTimerTask, TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(1));
                }
            });
        }
    }


    class PlankRingTimerTask extends TimerTask {

        private PlankRingHandle plankRingHandle;
        private Timer timer;
        private PlankRing plankRing;
        private PlankRing.PlankRingConfig plankRingConfig;
        private Integer cycles;

        public PlankRingTimerTask(PlankRingHandle plankRingHandle, Timer timer, PlankRing plankRing, PlankRing.PlankRingConfig plankRingConfig) {
            this.plankRingHandle = plankRingHandle;
            this.timer = timer;
            this.plankRing = plankRing;
            this.plankRingConfig = plankRingConfig;
            this.cycles = plankRingConfig.ringCycle;
        }

        @Override
        public void run() {
            if (plankRing.getRingCycle() == 0 && (plankRing.getCurSec().equals(plankRing.getTotalSec()))) {
                timer.cancel();
            }
            Message message = new Message();
            message.what = 1;
            plankRingHandle.sendMessage(message);
        }
    }

    static class PlankRingHandle extends Handler {

        private PlankRing plankRing;

        public PlankRingHandle(PlankRing plankRing) {
            this.plankRing = plankRing;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    plankRing.plusSec();
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
            startActivity(new Intent(this, PlankSettingActivity.class));
            plankRing.setPlankRingConfig(getPlankRingConfig());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private PlankRing.PlankRingConfig getPlankRingConfig() {
        PlankRing.PlankRingConfig plankRingConfig = new PlankRing.PlankRingConfig();

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        plankRingConfig.plankSec = sharedPref.getInt(getString(R.string.plank_settings_ring_time_key), 60);
        plankRingConfig.restSec = sharedPref.getInt(getString(R.string.plank_settings_ring_rest_time_key), 5);
        plankRingConfig.ringCycle = sharedPref.getInt(getString(R.string.plank_settings_ring_cycles_key), 5);

        return plankRingConfig;
    }

}
