package com.gokul.personal.ViewPersonalDetails;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.gokul.personal.General.AdminData;
import com.gokul.personal.General.ConnectionCheck;
import com.gokul.personal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class ViewPersonalDetailsActivity extends AppCompatActivity {

    private ArrayList<AdminData> list;
    private Dialog dialog;
    private ViewPersonalDetailsRecyclerAdapter adapter;
    private Vibrator vibrator;
    private Menu myMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_personal_details);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.background)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        RecyclerView recyclerView = findViewById(R.id.personalDetail);
        LottieAnimationView animationView = findViewById(R.id.detailsAnimation);
        animationView.setVisibility(View.GONE);

        vibrator = (Vibrator) getSystemService((VIBRATOR_SERVICE));

        list = new ArrayList<>();

        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);

        dialog = new Dialog(ViewPersonalDetailsActivity.this);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        if (ConnectionCheck.getConnectionType(getApplicationContext()) != 0) {
            dialog.show();
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseDatabase.getInstance().getReference("Documents Data").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    AdminData data1 = dataSnapshot.getValue(AdminData.class);
                    list.add(data1);
                    dialog.dismiss();
                }
                adapter.notifyDataSetChanged();
                if (list.isEmpty()) {
                    myMenu.findItem(R.id.search).setVisible(false);
                    myMenu.findItem(R.id.sortByAZ).setVisible(false);
                    myMenu.findItem(R.id.sortByZA).setVisible(false);
                    animationView.setVisibility(View.VISIBLE);
                    animationView.playAnimation();
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(ViewPersonalDetailsActivity.this, "Personal Details Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

        adapter = new ViewPersonalDetailsRecyclerAdapter(this, list, ViewPersonalDetailsActivity.this);
        recyclerView.setAdapter(adapter);

    }
}