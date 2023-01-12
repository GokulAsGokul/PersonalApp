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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class MatureInvestmentDetailsActivity extends AppCompatActivity {

    private TextInputLayout investMatAmountLayout, investMatRemAmtLayout, investMatSourceLayout, investMatTranDateLayout, investMatRemarksLayout;
    private TextInputEditText investMatAmount, investMatRemAmt, investMatRemarks;
    private AutoCompleteTextView investMatSource, investMatTranDate;
    private Vibrator vibrator;
    private String amount, remAmount, source, sourceDropDown;
    private Dialog dialog;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private final ArrayList<String> sourceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mature_investment_details);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.background)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        investMatAmountLayout = findViewById(R.id.investMatAmountLayout);
        investMatRemAmtLayout = findViewById(R.id.investMatRemAmtLayout);
        investMatSourceLayout = findViewById(R.id.investMatSourceLayout);
        investMatTranDateLayout = findViewById(R.id.investMatTranDateLayout);
        investMatRemarksLayout = findViewById(R.id.investMatRemarksLayout);
        investMatAmount = findViewById(R.id.investMatAmount);
        investMatRemAmt = findViewById(R.id.investMatRemAmt);
        investMatSource = findViewById(R.id.investMatSource);
        investMatTranDate = findViewById(R.id.investMatTranDate);
        investMatRemarks = findViewById(R.id.investMatRemarks);
        Button investMatSubmit = findViewById(R.id.investMatSubmit);

        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);

        dialog = new Dialog(MatureInvestmentDetailsActivity.this, android.R.style.Theme_DeviceDefault_NoActionBar);
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

        investMatAmount.setText(getIntent().getStringExtra("amount"));

        investMatRemAmt.setInputType(InputType.TYPE_NULL);
        investMatRemAmt.setClickable(false);
        investMatRemAmt.setFocusable(false);
        investMatRemAmtLayout.setClickable(false);

        investMatSource.setInputType(InputType.TYPE_NULL);
        investMatSource.setClickable(true);
        investMatSource.setFocusable(false);
        investMatSourceLayout.setClickable(true);

        databaseReference.child("DropDown Values").child("IndiaLentTo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String temp = childDataSnapshot.child("listValue").getValue(String.class);
                    dialog.show();
                    sourceList.add(temp);
                }
                ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(MatureInvestmentDetailsActivity.this, R.layout.dropdown_list, sourceList);
                sourceAdapter.sort(Comparator.naturalOrder());
                investMatSource.setAdapter(sourceAdapter);
                dialog.dismiss();
                investMatSource.setOnItemClickListener((parent, view, position, id) -> {
                    remAmountCall();
                    UIUtil.hideKeyboard(MatureInvestmentDetailsActivity.this);
                    sourceDropDown = parent.getItemAtPosition(position).toString();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                StyleableToast.makeText(MatureInvestmentDetailsActivity.this, "Source Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

        investMatTranDate.setInputType(InputType.TYPE_NULL);
        investMatTranDate.setClickable(false);
        investMatTranDate.setFocusable(false);
        investMatTranDateLayout.setClickable(false);
        investMatTranDate.setOnClickListener(v -> {
            remAmountCall();
            UIUtil.hideKeyboard(MatureInvestmentDetailsActivity.this);
            MatureInvestmentDetailsActivity.this.dayPicker();
        });

        investMatRemarks.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                remAmountCall();
                maturityDetailsCall();
                return true;
            }
            return false;
        });

        investMatSubmit.setOnClickListener(view1 -> {
            remAmountCall();
            maturityDetailsCall();

        });

    }

    private void remAmountCall() {

        if ((Objects.requireNonNull(investMatAmount.getText()).toString()).isEmpty()) {
            investMatAmountLayout.setErrorEnabled(true);
            investMatAmountLayout.setError("Amount is required");
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else if (Integer.parseInt(getIntent().getStringExtra("amount")) < (Integer.parseInt(Objects.requireNonNull(investMatAmount.getText()).toString()))) {
            investMatAmountLayout.setErrorEnabled(true);
            investMatRemAmtLayout.setError("Entered amount is high");
            investMatRemAmtLayout.setErrorEnabled(true);
            investMatAmountLayout.setError("Entered amount is high");
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            investMatAmountLayout.setError(null);
            investMatAmountLayout.setErrorEnabled(false);
            investMatRemAmtLayout.setError(null);
            investMatRemAmtLayout.setErrorEnabled(false);
            investMatRemAmt.setText(String.valueOf(Integer.parseInt(getIntent().getStringExtra("amount")) - Integer.parseInt(investMatAmount.getText().toString())));
        }
    }

    private void dayPicker() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                MatureInvestmentDetailsActivity.this, R.style.DateDialogTheme, (view, year1, month1, day1) -> {
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
            investMatTranDate.setText(format2.format(formattedDate));
        }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.background));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.background));
    }

    private void maturityDetailsCall() {

        if (!investMatRemarksLayout.isErrorEnabled()) {

            dialog.setCancelable(true);
            dialog.show();

            if (ConnectionCheck.getConnectionType(getApplicationContext()) == 0) {
                investMatAmountLayout.setError(null);
                investMatAmountLayout.setErrorEnabled(false);
                investMatRemAmtLayout.setError(null);
                investMatRemAmtLayout.setErrorEnabled(false);
                investMatSourceLayout.setError(null);
                investMatSourceLayout.setErrorEnabled(false);
                investMatTranDateLayout.setError(null);
                investMatTranDateLayout.setErrorEnabled(false);
                investMatRemarksLayout.setError(null);
                investMatRemarksLayout.setErrorEnabled(false);
                UIUtil.hideKeyboard(MatureInvestmentDetailsActivity.this);
                dialog.dismiss();
                ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
            } else {

                UIUtil.hideKeyboard(MatureInvestmentDetailsActivity.this);

                if ((Objects.requireNonNull(investMatRemarks.getText()).toString()).equalsIgnoreCase("Mention If Any")) {
                    investMatRemarks.setText(R.string.nill);
                }

                amount = Objects.requireNonNull(investMatAmount.getText()).toString().trim();
                remAmount = Objects.requireNonNull(investMatRemAmt.getText()).toString().trim();
                source = investMatSource.getText().toString().trim();
                String date = investMatTranDate.getText().toString().trim();
                String remarks = investMatRemarks.getText().toString().trim();

                String remarksPattern = "^[a-zA-Z0-9\\s]{4,20}$";

                if (amount.isEmpty()) {
                    investMatAmountLayout.setErrorEnabled(true);
                    investMatAmountLayout.setError("Amount is Required");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if (amount.equals("0")) {
                    investMatAmountLayout.setErrorEnabled(true);
                    investMatAmountLayout.setError("Amount is Required");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if (remAmount.isEmpty()) {
                    investMatAmountLayout.setError(null);
                    investMatAmountLayout.setErrorEnabled(false);
                    investMatRemAmtLayout.setErrorEnabled(true);
                    investMatRemAmtLayout.setError("Remaining Amount is Required");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if (source.isEmpty()) {
                    investMatAmountLayout.setError(null);
                    investMatAmountLayout.setErrorEnabled(false);
                    investMatRemAmtLayout.setError(null);
                    investMatRemAmtLayout.setErrorEnabled(false);
                    investMatSourceLayout.setErrorEnabled(true);
                    investMatSourceLayout.setError("Source is Required");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if (date.isEmpty()) {
                    investMatAmountLayout.setError(null);
                    investMatAmountLayout.setErrorEnabled(false);
                    investMatRemAmtLayout.setError(null);
                    investMatRemAmtLayout.setErrorEnabled(false);
                    investMatSourceLayout.setError(null);
                    investMatSourceLayout.setErrorEnabled(false);
                    investMatTranDateLayout.setErrorEnabled(true);
                    investMatTranDateLayout.setError("Date is Required");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if (remarks.isEmpty()) {
                    investMatAmountLayout.setError(null);
                    investMatAmountLayout.setErrorEnabled(false);
                    investMatRemAmtLayout.setError(null);
                    investMatRemAmtLayout.setErrorEnabled(false);
                    investMatSourceLayout.setError(null);
                    investMatSourceLayout.setErrorEnabled(false);
                    investMatTranDateLayout.setError(null);
                    investMatTranDateLayout.setErrorEnabled(false);
                    investMatRemarksLayout.setErrorEnabled(true);
                    investMatRemarksLayout.setError("Remarks is Required");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if (!remarks.matches(remarksPattern)) {
                    investMatAmountLayout.setError(null);
                    investMatAmountLayout.setErrorEnabled(false);
                    investMatRemAmtLayout.setError(null);
                    investMatRemAmtLayout.setErrorEnabled(false);
                    investMatSourceLayout.setError(null);
                    investMatSourceLayout.setErrorEnabled(false);
                    investMatTranDateLayout.setError(null);
                    investMatTranDateLayout.setErrorEnabled(false);
                    investMatRemarksLayout.setErrorEnabled(true);
                    investMatRemarksLayout.setError("Remarks should be length of 4 to 20 alphanumeric");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    if (source.equals("Others")) {
                        if (remarks.equalsIgnoreCase("Mention If Any") || remarks.equalsIgnoreCase("Nill")) {
                            investMatAmountLayout.setError(null);
                            investMatAmountLayout.setErrorEnabled(false);
                            investMatRemAmtLayout.setError(null);
                            investMatRemAmtLayout.setErrorEnabled(false);
                            investMatSourceLayout.setError(null);
                            investMatSourceLayout.setErrorEnabled(false);
                            investMatTranDateLayout.setError(null);
                            investMatTranDateLayout.setErrorEnabled(false);
                            investMatRemarksLayout.setErrorEnabled(true);
                            investMatRemarksLayout.setError("Mention Source Detail");
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
    }

    private void availableBalanceUpdateCall() {
        investMatAmountLayout.setError(null);
        investMatAmountLayout.setErrorEnabled(false);
        investMatRemAmtLayout.setError(null);
        investMatRemAmtLayout.setErrorEnabled(false);
        investMatSourceLayout.setError(null);
        investMatSourceLayout.setErrorEnabled(false);
        investMatTranDateLayout.setError(null);
        investMatTranDateLayout.setErrorEnabled(false);
        investMatRemarksLayout.setError(null);
        investMatRemarksLayout.setErrorEnabled(false);
        UIUtil.hideKeyboard(MatureInvestmentDetailsActivity.this);
        if (ConnectionCheck.getConnectionType(getApplicationContext()) == 0) {
            dialog.dismiss();
            ConnectionCheck.connectionDialogShow(getApplicationContext(), MatureInvestmentDetailsActivity.this);
        } else {
            databaseReference.child("Available Balance Details India").child(source).child("amount").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String temp = snapshot.getValue(String.class);
                    String updateFinder = getIntent().getStringExtra("updateFinder");
                    if (temp == null) {
                        availableBalanceSubmit(String.valueOf(Integer.parseInt(amount)), updateFinder);
                    } else {
                        availableBalanceSubmit(String.valueOf(Integer.parseInt(temp) + Integer.parseInt(amount)), updateFinder);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(MatureInvestmentDetailsActivity.this, "Available Balance Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                }
            });
        }

    }

    private void availableBalanceSubmit(String updateAmount, String updateFinder) {

        BalanceDetailsData data = new BalanceDetailsData();
        data.setSource(source);
        data.setAmount(updateAmount);
        databaseReference.child("Available Balance Details India").child(source).setValue(data).addOnCompleteListener(task -> updateInvestmentDetails(updateFinder)).addOnFailureListener(e1 -> {
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(MatureInvestmentDetailsActivity.this, "Available Balance Submit Error: " + e1.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });
    }

    private void updateInvestmentDetails(String updateFinder) {

        if (remAmount.equalsIgnoreCase("0")) {
            databaseReference.child("Investment Details").child(updateFinder).removeValue().addOnSuccessListener(unused -> addBalanceDetails()).addOnFailureListener(e -> {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(MatureInvestmentDetailsActivity.this, "Investment Details Delete Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            });
        } else {
            databaseReference.child("Investment Details").child(updateFinder).child("remAmount").setValue(remAmount).
                    addOnCompleteListener(task -> addBalanceDetails()).addOnFailureListener(e -> {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(MatureInvestmentDetailsActivity.this, "Investment Details Update Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            });
        }
    }

    private void addBalanceDetails() {

        String date = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.getDefault()).format(new Date());

        BalanceDetailsData data = new BalanceDetailsData();
        data.setSource(source);
        data.setAmount(amount);
        data.setTransAmount(amount);
        data.setCategory("Investment Mature");
        data.setType("Income");
        data.setDate(date);
        data.setFilter(date.substring(3, 10).replaceAll("-", "_"));
        data.setUpdateFinder(date);
        data.setRemarks("From " + getIntent().getStringExtra("folio"));

        databaseReference.child("Balance Details India").child(source).child(date).setValue(data).
                addOnSuccessListener(unused -> {
                    startActivity(new Intent(MatureInvestmentDetailsActivity.this, ViewInvestmentDetailsActivity.class));
                    finish();
                    dialog.dismiss();
                    StyleableToast.makeText(MatureInvestmentDetailsActivity.this, "Investment Mature Details Updated Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                }).addOnFailureListener(e -> {
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(MatureInvestmentDetailsActivity.this, "Add Balance Details Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(MatureInvestmentDetailsActivity.this, ViewInvestmentDetailsActivity.class));
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
        startActivity(new Intent(MatureInvestmentDetailsActivity.this, ViewInvestmentDetailsActivity.class));
        finish();
    }

}