package com.gokul.personal.ViewLentDetails;

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
import com.gokul.personal.AddLent.LentDetailsData;
import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.muddz.styleabletoast.StyleableToast;

public class ViewLentDetailsRecyclerActivity extends AppCompatActivity {

    private ArrayList<LentDetailsData> list;
    private final ArrayList<String> filterData = new ArrayList<>();
    private final ArrayList<LentDetailsData> searchList = new ArrayList<>();
    private final ArrayList<LentDetailsData> filterList = new ArrayList<>();
    private Dialog dialog;
    private ViewLentDetailsRecyclerAdapter adapter;
    private Vibrator vibrator;
    private Menu myMenu;
    private TextInputLayout viewLentFilterLayout, viewLentSortLayout;
    private AutoCompleteTextView viewLentFilter, viewLentSort;
    private String filterDropDown, sortDropDown;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lent_details_recycler);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.background)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Lents");
        actionBar.setDisplayHomeAsUpEnabled(true);

        filterData.add("All");

        RecyclerView recyclerView = findViewById(R.id.viewLentRecycler);
        animationView = findViewById(R.id.viewLentAnimation);
        animationView.setVisibility(View.GONE);

        ConstraintLayout viewLentConstraintLayout = findViewById(R.id.viewLentConstraintLayout);
        viewLentFilterLayout = findViewById(R.id.viewLentFilterLayout);
        viewLentSortLayout = findViewById(R.id.viewLentSortLayout);
        viewLentFilter = findViewById(R.id.viewLentFilter);
        viewLentSort = findViewById(R.id.viewLentSort);

        vibrator = (Vibrator) getSystemService((VIBRATOR_SERVICE));

        list = new ArrayList<>();

        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);

        dialog = new Dialog(ViewLentDetailsRecyclerActivity.this, android.R.style.Theme_DeviceDefault_NoActionBar);
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

        if (getIntent().getExtras().getString("name").equalsIgnoreCase("All")) {
            FirebaseDatabase.getInstance().getReference("Lent Details " + (getIntent().getExtras().getString("country"))).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        LentDetailsData data1 = dataSnapshot.getValue(LentDetailsData.class);
                        list.add(data1);
                        filterData.add(dataSnapshot.child("filter").getValue(String.class));
                    }
                    adapter.notifyDataSetChanged();
                    if (list.isEmpty()) {
                        viewLentConstraintLayout.setVisibility(View.GONE);
                        myMenu.findItem(R.id.lentSearch).setVisible(false);
                        animationView.setVisibility(View.VISIBLE);
                        animationView.playAnimation();
                        dialog.dismiss();
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        filterDataCall();
                        sortCall();
                        list.sort((b1, b2) -> {
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            try {
                                return String.valueOf(Objects.requireNonNull(dateFormat.parse(b2.getDate())).getTime()).compareTo(
                                        String.valueOf(Objects.requireNonNull(dateFormat.parse(b1.getDate())).getTime()));
                            } catch (ParseException e) {
                                StyleableToast.makeText(ViewLentDetailsRecyclerActivity.this, "List Sort Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                            }
                            return 0;
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(ViewLentDetailsRecyclerActivity.this, "India Lent Details Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                }
            });
        } else {
            FirebaseDatabase.getInstance().getReference("Lent Details " + (getIntent().getExtras().getString("country"))).
                    orderByChild("name").equalTo(getIntent().getExtras().getString("name")).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        LentDetailsData data1 = dataSnapshot.getValue(LentDetailsData.class);
                        list.add(data1);
                        filterData.add(dataSnapshot.child("filter").getValue(String.class));
                    }
                    adapter.notifyDataSetChanged();
                    if (list.isEmpty()) {
                        viewLentConstraintLayout.setVisibility(View.GONE);
                        myMenu.findItem(R.id.lentSearch).setVisible(false);
                        animationView.setVisibility(View.VISIBLE);
                        animationView.playAnimation();
                        dialog.dismiss();
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        filterDataCall();
                        sortCall();
                        list.sort((b1, b2) -> {
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            try {
                                return String.valueOf(Objects.requireNonNull(dateFormat.parse(b2.getDate())).getTime()).compareTo(
                                        String.valueOf(Objects.requireNonNull(dateFormat.parse(b1.getDate())).getTime()));
                            } catch (ParseException e) {
                                StyleableToast.makeText(ViewLentDetailsRecyclerActivity.this, "List Sort Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                            }
                            return 0;
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(ViewLentDetailsRecyclerActivity.this, "India Lent Details Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                }
            });
        }

        adapter = new ViewLentDetailsRecyclerAdapter(this, list, ViewLentDetailsRecyclerActivity.this, animationView, vibrator, getIntent().getExtras().getString("country"));
        recyclerView.setAdapter(adapter);

    }

    private void filterDataCall() {

        viewLentFilter.setInputType(InputType.TYPE_NULL);
        viewLentFilter.setClickable(true);
        viewLentFilter.setFocusable(false);
        viewLentFilterLayout.setClickable(true);
        List<String> newList = filterData.stream().distinct().sorted(LentDetailsData.lentComparatorFilter).collect(Collectors.toList());
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(ViewLentDetailsRecyclerActivity.this, R.layout.filter_list, newList);
        viewLentFilter.setAdapter(filterAdapter);
        dialog.dismiss();
        viewLentFilter.setOnItemClickListener((parent, view, position, id) -> {
            UIUtil.hideKeyboard(ViewLentDetailsRecyclerActivity.this);
            filterDropDown = parent.getItemAtPosition(position).toString();
            filterDD(newList.get(position));
        });
    }

    private void sortCall() {

        ArrayList<String> sortList = new ArrayList<>();
        sortList.add("Name");
        sortList.add("Latest");
        sortList.add("Oldest");
        sortList.add("Rem Amount");
        sortList.add("Highest");
        sortList.add("Lowest");

        viewLentSort.setInputType(InputType.TYPE_NULL);
        viewLentSort.setClickable(true);
        viewLentSort.setFocusable(false);
        viewLentSortLayout.setClickable(true);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(ViewLentDetailsRecyclerActivity.this, R.layout.filter_list, sortList);
        viewLentSort.setAdapter(sortAdapter);
        dialog.dismiss();

        viewLentSort.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            UIUtil.hideKeyboard(ViewLentDetailsRecyclerActivity.this);
            sortDropDown = parent.getItemAtPosition(position).toString();

            if ((sortList.get(position)).equalsIgnoreCase("Name")) {
                if (filterList.size() > 0) {
                    filterList.sort(LentDetailsData.lentComparatorName);
                } else if (searchList.size() > 0) {
                    searchList.sort(LentDetailsData.lentComparatorName);
                } else {
                    list.sort(LentDetailsData.lentComparatorName);
                }
                adapter.notifyDataSetChanged();
            } else if ((sortList.get(position)).equalsIgnoreCase("Latest")) {
                if (filterList.size() > 0) {
                    filterList.sort(LentDetailsData.lentComparatorLatest);
                } else if (searchList.size() > 0) {
                    searchList.sort(LentDetailsData.lentComparatorLatest);
                } else {
                    list.sort(LentDetailsData.lentComparatorLatest);
                }
                adapter.notifyDataSetChanged();
            } else if ((sortList.get(position)).equalsIgnoreCase("Oldest")) {
                if (filterList.size() > 0) {
                    filterList.sort(LentDetailsData.lentComparatorOldest);
                }
                if (searchList.size() > 0) {
                    searchList.sort(LentDetailsData.lentComparatorOldest);
                } else {
                    list.sort(LentDetailsData.lentComparatorOldest);
                }
                adapter.notifyDataSetChanged();
            } else if ((sortList.get(position)).equalsIgnoreCase("Rem Amount")) {
                if (filterList.size() > 0) {
                    filterList.sort(LentDetailsData.lentComparatorRemAmount);
                } else if (searchList.size() > 0) {
                    searchList.sort(LentDetailsData.lentComparatorRemAmount);
                } else {
                    list.sort(LentDetailsData.lentComparatorRemAmount);
                }
                adapter.notifyDataSetChanged();
            } else if ((sortList.get(position)).equalsIgnoreCase("Highest")) {
                if (filterList.size() > 0) {
                    filterList.sort(LentDetailsData.lentComparatorHighest);
                } else if (searchList.size() > 0) {
                    searchList.sort(LentDetailsData.lentComparatorHighest);
                } else {
                    list.sort(LentDetailsData.lentComparatorHighest);
                }
                adapter.notifyDataSetChanged();
            } else if ((sortList.get(position)).equalsIgnoreCase("Lowest")) {
                if (filterList.size() > 0) {
                    filterList.sort(LentDetailsData.lentComparatorLowest);
                } else if (searchList.size() > 0) {
                    searchList.sort(LentDetailsData.lentComparatorLowest);
                } else {
                    list.sort(LentDetailsData.lentComparatorLowest);
                }
                adapter.notifyDataSetChanged();
            }
        });

    }

    public void searchFilter(String text) {
        searchList.clear();
        if (filterList.size() > 0) {
            for (LentDetailsData data : filterList) {
                if ((data.getName().toLowerCase()).startsWith(text)) {
                    searchList.add(data);
                }
            }
        } else {
            for (LentDetailsData data : list) {
                if ((data.getName().toLowerCase()).startsWith(text)) {
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
            for (LentDetailsData data : searchList) {
                if (text.equalsIgnoreCase((data.getDate().toLowerCase()))) {
                    filterList.add(data);
                } else if (text.equalsIgnoreCase("All")) {
                    filterList.add(data);
                }
            }
        } else {
            for (LentDetailsData data : list) {
                if (text.equalsIgnoreCase((data.getDate().toLowerCase()))) {
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

        getMenuInflater().inflate(R.menu.view_lent_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.lentSearch);
        myMenu = menu;
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search By Name");
        searchView.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
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

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(ViewLentDetailsRecyclerActivity.this, ViewLentDetailsActivity.class));
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ViewLentDetailsRecyclerActivity.this, ViewLentDetailsActivity.class));
        finish();
    }

}