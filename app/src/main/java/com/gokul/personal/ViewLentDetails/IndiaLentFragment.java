package com.gokul.personal.ViewLentDetails;

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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

import static android.content.Context.VIBRATOR_SERVICE;

public class IndiaLentFragment extends Fragment {

    private PieChart lentPieChart;
    private final ArrayList<PieEntry> entries = new ArrayList<>();
    private Dialog dialog;
    private Vibrator vibrator;
    private LottieAnimationView lentAnimation;
    private final ArrayList<LentListViewData> lentList = new ArrayList<>();
    private ListView indiaLentListView;
    private View view;
    private Boolean balCall = false;
    private Double totalAmount = 0.00;
    private final HashMap<String, Double> lentMap = new HashMap<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_view_india_lent, container, false);

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

        lentAnimation = view.findViewById(R.id.lentAnimation);
        lentPieChart = view.findViewById(R.id.lentPieChart);
        indiaLentListView = view.findViewById(R.id.indiaLentListView);
        lentAnimation.setVisibility(View.GONE);

        lentDataCall(view);
        return view;

    }

    private void lentDataCall(View view) {
        balCall = true;
        dialog.show();
        FirebaseDatabase.getInstance().getReference().child("Lent Details India").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String name = Objects.requireNonNull(childDataSnapshot.child("name").getValue(String.class));
                    String remAmount = Objects.requireNonNull(childDataSnapshot.child("remAmount").getValue(String.class));
                    if (!lentMap.containsKey(name)) {
                        lentMap.put(name, Double.parseDouble(remAmount));
                    } else {
                        lentMap.put(name, Double.parseDouble(String.valueOf((BigDecimal.valueOf(lentMap.get(name)).add(BigDecimal.valueOf(Double.parseDouble(remAmount)))))));
                    }
                    totalAmount = Double.parseDouble(String.valueOf(BigDecimal.valueOf(totalAmount).add(BigDecimal.valueOf(Double.parseDouble(remAmount)))));
                }

                if (!lentMap.isEmpty()) {
                    for (Map.Entry<String, Double> mapData : lentMap.entrySet()) {
                        entries.add(new PieEntry(Float.parseFloat(String.valueOf(mapData.getValue())), mapData.getKey()));
                        LentListViewData data = new LentListViewData(mapData.getKey(), String.valueOf(mapData.getValue()));
                        lentList.add(data);
                    }
                }

                dialog.dismiss();
                if (entries.size() == 0) {
                    lentPieChart.setCenterText("");
                    lentAnimation.setVisibility(View.VISIBLE);
                    CardView indiaLentCard = view.findViewById(R.id.indiaLentCard);
                    indiaLentCard.setVisibility(View.GONE);
                } else {
                    lentList.sort(LentListViewData.listSorter);
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    LentListViewData data = new LentListViewData("All", String.valueOf(decimalFormat.format(totalAmount)));
                    lentList.add(data);
                    lentPieChart.setCenterText("India Lents");
                    lentAnimation.setVisibility(View.GONE);
                    IndiaLentListViewAdapter indiaAdapter = new IndiaLentListViewAdapter(view.getContext(), lentList);
                    indiaLentListView.setAdapter(indiaAdapter);
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

        lentPieChart.setDrawHoleEnabled(true);
        lentPieChart.setUsePercentValues(true);
        lentPieChart.setEntryLabelTextSize(10);
        lentPieChart.setEntryLabelColor(Color.BLACK);
        lentPieChart.setCenterTextSize(20);

        Description description = new Description();
        description.setText("");
        lentPieChart.setDescription(description);
        lentPieChart.getLegend().setEnabled(false);

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
        data.setValueFormatter(new PercentFormatter(lentPieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        lentPieChart.setData(data);
        lentPieChart.invalidate();
        lentPieChart.animateY(1400, Easing.EaseInOutQuad);

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
            lentList.clear();
            entries.clear();
            totalAmount = 0.00;
            lentDataCall(view);
        }
    }

}