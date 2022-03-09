package com.info.interfaces;

import com.google.errorprone.annotations.Var;

/**
 * Created by Amit Gupta on 20,April,2021
 */
public interface ShowHideProgressDialogInterface {
    void showDialog();

    void hideDialog();

    void notifyAdapter(boolean needToNotify);

}
