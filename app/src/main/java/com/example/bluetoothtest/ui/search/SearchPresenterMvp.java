package com.example.bluetoothtest.ui.search;

import android.view.View;

import com.example.bluetoothtest.base.MvpPresenter;

public interface SearchPresenterMvp extends MvpPresenter<SearchViewMvp> {
    void onBottonClick(View v);
}
