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

public class UpdateLentDetailsActivity extends AppCompatActivity {

    private TextInputLayout lendUpdateNameLayout, lendUpdateAmountLayout, lendUpdateRemAmountLayout, lendUpdateFromLayout, lendUpdateToLayout, lendUpdateTranDateLayout, lendUpdateRemarksLayout;
    private TextInputEditText lendUpdateName, lendUpdateAmount, lendUpdateRemAmount, lendUpdateRemarks;
    private AutoCompleteTextView lendUpdateFrom, lendUpdateTo, lendUpdateTranDate;
    private Vibrator vibrator;
    private String name, amount, remAmount, from, to, date, remarks, oldName, oldAmount, oldTo, oldDate, oldRemarks, country, updateFinder, toDropDown;
    private Dialog dialog;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private final ArrayList<String> toList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_lent_details);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.background)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        lendUpdateNameLayout = findViewById(R.id.lendUpdateNameLayout);
        lendUpdateAmountLayout = findViewById(R.id.lendUpdateAmountLayout);
        lendUpdateRemAmountLayout = findViewById(R.id.lendUpdateRemAmountLayout);
        lendUpdateFromLayout = findViewById(R.id.lendUpdateFromLayout);
        lendUpdateToLayout = findViewById(R.id.lendUpdateToLayout);
        lendUpdateTranDateLayout = findViewById(R.id.lendUpdateTranDateLayout);
        lendUpdateRemarksLayout = findViewById(R.id.lendUpdateRemarksLayout);
        lendUpdateName = findViewById(R.id.lendUpdateName);
        lendUpdateAmount = findViewById(R.id.lendUpdateAmount);
        lendUpdateRemAmount = findViewById(R.id.lendUpdateRemAmount);
        lendUpdateRemarks = findViewById(R.id.lendUpdateRemarks);
        lendUpdateFrom = findViewById(R.id.lendUpdateFrom);
        lendUpdateTo = findViewById(R.id.lendUpdateTo);
        lendUpdateTranDate = findViewById(R.id.lendUpdateTranDate);
        Button lendUpdateSubmit = findViewById(R.id.lendUpdateSubmit);

        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);

        dialog = new Dialog(UpdateLentDetailsActivity.this, android.R.style.Theme_DeviceDefault_NoActionBar);
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

        oldName = getIntent().getStringExtra("name");
        oldAmount = getIntent().getStringExtra("amount");
        String remoldAmount = getIntent().getStringExtra("remAmount");
        oldTo = getIntent().getStringExtra("to");
        oldDate = getIntent().getStringExtra("date");
        oldRemarks = getIntent().getStringExtra("remarks");
        country = getIntent().getStringExtra("country");
        updateFinder = getIntent().getStringExtra("updateFinder");

        lendUpdateName.setText(oldName);
        lendUpdateAmount.setText(oldAmount);
        lendUpdateRemAmount.setText(remoldAmount);
        lendUpdateFrom.setText(getIntent().getStringExtra("from"));
        lendUpdateTo.setText(oldTo);
        lendUpdateTranDate.setText(oldDate);
        lendUpdateRemarks.setText(oldRemarks);

        lendUpdateFrom.setInputType(InputType.TYPE_NULL);
        lendUpdateFrom.setClickable(false);
        lendUpdateFromLayout.setClickable(false);
        lendUpdateFrom.setFocusable(false);

        lendUpdateTo.setInputType(InputType.TYPE_NULL);
        lendUpdateTo.setClickable(true);
        lendUpdateTo.setFocusable(false);
        lendUpdateToLayout.setClickable(true);
        databaseReference.child("DropDown Values").child("IndiaLentTo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String temp = childDataSnapshot.child("listValue").getValue(String.class);
                    dialog.show();
                    toList.add(temp);
                }
                ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(UpdateLentDetailsActivity.this, R.layout.dropdown_list, toList);
                sourceAdapter.sort(Comparator.naturalOrder());
                lendUpdateTo.setAdapter(sourceAdapter);
                dialog.dismiss();
                lendUpdateTo.setOnItemClickListener((parent, view, position, id) -> {
                    UIUtil.hideKeyboard(UpdateLentDetailsActivity.this);
                    toDropDown = parent.getItemAtPosition(position).toString();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                StyleableToast.makeText(UpdateLentDetailsActivity.this, "From Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

        lendUpdateTranDate.setInputType(InputType.TYPE_NULL);
        lendUpdateTranDate.setClickable(true);
        lendUpdateTranDate.setFocusable(false);
        lendUpdateTranDate.setOnClickListener(v -> {
            UIUtil.hideKeyboard(UpdateLentDetailsActivity.this);
            UpdateLentDetailsActivity.this.dayPicker();
        });

        lendUpdateRemarks.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                dialog.setCancelable(false);
                dialog.show();
                updateLentDetailsCall();
                return true;
            }
            return false;
        });

        lendUpdateSubmit.setOnClickListener(view1 -> {
            dialog.setCancelable(false);
            dialog.show();
            updateLentDetailsCall();
        });

    }

    private void dayPicker() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                UpdateLentDetailsActivity.this, R.style.DateDialogTheme, (view1, year1, month1, day1) -> {
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
            lendUpdateTranDate.setText(format2.format(formattedDate));
        }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(UpdateLentDetailsActivity.this, R.color.white));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(UpdateLentDetailsActivity.this, R.color.white));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(UpdateLentDetailsActivity.this, R.color.background));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(UpdateLentDetailsActivity.this, R.color.background));
    }

    private void updateLentDetailsCall() {

        if (ConnectionCheck.getConnectionType(getApplicationContext()) == 0) {
            lendUpdateNameLayout.setError(null);
            lendUpdateNameLayout.setErrorEnabled(false);
            lendUpdateAmountLayout.setError(null);
            lendUpdateAmountLayout.setErrorEnabled(false);
            lendUpdateFromLayout.setError(null);
            lendUpdateFromLayout.setErrorEnabled(false);
            lendUpdateToLayout.setError(null);
            lendUpdateToLayout.setErrorEnabled(false);
            lendUpdateTranDateLayout.setError(null);
            lendUpdateTranDateLayout.setErrorEnabled(false);
            lendUpdateRemarksLayout.setError(null);
            lendUpdateRemarksLayout.setErrorEnabled(false);
            UIUtil.hideKeyboard(UpdateLentDetailsActivity.this);
            dialog.dismiss();
            ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
        } else {

            UIUtil.hideKeyboard(UpdateLentDetailsActivity.this);

            if ((Objects.requireNonNull(lendUpdateRemarks.getText()).toString()).equalsIgnoreCase("Mention If Any")) {
                lendUpdateRemarks.setText(R.string.nill);
            }

            name = Objects.requireNonNull(lendUpdateName.getText()).toString();
            amount = Objects.requireNonNull(lendUpdateAmount.getText()).toString();
            remAmount = Objects.requireNonNull(lendUpdateRemAmount.getText()).toString();
            from = lendUpdateFrom.getText().toString().trim();
            to = lendUpdateTo.getText().toString().trim();
            date = lendUpdateTranDate.getText().toString().trim();
            remarks = lendUpdateRemarks.getText().toString().trim();

            String remarksPattern = "^[a-zA-Z0-9\\s]{4,20}$";

            if (name.isEmpty()) {
                lendUpdateNameLayout.setErrorEnabled(true);
                lendUpdateNameLayout.setError("Borrower Name is Required");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (amount.isEmpty()) {
                lendUpdateNameLayout.setError(null);
                lendUpdateNameLayout.setErrorEnabled(false);
                lendUpdateAmountLayout.setErrorEnabled(true);
                lendUpdateAmountLayout.setError("Amount is Required");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (amount.equals("0")) {
                lendUpdateNameLayout.setError(null);
                lendUpdateNameLayout.setErrorEnabled(false);
                lendUpdateAmountLayout.setErrorEnabled(true);
                lendUpdateAmountLayout.setError("Amount is Required");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (remAmount.isEmpty()) {
                lendUpdateNameLayout.setError(null);
                lendUpdateNameLayout.setErrorEnabled(false);
                lendUpdateAmountLayout.setError(null);
                lendUpdateAmountLayout.setErrorEnabled(false);
                lendUpdateRemAmountLayout.setErrorEnabled(true);
                lendUpdateRemAmountLayout.setError("Amount is Required");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (remAmount.equals("0")) {
                lendUpdateNameLayout.setError(null);
                lendUpdateNameLayout.setErrorEnabled(false);
                lendUpdateAmountLayout.setError(null);
                lendUpdateAmountLayout.setErrorEnabled(false);
                lendUpdateRemAmountLayout.setErrorEnabled(true);
                lendUpdateRemAmountLayout.setError("Amount is Required");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (from.isEmpty()) {
                lendUpdateNameLayout.setError(null);
                lendUpdateNameLayout.setErrorEnabled(false);
                lendUpdateAmountLayout.setError(null);
                lendUpdateAmountLayout.setErrorEnabled(false);
                lendUpdateRemAmountLayout.setError(null);
                lendUpdateRemAmountLayout.setErrorEnabled(false);
                lendUpdateFromLayout.setErrorEnabled(true);
                lendUpdateFromLayout.setError("From is Required");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (to.isEmpty()) {
                lendUpdateNameLayout.setError(null);
                lendUpdateNameLayout.setErrorEnabled(false);
                lendUpdateAmountLayout.setError(null);
                lendUpdateAmountLayout.setErrorEnabled(false);
                lendUpdateRemAmountLayout.setError(null);
                lendUpdateRemAmountLayout.setErrorEnabled(false);
                lendUpdateFromLayout.setError(null);
                lendUpdateFromLayout.setErrorEnabled(false);
                lendUpdateToLayout.setErrorEnabled(true);
                lendUpdateToLayout.setError("To is Required");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (date.isEmpty()) {
                lendUpdateNameLayout.setError(null);
                lendUpdateNameLayout.setErrorEnabled(false);
                lendUpdateAmountLayout.setError(null);
                lendUpdateAmountLayout.setErrorEnabled(false);
                lendUpdateRemAmountLayout.setError(null);
                lendUpdateRemAmountLayout.setErrorEnabled(false);
                lendUpdateFromLayout.setError(null);
                lendUpdateFromLayout.setErrorEnabled(false);
                lendUpdateToLayout.setError(null);
                lendUpdateToLayout.setErrorEnabled(false);
                lendUpdateTranDateLayout.setErrorEnabled(true);
                lendUpdateTranDateLayout.setError("Date is Required");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (remarks.isEmpty()) {
                lendUpdateNameLayout.setError(null);
                lendUpdateNameLayout.setErrorEnabled(false);
                lendUpdateAmountLayout.setError(null);
                lendUpdateAmountLayout.setErrorEnabled(false);
                lendUpdateRemAmountLayout.setError(null);
                lendUpdateRemAmountLayout.setErrorEnabled(false);
                lendUpdateFromLayout.setError(null);
                lendUpdateFromLayout.setErrorEnabled(false);
                lendUpdateToLayout.setError(null);
                lendUpdateToLayout.setErrorEnabled(false);
                lendUpdateTranDateLayout.setError(null);
                lendUpdateTranDateLayout.setErrorEnabled(false);
                lendUpdateRemarksLayout.setErrorEnabled(true);
                lendUpdateRemarksLayout.setError("Remarks is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (!remarks.matches(remarksPattern)) {
                lendUpdateNameLayout.setError(null);
                lendUpdateNameLayout.setErrorEnabled(false);
                lendUpdateAmountLayout.setError(null);
                lendUpdateAmountLayout.setErrorEnabled(false);
                lendUpdateRemAmountLayout.setError(null);
                lendUpdateRemAmountLayout.setErrorEnabled(false);
                lendUpdateFromLayout.setError(null);
                lendUpdateFromLayout.setErrorEnabled(false);
                lendUpdateToLayout.setError(null);
                lendUpdateToLayout.setErrorEnabled(false);
                lendUpdateTranDateLayout.setError(null);
                lendUpdateTranDateLayout.setErrorEnabled(false);
                lendUpdateRemarksLayout.setErrorEnabled(true);
                lendUpdateRemarksLayout.setError("Remarks should be length of 4 to 20 alphanumeric");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (from.equals("Others")) {
                if ((remarks.isEmpty()) || (remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nill"))) {
                    lendUpdateNameLayout.setError(null);
                    lendUpdateNameLayout.setErrorEnabled(false);
                    lendUpdateAmountLayout.setError(null);
                    lendUpdateAmountLayout.setErrorEnabled(false);
                    lendUpdateRemAmountLayout.setError(null);
                    lendUpdateRemAmountLayout.setErrorEnabled(false);
                    lendUpdateFromLayout.setError(null);
                    lendUpdateFromLayout.setErrorEnabled(false);
                    lendUpdateToLayout.setError(null);
                    lendUpdateToLayout.setErrorEnabled(false);
                    lendUpdateTranDateLayout.setError(null);
                    lendUpdateTranDateLayout.setErrorEnabled(false);
                    lendUpdateRemarksLayout.setErrorEnabled(true);
                    lendUpdateRemarksLayout.setError("Mention From Detail");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    availableBalanceUpdateCall();
                }
            } else if (to.equalsIgnoreCase("Bank Account")) {
                if ((remarks.isEmpty()) || (remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nill"))) {
                    lendUpdateNameLayout.setError(null);
                    lendUpdateNameLayout.setErrorEnabled(false);
                    lendUpdateAmountLayout.setError(null);
                    lendUpdateAmountLayout.setErrorEnabled(false);
                    lendUpdateRemAmountLayout.setError(null);
                    lendUpdateRemAmountLayout.setErrorEnabled(false);
                    lendUpdateFromLayout.setError(null);
                    lendUpdateFromLayout.setErrorEnabled(false);
                    lendUpdateToLayout.setError(null);
                    lendUpdateToLayout.setErrorEnabled(false);
                    lendUpdateTranDateLayout.setError(null);
                    lendUpdateTranDateLayout.setErrorEnabled(false);
                    lendUpdateRemarksLayout.setErrorEnabled(true);
                    lendUpdateRemarksLayout.setError("Mention Bank Name");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    availableBalanceUpdateCall();
                }
            } else if (to.equals("Others")) {
                if ((remarks.isEmpty()) || (remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nill"))) {
                    lendUpdateNameLayout.setError(null);
                    lendUpdateNameLayout.setErrorEnabled(false);
                    lendUpdateAmountLayout.setError(null);
                    lendUpdateAmountLayout.setErrorEnabled(false);
                    lendUpdateRemAmountLayout.setError(null);
                    lendUpdateRemAmountLayout.setErrorEnabled(false);
                    lendUpdateFromLayout.setError(null);
                    lendUpdateFromLayout.setErrorEnabled(false);
                    lendUpdateToLayout.setError(null);
                    lendUpdateToLayout.setErrorEnabled(false);
                    lendUpdateTranDateLayout.setError(null);
                    lendUpdateTranDateLayout.setErrorEnabled(false);
                    lendUpdateRemarksLayout.setErrorEnabled(true);
                    lendUpdateRemarksLayout.setError("Mention To Detail");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    availableBalanceUpdateCall();
                }
            } else if (name.equalsIgnoreCase(oldName) && amount.equalsIgnoreCase(oldAmount) && to.equalsIgnoreCase(oldTo) &&
                    date.equalsIgnoreCase(oldDate) && remarks.equalsIgnoreCase(oldRemarks)) {
                lendUpdateNameLayout.setError(null);
                lendUpdateNameLayout.setErrorEnabled(false);
                lendUpdateAmountLayout.setError(null);
                lendUpdateAmountLayout.setErrorEnabled(false);
                lendUpdateRemAmountLayout.setError(null);
                lendUpdateRemAmountLayout.setErrorEnabled(false);
                lendUpdateFromLayout.setError(null);
                lendUpdateFromLayout.setErrorEnabled(false);
                lendUpdateToLayout.setError(null);
                lendUpdateToLayout.setErrorEnabled(false);
                lendUpdateTranDateLayout.setError(null);
                lendUpdateTranDateLayout.setErrorEnabled(false);
                lendUpdateRemarksLayout.setErrorEnabled(true);
                lendUpdateRemarksLayout.setError(null);
                StyleableToast.makeText(UpdateLentDetailsActivity.this, "Enter New Lent Details", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                availableBalanceUpdateCall();
            }
        }
    }

    private void availableBalanceUpdateCall() {
        if (ConnectionCheck.getConnectionType(getApplicationContext()) == 0) {
            lendUpdateNameLayout.setError(null);
            lendUpdateNameLayout.setErrorEnabled(false);
            lendUpdateAmountLayout.setError(null);
            lendUpdateAmountLayout.setErrorEnabled(false);
            lendUpdateFromLayout.setError(null);
            lendUpdateFromLayout.setErrorEnabled(false);
            lendUpdateToLayout.setError(null);
            lendUpdateToLayout.setErrorEnabled(false);
            lendUpdateTranDateLayout.setError(null);
            lendUpdateTranDateLayout.setErrorEnabled(false);
            lendUpdateRemarksLayout.setError(null);
            lendUpdateRemarksLayout.setErrorEnabled(false);
            UIUtil.hideKeyboard(UpdateLentDetailsActivity.this);
            dialog.dismiss();
            ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
        } else {
            databaseReference.child("Available Balance Details " + country).child(from).child("amount").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String temp = snapshot.getValue(String.class);

                    if (country.equalsIgnoreCase("India")) {

                        final boolean validate = Integer.parseInt(amount) < Integer.parseInt(oldAmount);
                        if (temp == null) {
                            if (validate) {
                                availableBalanceSubmit(String.valueOf(Integer.parseInt(oldAmount) - Integer.parseInt(amount)));
                            } else {
                                dialog.dismiss();
                                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                                StyleableToast.makeText(UpdateLentDetailsActivity.this, "Available balance is less than entered amount", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                            }
                        } else {
                            if (validate) {
                                availableBalanceSubmit(String.valueOf(Integer.parseInt(temp) + (Integer.parseInt(oldAmount) - Integer.parseInt(amount))));
                            } else {
                                if (Integer.parseInt(temp) > (Integer.parseInt(amount) - Integer.parseInt(oldAmount))) {
                                    availableBalanceSubmit(String.valueOf(Integer.parseInt(temp) - (Integer.parseInt(amount) - Integer.parseInt(oldAmount))));
                                } else {
                                    dialog.dismiss();
                                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                                    StyleableToast.makeText(UpdateLentDetailsActivity.this, "Available balance is less than entered amount", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                                }
                            }
                        }
                    } else if (country.equalsIgnoreCase("Oman")) {
                        final boolean validate = Double.parseDouble(amount) < Double.parseDouble(oldAmount);
                        if (temp == null) {
                            if (validate) {
                                availableBalanceSubmit(String.valueOf(BigDecimal.valueOf(Double.parseDouble(oldAmount)).subtract(BigDecimal.valueOf(Double.parseDouble(amount)))));
                            } else {
                                dialog.dismiss();
                                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                                StyleableToast.makeText(UpdateLentDetailsActivity.this, "Available balance is less than entered amount", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                            }
                        } else {
                            if (validate) {
                                availableBalanceSubmit(String.valueOf(BigDecimal.valueOf(Double.parseDouble(temp)).add(BigDecimal.valueOf(Double.parseDouble(oldAmount)).subtract(BigDecimal.valueOf(Double.parseDouble(amount))))));
                            } else {
                                if (Double.parseDouble(temp) > (Double.parseDouble(amount) - Double.parseDouble(oldAmount))) {
                                    availableBalanceSubmit(String.valueOf(BigDecimal.valueOf(Double.parseDouble(temp)).add(BigDecimal.valueOf(Double.parseDouble(oldAmount)).subtract(BigDecimal.valueOf(Double.parseDouble(amount))))));
                                } else {
                                    dialog.dismiss();
                                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                                    StyleableToast.makeText(UpdateLentDetailsActivity.this, "Available balance is less than entered amount", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                                }
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(UpdateLentDetailsActivity.this, "Available Balance Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                }
            });
        }
    }

    private void availableBalanceSubmit(String updateAmount) {

        BalanceDetailsData data = new BalanceDetailsData();
        data.setSource(from);
        if (country.equalsIgnoreCase("India")) {
            data.setAmount(updateAmount);
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#.000");
            data.setAmount(decimalFormat.format(Double.parseDouble(updateAmount)));
        }

        databaseReference.child("Available Balance Details " + country).child(from).setValue(data).addOnCompleteListener(task -> updateLentDetails()).addOnFailureListener(e1 -> {
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(UpdateLentDetailsActivity.this, "Available Balance Submit Error: " + e1.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });
    }

    private void updateLentDetails() {

        LentData lentData = new LentData();
        lentData.setName(name);
        if (country.equalsIgnoreCase("India")) {
            lentData.setAmount(amount);
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("0.000");
            String finalAmount = decimalFormat.format(Double.parseDouble(amount));
            lentData.setAmount(finalAmount);
        }
        if (country.equalsIgnoreCase("India")) {
            lentData.setRemAmount(remAmount);
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("0.000");
            String finalAmount = decimalFormat.format(Double.parseDouble(remAmount));
            lentData.setRemAmount(finalAmount);
        }
        lentData.setFrom(from);
        lentData.setTo(to);
        lentData.setDate(date);
        lentData.setRemarks(remarks);
        lentData.setUpdateFinder(updateFinder);

        databaseReference.child("Lent Details " + country).child(updateFinder).setValue(lentData).
                addOnCompleteListener(task -> balanceDetailsUpdate()).addOnFailureListener(e -> {
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(UpdateLentDetailsActivity.this, "Lent Details Update Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });

    }

    private void balanceDetailsUpdate() {

        BalanceDetailsData data = new BalanceDetailsData();
        data.setSource(from);
        if (country.equalsIgnoreCase("India")) {
            data.setAmount(amount);
            data.setTransAmount(amount);
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("0.000");
            String finalAmount = decimalFormat.format(Double.parseDouble(amount));
            data.setAmount(finalAmount);
            data.setTransAmount(finalAmount);
        }
        data.setCategory("Lent");
        data.setType("Expense");
        data.setDate(date);
        data.setRemarks(remarks);
        data.setFilter(date.substring(3, 10).replaceAll("-", "_"));
        data.setUpdateFinder(updateFinder);

        databaseReference.child("Balance Details " + country).child(from).child(updateFinder).setValue(data).
                addOnSuccessListener(unused -> {
                    startActivity(new Intent(UpdateLentDetailsActivity.this, ViewLentDetailsActivity.class));
                    finish();
                    dialog.dismiss();
                    StyleableToast.makeText(UpdateLentDetailsActivity.this, "Lent Details Updated Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                }).addOnFailureListener(e -> {
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(UpdateLentDetailsActivity.this, "Balance Details Update Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(UpdateLentDetailsActivity.this, ViewLentDetailsActivity.class));
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
        startActivity(new Intent(UpdateLentDetailsActivity.this, ViewLentDetailsActivity.class));
        finish();
    }

}