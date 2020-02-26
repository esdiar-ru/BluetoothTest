package com.example.bluetoothtest.ui.main;

import android.content.ServiceConnection;

import com.example.bluetoothtest.base.MvpView;
import com.example.bluetoothtest.model.Command;
import com.example.bluetoothtest.util.BluetoothUtil;

public interface MainViewMVP extends MvpView<MainPresenterMVP> {
    void bindService(Class clazz, ServiceConnection connection, int code);

    void unBindService(ServiceConnection connection);

    void showCommand(Command command);

    BluetoothUtil getBluetoothUtil();
}
