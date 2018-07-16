package jkjan.lathemeter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Objects;

import jkjan.lathemeter.Utils.MoneyTextWatcher;
import jkjan.lathemeter.Utils.Util;
import jkjan.lathemeter.R;

public class SettingsActivity extends AppCompatActivity {
    private int interval = 1;
    private int price;

    private Activity activity;
    private MoneyTextWatcher moneyTextWatcher;
    private SharedPreferences preferences;

    private EditText priceEditText;
    private SeekBar priceSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        activity = this;

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        price = preferences.getInt("price", 3);
        interval = preferences.getInt("interval", 1);
        boolean isTimerStarted = getIntent().getBooleanExtra("isTimerStarted", false);

        initPriceSeekBar();
        initIntervalSpinner();
        initPriceEditText();
        if (isTimerStarted)
            Toast.makeText(this, R.string.settingsInfo, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor preferenceEditor = preferences.edit();
        preferenceEditor.putInt("price", price);
        preferenceEditor.putInt("interval", interval);
        preferenceEditor.apply();
    }

    private void initPriceSeekBar() {
        priceSeekBar = findViewById(R.id.priceSeekBar);
        priceEditText = findViewById(R.id.priceEditText);
        priceSeekBar.setMax(9999);
        priceSeekBar.setProgress(price - 1);
        priceEditText.setText(Util.formatPrice(price));

        priceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser)
                    return;
                price = progress + 1;
                priceEditText.setText(Util.formatPrice(price));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void initIntervalSpinner() {
        Spinner intervalSpinner = findViewById(R.id.intervalSpinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.interval, android.R.layout.simple_spinner_item);
        intervalSpinner.setAdapter(adapter);
        switch (interval) {
            case 1:
                intervalSpinner.setSelection(0);
                break;
            case 60:
                intervalSpinner.setSelection(1);
                break;
            case 3600:
            default:
                intervalSpinner.setSelection(2);
                break;
        }
        intervalSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        interval = 1;
                        break;
                    case 1:
                        interval = 60;
                        break;
                    case 2:
                        interval = 3600;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initPriceEditText() {
        priceEditText = findViewById(R.id.priceEditText);
        moneyTextWatcher = new MoneyTextWatcher(priceEditText, getString(R.string.priceSeparator));
        priceEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    priceEditText.setText(Util.formatPrice(price, false));
                    priceEditText.selectAll();
                    priceEditText.addTextChangedListener(moneyTextWatcher);
                } else {
                    priceEditText.removeTextChangedListener(moneyTextWatcher);
                    Util.hideSoftKeyboard(activity, view);
                    String string = priceEditText.getText().toString();
                    String cleanString = string.replaceAll("[^\\d]", "");
                    cleanString = cleanString.replaceFirst("^0+(?!$)", "");
                    price = Integer.parseInt(cleanString);
                    if (price < 1)
                        price = 1;
                    priceSeekBar.setProgress(price - 1);
                    priceEditText.setText(Util.formatPrice(price));
                }
            }
        });
        priceEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    findViewById(R.id.settingsLayout).requestFocus();
                    return !event.isShiftPressed();
                }
                return false; // pass on to other listeners.
            }
        });
    }
}
