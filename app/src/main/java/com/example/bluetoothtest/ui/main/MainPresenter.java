package com.example.bluetoothtest.ui.main;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import com.example.bluetoothtest.R;
import com.example.bluetoothtest.base.BasePresenter;
import com.example.bluetoothtest.services.BluetoothService;
import com.example.bluetoothtest.ui.search.SearchActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;

import static android.content.Context.BIND_AUTO_CREATE;

public class MainPresenter extends BasePresenter<MainViewMVP> implements MainPresenterMVP {

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bound = true;
            MainPresenter.this.service = ((BluetoothService.BluetoothBinder) service).getService();
            mDisposable.add(MainPresenter.this.service.getResponseCommandFlowable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(t -> view.showCommand(t)));
            mDisposable.add(MainPresenter.this.service.getSubjectSendByte()
                    .subscribe());
            Log.d("Main", "ServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("Main", "ServiceDisconnected");
            bound = false;
        }
    };
    private BluetoothService service;
    private boolean bound = false;
    private CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    public void isAttach() {
        if (service == null && !bound) {
            view.bindService(BluetoothService.class, connection, BIND_AUTO_CREATE);
        }
    }

    @Override
    public void isDetach() {
        /*if (service != null) {
            service.close();
        }*/
        //view.unBindService(connection);
        mDisposable.dispose();
    }

    @Override
    public void onButtonClick(View view) {
        ArrayList<Byte> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add((byte) i);
        }
        switch (view.getId()) {
            case R.id.command1: {
                service.sendBytes(list.subList(0, 10));
                break;
            }
            case R.id.command2: {
                service.sendBytes(list.subList(0, 30));
                break;
            }
            case R.id.command3: {
                service.sendBytes(list);
                break;
            }
            case R.id.connect: {
                this.view.startActivity(SearchActivity.class);
                break;
            }
        }
    }

    @Override
    public void isStop() {//TODO
        if (view != null && bound) {
            //service.close();
            Log.d("Main", "Unbind");
            view.unBindService(connection);
        }
    }

    @Override
    public void isStart() {//TODO
        Log.d("Main", "isStart");
        if (!bound) {
            Log.d("Main", "bind");
            view.bindService(BluetoothService.class, connection, BIND_AUTO_CREATE);
        }
    }
}
