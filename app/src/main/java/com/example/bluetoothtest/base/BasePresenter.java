package com.example.bluetoothtest.base;

public abstract class BasePresenter<V extends MvpView> {
    protected V view;

    public void attach(V view) {
        this.view = view;
        this.isAttach();
    }

    public void detach() {
        this.view = null;
        this.isDetach();
    }

    public void isAttach() {

    }

    public void isDetach() {

    }

    public void isStop() {

    }

    public void isStart() {

    }
}
