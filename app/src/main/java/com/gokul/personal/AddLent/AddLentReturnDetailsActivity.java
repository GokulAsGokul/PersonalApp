package com.gokul.personal.AddLent;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class AddLentReturnDetailsActivity extends AppCompatActivity {

    private TextInputLayout lentRetAmountLayout, lentRetRemAmountLayout, lentRetFromLayout, lentRetToLayout, lentRetTranDateLayout, lentRetRemarksLayout;
    private TextInputEditText lentRetAmount, lentRetRemAmount, lentRetRemarks;
    private AutoCompleteTextView lentRetFrom, lentRetTo, lentRetTranDate;
    private String fromDropDown, toDropDown = "";
    private Vibrator vibrator;
    private String name, amount, from, to, date, remarks;
    private Dialog dialog;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private final ArrayList<String> fromList = new ArrayList<>();
    private final ArrayList<String> toList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lent_return_details);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.background)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);


        lentRetAmountLayout = findViewById(R.id.lentRetAmountLayout);
        lentRetRemAmountLayout = findViewById(R.id.lentRetRemAmountLayout);
        lentRetFromLayout = findViewById(R.id.lentRetFromLayout);
        lentRetToLayout = findViewById(R.id.lentRetToLayout);
        lentRetTranDateLayout = findViewById(R.id.lentRetTranDateLayout);
        lentRetRemarksLayout = findViewById(R.id.lentRetRemarksLayout);
        lentRetAmount = findViewById(R.id.lentRetAmount);
        lentRetRemAmount = findViewById(R.id.lentRetRemAmount);
        lentRetRemarks = findViewById(R.id.lentRetRemarks);
        lentRetFrom = findViewById(R.id.lentRetFrom);
        lentRetTo = findViewById(R.id.lentRetTo);
        lentRetTranDate = findViewById(R.id.lentRetTranDate);
        Button lentRetSubmit = findViewById(R.id.lentRetSubmit);

        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);

        dialog = new Dialog(AddLentReturnDetailsActivity.this, android.R.style.Theme_DeviceDefault_NoActionBar);
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

        vibrator = (Vibrator) getSystemService((VIBRATOR_SERVICE));

        lentRetFrom.setInputType(InputType.TYPE_NULL);
        lentRetFrom.setClickable(false);
        lentRetFrom.setFocusable(false);
        lentRetFromLayout.setClickable(false);

        lentRetFrom.setInputType(InputType.TYPE_NULL);
        lentRetFrom.setClickable(true);
        lentRetFrom.setFocusable(false);
        lentRetFromLayout.setClickable(true);
        databaseReference.child("DropDown Values").child("IndiaLentTo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String temp = childDataSnapshot.child("listValue").getValue(String.class);
                    dialog.show();
                    fromList.add(temp);
                }
                ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(AddLentReturnDetailsActivity.this, R.layout.dropdown_list, fromList);
                sourceAdapter.sort(Comparator.naturalOrder());
                lentRetFrom.setAdapter(sourceAdapter);
                dialog.dismiss();
                lentRetFrom.setOnItemClickListener((parent, view, position, id) -> {
                    remAmountCall();
                    UIUtil.hideKeyboard(AddLentReturnDetailsActivity.this);
                    fromDropDown = parent.getItemAtPosition(position).toString();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                StyleableToast.makeText(AddLentReturnDetailsActivity.this, "From Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

        lentRetTo.setInputType(InputType.TYPE_NULL);
        lentRetTo.setClickable(true);
        lentRetTo.setFocusable(false);
        lentRetToLayout.setClickable(true);
        databaseReference.child("DropDown Values").child("IndiaSource").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String temp = childDataSnapshot.child("listValue").getValue(String.class);
                    dialog.show();
                    toList.add(temp);
                }
                ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(AddLentReturnDetailsActivity.this, R.layout.dropdown_list, toList);
                sourceAdapter.sort(Comparator.naturalOrder());
                lentRetTo.setAdapter(sourceAdapter);
                dialog.dismiss();
                lentRetTo.setOnItemClickListener((parent, view, position, id) -> {
                    remAmountCall();
                    UIUtil.hideKeyboard(AddLentReturnDetailsActivity.this);
                    toDropDown = parent.getItemAtPosition(position).toString();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                StyleableToast.makeText(AddLentReturnDetailsActivity.this, "To Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

        lentRetTranDate.setInputType(InputType.TYPE_NULL);
        lentRetTranDate.setClickable(true);
        lentRetTranDate.setFocusable(false);
        lentRetTranDate.setOnClickListener(v -> {
            UIUtil.hideKeyboard(AddLentReturnDetailsActivity.this);
            AddLentReturnDetailsActivity.this.dayPicker();
        });

        lentRetRemarks.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                addBalanceDetails();
                return true;
            }
            return false;
        });

        lentRetSubmit.setOnClickListener(view1 -> addBalanceDetails());

    }

    private void remAmountCall() {

        if ((Objects.requireNonNull(lentRetAmount.getText()).toString()).isEmpty()) {
            lentRetAmountLayout.setErrorEnabled(true);
            lentRetAmountLayout.setError("Amount is required");
        } else if ((lentRetAmount.getText().toString()).equalsIgnoreCase("0")) {
            lentRetAmountLayout.setErrorEnabled(true);
            lentRetAmountLayout.setError("Amount is required");
        } else if (Integer.parseInt(getIntent().getStringExtra("amount")) < (Integer.parseInt(Objects.requireNonNull(lentRetAmount.getText()).toString()))) {
            lentRetAmountLayout.setErrorEnabled(true);
            lentRetAmountLayout.setError("Entered amount is high");
        } else {
            lentRetAmountLayout.setError(null);
            lentRetAmountLayout.setErrorEnabled(false);
            if (getIntent().getStringExtra("country").equalsIgnoreCase("India")) {
                lentRetRemAmount.setText(String.valueOf(Integer.parseInt(getIntent().getStringExtra("amount")) - Integer.parseInt(lentRetAmount.getText().toString())));
            } else {
                lentRetRemAmount.setText(String.valueOf(BigDecimal.valueOf(Double.parseDouble(getIntent().getStringExtra("amount"))).subtract(BigDecimal.valueOf(Double.parseDouble(lentRetAmount.getText().toString())))));
            }
        }
    }

    private void dayPicker() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddLentReturnDetailsActivity.this, R.style.DateDialogTheme, (view, year1, month1, day1) -> {
            month1 = month1 + 1;
            String date4 = day1 + "-" + month1 + "-" + year1;
            SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            Date formattedDate = null;
            try {
                formattedDate = format2.parse(date4);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert formattedDate != null;
            lentRetTranDate.setText(format2.format(formattedDate));
        }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.background));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.background));
    }

    private void addBalanceDetails() {

    }

}