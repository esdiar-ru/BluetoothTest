package com.example.bluetoothtest.ui.search;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bluetoothtest.R;
import com.example.bluetoothtest.adapters.BluetoothListAdapter;
import com.example.bluetoothtest.base.BaseView;
import com.example.bluetoothtest.ui.main.MainActivity;
import com.example.bluetoothtest.util.BluetoothUtil;
import java.util.ArrayList;

public class SearchActivity extends BaseView<SearchPresenterMvp> implements SearchViewMvp {

    private Button button1, button2, button3;
    private RecyclerView recyclerView;
    private BluetoothListAdapter adapter;
    private ArrayList<BluetoothDevice> arrayList;
    private BluetoothUtil bluetoothUtil;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detach();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        bluetoothUtil = new BluetoothUtil(this);

        recyclerView = findViewById(R.id.conteiner);
        arrayList = new ArrayList<>();
        adapter = new BluetoothListAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        button1 = findViewById(R.id.test_button1);
        button2 = findViewById(R.id.test_button2);
        button3 = findViewById(R.id.test_button3);
        button3.setVisibility(View.INVISIBLE);
        setPresenter(new SearchPresenter());
        presenter.attach(this);
        button1.setOnClickListener((view) -> presenter.onBottonClick(view));
        button2.setOnClickListener((view) -> presenter.onBottonClick(view));
        button3.setOnClickListener((view) -> presenter.onBottonClick(view));
    }

    public void addToListBluetooth(BluetoothDevice bluetoothDevice) {
        if (!arrayList.contains(bluetoothDevice)) {
            arrayList.add(bluetoothDevice);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void bindService(Class clazz, ServiceConnection connection, int code) {
        Intent bluetoothIntent = new Intent(this, clazz);
        bindService(bluetoothIntent, connection, code);
    }

    @Override
    public void unBindService(ServiceConnection connection) {
        unbindService(connection);
    }

    public BluetoothListAdapter getAdapter() {
        return adapter;
    }

    public BluetoothUtil getBluetoothUtil() {
        return bluetoothUtil;
    }

    public void backToActivity() {

        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
