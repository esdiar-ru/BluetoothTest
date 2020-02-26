package com.example.bluetoothtest.ui.main;

import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bluetoothtest.R;
import com.example.bluetoothtest.adapters.CommandListAdapter;
import com.example.bluetoothtest.base.BaseView;
import com.example.bluetoothtest.model.Command;
import com.example.bluetoothtest.services.BluetoothService;
import com.example.bluetoothtest.util.BluetoothUtil;
import io.reactivex.disposables.CompositeDisposable;
import java.util.ArrayList;

public class MainActivity extends BaseView<MainPresenterMVP> implements MainViewMVP {
    private Button command1;
    private Button command2;
    private Button command3;
    private BluetoothUtil bluetoothUtil;
    private CompositeDisposable mDisposable = new CompositeDisposable();
    private Button connect;
    private RecyclerView recyclerView;
    private ArrayList<Command> list;
    private CommandListAdapter adapter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detach();
        mDisposable.dispose();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setPresenter(new MainPresenter());
        bluetoothUtil = new BluetoothUtil(this);
        bluetoothUtil.enableBluetooth();
        command1 = findViewById(R.id.command1);
        command2 = findViewById(R.id.command2);
        command3 = findViewById(R.id.command3);
        connect = findViewById(R.id.connect);
        connect.setOnClickListener(view -> presenter.onButtonClick(view));
        command1.setOnClickListener(view -> presenter.onButtonClick(view));
        command2.setOnClickListener(view -> presenter.onButtonClick(view));
        command3.setOnClickListener(view -> presenter.onButtonClick(view));
        recyclerView = findViewById(R.id.containerMain);
        list = new ArrayList<>();
        adapter = new CommandListAdapter(this, list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        presenter.attach(this);
    }

    @Override
    public void bindService(Class clazz, ServiceConnection connection, int code) {
        if(BluetoothService.getBluetoothService()==null)
            startService(new Intent(this,clazz));
        Intent bluetoothIntent = new Intent(this, clazz);
        bindService(bluetoothIntent, connection, code);
    }

    @Override
    public void unBindService(ServiceConnection connection) {
        unbindService(connection);
    }

    public void showCommand(Command command) {
        list.add(command);
        adapter.notifyDataSetChanged();
    }

    public BluetoothUtil getBluetoothUtil() {
        return bluetoothUtil;
    }
}
