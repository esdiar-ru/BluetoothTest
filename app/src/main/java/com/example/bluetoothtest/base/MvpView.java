package com.example.bluetoothtest.base;

import com.example.bluetoothtest.ui.main.MainViewMVP;

public interface MvpView<M> {
    void showToast(String str);

    void startActivity(Class clazz);
}
