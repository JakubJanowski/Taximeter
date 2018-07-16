package jkjan.lathemeter.Utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;

import static java.lang.Math.max;


public class MoneyTextWatcher implements TextWatcher {
    private final WeakReference<EditText> editTextWeakReference;
    private String priceSeparator;
    private boolean restorePreviousText = false;
    private int previousLength;
    private int previousSelection;
    private String previousString;

    public MoneyTextWatcher(EditText editText, String priceSeparator) {
        editTextWeakReference = new WeakReference<>(editText);
        this.priceSeparator = priceSeparator;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        previousString = s.toString();
        String cleanString = previousString.replaceAll("[^\\d]", "");
        cleanString = cleanString.replaceFirst("^0+(?!$)", "");
        restorePreviousText = cleanString.length() >= 4 && after > count && !cleanString.equals("1000"); // 100.00
        previousLength = max(3, cleanString.length());
        EditText editText = editTextWeakReference.get();
        if (editText == null)
            return;
        previousSelection = editText.getSelectionEnd();
        if (previousSelection >= previousString.length() - 2)
            previousSelection--;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        EditText editText = editTextWeakReference.get();
        if (editText == null)
            return;
        int selection;
        String string = editable.toString();
        String formattedString;
        if(restorePreviousText) {
            formattedString = previousString;
            selection = previousSelection;
        } else {
            if (string.isEmpty()) {
                formattedString = "0" + priceSeparator + "00";
                selection = 3;
            } else {
                String cleanString = string.replaceAll("[^\\d]", "");
                cleanString = cleanString.replaceFirst("^0+(?!$)", "");   //remove leading zeros
                selection = previousSelection - previousLength + max(3, cleanString.length());
                if (cleanString.length() >= 5) // 100.00
                    formattedString = "100" + priceSeparator + "00";
                else {
                    StringBuilder stringBuilder;
                    if (cleanString.length() >= 3) {
                        stringBuilder = new StringBuilder(cleanString);
                        stringBuilder.insert(cleanString.length() - 2, priceSeparator);
                    } else {
                        stringBuilder = new StringBuilder("0" + priceSeparator);
                        for (byte i = 2; i > cleanString.length(); i--)
                            stringBuilder.append('0');
                        stringBuilder.append(cleanString);
                    }
                    formattedString = stringBuilder.toString();
                }
            }
        }
        if (selection >= formattedString.length() - 2)
            selection++;
        editText.removeTextChangedListener(this);
        editText.setText(formattedString);
        editText.setSelection(selection);
        editText.addTextChangedListener(this);
    }
}
