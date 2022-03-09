package com.info.interfaces;


import android.content.Intent;
import androidx.annotation.NonNull;

public interface PhotoOptionSelectListener {
    void requestPermissions(@NonNull String[] permissions, int requestCode);
    void startActivityForResult(Intent intent, int requestCode);
}
