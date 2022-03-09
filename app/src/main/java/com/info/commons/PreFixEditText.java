package com.info.commons;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatEditText;

import com.info.interfaces.CustomTextWatcher;

import java.text.DecimalFormat;

public class PreFixEditText extends AppCompatEditText implements TextWatcher {

    private CustomTextWatcher changeListener;
    public PreFixEditText(Context context) {
        this(context, null);
    }
    public PreFixEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }
    public PreFixEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public void setChangeListener(CustomTextWatcher changeListener) {
        this.changeListener=changeListener;
        addTextChangedListener(this);
     }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.e("onTextChanged",s+"");
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(changeListener!=null)
            changeListener.onTextChange(Converter.parseStringToFloat(getValue()));
    }

    public String getValue(){
     String s=getText().toString();
     return Converter.formatAmount(Converter.parseStringToFloat(s));
 }

    public void formatValue(){
        removeTextChangedListener(this);
        String s=getText().toString();
        String formatedValue=Converter.formatAmount(Converter.parseStringToFloat(s));
        setText(formatedValue);
        if(changeListener!=null)
        addTextChangedListener(this);
    }
}
