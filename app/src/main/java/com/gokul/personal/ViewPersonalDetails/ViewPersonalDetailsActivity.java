package com.gokul.personal.ViewPersonalDetails;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import com.gokul.personal.AdminActivity.AdminData;
import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.muddz.styleabletoast.StyleableToast;

public class ViewPersonalDetailsActivity extends AppCompatActivity {

    private final ArrayList<AdminData> dataList = new ArrayList<>();
    private final ArrayList<AdminData> filterList = new ArrayList<>();
    private final ArrayList<AdminData> searchList = new ArrayList<>();
    private final ArrayList<String> filterDDList = new ArrayList<>();
    private Dialog dialog;
    private ViewPersonalDetailsRecyclerAdapter adapter;
    private Vibrator vibrator;
    private Menu myMenu;
    private TextInputLayout viewDetFilterLayout, viewDetSortLayout;
    private AutoCompleteTextView viewDetFilter, viewDetSort;
    private String filterDropDown, sortDropDown;
    private LottieAnimationView animationView;
    private ConstraintLayout viewDetRecycLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_personal_details);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.background)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Personal Details");
        actionBar.setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.personalDetail);
        animationView = findViewById(R.id.detailsAnimation);
        animationView.setVisibility(View.GONE);

        viewDetRecycLayout = findViewById(R.id.viewDetRecycLayout);
        viewDetFilterLayout = findViewById(R.id.viewDetFilterLayout);
        viewDetSortLayout = findViewById(R.id.viewDetSortLayout);
        viewDetFilter = findViewById(R.id.viewDetFilter);
        viewDetSort = findViewById(R.id.viewDetSort);

        vibrator = (Vibrator) getSystemService((VIBRATOR_SERVICE));

        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);

        dialog = new Dialog(ViewPersonalDetailsActivity.this, android.R.style.Theme_DeviceDefault_NoActionBar);
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

        FirebaseDatabase.getInstance().getReference("Personal Details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    AdminData data1 = dataSnapshot.getValue(AdminData.class);
                    dataList.add(data1);
                    String filterData = dataSnapshot.child("personalType").getValue(String.class);
                    filterDDList.add(filterData);
                }
                adapter.notifyDataSetChanged();
                if (dataList.isEmpty()) {
                    viewDetRecycLayout.setVisibility(View.GONE);
                    myMenu.findItem(R.id.search).setVisible(false);
                    animationView.setVisibility(View.VISIBLE);
                    animationView.playAnimation();
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    filterDataCall();
                    sortCall();
                    dataList.sort((b1, b2) -> b1.getPersonalName().compareToIgnoreCase(b2.getPersonalName()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(ViewPersonalDetailsActivity.this, "Personal Details Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

        adapter = new ViewPersonalDetailsRecyclerAdapter(this, dataList, ViewPersonalDetailsActivity.this, vibrator);
        recyclerView.setAdapter(adapter);

    }

    private void filterDataCall() {

        List<String> filterList = filterDDList.stream().distinct().collect(Collectors.toList());
        filterList.add("All");

        viewDetFilter.setInputType(InputType.TYPE_NULL);
        viewDetFilter.setClickable(true);
        viewDetFilter.setFocusable(false);
        viewDetFilterLayout.setClickable(true);
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(ViewPersonalDetailsActivity.this, R.layout.filter_list, filterList);
        filterAdapter.sort(Comparator.naturalOrder());
        viewDetFilter.setAdapter(filterAdapter);
        dialog.dismiss();
        viewDetFilter.setOnItemClickListener((parent, view, position, id) -> {
            UIUtil.hideKeyboard(ViewPersonalDetailsActivity.this);
            filterDropDown = parent.getItemAtPosition(position).toString();
            filterDD(filterList.get(position));
        });
    }

    private void sortCall() {

        ArrayList<String> sortList = new ArrayList<>();
        sortList.add("A-Z");
        sortList.add("Z-A");
        sortList.add("Type");

        viewDetSort.setInputType(InputType.TYPE_NULL);
        viewDetSort.setClickable(true);
        viewDetSort.setFocusable(false);
        viewDetSortLayout.setClickable(true);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(ViewPersonalDetailsActivity.this, R.layout.filter_list, sortList);
        viewDetSort.setAdapter(sortAdapter);
        dialog.dismiss();

        viewDetSort.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            UIUtil.hideKeyboard(ViewPersonalDetailsActivity.this);
            sortDropDown = parent.getItemAtPosition(position).toString();
            if ((sortList.get(position)).equalsIgnoreCase("A-Z")) {
                if (filterList.size() > 0) {
                    filterList.sort(AdminData.dataComparatorAZ);
                } else if (searchList.size() > 0) {
                    searchList.sort(AdminData.dataComparatorAZ);
                } else {
                    dataList.sort(AdminData.dataComparatorAZ);
                }
                adapter.notifyDataSetChanged();
            } else if ((sortList.get(position)).equalsIgnoreCase("Z-A")) {
                if (filterList.size() > 0) {
                    filterList.sort(AdminData.dataComparatorZA);
                } else if (searchList.size() > 0) {
                    searchList.sort(AdminData.dataComparatorZA);
                } else {
                    dataList.sort(AdminData.dataComparatorZA);
                }
                adapter.notifyDataSetChanged();
            } else if ((sortList.get(position)).equalsIgnoreCase("Type")) {
                if (filterList.size() > 0) {
                    filterList.sort(AdminData.dataComparatorType);
                } else if (searchList.size() > 0) {
                    searchList.sort(AdminData.dataComparatorType);
                } else {
                    dataList.sort(AdminData.dataComparatorType);
                }
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.view_docs_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        myMenu = menu;
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search By Name");
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

    public void searchFilter(String text) {
        searchList.clear();
        if (filterList.size() > 0) {
            for (AdminData data : filterList) {
                if (((data.getPersonalName().toLowerCase()).contains(text))) {
                    searchList.add(data);
                }
            }
        } else {
            for (AdminData data : dataList) {
                if (((data.getPersonalName().toLowerCase()).contains(text))) {
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
            for (AdminData data : searchList) {
                if (text.equalsIgnoreCase((data.getPersonalType().toLowerCase()))) {
                    filterList.add(data);
                } else if (text.equalsIgnoreCase("All")) {
                    filterList.add(data);
                }
            }
        } else {
            for (AdminData data : dataList) {
                if (text.equalsIgnoreCase((data.getPersonalType().toLowerCase()))) {
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
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

}