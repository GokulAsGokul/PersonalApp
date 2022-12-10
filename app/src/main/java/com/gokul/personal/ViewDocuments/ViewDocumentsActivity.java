package com.gokul.personal.ViewDocuments;

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
import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.Miscellaneous.GeneralData;
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

public class ViewDocumentsActivity extends AppCompatActivity {

    private final ArrayList<GeneralData> dataList = new ArrayList<>();
    private final ArrayList<GeneralData> filterList = new ArrayList<>();
    private final ArrayList<GeneralData> searchList = new ArrayList<>();
    private final ArrayList<String> filterDDList = new ArrayList<>();
    private Dialog dialog;
    private ViewDocumentsRecyclerAdapter adapter;
    private Vibrator vibrator;
    private Menu myMenu;
    private TextInputLayout viewDocFilterLayout, viewDocSortLayout;
    private AutoCompleteTextView viewDocFilter, viewDocSort;
    private String filterDropDown, sortDropDown, searchIdentifier;
    private LottieAnimationView animationView;
    private ConstraintLayout viewDocRecycLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_documents);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.background)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Documents");
        actionBar.setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.docsList);
        animationView = findViewById(R.id.docsAnimation);
        animationView.setVisibility(View.GONE);

        viewDocRecycLayout = findViewById(R.id.viewDocRecycLayout);
        viewDocFilterLayout = findViewById(R.id.viewDocFilterLayout);
        viewDocSortLayout = findViewById(R.id.viewDocSortLayout);
        viewDocFilter = findViewById(R.id.viewDocFilter);
        viewDocSort = findViewById(R.id.viewDocSort);

        vibrator = (Vibrator) getSystemService((VIBRATOR_SERVICE));

        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);

        dialog = new Dialog(ViewDocumentsActivity.this, android.R.style.Theme_DeviceDefault_NoActionBar);
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

        FirebaseDatabase.getInstance().getReference("Documents Data").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    GeneralData data1 = dataSnapshot.getValue(GeneralData.class);
                    dataList.add(data1);
                    String filterData = dataSnapshot.child("fileCategory").getValue(String.class);
                    filterDDList.add(filterData);
                }
                adapter.notifyDataSetChanged();
                if (dataList.isEmpty()) {
                    viewDocRecycLayout.setVisibility(View.GONE);
                    myMenu.findItem(R.id.search).setVisible(false);
                    animationView.setVisibility(View.VISIBLE);
                    animationView.playAnimation();
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    filterDataCall();
                    sortCall();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(ViewDocumentsActivity.this, "Document Details Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

        adapter = new ViewDocumentsRecyclerAdapter(this, dataList, ViewDocumentsActivity.this, vibrator);
        recyclerView.setAdapter(adapter);
    }

    private void filterDataCall() {

        List<String> filterList = filterDDList.stream().distinct().collect(Collectors.toList());
        filterList.add("All");

        viewDocFilter.setInputType(InputType.TYPE_NULL);
        viewDocFilter.setClickable(true);
        viewDocFilter.setFocusable(false);
        viewDocFilterLayout.setClickable(true);
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(ViewDocumentsActivity.this, R.layout.filter_list, filterList);
        filterAdapter.sort(Comparator.naturalOrder());
        viewDocFilter.setAdapter(filterAdapter);
        dialog.dismiss();
        viewDocFilter.setOnItemClickListener((parent, view, position, id) -> {
            UIUtil.hideKeyboard(ViewDocumentsActivity.this);
            filterDropDown = parent.getItemAtPosition(position).toString();
            searchIdentifier = filterList.get(position);
            filterDD(searchIdentifier);
        });
    }

    private void sortCall() {

        ArrayList<String> sortList = new ArrayList<>();
        sortList.add("A-Z");
        sortList.add("Z-A");
        sortList.add("Category");
        sortList.add("Type");

        viewDocSort.setInputType(InputType.TYPE_NULL);
        viewDocSort.setClickable(true);
        viewDocSort.setFocusable(false);
        viewDocSortLayout.setClickable(true);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(ViewDocumentsActivity.this, R.layout.filter_list, sortList);
        viewDocSort.setAdapter(sortAdapter);
        dialog.dismiss();

        viewDocSort.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            UIUtil.hideKeyboard(ViewDocumentsActivity.this);
            sortDropDown = parent.getItemAtPosition(position).toString();
            if ((sortList.get(position)).equalsIgnoreCase("A-Z")) {
                if (filterList.size() > 0) {
                    filterList.sort(GeneralData.docComparatorAZ);
                } else if (searchList.size() > 0) {
                    searchList.sort(GeneralData.docComparatorAZ);
                } else {
                    dataList.sort(GeneralData.docComparatorAZ);
                }
                adapter.notifyDataSetChanged();
            } else if ((sortList.get(position)).equalsIgnoreCase("Z-A")) {
                if (filterList.size() > 0) {
                    filterList.sort(GeneralData.docComparatorZA);
                } else if (searchList.size() > 0) {
                    searchList.sort(GeneralData.docComparatorZA);
                } else {
                    dataList.sort(GeneralData.docComparatorZA);
                }
                adapter.notifyDataSetChanged();
            } else if ((sortList.get(position)).equalsIgnoreCase("Category")) {
                if (filterList.size() > 0) {
                    filterList.sort(GeneralData.docComparatorCategory);
                } else if (searchList.size() > 0) {
                    searchList.sort(GeneralData.docComparatorCategory);
                } else {
                    dataList.sort(GeneralData.docComparatorCategory);
                }
                adapter.notifyDataSetChanged();
            } else if ((sortList.get(position)).equalsIgnoreCase("Type")) {
                if (filterList.size() > 0) {
                    filterList.sort(GeneralData.docComparatorType);
                } else if (searchList.size() > 0) {
                    searchList.sort(GeneralData.docComparatorType);
                } else {
                    dataList.sort(GeneralData.docComparatorType);
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
            for (GeneralData data : filterList) {
                if (((data.getFileName().toLowerCase()).contains(text))) {
                    searchList.add(data);
                }
            }
        } else {
            for (GeneralData data : dataList) {
                if (((data.getFileName().toLowerCase()).contains(text))) {
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
            for (GeneralData data : searchList) {
                if (text.equalsIgnoreCase((data.getFileCategory().toLowerCase()))) {
                    filterList.add(data);
                } else if (text.equalsIgnoreCase("All")) {
                    filterList.add(data);
                }
            }
        } else {
            for (GeneralData data : dataList) {
                if (text.equalsIgnoreCase((data.getFileCategory().toLowerCase()))) {
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

    @SuppressLint("NonConstantResourceId")
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