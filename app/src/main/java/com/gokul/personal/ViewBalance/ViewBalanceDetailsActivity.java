package com.gokul.personal.ViewBalance;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.gokul.personal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class ViewBalanceDetailsActivity extends AppCompatActivity {

    private PieChart balancePieChart;
    private final ArrayList<PieEntry> entries = new ArrayList<>();
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_balance_details);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.background)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Balances");
        actionBar.setDisplayHomeAsUpEnabled(true);

        balancePieChart = findViewById(R.id.balancePieChart);

        dialog = new Dialog(ViewBalanceDetailsActivity.this);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        setupPieChart();
        loadPieChartDataCall();
    }

    private void setupPieChart() {
        balancePieChart.setDrawHoleEnabled(true);
        balancePieChart.setUsePercentValues(true);
        balancePieChart.setEntryLabelTextSize(10);
        balancePieChart.setEntryLabelColor(Color.BLACK);
        balancePieChart.setCenterText("Balance Details");
        balancePieChart.setCenterTextSize(20);
        Description description = new Description();
        description.setText("");
        balancePieChart.setDescription(description);
        balancePieChart.getLegend().setEnabled(false);
        balancePieChart.setNoDataText("");
    }

    private void loadPieChartDataCall() {

        databaseReference.child("India Balance Details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String amount = childDataSnapshot.child("amount").getValue(String.class);
                    assert amount != null;
                    entries.add(new PieEntry(Float.parseFloat(amount), childDataSnapshot.child("source").getValue(String.class)));

                }
                loadPieChartData();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(ViewBalanceDetailsActivity.this, "Chart Data Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

    }

    private void loadPieChartData() {

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(balancePieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        balancePieChart.setData(data);
        balancePieChart.invalidate();

        balancePieChart.animateY(1400, Easing.EaseInOutQuad);
    }

}