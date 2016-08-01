package android.yannxia.info.plank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.SoundPool;
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
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class PlankActivity extends AppCompatActivity {

    private PlankRing plankRing;
    private String remainCyclesStr;
    private SoundPool soundPool;
    private int soundID;
    boolean plays = false, loaded = false;
    float actVolume, maxVolume, volume;
    AudioManager audioManager;
    int counter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plank);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        plankRing = (PlankRing) findViewById(R.id.plank_ring);
        setSupportActionBar(toolbar);
        Button startButton = (Button) findViewById(R.id.start_button);
        final TextView remainCycles = (TextView) findViewById(R.id.remain_cycles);
        final Timer timer = new Timer(true);
        final PlankRing.PlankRingConfig plankRingConfig = getPlankRingConfig();
        remainCyclesStr = getString(R.string.remain_cycles);

        remainCycles.setText(String.format(remainCyclesStr, plankRingConfig.ringCycle));
        if (plankRing != null && startButton != null) {

            plankRing.setPlankRingConfig(getPlankRingConfig());
            plankRing.invalidate();
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlankRingTimerTask
                            plankRingTimerTask = new PlankRingTimerTask(new PlankRingHandle(plankRing, remainCycles), timer, plankRing, plankRingConfig);
                    timer.schedule(plankRingTimerTask, TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(1));


                    playSound();
                }
            });
        }


        /* sound */
        // AudioManager audio settings for adjusting the volume
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;

        //Hardware buttons setting to adjust the media sound
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // the counter will help us recognize the stream id of the sound played  now
        counter = 0;
        // Load the sounds
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });
        soundID = soundPool.load(this, R.raw.beep, 1);

    }


    public void playSound() {
        // Is the sound loaded does it already play?
        if (loaded && !plays) {
            soundPool.play(soundID, volume, volume, 1, 0, 1f);
            counter = counter++;
            Toast.makeText(this, "Played sound", Toast.LENGTH_SHORT).show();
            plays = true;
        }
    }


    private class PlankRingTimerTask extends TimerTask {

        private PlankRingHandle plankRingHandle;
        private Timer timer;
        private PlankRing plankRing;

        public PlankRingTimerTask(PlankRingHandle plankRingHandle, Timer timer, PlankRing plankRing, PlankRing.PlankRingConfig plankRingConfig) {
            this.plankRingHandle = plankRingHandle;
            this.timer = timer;
            this.plankRing = plankRing;
        }

        @Override
        public void run() {
            if (plankRing.getRingCycle() == 0 && plankRing.getPlankRingStatus().equals(PlankRing.PlankRingStatus.RESTING)) {
                timer.cancel();
            }
            Message message = new Message();
            message.what = 1;
            plankRingHandle.sendMessage(message);
        }
    }

    private class PlankRingHandle extends Handler {

        private PlankRing plankRing;
        private TextView remainCycles;

        public PlankRingHandle(PlankRing plankRing, TextView remainCycles) {
            this.plankRing = plankRing;
            this.remainCycles = remainCycles;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    remainCycles.setText(String.format(remainCyclesStr, plankRing.getRingCycle()));
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
