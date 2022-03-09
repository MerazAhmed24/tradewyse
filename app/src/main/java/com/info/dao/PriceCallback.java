package com.info.dao;
public interface PriceCallback<T> {
    void onPriceUpdate(T data);
}
