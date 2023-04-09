package com.gokul.personal.ViewBalance;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class IndiaListViewAdapter extends ArrayAdapter<BalanceListViewData> {

    public IndiaListViewAdapter(Context context, ArrayList<BalanceListViewData> balanceData) {
        super(context, R.layout.bal_list_view, balanceData);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        BalanceListViewData balanceData = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bal_list_view, parent, false);
        }

        TextView listBalName = convertView.findViewById(R.id.listBalName);
        TextView listBalAmount = convertView.findViewById(R.id.listBalAmount);
        TextView listBalButton = convertView.findViewById(R.id.listBalButton);

        listBalName.setText(balanceData.name);
        listBalAmount.setText(String.format(": Rs %s", new DecimalFormat("0.00").format(Double.parseDouble(balanceData.amount))));
        listBalButton.setOnClickListener(view -> {

            if (balanceData.name.equalsIgnoreCase("All")) {
                view.getContext().startActivity(new Intent(view.getContext(), ViewAllIndiaBalanceRecyclerActivity.class));
                ((Activity) view.getContext()).finish();
            } else {
                Intent intent = new Intent(view.getContext(), ViewIndiaBalanceRecyclerActivity.class);
                intent.putExtra("category", balanceData.name);
                view.getContext().startActivity(intent);
                ((Activity) view.getContext()).finish();
            }

        });

        return convertView;
    }
}