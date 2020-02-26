package com.example.bluetoothtest.base;

import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseView<P extends MvpPresenter> extends AppCompatActivity {
    protected P presenter;

    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public void setPresenter(P presenter) {
        this.presenter = presenter;
    }

    public void startActivity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.isStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.isStop();
    }
}
