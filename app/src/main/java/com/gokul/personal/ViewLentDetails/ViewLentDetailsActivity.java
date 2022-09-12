package com.gokul.personal.ViewLentDetails;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.gokul.personal.AddLent.LentData;
import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class ViewLentDetailsActivity extends AppCompatActivity {

    private ArrayList<LentData> list;
    private Dialog dialog;
    private ViewLentDetailsRecyclerAdapter adapter;
    private Vibrator vibrator;
    private Menu myMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lent_details);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.background)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Lents");
        actionBar.setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.lentDetail);
        LottieAnimationView animationView = findViewById(R.id.lentAnimation);
        animationView.setVisibility(View.GONE);

        vibrator = (Vibrator) getSystemService((VIBRATOR_SERVICE));

        list = new ArrayList<>();

        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);

        dialog = new Dialog(ViewLentDetailsActivity.this);
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

        FirebaseDatabase.getInstance().getReference("Lent Details India").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    LentData data1 = dataSnapshot.getValue(LentData.class);
                    list.add(data1);
                    dialog.dismiss();
                }
                adapter.notifyDataSetChanged();
                if (list.isEmpty()) {
                    myMenu.findItem(R.id.lentSearch).setVisible(false);
                    myMenu.findItem(R.id.sortByLatest).setVisible(false);
                    myMenu.findItem(R.id.sortByOldest).setVisible(false);
                    animationView.setVisibility(View.VISIBLE);
                    animationView.playAnimation();
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(ViewLentDetailsActivity.this, "Lent Details Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

        adapter = new ViewLentDetailsRecyclerAdapter(this, list, ViewLentDetailsActivity.this, animationView, vibrator);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.view_lent_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.lentSearch);
        myMenu = menu;
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search by Name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
        try {
            @SuppressLint("SoonBlockedPrivateApi") Field mCursorDrawableRes = TextView.class.getDeclaredField("mDrawableForCursor");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchView, R.drawable.color_cursor);
        } catch (Exception ignored) {
        }

        return super.onCreateOptionsMenu(menu);
    }

    public void filter(String text) {
        ArrayList<LentData> temp = new ArrayList<>();
        for (LentData data : list) {
            if ((data.getName().toLowerCase()).contains(text)) {
                temp.add(data);
            }
        }
        adapter.updateList(temp);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.sortByLatest:
                list.sort(LentData.docComparatorLatest);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.sortByOldest:
                list.sort(LentData.docComparatorOldest);
                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
    }

}