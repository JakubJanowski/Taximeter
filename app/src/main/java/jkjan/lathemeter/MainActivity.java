package jkjan.lathemeter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import jkjan.lathemeter.Utils.Util;
import jkjan.lathemeter.R;

public class MainActivity extends AppCompatActivity {
    private Button startButton;
    private Button resetButton;
    private TextView timeTextView;
    private TextView priceTextView;

    private boolean isTimerRunning = false;
    private boolean isTimerStarted = false;
    private float pricePerSecond;
    private long timerValue = 0;
    private long prevIntervalTimerValue;
    private long intervalTimerValue;

    private SharedPreferences preferences;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.priceSeparator = getString(R.string.priceSeparator);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        timeTextView = findViewById(R.id.timeTextView);
        priceTextView = findViewById(R.id.priceTextView);

        initStartButton();
        initResetButton();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int price = preferences.getInt("price", 3);
        int interval = preferences.getInt("interval", 1);
        pricePerSecond = (float) price / interval;
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor preferenceEditor = preferences.edit();
        preferenceEditor.putLong("timerValue", timerValue);
        preferenceEditor.putLong("intervalTimerValue", intervalTimerValue);
        preferenceEditor.putBoolean("isTimerStarted", isTimerStarted);
        preferenceEditor.putBoolean("isTimerRunning", isTimerRunning);
        preferenceEditor.putFloat("pricePerSecond", pricePerSecond);
        preferenceEditor.apply();
        if (isTimerRunning) {
            timer.cancel();
            timer.purge();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        timerValue = preferences.getLong("timerValue", 0);
        intervalTimerValue = preferences.getLong("intervalTimerValue", 0);
        isTimerStarted = preferences.getBoolean("isTimerStarted", false);
        isTimerRunning = preferences.getBoolean("isTimerRunning", false);

        if (isTimerStarted) {
            pricePerSecond = preferences.getFloat("pricePerSecond", pricePerSecond);
            timeTextView.setText(Util.formatTime(timerValue));
            priceTextView.setText(Util.formatPrice(pricePerSecond, timerValue));
            if (isTimerRunning) {
                prevIntervalTimerValue = intervalTimerValue;
                startTimer();
            } else {
                resetButton.setEnabled(true);
                startButton.setText(R.string.resume);
            }
        } else {
            isTimerRunning = false;
            timerValue = 0;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("isTimerStarted", isTimerStarted);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initStartButton() {
        startButton = findViewById(R.id.startButton);
        isTimerRunning = false;
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intervalTimerValue = System.currentTimeMillis();
                if (!isTimerRunning) {
                    isTimerStarted = true;
                    isTimerRunning = true;
                    startTimer();
                } else {
                    timer.cancel();
                    timer.purge();
                    timerValue += intervalTimerValue - prevIntervalTimerValue;
                    timeTextView.setText(Util.formatTime(timerValue));
                    priceTextView.setText(Util.formatPrice(pricePerSecond, timerValue));
                    isTimerRunning = false;
                    resetButton.setEnabled(true);
                    startButton.setText(R.string.resume);
                }
            }
        });
    }

    private void initResetButton() {
        resetButton = findViewById(R.id.resetButton);
        resetButton.setEnabled(false);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int price = 0;
                timerValue = 0;
                isTimerStarted = false;
                timeTextView.setText(Util.formatTime(timerValue));
                priceTextView.setText(Util.formatPrice(price));
                resetButton.setEnabled(false);
                startButton.setText(R.string.start);
                price = preferences.getInt("price", 1);
                int interval = preferences.getInt("interval", 1);
                pricePerSecond = (float) price / interval;
            }
        });
    }

    private void startTimer() {
        prevIntervalTimerValue = intervalTimerValue;
        resetButton.setEnabled(false);
        startButton.setText(R.string.stop);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                intervalTimerValue = System.currentTimeMillis();
                timerValue += intervalTimerValue - prevIntervalTimerValue;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timeTextView.setText(Util.formatTime(timerValue));
                        priceTextView.setText(Util.formatPrice(pricePerSecond, timerValue));
                    }
                });
                prevIntervalTimerValue = intervalTimerValue;
            }
        }, 11, 11);
    }
}
