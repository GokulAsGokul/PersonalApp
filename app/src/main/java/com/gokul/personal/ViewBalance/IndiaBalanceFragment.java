package com.gokul.personal.ViewBalance;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

import io.github.muddz.styleabletoast.StyleableToast;

import static android.content.Context.VIBRATOR_SERVICE;

public class IndiaBalanceFragment extends Fragment {

    private PieChart balancePieChart;
    private final ArrayList<PieEntry> entries = new ArrayList<>();
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private Dialog dialog;
    private Vibrator vibrator;
    private LottieAnimationView balanceAnimation;
    private final ArrayList<BalanceListViewData> balanceList = new ArrayList<>();
    private ListView indiaBalListView;
    private View view;
    private Boolean balCall = false;
    private Double totalAmount = 0.00;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_india_balance, container, false);

        vibrator = (Vibrator) requireActivity().getSystemService((VIBRATOR_SERVICE));

        dialog = new Dialog(view.getContext(), android.R.style.Theme_DeviceDefault_NoActionBar);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setStatusBarColor(ContextCompat.getColor(view.getContext(), R.color.background));
            dialog.getWindow().setNavigationBarColor(ContextCompat.getColor(view.getContext(), R.color.background));
        }

        balanceAnimation = view.findViewById(R.id.balanceAnimation);
        balancePieChart = view.findViewById(R.id.balancePieChart);
        indiaBalListView = view.findViewById(R.id.indiaBalListView);
        balanceAnimation.setVisibility(View.GONE);

        availableBalanceCall(view);
        return view;
    }

    private void availableBalanceCall(View view) {
        balCall = true;
        dialog.show();
        databaseReference.child("Available Balance Details India").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String source = childDataSnapshot.child("source").getValue(String.class);
                    String amount = childDataSnapshot.child("amount").getValue(String.class);
                    assert source != null;
                    assert amount != null;
                    entries.add(new PieEntry(Float.parseFloat(amount), source));
                    BalanceListViewData data = new BalanceListViewData(source, amount);
                    balanceList.add(data);
                    totalAmount = totalAmount + Double.parseDouble(amount);
                }
                dialog.dismiss();
                if (entries.size() == 0) {
                    balancePieChart.setCenterText("");
                    balanceAnimation.setVisibility(View.VISIBLE);
                    CardView indiaBalCard = view.findViewById(R.id.indiaBalCard);
                    indiaBalCard.setVisibility(View.GONE);
                } else {
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    BalanceListViewData data = new BalanceListViewData("All", String.valueOf(decimalFormat.format(totalAmount)));
                    balanceList.add(data);
                    balancePieChart.setCenterText("India Balances");
                    balanceAnimation.setVisibility(View.GONE);
                    IndiaListViewAdapter indiaAdapter = new IndiaListViewAdapter(view.getContext(), balanceList);
                    indiaBalListView.setAdapter(indiaAdapter);
                }
                setupPieChartData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(view.getContext(), "India Chart Data Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

    }

    private void setupPieChartData() {

        balancePieChart.setDrawHoleEnabled(true);
        balancePieChart.setUsePercentValues(true);
        balancePieChart.setEntryLabelTextSize(10);
        balancePieChart.setEntryLabelColor(Color.BLACK);
        balancePieChart.setCenterTextSize(20);

        Description description = new Description();
        description.setText("");
        balancePieChart.setDescription(description);
        balancePieChart.getLegend().setEnabled(false);

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

    @Override
    public void onPause() {
        super.onPause();
        balCall = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!balCall) {
            balanceList.clear();
            entries.clear();
            totalAmount = 0.00;
            availableBalanceCall(view);
        }
    }

}