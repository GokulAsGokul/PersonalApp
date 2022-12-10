package com.gokul.personal.AddInvestmentDetails;

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
import com.gokul.personal.ViewInvestmentDetails.ViewInvestmentDetailsActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class UpdateInvestmentDetailsActivity extends AppCompatActivity {

    private TextInputLayout updateInvestNameLayout, updateInvestAmountLayout, updateInvestFolioLayout, updateInvestTermLayout, updateInvestSourceLayout, updateInvestTranDateLayout, updateInvestRemarksLayout;
    private TextInputEditText updateInvestName, updateInvestAmount, updateInvestFolio, updateInvestTerm, updateInvestRemarks;
    private AutoCompleteTextView updateInvestSource, updateInvestTranDate;
    private String freqDropDown;
    private Vibrator vibrator;
    private String name, amount, folioNumber, term, source, date, remarks, name1, amount1, folioNumber1, term1, date1, remarks1, updateFinder;
    private Dialog dialog;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_investment_details);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.background)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        updateInvestNameLayout = findViewById(R.id.updateInvestNameLayout);
        updateInvestAmountLayout = findViewById(R.id.updateInvestAmountLayout);
        updateInvestFolioLayout = findViewById(R.id.updateInvestFolioLayout);
        updateInvestTermLayout = findViewById(R.id.updateInvestTermLayout);
        updateInvestSourceLayout = findViewById(R.id.updateInvestSourceLayout);
        updateInvestTranDateLayout = findViewById(R.id.updateInvestTranDateLayout);
        updateInvestRemarksLayout = findViewById(R.id.updateInvestRemarksLayout);
        updateInvestName = findViewById(R.id.updateInvestName);
        updateInvestAmount = findViewById(R.id.updateInvestAmount);
        updateInvestTerm = findViewById(R.id.updateInvestTerm);
        updateInvestRemarks = findViewById(R.id.updateInvestRemarks);
        updateInvestFolio = findViewById(R.id.updateInvestFolio);
        updateInvestSource = findViewById(R.id.updateInvestSource);
        updateInvestTranDate = findViewById(R.id.updateInvestTranDate);
        Button updateInvestSubmit = findViewById(R.id.updateInvestSubmit);

        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);

        dialog = new Dialog(UpdateInvestmentDetailsActivity.this, android.R.style.Theme_DeviceDefault_NoActionBar);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
            dialog.getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));
        }

        vibrator = (Vibrator) getSystemService((VIBRATOR_SERVICE));

        name1 = getIntent().getStringExtra("name");
        amount1 = getIntent().getStringExtra("amount");
        folioNumber1 = getIntent().getStringExtra("folio");
        term1 = getIntent().getStringExtra("term");
        date1 = getIntent().getStringExtra("date");
        remarks1 = getIntent().getStringExtra("remarks");
        updateFinder = getIntent().getStringExtra("updateFinder");

        updateInvestName.setText(name1);
        updateInvestAmount.setText(amount1);
        updateInvestFolio.setText(folioNumber1);
        updateInvestTerm.setText(term1);
        updateInvestSource.setText(getIntent().getStringExtra("source"));
        updateInvestTranDate.setText(date1);
        updateInvestRemarks.setText(remarks1);

        updateInvestSource.setInputType(InputType.TYPE_NULL);
        updateInvestSource.setClickable(false);
        updateInvestSourceLayout.setClickable(false);
        updateInvestSource.setFocusable(false);

        updateInvestTranDate.setInputType(InputType.TYPE_NULL);
        updateInvestTranDate.setClickable(true);
        updateInvestTranDate.setFocusable(false);
        updateInvestTranDate.setOnClickListener(v -> {
            UIUtil.hideKeyboard(UpdateInvestmentDetailsActivity.this);
            UpdateInvestmentDetailsActivity.this.dayPicker();
        });

        updateInvestRemarks.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                updateInvestSubmitCall();
                return true;
            }
            return false;
        });

        updateInvestSubmit.setOnClickListener(view -> updateInvestSubmitCall());

    }

    private void dayPicker() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                UpdateInvestmentDetailsActivity.this, R.style.DateDialogTheme, (view, year1, month1, day1) -> {
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
            updateInvestTranDate.setText(format2.format(formattedDate));
        }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.background));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.background));
    }

    private void updateInvestSubmitCall() {

        if (ConnectionCheck.getConnectionType(getApplicationContext()) == 0) {
            updateInvestNameLayout.setError(null);
            updateInvestNameLayout.setErrorEnabled(false);
            updateInvestAmountLayout.setError(null);
            updateInvestAmountLayout.setErrorEnabled(false);
            updateInvestFolioLayout.setError(null);
            updateInvestFolio.setError(null);
            updateInvestTermLayout.setError(null);
            updateInvestTermLayout.setErrorEnabled(false);
            updateInvestSourceLayout.setError(null);
            updateInvestSourceLayout.setErrorEnabled(false);
            updateInvestTranDateLayout.setError(null);
            updateInvestTranDateLayout.setErrorEnabled(false);
            updateInvestRemarksLayout.setError(null);
            updateInvestRemarksLayout.setErrorEnabled(false);
            UIUtil.hideKeyboard(UpdateInvestmentDetailsActivity.this);
            ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
        } else {

            if ((Objects.requireNonNull(updateInvestRemarks.getText()).toString()).equalsIgnoreCase("Mention If Any")) {
                updateInvestRemarks.setText(R.string.nill);
            }

            name = Objects.requireNonNull(updateInvestName.getText()).toString();
            amount = Objects.requireNonNull(updateInvestAmount.getText()).toString();
            folioNumber = Objects.requireNonNull(updateInvestFolio.getText()).toString().trim();
            term = Objects.requireNonNull(updateInvestTerm.getText()).toString().trim();
            source = updateInvestSource.getText().toString().trim();
            date = updateInvestTranDate.getText().toString().trim();
            remarks = updateInvestRemarks.getText().toString().trim();

            String remarksPattern = "^[a-zA-Z0-9\\s]{4,20}$";

            if (name.isEmpty()) {
                updateInvestNameLayout.setErrorEnabled(true);
                updateInvestNameLayout.setError("Company Name is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (amount.isEmpty()) {
                updateInvestNameLayout.setError(null);
                updateInvestNameLayout.setErrorEnabled(false);
                updateInvestAmountLayout.setErrorEnabled(true);
                updateInvestAmountLayout.setError("Amount is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (amount.equals("0")) {
                updateInvestNameLayout.setError(null);
                updateInvestNameLayout.setErrorEnabled(false);
                updateInvestAmountLayout.setErrorEnabled(true);
                updateInvestAmountLayout.setError("Amount is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (amount.equalsIgnoreCase(name1)) {
                updateInvestNameLayout.setError(null);
                updateInvestNameLayout.setErrorEnabled(false);
                updateInvestAmountLayout.setErrorEnabled(true);
                updateInvestAmountLayout.setError("New Amount is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (folioNumber.isEmpty()) {
                updateInvestNameLayout.setError(null);
                updateInvestNameLayout.setErrorEnabled(false);
                updateInvestAmountLayout.setError(null);
                updateInvestAmountLayout.setErrorEnabled(false);
                updateInvestFolioLayout.setErrorEnabled(true);
                updateInvestFolioLayout.setError("Frequency is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (term.isEmpty()) {
                updateInvestNameLayout.setError(null);
                updateInvestNameLayout.setErrorEnabled(false);
                updateInvestAmountLayout.setError(null);
                updateInvestAmountLayout.setErrorEnabled(false);
                updateInvestFolioLayout.setError(null);
                updateInvestFolioLayout.setErrorEnabled(false);
                updateInvestTermLayout.setErrorEnabled(true);
                updateInvestTermLayout.setError("Term is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (source.isEmpty()) {
                updateInvestNameLayout.setError(null);
                updateInvestNameLayout.setErrorEnabled(false);
                updateInvestAmountLayout.setError(null);
                updateInvestAmountLayout.setErrorEnabled(false);
                updateInvestFolioLayout.setError(null);
                updateInvestFolioLayout.setErrorEnabled(false);
                updateInvestTermLayout.setError(null);
                updateInvestTermLayout.setErrorEnabled(false);
                updateInvestSourceLayout.setErrorEnabled(true);
                updateInvestSourceLayout.setError("Source is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (date.isEmpty()) {
                updateInvestNameLayout.setError(null);
                updateInvestNameLayout.setErrorEnabled(false);
                updateInvestAmountLayout.setError(null);
                updateInvestAmountLayout.setErrorEnabled(false);
                updateInvestFolioLayout.setError(null);
                updateInvestFolioLayout.setErrorEnabled(false);
                updateInvestTermLayout.setError(null);
                updateInvestTermLayout.setErrorEnabled(false);
                updateInvestSourceLayout.setError(null);
                updateInvestSourceLayout.setErrorEnabled(false);
                updateInvestTranDateLayout.setErrorEnabled(true);
                updateInvestTranDateLayout.setError("Date is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (remarks.isEmpty()) {
                updateInvestNameLayout.setError(null);
                updateInvestNameLayout.setErrorEnabled(false);
                updateInvestAmountLayout.setError(null);
                updateInvestAmountLayout.setErrorEnabled(false);
                updateInvestFolioLayout.setError(null);
                updateInvestFolioLayout.setErrorEnabled(false);
                updateInvestTermLayout.setError(null);
                updateInvestTermLayout.setErrorEnabled(false);
                updateInvestSourceLayout.setError(null);
                updateInvestSourceLayout.setErrorEnabled(false);
                updateInvestTranDateLayout.setError(null);
                updateInvestTranDateLayout.setErrorEnabled(false);
                updateInvestRemarksLayout.setErrorEnabled(true);
                updateInvestRemarksLayout.setError("Remarks is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (!remarks.matches(remarksPattern)) {
                updateInvestNameLayout.setError(null);
                updateInvestNameLayout.setErrorEnabled(false);
                updateInvestAmountLayout.setError(null);
                updateInvestAmountLayout.setErrorEnabled(false);
                updateInvestFolioLayout.setError(null);
                updateInvestFolioLayout.setErrorEnabled(false);
                updateInvestTermLayout.setError(null);
                updateInvestTermLayout.setErrorEnabled(false);
                updateInvestSourceLayout.setError(null);
                updateInvestSourceLayout.setErrorEnabled(false);
                updateInvestTranDateLayout.setError(null);
                updateInvestTranDateLayout.setErrorEnabled(false);
                updateInvestRemarksLayout.setErrorEnabled(true);
                updateInvestRemarksLayout.setError("Remarks should be length of 4 to 20 alphanumeric");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (source.equals("Others")) {
                if ((remarks.isEmpty()) || (remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nill"))) {
                    updateInvestNameLayout.setError(null);
                    updateInvestNameLayout.setErrorEnabled(false);
                    updateInvestAmountLayout.setError(null);
                    updateInvestAmountLayout.setErrorEnabled(false);
                    updateInvestFolioLayout.setError(null);
                    updateInvestFolioLayout.setErrorEnabled(false);
                    updateInvestTermLayout.setError(null);
                    updateInvestTermLayout.setErrorEnabled(false);
                    updateInvestSourceLayout.setError(null);
                    updateInvestSourceLayout.setErrorEnabled(false);
                    updateInvestTranDateLayout.setError(null);
                    updateInvestTranDateLayout.setErrorEnabled(false);
                    updateInvestRemarksLayout.setErrorEnabled(true);
                    updateInvestRemarksLayout.setError("Mention the Source Detail");
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    availableBalanceUpdateCall();
                }
            } else if (source.equals("Bank")) {
                if ((remarks.isEmpty()) || (remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nill"))) {
                    updateInvestNameLayout.setError(null);
                    updateInvestNameLayout.setErrorEnabled(false);
                    updateInvestAmountLayout.setError(null);
                    updateInvestAmountLayout.setErrorEnabled(false);
                    updateInvestFolioLayout.setError(null);
                    updateInvestFolioLayout.setErrorEnabled(false);
                    updateInvestTermLayout.setError(null);
                    updateInvestTermLayout.setErrorEnabled(false);
                    updateInvestSourceLayout.setError(null);
                    updateInvestSourceLayout.setErrorEnabled(false);
                    updateInvestTranDateLayout.setError(null);
                    updateInvestTranDateLayout.setErrorEnabled(false);
                    updateInvestRemarksLayout.setErrorEnabled(true);
                    updateInvestRemarksLayout.setError("Mention the Bank Name");
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    availableBalanceUpdateCall();
                }
            } else if (name.equalsIgnoreCase(name1) && amount.equalsIgnoreCase(amount1) && folioNumber.equalsIgnoreCase(folioNumber1)
                    && term.equalsIgnoreCase(term1) && date.equalsIgnoreCase(date1) && remarks.equalsIgnoreCase(remarks1)) {
                updateInvestNameLayout.setError(null);
                updateInvestNameLayout.setErrorEnabled(false);
                updateInvestAmountLayout.setError(null);
                updateInvestAmountLayout.setErrorEnabled(false);
                updateInvestFolioLayout.setError(null);
                updateInvestFolioLayout.setErrorEnabled(false);
                updateInvestTermLayout.setError(null);
                updateInvestTermLayout.setErrorEnabled(false);
                updateInvestSourceLayout.setError(null);
                updateInvestSourceLayout.setErrorEnabled(false);
                updateInvestTranDateLayout.setError(null);
                updateInvestTranDateLayout.setErrorEnabled(false);
                updateInvestRemarksLayout.setErrorEnabled(true);
                updateInvestRemarksLayout.setError(null);
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(UpdateInvestmentDetailsActivity.this, "Enter New Investment Details", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            } else {
                availableBalanceUpdateCall();
            }

        }
    }

    private void availableBalanceUpdateCall() {

        dialog.show();

        databaseReference.child("Available Balance Details India").child(source).child("amount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String temp = snapshot.getValue(String.class);
                final boolean validate = Integer.parseInt(amount) < Integer.parseInt(amount1);
                final int diffAmount = Integer.parseInt(amount1) - Integer.parseInt(amount);
                if (temp == null) {
                    if (validate) {
                        availableBalanceSubmit(amount);
                    } else {
                        dialog.dismiss();
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                        StyleableToast.makeText(UpdateInvestmentDetailsActivity.this, "Available balance is less than entered amount", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                    }
                } else {
                    final String updateAmount = String.valueOf(Integer.parseInt(temp) + diffAmount);
                    if (validate) {
                        availableBalanceSubmit(updateAmount);
                    } else {
                        availableBalanceSubmit(updateAmount);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(UpdateInvestmentDetailsActivity.this, "Available Balance Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

    }

    private void availableBalanceSubmit(String updateAmount) {

        BalanceDetailsData data = new BalanceDetailsData();
        data.setSource(source);
        data.setAmount(updateAmount);

        databaseReference.child("Available Balance Details India").child(source).setValue(data).addOnCompleteListener(task -> updateInvestDetails()).addOnFailureListener(e1 -> {
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(this, "Available Balance Submit Error: " + e1.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });
    }

    private void updateInvestDetails() {

        InvestmentData data = new InvestmentData();
        data.setName(name);
        data.setAmount(amount);
        data.setFolioNumber(folioNumber);
        data.setTerm(term);
        data.setSource(source);
        data.setDate(date);
        data.setRemarks(remarks);
        data.setUpdateFinder(updateFinder);

        databaseReference.child("Investment Details").child(updateFinder).setValue(data).
                addOnCompleteListener(task -> balanceDetailsUpdate()).addOnFailureListener(e -> {
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(UpdateInvestmentDetailsActivity.this, "Investment Amount Update Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });
    }

    private void balanceDetailsUpdate() {

        BalanceDetailsData data = new BalanceDetailsData();
        data.setSource(source);
        data.setAmount(amount);
        data.setTransAmount(amount);
        data.setCategory("Investment");
        data.setType("Expense");
        data.setDate(date);
        data.setRemarks(remarks);
        data.setFilter(date.substring(3, 10).replaceAll("-", "_"));
        data.setUpdateFinder(updateFinder);

        databaseReference.child("Balance Details India").child(source).child(updateFinder).setValue(data).
                addOnSuccessListener(unused -> {
                    startActivity(new Intent(UpdateInvestmentDetailsActivity.this, ViewInvestmentDetailsActivity.class));
                    dialog.dismiss();
                    StyleableToast.makeText(UpdateInvestmentDetailsActivity.this, "Investment Details Updated Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                }).addOnFailureListener(e -> {
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(UpdateInvestmentDetailsActivity.this, "Balance Details Update Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(UpdateInvestmentDetailsActivity.this, ViewInvestmentDetailsActivity.class));
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