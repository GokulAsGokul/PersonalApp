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

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

import io.github.muddz.styleabletoast.StyleableToast;

import static android.content.Context.VIBRATOR_SERVICE;

public class OmanBalanceFragment extends Fragment {

    private PieChart balanceOMPieChart;
    private final ArrayList<PieEntry> entries = new ArrayList<>();
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private Dialog dialog;
    private Vibrator vibrator;
    private LottieAnimationView balanceOMAnimation;
    private final ArrayList<BalanceListViewData> balanceList = new ArrayList<>();
    private ListView omanBalListView;
    private View view;
    private Boolean balCall = false;
    private Double totalAmount = 0.000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_oman_balance, container, false);

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

        balanceOMAnimation = view.findViewById(R.id.balanceOMAnimation);
        balanceOMPieChart = view.findViewById(R.id.balanceOMPieChart);
        omanBalListView = view.findViewById(R.id.omanBalListView);
        balanceOMAnimation.setVisibility(View.GONE);

        availableBalanceCall(view);

        return view;

    }

    private void availableBalanceCall(View view) {
        balCall = true;
        dialog.show();
        databaseReference.child("Available Balance Details Oman").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String source = childDataSnapshot.child("source").getValue(String.class);
                    String amount = childDataSnapshot.child("amount").getValue(String.class);
                    assert source != null;
                    assert amount != null;
                    entries.add(new PieEntry(Float.parseFloat(amount), source));
                    BalanceListViewData data = new BalanceListViewData(source, amount);
                    balanceList.add(data);
                    totalAmount = Double.parseDouble(String.valueOf(BigDecimal.valueOf(totalAmount).add(BigDecimal.valueOf(Double.parseDouble(amount)))));
                }
                dialog.dismiss();
                if (entries.size() == 0) {
                    balanceOMPieChart.setCenterText("");
                    balanceOMAnimation.setVisibility(View.VISIBLE);
                    CardView omanBalCard = view.findViewById(R.id.omanBalCard);
                    omanBalCard.setVisibility(View.GONE);
                } else {
                    DecimalFormat decimalFormat = new DecimalFormat("0.000");
                    BalanceListViewData data = new BalanceListViewData("All", String.valueOf(decimalFormat.format(totalAmount)));
                    balanceList.add(data);
                    balanceOMPieChart.setCenterText("Oman Balances");
                    balanceOMAnimation.setVisibility(View.GONE);
                    OmanListViewAdapter omanAdapter = new OmanListViewAdapter(view.getContext(), balanceList);
                    omanBalListView.setAdapter(omanAdapter);
                }
                setupPieChartData();

            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(view.getContext(), "Oman Chart Data Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });
    }

    private void setupPieChartData() {

        balanceOMPieChart.setDrawHoleEnabled(true);
        balanceOMPieChart.setUsePercentValues(true);
        balanceOMPieChart.setEntryLabelTextSize(10);
        balanceOMPieChart.setEntryLabelColor(Color.BLACK);
        balanceOMPieChart.setCenterTextSize(20);

        Description description = new Description();
        description.setText("");
        balanceOMPieChart.setDescription(description);
        balanceOMPieChart.getLegend().setEnabled(false);

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
        data.setValueFormatter(new PercentFormatter(balanceOMPieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        balanceOMPieChart.setData(data);
        balanceOMPieChart.invalidate();
        balanceOMPieChart.animateY(1400, Easing.EaseInOutQuad);

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
            totalAmount = 0.000;
            availableBalanceCall(view);
        }
    }

}