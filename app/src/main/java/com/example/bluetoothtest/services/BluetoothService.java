package com.example.bluetoothtest.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.RequiresApi;
import com.example.bluetoothtest.model.Command;
import com.example.bluetoothtest.util.BluetoothUtil;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BluetoothService extends Service {

    private final String TAG = "BluetoothService";
    private final int TIME_DELAY_SEND_BYTES = 1000;
    private final UUID CHARACTERISTIC_UUID =
            UUID.fromString("88ecd1da-e923-469c-8ed5-00035a6feabd");
    private final UUID DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private final UUID SERVICE_UUID = UUID.fromString("22581a79-5a33-4fa3-bdc5-228c09973157");
    private CompositeDisposable mDisposable = new CompositeDisposable();
    private BluetoothBinder binder = new BluetoothBinder();
    private BluetoothDevice connectDevice;
    private BluetoothManager bluetoothManager;
    private BluetoothGattServer bluetoothGattServer;
    private BluetoothGattCharacteristic characteristic;
    private BluetoothUtil bluetoothUtil;
    private PublishSubject<List<Byte>> sendBytesSubject = PublishSubject.create();
    private PublishSubject<Integer> subjectStateChange = PublishSubject.create();
    private PublishSubject<Command> commandSubject = PublishSubject.create();
    private boolean FOREGROUND;

    private static BluetoothService bluetoothService;

    private BluetoothGattServerCallback gattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("Test", "connect " + device.getName());
                connectDevice = device;
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                connectDevice = null;
            }
            subjectStateChange.onNext(newState);
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset,
                BluetoothGattCharacteristic characteristic) {

            Log.d("Test", requestId + " " + offset);
            if (characteristic.getValue() != null) {
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS,
                        offset, characteristic.getValue());
            } else {
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS,
                        offset, "Hello".getBytes());
            }
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId,
                BluetoothGattCharacteristic characteristic, boolean preparedWrite,
                boolean responseNeeded, int offset, byte[] value) {
            characteristic.setValue(value);
            Log.d("Test_write", "Write_request");
            commandSubject.onNext(new Command(Command.Mode.RX, Arrays.toString(value)));
            bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
                    characteristic.getValue());
            bluetoothGattServer.notifyCharacteristicChanged(device, characteristic, true);
        }
    };

    public static BluetoothService getBluetoothService() {
        return bluetoothService;
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        bluetoothService = this;
        //startForeground(1,new );
        return super.onStartCommand(intent, flags, startId);
    }

    public PublishSubject<Command> getResponseCommandFlowable() {//Принятые и отправленные байты
        return commandSubject;
    }

    public PublishSubject<Integer> getSubjectStateChange() { //Изменение состояния подключения
        return subjectStateChange;
    }

    public boolean isConnect() {
        return connectDevice != null;
    }

    public BluetoothService() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        bluetoothUtil = new BluetoothUtil(this);
        bluetoothUtil.enableBluetooth();
        while ((!bluetoothUtil.isEnable())) {
        }
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        Log.d(TAG, "onCreate: ");
        this.createService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
        close();
        Log.d(TAG, "onDestroy: ");
    }

    public Observable<byte[]> getSubjectSendByte() {//Отправленные байты
        Log.d("Subject", "Return");
        return sendBytesSubject.flatMap(Observable::fromIterable)
                .buffer(TIME_DELAY_SEND_BYTES, TimeUnit.MILLISECONDS, 22)
                .filter(bytes -> bytes != null && bytes.size() != 0)
                .map(bytes -> {
                    byte[] temp = new byte[bytes.size()];

                    for (int i = 0; i < bytes.size(); i++) {
                        temp[i] = bytes.get(i);
                    }
                    Log.d("Timer", "Start");
                    return temp;
                })
                .subscribeOn(Schedulers.io())
                .concatMap(item ->
                        Observable.timer(TIME_DELAY_SEND_BYTES, TimeUnit.MILLISECONDS)
                                .map(s -> item)
                )
                //.doOnNext(bytes -> Thread.currentThread().sleep(TIME_DELAY_SEND_BYTES))
                //.doOnNext(bytes -> )
                .doOnNext(bytes -> {
                    // characteristic.setValue(bytes);
                    commandSubject.onNext(new Command(Command.Mode.TX, Arrays.toString(bytes)));
                    Log.d("Test_sendBytes", Arrays.toString(bytes));
                    characteristic.setValue(bytes);
                    bluetoothGattServer.notifyCharacteristicChanged(connectDevice,
                            characteristic,
                            true);
                }).doOnError(e -> Log.d("SendMessage", "Error"));
        // .observeOn(AndroidSchedulers.mainThread())

    }

    public void createService() {
        Log.d("TestCreateServer", "Hi");
        bluetoothGattServer =
                bluetoothManager.openGattServer(BluetoothService.this, gattServerCallback);
        BluetoothGattService service =
                new BluetoothGattService(SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        characteristic = new BluetoothGattCharacteristic(CHARACTERISTIC_UUID,
                BluetoothGattCharacteristic.PROPERTY_READ
                        | BluetoothGattCharacteristic.PROPERTY_WRITE
                        |
                        BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ
                        | BluetoothGattCharacteristic.PERMISSION_WRITE);

        characteristic.addDescriptor(new BluetoothGattDescriptor(DESCRIPTOR_UUID,
                BluetoothGattCharacteristic.PERMISSION_WRITE));

        service.addCharacteristic(characteristic);

        bluetoothGattServer.addService(service);
    }

    public void createConnection(BluetoothDevice address) {

        bluetoothGattServer.connect(address, true);
        Log.d("Test", "Hi1");
    }

    public void close() {
        if (bluetoothGattServer != null) {
            connectDevice = null;

            bluetoothGattServer.close();
            stopSelf();
        }
    }

    @SuppressLint("CheckResult")
    public void sendBytes(List<Byte> data) {
        Log.d("sendBytes", "try");
        sendBytesSubject.onNext(data);

        Log.d("sendBytes", "try2");
    }

    public class BluetoothBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        FOREGROUND = false;
        stopForeground(true);
        return binder;
    }

    @Override public boolean onUnbind(Intent intent) {
        if (intent != null) {
            Log.d(TAG, "Unbind");
        }
        FOREGROUND = true;
        startForeground(3,new Notification.Builder(this)
                .setContentText("Hello")
                .setContentTitle("Title")
        .build());
        return super.onUnbind(intent);
    }
}
