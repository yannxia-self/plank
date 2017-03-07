package android.yannxia.info.plank;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import org.apache.commons.lang3.Validate;

/**
 * Created by yann on 16/7/29.
 */
public class PlankSettingActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_plank);

        EditText ringTimeET = (EditText) findViewById(R.id.plank_settings_ring_time);
        EditText ringRestTimeET = (EditText) findViewById(R.id.plank_settings_ring_rest_time);
        EditText ringCyclesET = (EditText) findViewById(R.id.plank_settings_ring_cycles);
        Validate.notNull(ringTimeET);
        Validate.notNull(ringRestTimeET);
        Validate.notNull(ringCyclesET);


        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        ringTimeET.setText(String.valueOf(sharedPref.getInt(getString(R.string.plank_settings_ring_time_key), 60)));
        ringRestTimeET.setText(String.valueOf(sharedPref.getInt(getString(R.string.plank_settings_ring_rest_time_key), 5)));
        ringCyclesET.setText(String.valueOf(sharedPref.getInt(getString(R.string.plank_settings_ring_cycles_key), 5)));


        ringTimeET.addTextChangedListener(new EditTextWatcher(getString(R.string.plank_settings_ring_time_key)));
        ringRestTimeET.addTextChangedListener(new EditTextWatcher(getString(R.string.plank_settings_ring_rest_time_key)));
        ringCyclesET.addTextChangedListener(new EditTextWatcher(getString(R.string.plank_settings_ring_cycles_key)));
    }

    private class EditTextWatcher implements TextWatcher {

        private String preferenceKey;

        public EditTextWatcher(String preferenceKey) {
            this.preferenceKey = preferenceKey;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(preferenceKey, Integer.valueOf(s.toString()));
                editor.apply();
            }
        }
    }
}
