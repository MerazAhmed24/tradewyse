package com.info.commons;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

import static com.info.commons.Common.formatDouble;

/**
 * Created by dbagv_000 on 12/18/2016.
 */
public class StringHelper {

    public static String getClipBoardUrl(Context context) {
        try {
            ClipData.Item item = ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).getPrimaryClip().getItemAt(0);
            Uri uri = item.getUri();
            if (uri != null && uri.toString().length() > 0) {
                return uri.toString();
            }
            CharSequence text = item.getText();
            if (text != null && text.toString().length() > 0) {
                return text.toString();
            }
            return "";
        } catch (NullPointerException e) {

        }
        return "";
    }

    public static void copyToClipBoard(Context context, String textMessage) {
        if (!TextUtils.isEmpty(textMessage)) {
            ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newRawUri("Copied URL", Uri.parse(textMessage)));
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_LONG).show();
        }
    }

    public static String convert(Map<String, String> map) {
        Gson gson = new Gson();
        String json = gson.toJson(map);
        return json;

    }


    public static String capitalFirstLetter(String string) {
        if (!isEmpty(string)) {
            return string.substring(0, 1).toUpperCase() + string.substring(1);
        }
        return "";
    }

    public static boolean isEmpty(String text) {
        return text == null || text.length() == 0 || text.trim().length() == 0 || text.trim().equalsIgnoreCase("null");
    }

    public static String getAmount(double amount, String defaultValue) {
        if (amount == 0) {
            return defaultValue;
        } else {
            String textAmount = formatDouble(amount);
            if (isEmpty(textAmount)) {
                if (isEmpty(defaultValue)) {
                    return "--";
                }
                return defaultValue;
            } else {
                return "₹" + textAmount;
            }
        }
    }

    public static String currencyFormatter(Double number) {
        String COUNTRY = "IN";
        String LANGUAGE = "en";
        String str = NumberFormat.getCurrencyInstance(new Locale(LANGUAGE, COUNTRY)).format(number);
        return str;
    }

    public static String getValue(String text, String defaultValue) {
        if (isEmpty(text)) {
            if (isEmpty(defaultValue)) {
                return "";
            }
            return defaultValue;
        } else {
            return "₹" + text;
        }
    }

    public static String get(String text, String defaultValue) {
        if (isEmpty(text)) {
            if (isEmpty(defaultValue)) {
                return "";
            }
            return defaultValue;
        } else {
            return "" + text;
        }
    }


}
