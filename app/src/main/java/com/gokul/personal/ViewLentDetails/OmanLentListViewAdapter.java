package com.gokul.personal.ViewLentDetails;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gokul.personal.R;

import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OmanLentListViewAdapter extends ArrayAdapter<LentListViewData> {

    public OmanLentListViewAdapter(Context context, ArrayList<LentListViewData> lentListData) {
        super(context, R.layout.bal_list_view, lentListData);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, @Nullable View convertView, ViewGroup parent) {

        LentListViewData lentListData = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bal_list_view, parent, false);
        }

        TextView listBalName = convertView.findViewById(R.id.listBalName);
        TextView listBalAmount = convertView.findViewById(R.id.listBalAmount);
        TextView listBalButton = convertView.findViewById(R.id.listBalButton);


        listBalName.setText(lentListData.name);
        listBalAmount.setText(String.format(": OMR %s", new DecimalFormat("0.000").format(Double.parseDouble(lentListData.remAmount))));
        listBalButton.setOnClickListener(view -> {

            Intent intent = new Intent(view.getContext(), ViewLentDetailsRecyclerActivity.class);
            if (lentListData.name.equalsIgnoreCase("All")) {
                intent.putExtra("name", "All");
            } else {
                intent.putExtra("name", lentListData.getName());
            }
            intent.putExtra("country", "Oman");
            view.getContext().startActivity(intent);
            ((Activity) view.getContext()).finish();
        });

        return convertView;
    }
}