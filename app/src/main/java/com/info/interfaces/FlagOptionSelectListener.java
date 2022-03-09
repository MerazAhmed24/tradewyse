package com.info.interfaces;


import android.content.Intent;

import androidx.annotation.NonNull;

public interface FlagOptionSelectListener {
    void startActivityForResult(int selectedOption, int requestCode);
}
