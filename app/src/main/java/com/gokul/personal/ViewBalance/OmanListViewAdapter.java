package com.gokul.personal.ViewBalance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gokul.personal.R;

import java.util.ArrayList;

public class OmanListViewAdapter extends ArrayAdapter<BalanceData> {

    public OmanListViewAdapter(Context context, ArrayList<BalanceData> balanceData) {
        super(context, R.layout.bal_list_view, balanceData);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        BalanceData balanceData = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bal_list_view, parent, false);
        }

        TextView listBalName = convertView.findViewById(R.id.listBalName);
        TextView listBalAmount = convertView.findViewById(R.id.listBalAmount);
        TextView listBalButton = convertView.findViewById(R.id.listBalButton);

        listBalName.setText(balanceData.name);
        listBalAmount.setText(String.format(": Ro %s", balanceData.amount));
        listBalButton.setOnClickListener(view -> {

            if (balanceData.name.equalsIgnoreCase("All")) {
                view.getContext().startActivity(new Intent(view.getContext(), ViewAllOmanBalanceRecyclerActivity.class));
            } else {
                Intent intent = new Intent(view.getContext(), ViewOmanBalanceRecyclerActivity.class);
                intent.putExtra("category", balanceData.name);
                view.getContext().startActivity(intent);
            }

        });

        return convertView;
    }
}