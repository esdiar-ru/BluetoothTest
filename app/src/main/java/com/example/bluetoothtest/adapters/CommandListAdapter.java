package com.example.bluetoothtest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bluetoothtest.R;
import com.example.bluetoothtest.model.Command;
import java.util.List;

public class CommandListAdapter extends RecyclerView.Adapter<CommandListAdapter.MyViewHolder> {
    private final LayoutInflater inflater;
    private List<Command> list;

    public CommandListAdapter(Context context, List<Command> list) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public CommandListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.command_item_list, parent, false);
        return new CommandListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommandListAdapter.MyViewHolder holder, int position) {
        holder.message.setText(list.get(position).toString());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView message;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);

        }
    }

}
