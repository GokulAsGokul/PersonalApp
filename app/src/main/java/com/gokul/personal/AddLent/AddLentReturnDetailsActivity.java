package com.gokul.personal.AddLent;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gokul.personal.AddBalances.BalanceDetailsData;
import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.R;
import com.gokul.personal.ViewLentDetails.ViewLentDetailsActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
    private Vibrator vibrator;
    private String amount, remAmount, to, remarks, country, updateFinder, fromDropDown, toDropDown;
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

        country = getIntent().getStringExtra("country");
        updateFinder = getIntent().getStringExtra("updateFinder");

        lentRetRemAmount.setInputType(InputType.TYPE_NULL);
        lentRetRemAmount.setClickable(false);
        lentRetRemAmount.setFocusable(false);
        lentRetRemAmountLayout.setClickable(false);

        lentRetFrom.setInputType(InputType.TYPE_NULL);
        lentRetFrom.setClickable(false);
        lentRetFrom.setFocusable(false);
        lentRetFromLayout.setClickable(false);

        lentRetFrom.setInputType(InputType.TYPE_NULL);
        lentRetFrom.setClickable(true);
        lentRetFrom.setFocusable(false);
        lentRetFromLayout.setClickable(true);

        if (country.equalsIgnoreCase("India")) {
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
        } else {
            databaseReference.child("DropDown Values").child("OmanLentTo").addListenerForSingleValueEvent(new ValueEventListener() {
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
        }

        lentRetTo.setInputType(InputType.TYPE_NULL);
        lentRetTo.setClickable(true);
        lentRetTo.setFocusable(false);
        lentRetToLayout.setClickable(true);

        if (country.equalsIgnoreCase("India")) {
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
        } else {
            databaseReference.child("DropDown Values").child("OmanSource").addListenerForSingleValueEvent(new ValueEventListener() {
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
        }

        lentRetTranDate.setInputType(InputType.TYPE_NULL);
        lentRetTranDate.setClickable(true);
        lentRetTranDate.setFocusable(false);
        lentRetTranDate.setOnClickListener(v -> {
            remAmountCall();
            UIUtil.hideKeyboard(AddLentReturnDetailsActivity.this);
            AddLentReturnDetailsActivity.this.dayPicker();
        });

        lentRetRemarks.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                remAmountCall();
                addReturnDetailsCall();
                return true;
            }
            return false;
        });

        lentRetSubmit.setOnClickListener(view1 -> {
            remAmountCall();
            addReturnDetailsCall();

        });

    }

    private void remAmountCall() {

        if ((Objects.requireNonNull(lentRetAmount.getText()).toString()).isEmpty()) {
            lentRetAmountLayout.setErrorEnabled(true);
            lentRetAmountLayout.setError("Amount is required");
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else if ((lentRetAmount.getText().toString()).equalsIgnoreCase("0")) {
            lentRetAmountLayout.setErrorEnabled(true);
            lentRetAmountLayout.setError("Amount is required");
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else if ((getIntent().getStringExtra("country").equalsIgnoreCase("India")) && (Integer.parseInt(getIntent().getStringExtra("amount")) < (Integer.parseInt(Objects.requireNonNull(lentRetAmount.getText()).toString())))) {
            lentRetAmountLayout.setErrorEnabled(true);
            lentRetAmountLayout.setError("Entered amount is high");
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else if ((getIntent().getStringExtra("country").equalsIgnoreCase("Oman")) && (Double.parseDouble(getIntent().getStringExtra("amount")) < (Double.parseDouble(Objects.requireNonNull(lentRetAmount.getText()).toString())))) {
            lentRetAmountLayout.setErrorEnabled(true);
            lentRetAmountLayout.setError("Entered amount is high");
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
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

    private void addReturnDetailsCall() {

        if (!lentRetAmountLayout.isErrorEnabled()) {

            dialog.setCancelable(true);
            dialog.show();

            if (ConnectionCheck.getConnectionType(getApplicationContext()) == 0) {
                lentRetAmountLayout.setError(null);
                lentRetAmountLayout.setErrorEnabled(false);
                lentRetRemAmountLayout.setError(null);
                lentRetRemAmountLayout.setErrorEnabled(false);
                lentRetFromLayout.setError(null);
                lentRetFromLayout.setErrorEnabled(false);
                lentRetToLayout.setError(null);
                lentRetToLayout.setErrorEnabled(false);
                lentRetTranDateLayout.setError(null);
                lentRetTranDateLayout.setErrorEnabled(false);
                lentRetTranDateLayout.setError(null);
                lentRetTranDateLayout.setErrorEnabled(false);
                lentRetRemarksLayout.setError(null);
                lentRetRemarksLayout.setErrorEnabled(false);
                UIUtil.hideKeyboard(AddLentReturnDetailsActivity.this);
                dialog.dismiss();
                ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
            } else {

                UIUtil.hideKeyboard(AddLentReturnDetailsActivity.this);

                if ((Objects.requireNonNull(lentRetRemarks.getText()).toString()).equalsIgnoreCase("Mention If Any")) {
                    lentRetRemarks.setText(R.string.nill);
                }

                if (country.equalsIgnoreCase("Oman")) {
                    DecimalFormat decimalFormat = new DecimalFormat("0.000");
                    amount = decimalFormat.format(Double.parseDouble(Objects.requireNonNull(lentRetAmount.getText()).toString().trim()));
                    remAmount = decimalFormat.format(Double.parseDouble(Objects.requireNonNull(lentRetRemAmount.getText()).toString().trim()));
                } else {
                    amount = Objects.requireNonNull(lentRetAmount.getText()).toString().trim();
                    remAmount = Objects.requireNonNull(lentRetRemAmount.getText()).toString().trim();
                }
                String from = lentRetFrom.getText().toString().trim();
                to = lentRetTo.getText().toString().trim();
                String date = lentRetTranDate.getText().toString().trim();
                remarks = lentRetRemarks.getText().toString().trim();

                String remarksPattern = "^[a-zA-Z0-9\\s]{4,20}$";

                if (amount.isEmpty()) {
                    lentRetAmountLayout.setErrorEnabled(true);
                    lentRetAmountLayout.setError("Amount is Required");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if (amount.equals("0")) {
                    lentRetAmountLayout.setErrorEnabled(true);
                    lentRetAmountLayout.setError("Amount is Required");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if (remAmount.isEmpty()) {
                    lentRetAmountLayout.setError(null);
                    lentRetAmountLayout.setErrorEnabled(false);
                    lentRetRemAmountLayout.setErrorEnabled(true);
                    lentRetRemAmountLayout.setError("Remaining Amount is Required");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if (from.isEmpty()) {
                    lentRetAmountLayout.setError(null);
                    lentRetAmountLayout.setErrorEnabled(false);
                    lentRetRemAmountLayout.setError(null);
                    lentRetRemAmountLayout.setErrorEnabled(false);
                    lentRetFromLayout.setErrorEnabled(true);
                    lentRetFromLayout.setError("From is Required");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if (to.isEmpty()) {
                    lentRetAmountLayout.setError(null);
                    lentRetAmountLayout.setErrorEnabled(false);
                    lentRetRemAmountLayout.setError(null);
                    lentRetRemAmountLayout.setErrorEnabled(false);
                    lentRetFromLayout.setError(null);
                    lentRetFromLayout.setErrorEnabled(false);
                    lentRetToLayout.setErrorEnabled(true);
                    lentRetToLayout.setError("To is Required");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if (date.isEmpty()) {
                    lentRetAmountLayout.setError(null);
                    lentRetAmountLayout.setErrorEnabled(false);
                    lentRetRemAmountLayout.setError(null);
                    lentRetRemAmountLayout.setErrorEnabled(false);
                    lentRetFromLayout.setError(null);
                    lentRetFromLayout.setErrorEnabled(false);
                    lentRetToLayout.setError(null);
                    lentRetToLayout.setErrorEnabled(false);
                    lentRetTranDateLayout.setErrorEnabled(true);
                    lentRetTranDateLayout.setError("Date is Required");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if (remarks.isEmpty()) {
                    lentRetAmountLayout.setError(null);
                    lentRetAmountLayout.setErrorEnabled(false);
                    lentRetRemAmountLayout.setError(null);
                    lentRetRemAmountLayout.setErrorEnabled(false);
                    lentRetFromLayout.setError(null);
                    lentRetFromLayout.setErrorEnabled(false);
                    lentRetToLayout.setError(null);
                    lentRetToLayout.setErrorEnabled(false);
                    lentRetTranDateLayout.setError(null);
                    lentRetTranDateLayout.setErrorEnabled(false);
                    lentRetRemarksLayout.setErrorEnabled(true);
                    lentRetRemarksLayout.setError("Remarks is Required");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if (!remarks.matches(remarksPattern)) {
                    lentRetAmountLayout.setError(null);
                    lentRetAmountLayout.setErrorEnabled(false);
                    lentRetRemAmountLayout.setError(null);
                    lentRetRemAmountLayout.setErrorEnabled(false);
                    lentRetFromLayout.setError(null);
                    lentRetFromLayout.setErrorEnabled(false);
                    lentRetToLayout.setError(null);
                    lentRetToLayout.setErrorEnabled(false);
                    lentRetTranDateLayout.setError(null);
                    lentRetTranDateLayout.setErrorEnabled(false);
                    lentRetRemarksLayout.setErrorEnabled(true);
                    lentRetRemarksLayout.setError("Remarks should be length of 4 to 20 alphanumeric");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if (from.equals("Others")) {
                    if ((remarks.isEmpty()) || (remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nill"))) {
                        lentRetAmountLayout.setError(null);
                        lentRetAmountLayout.setErrorEnabled(false);
                        lentRetRemAmountLayout.setError(null);
                        lentRetRemAmountLayout.setErrorEnabled(false);
                        lentRetFromLayout.setError(null);
                        lentRetFromLayout.setErrorEnabled(false);
                        lentRetToLayout.setError(null);
                        lentRetToLayout.setErrorEnabled(false);
                        lentRetTranDateLayout.setError(null);
                        lentRetTranDateLayout.setErrorEnabled(false);
                        lentRetRemarksLayout.setErrorEnabled(true);
                        lentRetRemarksLayout.setError("Mention From Detail");
                        dialog.dismiss();
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        availableBalanceUpdateCall();
                    }
                } else if (to.equalsIgnoreCase("Bank Account")) {
                    if ((remarks.isEmpty()) || (remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nill"))) {
                        lentRetAmountLayout.setError(null);
                        lentRetAmountLayout.setErrorEnabled(false);
                        lentRetRemAmountLayout.setError(null);
                        lentRetRemAmountLayout.setErrorEnabled(false);
                        lentRetFromLayout.setError(null);
                        lentRetFromLayout.setErrorEnabled(false);
                        lentRetToLayout.setError(null);
                        lentRetToLayout.setErrorEnabled(false);
                        lentRetTranDateLayout.setError(null);
                        lentRetTranDateLayout.setErrorEnabled(false);
                        lentRetRemarksLayout.setErrorEnabled(true);
                        lentRetRemarksLayout.setError("Mention Bank Name");
                        dialog.dismiss();
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        availableBalanceUpdateCall();
                    }
                } else if (to.equals("Others")) {
                    if ((remarks.isEmpty()) || (remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nill"))) {
                        lentRetAmountLayout.setError(null);
                        lentRetAmountLayout.setErrorEnabled(false);
                        lentRetRemAmountLayout.setError(null);
                        lentRetRemAmountLayout.setErrorEnabled(false);
                        lentRetFromLayout.setError(null);
                        lentRetFromLayout.setErrorEnabled(false);
                        lentRetToLayout.setError(null);
                        lentRetToLayout.setErrorEnabled(false);
                        lentRetTranDateLayout.setError(null);
                        lentRetTranDateLayout.setErrorEnabled(false);
                        lentRetRemarksLayout.setErrorEnabled(true);
                        lentRetRemarksLayout.setError("Mention To Details");
                        dialog.dismiss();
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        availableBalanceUpdateCall();
                    }
                } else {
                    availableBalanceUpdateCall();
                }
            }
        }
    }

    private void availableBalanceUpdateCall() {
        lentRetAmountLayout.setError(null);
        lentRetAmountLayout.setErrorEnabled(false);
        lentRetRemAmountLayout.setError(null);
        lentRetRemAmountLayout.setErrorEnabled(false);
        lentRetFromLayout.setError(null);
        lentRetFromLayout.setErrorEnabled(false);
        lentRetToLayout.setError(null);
        lentRetToLayout.setErrorEnabled(false);
        lentRetTranDateLayout.setError(null);
        lentRetTranDateLayout.setErrorEnabled(false);
        lentRetTranDateLayout.setError(null);
        lentRetTranDateLayout.setErrorEnabled(false);
        lentRetRemarksLayout.setError(null);
        lentRetRemarksLayout.setErrorEnabled(false);
        UIUtil.hideKeyboard(AddLentReturnDetailsActivity.this);
        if (ConnectionCheck.getConnectionType(getApplicationContext()) == 0) {
            dialog.dismiss();
            ConnectionCheck.connectionDialogShow(getApplicationContext(), AddLentReturnDetailsActivity.this);
        } else {
            databaseReference.child("Available Balance Details " + country).child(to).child("amount").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String temp = snapshot.getValue(String.class);

                    if (country.equalsIgnoreCase("India")) {

                        if (temp == null) {
                            availableBalanceSubmit(String.valueOf(Integer.parseInt(amount)), updateFinder);
                        } else {
                            availableBalanceSubmit(String.valueOf(Integer.parseInt(temp) + Integer.parseInt(amount)), updateFinder);
                        }

                    } else if (country.equalsIgnoreCase("Oman")) {
                        if (temp == null) {
                            availableBalanceSubmit((String.valueOf(BigDecimal.valueOf(Double.parseDouble(amount)))), updateFinder);
                        } else {
                            availableBalanceSubmit(String.valueOf(BigDecimal.valueOf(Double.parseDouble(temp)).add(BigDecimal.valueOf(Double.parseDouble(amount)))), updateFinder);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(AddLentReturnDetailsActivity.this, "Available Balance Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                }
            });
        }

    }

    private void availableBalanceSubmit(String updateAmount, String updateFinder) {

        BalanceDetailsData data = new BalanceDetailsData();
        data.setSource(to);
        if (country.equalsIgnoreCase("India")) {
            data.setAmount(updateAmount);
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#.000");
            data.setAmount(decimalFormat.format(Double.parseDouble(updateAmount)));
        }

        databaseReference.child("Available Balance Details " + country).child(to).setValue(data).addOnCompleteListener(task -> updateLentDetails(updateFinder)).addOnFailureListener(e1 -> {
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(AddLentReturnDetailsActivity.this, "Available Balance Submit Error: " + e1.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });
    }

    private void updateLentDetails(String updateFinder) {

        String finalAmount;
        if (country.equalsIgnoreCase("India")) {
            finalAmount = remAmount;
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("0.000");
            finalAmount = decimalFormat.format(Double.parseDouble(remAmount));
        }

        databaseReference.child("Lent Details " + country).child(updateFinder).child("remAmount").setValue(finalAmount).
                addOnCompleteListener(task -> addBalanceDetails()).addOnFailureListener(e -> {
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(AddLentReturnDetailsActivity.this, "Lent Details Update Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });
    }

    private void addBalanceDetails() {

        String date = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.getDefault()).format(new Date());

        BalanceDetailsData data = new BalanceDetailsData();
        data.setSource(to);
        if (country.equalsIgnoreCase("India")) {
            data.setAmount(amount);
            data.setTransAmount(amount);
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("0.000");
            String finalAmount = decimalFormat.format(Double.parseDouble(amount));
            data.setAmount(finalAmount);
            data.setTransAmount(finalAmount);
        }
        data.setCategory("Lent Return");
        data.setType("Income");
        data.setDate(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
        data.setRemarks(remarks);
        data.setFilter(date.substring(3, 10).replaceAll("-", "_"));
        data.setUpdateFinder(date);

        databaseReference.child("Balance Details " + country).child(to).child(date).setValue(data).
                addOnSuccessListener(unused -> {
                    startActivity(new Intent(AddLentReturnDetailsActivity.this, ViewLentDetailsActivity.class));
                    finish();
                    dialog.dismiss();
                    StyleableToast.makeText(AddLentReturnDetailsActivity.this, "Lent Return Details Updated Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                }).addOnFailureListener(e -> {
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(AddLentReturnDetailsActivity.this, "Balance Details Update Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(AddLentReturnDetailsActivity.this, ViewLentDetailsActivity.class));
            finish();
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
        startActivity(new Intent(AddLentReturnDetailsActivity.this, ViewLentDetailsActivity.class));
        finish();
    }

}