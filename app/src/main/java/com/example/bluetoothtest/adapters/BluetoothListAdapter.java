package com.example.bluetoothtest.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bluetoothtest.R;
import java.util.List;

public class BluetoothListAdapter extends RecyclerView.Adapter<BluetoothListAdapter.MyViewHolder> {
    private final LayoutInflater inflater;
    private List<BluetoothDevice> list;
    private ItemClickCallback callback;

    public BluetoothListAdapter(Context context, List<BluetoothDevice> list) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(list.get(position).getName());
        holder.adress.setText(list.get(position).getAddress());
        holder.itemView.setOnClickListener(v -> {
                    Log.d("Test", list.get(position).getName());
                    if (callback != null)
                        callback.onClick(list.get(position));
                    else
                        Log.d("Test", "null");
                }
        );

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView adress;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            adress = itemView.findViewById(R.id.adress);
            //textView.setOnClickListener(v -> callback.onClick());
        }
    }

    public void setCallback(ItemClickCallback callback) {
        this.callback = callback;
    }
}
