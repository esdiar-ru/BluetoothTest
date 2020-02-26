package com.example.bluetoothtest.ui.search;

import android.bluetooth.BluetoothDevice;
import android.content.ServiceConnection;
import android.view.View;

import com.example.bluetoothtest.adapters.BluetoothListAdapter;
import com.example.bluetoothtest.base.MvpView;
import com.example.bluetoothtest.util.BluetoothUtil;

import io.reactivex.Observable;

public interface SearchViewMvp extends MvpView<SearchPresenterMvp> {
    void addToListBluetooth(BluetoothDevice bluetoothDevice);

    void bindService(Class clazz, ServiceConnection connection, int code);

    void unBindService(ServiceConnection connection);

    BluetoothListAdapter getAdapter();

    BluetoothUtil getBluetoothUtil();

    void backToActivity();
}
