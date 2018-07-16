package jkjan.lathemeter.Utils;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public final class Util {
    public static String priceSeparator = ".";

    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (view != null) {
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static String formatPrice(float pricePerSecond, long timerValue) {
        return formatPrice((int) (pricePerSecond * timerValue / 1000f), true);
    }

    public static String formatPrice(int price) {
        return formatPrice(price, true);
    }

    public static String formatPrice(int price, boolean addCurrency) {
        String text = price / 100 + priceSeparator;
        price %= 100;
        if (price < 10)
            text += "0";
        text += price;
        if (addCurrency)
            text += " zł";
        return text;
    }


    public static String formatTime(long timerValue) {
        long seconds;
        long minutes;
        long hours;
        String text;
        seconds = timerValue / 1000;
        minutes = seconds / 60;
        hours = minutes / 60;
        seconds %= 60;
        minutes %= 60;
        text = hours + ":";
        if (minutes < 10)
            text += "0";
        text += minutes + ":";
        if (seconds < 10)
            text += "0";
        text += seconds;
        return text;
    }
}
