package com.info.ComplexPreference;
/**
 * Created by Amit Gupta on 3/24/2019.
 */

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class ComplexPreferences {

    private static com.info.ComplexPreference.ComplexPreferences complexPreferences;
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static Gson GSON = new Gson();
    Type typeOfObject = new TypeToken<Object>() {
    }.getType();

    private ComplexPreferences(Context context, String namePreferences, int mode) {
        this.context = context;
        if (namePreferences == null || namePreferences.equals("")) {
            namePreferences = "complex_preferences";
        }
        preferences = context.getSharedPreferences(namePreferences, mode);
        editor = preferences.edit();
    }

    public static com.info.ComplexPreference.ComplexPreferences getComplexPreferences(Context context,
                                                                                             String namePreferences, int mode) {

        /* if (complexPreferences == null) {*/
        complexPreferences = new com.info.ComplexPreference.ComplexPreferences(context,
                namePreferences, mode);
        //}

        return complexPreferences;
    }

    public void putObject(String key, Object object) {
        if (object == null) {
            throw new IllegalArgumentException("object is null");
        }

        if (key.equals("") || key == null) {
            throw new IllegalArgumentException("key is empty or null");
        }

        editor.putString(key, GSON.toJson(object));
    }

    public void commit() {
        editor.commit();
    }

    public void clear() {
        editor.clear();
    }

    public <T> T getObject(String key, Class<T> a) {

        String gson = preferences.getString(key, null);
        if (gson == null) {
            if (a == int.class) {
                return GSON.fromJson("0", a);
            } else if (a == long.class) {
                return GSON.fromJson("0", a);
            } else if (a == double.class) {
                return GSON.fromJson("0", a);
            } else if (a == boolean.class) {
                return GSON.fromJson("false", a);
            } else {
                return null;
            }
        } else {
            try {
                return GSON.fromJson(gson, a);
            } catch (Exception e) {
                throw new IllegalArgumentException("Object storage with key "
                        + key + " is instanceof other class");
            }
        }
    }

    public <T> T getArray(String key, Type typeOfObject) {

        String gson = preferences.getString(key, null);
        if (gson == null) {
            return null;
        } else {
            try {
                return GSON.fromJson(gson, typeOfObject);
            } catch (Exception e) {
                throw new IllegalArgumentException("Object storaged with key "
                        + key + " is instanceof other class");
            }
        }
    }
}
