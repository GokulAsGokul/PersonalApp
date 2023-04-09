package com.gokul.personal.ViewBalance;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.gokul.personal.AddBalances.BalanceDetailsData;
import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.muddz.styleabletoast.StyleableToast;

public class ViewAllOmanBalanceRecyclerActivity extends AppCompatActivity {

    private ArrayList<BalanceDetailsData> list;
    private final ArrayList<String> filterData = new ArrayList<>();
    private final ArrayList<BalanceDetailsData> searchList = new ArrayList<>();
    private final ArrayList<BalanceDetailsData> filterList = new ArrayList<>();
    private Dialog dialog;
    private ViewAllOmanBalanceDetailsRecyclerAdapter adapter;
    private Vibrator vibrator;
    private Menu myMenu;
    private TextInputLayout viewBalFilterLayout, viewBalSortLayout;
    private AutoCompleteTextView viewBalFilter, viewBalSort;
    private String filterDropDown, sortDropDown;
    private LottieAnimationView animationView;
    private RecyclerView recyclerView;
    private ConstraintLayout viewBalConstraintLayout;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private final ArrayList<String> omanBalanceNameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_oman_balance_recycler);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.background)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Balances");
        actionBar.setDisplayHomeAsUpEnabled(true);

        filterData.add("All");

        recyclerView = findViewById(R.id.viewOMBalRecycler);
        animationView = findViewById(R.id.viewOMBalAnimation);
        animationView.setVisibility(View.GONE);

        viewBalConstraintLayout = findViewById(R.id.viewOMBalConstraintLayout);
        viewBalFilterLayout = findViewById(R.id.viewOMBalFilterLayout);
        viewBalSortLayout = findViewById(R.id.viewOMBalSortLayout);
        viewBalFilter = findViewById(R.id.viewOMBalFilter);
        viewBalSort = findViewById(R.id.viewOMBalSort);

        vibrator = (Vibrator) getSystemService((VIBRATOR_SERVICE));

        list = new ArrayList<>();

        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);

        dialog = new Dialog(ViewAllOmanBalanceRecyclerActivity.this, android.R.style.Theme_DeviceDefault_NoActionBar);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
            dialog.getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));
        }
        if (ConnectionCheck.getConnectionType(getApplicationContext()) != 0) {
            dialog.show();
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference.child("Available Balance Details Oman").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    omanBalanceNameList.add(childDataSnapshot.child("source").getValue(String.class));
                }
                if (omanBalanceNameList.isEmpty()) {
                    viewBalConstraintLayout.setVisibility(View.GONE);
                    myMenu.findItem(R.id.balSearch).setVisible(false);
                    animationView.setVisibility(View.VISIBLE);
                    animationView.playAnimation();
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    recyclerData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(ViewAllOmanBalanceRecyclerActivity.this, "Oman Balance Details Fetch Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

    }

    private void recyclerData() {

        for (int i = 0; i < omanBalanceNameList.size(); i++) {
            databaseReference.child("Balance Details Oman").child(omanBalanceNameList.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        BalanceDetailsData data1 = dataSnapshot.getValue(BalanceDetailsData.class);
                        list.add(data1);
                        filterData.add(dataSnapshot.child("filter").getValue(String.class));
                    }
                    adapter.notifyDataSetChanged();

                    filterDataCall();
                    sortCall();
                    list.sort((b1, b2) -> {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            return String.valueOf(Objects.requireNonNull(dateFormat.parse(b2.getDate())).getTime()).compareTo(
                                    String.valueOf(Objects.requireNonNull(dateFormat.parse(b1.getDate())).getTime()));
                        } catch (ParseException e) {
                            StyleableToast.makeText(ViewAllOmanBalanceRecyclerActivity.this, "List Sort Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                        }
                        return 0;
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(ViewAllOmanBalanceRecyclerActivity.this, "oman Balance Details Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                }
            });
        }
        adapter = new ViewAllOmanBalanceDetailsRecyclerAdapter(this, list, ViewAllOmanBalanceRecyclerActivity.this, animationView, vibrator);
        recyclerView.setAdapter(adapter);
    }

    private void filterDataCall() {

        viewBalFilter.setInputType(InputType.TYPE_NULL);
        viewBalFilter.setClickable(true);
        viewBalFilter.setFocusable(false);
        viewBalFilterLayout.setClickable(true);
        List<String> newList = filterData.stream().distinct().sorted(BalanceDetailsData.balComparatorFilter).collect(Collectors.toList());
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(ViewAllOmanBalanceRecyclerActivity.this, R.layout.filter_list, newList);
        viewBalFilter.setAdapter(filterAdapter);
        dialog.dismiss();
        viewBalFilter.setOnItemClickListener((parent, view, position, id) -> {
            UIUtil.hideKeyboard(ViewAllOmanBalanceRecyclerActivity.this);
            filterDropDown = parent.getItemAtPosition(position).toString();
            filterDD(newList.get(position));
        });
    }

    private void sortCall() {

        ArrayList<String> sortList = new ArrayList<>();
        sortList.add("Latest");
        sortList.add("Oldest");
        sortList.add("Category");
        sortList.add("Mode");
        sortList.add("Type");
        sortList.add("Highest");
        sortList.add("Lowest");
        sortList.add("Source");

        viewBalSort.setInputType(InputType.TYPE_NULL);
        viewBalSort.setClickable(true);
        viewBalSort.setFocusable(false);
        viewBalSortLayout.setClickable(true);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(ViewAllOmanBalanceRecyclerActivity.this, R.layout.filter_list, sortList);
        viewBalSort.setAdapter(sortAdapter);
        dialog.dismiss();

        viewBalSort.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            UIUtil.hideKeyboard(ViewAllOmanBalanceRecyclerActivity.this);
            sortDropDown = parent.getItemAtPosition(position).toString();

            if ((sortList.get(position)).equalsIgnoreCase("Latest")) {
                if (filterList.size() > 0) {
                    filterList.sort(BalanceDetailsData.balComparatorLatest);
                } else if (searchList.size() > 0) {
                    searchList.sort(BalanceDetailsData.balComparatorLatest);
                } else {
                    list.sort(BalanceDetailsData.balComparatorLatest);
                }
                adapter.notifyDataSetChanged();
            } else if ((sortList.get(position)).equalsIgnoreCase("Oldest")) {
                if (filterList.size() > 0) {
                    filterList.sort(BalanceDetailsData.balComparatorOldest);
                }
                if (searchList.size() > 0) {
                    searchList.sort(BalanceDetailsData.balComparatorOldest);
                } else {
                    list.sort(BalanceDetailsData.balComparatorOldest);
                }
                adapter.notifyDataSetChanged();
            } else if ((sortList.get(position)).equalsIgnoreCase("Category")) {
                if (filterList.size() > 0) {
                    filterList.sort(BalanceDetailsData.balComparatorCategory);
                } else if (searchList.size() > 0) {
                    searchList.sort(BalanceDetailsData.balComparatorCategory);
                } else {
                    list.sort(BalanceDetailsData.balComparatorCategory);
                }
                adapter.notifyDataSetChanged();
            } else if ((sortList.get(position)).equalsIgnoreCase("Mode")) {
                if (filterList.size() > 0) {
                    filterList.sort(BalanceDetailsData.balComparatorMode);
                } else if (searchList.size() > 0) {
                    searchList.sort(BalanceDetailsData.balComparatorMode);
                } else {
                    list.sort(BalanceDetailsData.balComparatorMode);
                }
                adapter.notifyDataSetChanged();
            } else if ((sortList.get(position)).equalsIgnoreCase("Highest")) {
                if (filterList.size() > 0) {
                    filterList.sort(BalanceDetailsData.balComparatorHighest);
                } else if (searchList.size() > 0) {
                    searchList.sort(BalanceDetailsData.balComparatorHighest);
                } else {
                    list.sort(BalanceDetailsData.balComparatorHighest);
                }
                adapter.notifyDataSetChanged();
            } else if ((sortList.get(position)).equalsIgnoreCase("Lowest")) {
                if (filterList.size() > 0) {
                    filterList.sort(BalanceDetailsData.balComparatorLowest);
                } else if (searchList.size() > 0) {
                    searchList.sort(BalanceDetailsData.balComparatorLowest);
                } else {
                    list.sort(BalanceDetailsData.balComparatorLowest);
                }
                adapter.notifyDataSetChanged();
            }
        });

    }

    public void searchFilter(String text) {
        searchList.clear();
        if (filterList.size() > 0) {
            for (BalanceDetailsData data : filterList) {
                if ((data.getDate().toLowerCase()).startsWith(text)) {
                    searchList.add(data);
                }
            }
        } else {
            for (BalanceDetailsData data : list) {
                if ((data.getDate().toLowerCase()).startsWith(text)) {
                    searchList.add(data);
                }
            }
        }

        if (searchList.isEmpty()) {
            animationView.setVisibility(View.VISIBLE);
        } else {
            animationView.setVisibility(View.GONE);
        }

        adapter.updateList(searchList);
    }

    public void filterDD(String text) {
        filterList.clear();
        if (searchList.size() > 0) {
            for (BalanceDetailsData data : searchList) {
                if (text.equalsIgnoreCase((data.getFilter().toLowerCase()))) {
                    filterList.add(data);
                } else if (text.equalsIgnoreCase("All")) {
                    filterList.add(data);
                }
            }
        } else {
            for (BalanceDetailsData data : list) {
                if (text.equalsIgnoreCase((data.getFilter().toLowerCase()))) {
                    filterList.add(data);
                } else if (text.equalsIgnoreCase("All")) {
                    filterList.add(data);
                }
            }
        }

        if (filterList.isEmpty()) {
            animationView.setVisibility(View.VISIBLE);
        } else {
            animationView.setVisibility(View.GONE);
        }

        adapter.updateList(filterList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.view_bal_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.balSearch);
        myMenu = menu;
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search By Date");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchFilter(newText);
                return false;
            }
        });
        try {
            @SuppressLint("SoonBlockedPrivateApi") Field mCursorDrawableRes = TextView.class.getDeclaredField("mDrawableForCursor");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchView, R.drawable.filter_color_cursor);
        } catch (Exception ignored) {
        }

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(ViewAllOmanBalanceRecyclerActivity.this, ViewBalanceDetailsActivity.class));
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ViewAllOmanBalanceRecyclerActivity.this, ViewBalanceDetailsActivity.class));
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
    }

}