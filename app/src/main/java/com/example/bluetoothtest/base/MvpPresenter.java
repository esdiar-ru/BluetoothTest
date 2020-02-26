package com.example.bluetoothtest.base;

public interface MvpPresenter<V> {
    void attach(V view);

    void detach();

    void isAttach();

    void isDetach();

    void isStop();

    void isStart();
}
