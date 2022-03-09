package com.info.interfaces;

/**
 * Created by Amit Gupta on 27,September,2020
 */
public interface DrawableClickListener {
    public static enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT };
    public void onClick(DrawablePosition target);
}
