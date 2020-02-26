package com.example.bluetoothtest.ui.search;

import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import androidx.annotation.RequiresApi;
import com.example.bluetoothtest.R;
import com.example.bluetoothtest.base.BasePresenter;
import com.example.bluetoothtest.services.BluetoothService;
import com.example.bluetoothtest.util.BluetoothUtil;
import io.reactivex.disposables.CompositeDisposable;
import java.util.ArrayList;

import static android.content.Context.BIND_AUTO_CREATE;

public class SearchPresenter extends BasePresenter<SearchViewMvp> implements SearchPresenterMvp {
    private BluetoothService service;
    private BluetoothUtil bluetoothUtil;
    private boolean bound = false;
    private CompositeDisposable mDisposable = new CompositeDisposable();
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bound = true;

            SearchPresenter.this.service =
                    ((BluetoothService.BluetoothBinder) service).getService();

            //SearchPresenter.this.service.createService();
            mDisposable.add(
                    SearchPresenter.this.service.getSubjectStateChange()
                            .subscribe(t -> {
                                if (t == BluetoothProfile.STATE_CONNECTED) {
                                    //view.showToast("Подключено");
                                    view.backToActivity();
                                }
                            })
            );
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    public void isAttach() {
        view.bindService(BluetoothService.class, connection, BIND_AUTO_CREATE);
        bluetoothUtil = view.getBluetoothUtil();
        if (bluetoothUtil.checkLocationPermission() != PackageManager.PERMISSION_GRANTED) {
            bluetoothUtil.requestLocationPermission();
        }
        view.getAdapter().setCallback(bluetoothDevice -> service.createConnection(bluetoothDevice));
    }

    @Override
    public void isDetach() {
        mDisposable.dispose();
    }

    @Override
    public void isStop() {
        if (view != null && bound) {
            //service.close();
            bound = false;
            Log.d("Search","Unbind");
            view.unBindService(connection);
        }
    }

    @Override
    public void isStart() {
        if (!bound) {
            Log.d("Search","bind");
            view.bindService(BluetoothService.class, connection, BIND_AUTO_CREATE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBottonClick(View v) {
        switch (v.getId()) {
            case R.id.test_button1: {
                mDisposable.add(bluetoothUtil.startDiscoveryBLE()
                        .subscribe(dev -> {
                                    if (dev.getDevice().getName() != null) {
                                        view.addToListBluetooth(dev.getDevice());
                                    }
                                }
                        ));
                break;
            }
            case R.id.test_button2: {
                if (bluetoothUtil.isScan()) {
                    bluetoothUtil.endDiscovery();
                }
                break;
            }
            case R.id.test_button3: {
                ArrayList<Byte> temp = new ArrayList<>();
                for (int i = 0; i < 25; i++) {
                    temp.add((byte) i);
                }
                service.sendBytes(temp);
                service.sendBytes(temp);
                break;
            }
        }
    }
}
