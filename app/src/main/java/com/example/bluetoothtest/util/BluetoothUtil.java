package com.example.bluetoothtest.util;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

import io.reactivex.Observable;

public class BluetoothUtil {
    private final String TAG = "BluetoothUtil";
    private Activity activity;
    private Context context;
    private boolean isScan = false;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BluetoothUtil(Context context) {
        Log.d(TAG, "Context");
        this.context = context;
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BluetoothUtil(Activity activity) {
        Log.d(TAG, "Activity");
        this.activity = activity;
        this.context = activity.getApplicationContext();
        bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    public boolean isScan() {
        return isScan;
    }

    public void enableBluetooth() {
        /*Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivity(intent);*/
        if (!bluetoothAdapter.isEnabled()) {
            /*bluetoothAdapter.enable();*/
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Observable<ScanResult> endDiscovery() {
        return Observable.create(sub -> {
                    isScan = false;
                    bluetoothLeScanner.stopScan(new ScanCallback() {
                        @Override
                        public void onScanResult(int callbackType, ScanResult result) {
                            sub.onNext(result);
                        }

                        @Override
                        public void onBatchScanResults(List<ScanResult> results) {
                            sub.onComplete();
                        }

                        @Override
                        public void onScanFailed(int errorCode) {
                            sub.onError(new Throwable());
                        }
                    });
                }
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Observable<ScanResult> startDiscoveryBLE() {
        return Observable.create(sub -> {
                    isScan = true;
                    bluetoothLeScanner.startScan(new ScanCallback() {
                        @Override
                        public void onScanResult(int callbackType, ScanResult result) {
                            sub.onNext(result);
                        }

                        @Override
                        public void onBatchScanResults(List<ScanResult> results) {
                            isScan = false;
                            super.onBatchScanResults(results);
                        }

                        @Override
                        public void onScanFailed(int errorCode) {
                            isScan = false;
                            super.onScanFailed(errorCode);
                        }
                    });
                }
        );
    }

    public int checkLocationPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public void requestLocationPermission() {
        if (activity != null) {
            ActivityCompat.requestPermissions(activity,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    100);
        }
    }

    public boolean isEnable() {
        return bluetoothAdapter.isEnabled();
    }
}
