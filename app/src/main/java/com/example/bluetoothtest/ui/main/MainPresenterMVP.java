package com.example.bluetoothtest.ui.main;

import android.view.View;

import com.example.bluetoothtest.base.MvpPresenter;

public interface MainPresenterMVP extends MvpPresenter<MainViewMVP> {
    void onButtonClick(View view);
}
