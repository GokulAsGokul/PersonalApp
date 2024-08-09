package com.gokul.personal.AddLent;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputFilter;
import android.text.InputType;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gokul.personal.AddBalances.BalanceDetailsData;
import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.Miscellaneous.DecimalDigitsInputFilter;
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

import org.jetbrains.annotations.NotNull;

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

    private TextInputLayout lentRetAmountLayout, lentRetRemAmountLayout, lentRetSourceLayout, lentRetTranDateLayout, lentRetRemarksLayout;
    private TextInputEditText lentRetAmount, lentRetRemAmount, lentRetRemarks;
    private AutoCompleteTextView lentRetSource, lentRetTranDate;
    private Vibrator vibrator;
    private String name, amount, remAmount, source, date, remarks, country, updateFinder, sourceDropDown;
    private Dialog dialog;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private final ArrayList<String> sourceList = new ArrayList<>();
    private DecimalFormat decimalFormat;

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

        name = getIntent().getStringExtra("name");
        country = getIntent().getStringExtra("country");
        updateFinder = getIntent().getStringExtra("updateFinder");

        lentRetAmountLayout = findViewById(R.id.lentRetAmountLayout);
        lentRetRemAmountLayout = findViewById(R.id.lentRetRemAmountLayout);
        lentRetSourceLayout = findViewById(R.id.lentRetSourceLayout);
        lentRetTranDateLayout = findViewById(R.id.lentRetTranDateLayout);
        lentRetRemarksLayout = findViewById(R.id.lentRetRemarksLayout);
        lentRetAmount = findViewById(R.id.lentRetAmount);
        lentRetRemAmount = findViewById(R.id.lentRetRemAmount);
        if (country.equalsIgnoreCase("India")) {
            lentRetAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
            lentRetRemAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
        } else {
            lentRetAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3)});
            lentRetRemAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3)});
        }
        lentRetRemarks = findViewById(R.id.lentRetRemarks);
        lentRetSource = findViewById(R.id.lentRetSource);
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

        lentRetRemAmount.setInputType(InputType.TYPE_NULL);
        lentRetRemAmount.setClickable(false);
        lentRetRemAmount.setFocusable(false);
        lentRetRemAmountLayout.setClickable(false);

        lentRetSource.setInputType(InputType.TYPE_NULL);
        lentRetSource.setClickable(false);
        lentRetSource.setFocusable(false);
        lentRetSourceLayout.setClickable(false);

        lentRetSource.setInputType(InputType.TYPE_NULL);
        lentRetSource.setClickable(true);
        lentRetSource.setFocusable(false);
        lentRetSourceLayout.setClickable(true);

        if (country.equalsIgnoreCase("India")) {
            databaseReference.child("DropDown Values").child("IndiaSource").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot snapshot) {
                    for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                        String temp = childDataSnapshot.child("listValue").getValue(String.class);
                        dialog.show();
                        sourceList.add(temp);
                    }
                    ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(AddLentReturnDetailsActivity.this, R.layout.dropdown_list, sourceList);
                    sourceAdapter.sort(Comparator.naturalOrder());
                    lentRetSource.setAdapter(sourceAdapter);
                    dialog.dismiss();
                    lentRetSource.setOnItemClickListener((parent, view, position, id) -> {
                        remAmountCall();
                        UIUtil.hideKeyboard(AddLentReturnDetailsActivity.this);
                        sourceDropDown = parent.getItemAtPosition(position).toString();
                    });
                }

                @Override
                public void onCancelled(@NotNull DatabaseError error) {
                    dialog.dismiss();
                    StyleableToast.makeText(AddLentReturnDetailsActivity.this, "From Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                }
            });
        } else {
            databaseReference.child("DropDown Values").child("OmanSource").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot snapshot) {
                    for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                        String temp = childDataSnapshot.child("listValue").getValue(String.class);
                        dialog.show();
                        sourceList.add(temp);
                    }
                    ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(AddLentReturnDetailsActivity.this, R.layout.dropdown_list, sourceList);
                    sourceAdapter.sort(Comparator.naturalOrder());
                    lentRetSource.setAdapter(sourceAdapter);
                    dialog.dismiss();
                    lentRetSource.setOnItemClickListener((parent, view, position, id) -> {
                        remAmountCall();
                        UIUtil.hideKeyboard(AddLentReturnDetailsActivity.this);
                        sourceDropDown = parent.getItemAtPosition(position).toString();
                    });
                }

                @Override
                public void onCancelled(@NotNull DatabaseError error) {
                    dialog.dismiss();
                    StyleableToast.makeText(AddLentReturnDetailsActivity.this, "From Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
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

        if ((Objects.requireNonNull(lentRetAmount.getText()).toString()).isEmpty() ||
                (lentRetAmount.getText().toString()).equalsIgnoreCase("0") ||
                (lentRetAmount.getText().toString()).startsWith(".")) {
            lentRetAmountLayout.setErrorEnabled(true);
            lentRetAmountLayout.setError("Amount is required");
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else if (Double.parseDouble(getIntent().getStringExtra("amount")) < (Double.parseDouble(Objects.requireNonNull(lentRetAmount.getText()).toString()))) {
            lentRetAmountLayout.setErrorEnabled(true);
            lentRetAmountLayout.setError("Entered amount is high");
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            lentRetAmountLayout.setError(null);
            lentRetAmountLayout.setErrorEnabled(false);
            if (country.equalsIgnoreCase("Oman")) {
                decimalFormat = new DecimalFormat("0.000");
            } else {
                decimalFormat = new DecimalFormat("0.00");
            }
            lentRetRemAmount.setText(String.valueOf(decimalFormat.format(BigDecimal.valueOf(Double.parseDouble(getIntent().getStringExtra("amount"))).subtract(BigDecimal.valueOf(Double.parseDouble(lentRetAmount.getText().toString()))))));
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
                lentRetSourceLayout.setError(null);
                lentRetSourceLayout.setErrorEnabled(false);
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
                    decimalFormat = new DecimalFormat("0.000");
                } else {
                    decimalFormat = new DecimalFormat("0.00");
                }
                amount = decimalFormat.format(Double.parseDouble(Objects.requireNonNull(lentRetAmount.getText()).toString().trim()));
                remAmount = decimalFormat.format(Double.parseDouble(Objects.requireNonNull(lentRetRemAmount.getText()).toString().trim()));
                source = lentRetSource.getText().toString().trim();
                date = lentRetTranDate.getText().toString().trim();
                remarks = lentRetRemarks.getText().toString().trim();

                String remarksPattern = "^[a-zA-Z0-9\\s]{4,20}$";

                if (amount.startsWith(".") || amount.isEmpty() || amount.equals("0") || amount.equals("0.0") || amount.equals("0.00") || amount.equals("0.000")) {
                    lentRetAmountLayout.setErrorEnabled(true);
                    lentRetAmountLayout.setError("Amount is Required");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if (source.isEmpty()) {
                    lentRetAmountLayout.setError(null);
                    lentRetAmountLayout.setErrorEnabled(false);
                    lentRetRemAmountLayout.setError(null);
                    lentRetRemAmountLayout.setErrorEnabled(false);
                    lentRetSourceLayout.setErrorEnabled(true);
                    lentRetSourceLayout.setError("Source is Required");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if (date.isEmpty()) {
                    lentRetAmountLayout.setError(null);
                    lentRetAmountLayout.setErrorEnabled(false);
                    lentRetRemAmountLayout.setError(null);
                    lentRetRemAmountLayout.setErrorEnabled(false);
                    lentRetSourceLayout.setError(null);
                    lentRetSourceLayout.setErrorEnabled(false);
                    lentRetTranDateLayout.setErrorEnabled(true);
                    lentRetTranDateLayout.setError("Date is Required");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if (remarks.isEmpty()) {
                    lentRetAmountLayout.setError(null);
                    lentRetAmountLayout.setErrorEnabled(false);
                    lentRetRemAmountLayout.setError(null);
                    lentRetRemAmountLayout.setErrorEnabled(false);
                    lentRetSourceLayout.setError(null);
                    lentRetSourceLayout.setErrorEnabled(false);
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
                    lentRetSourceLayout.setError(null);
                    lentRetSourceLayout.setErrorEnabled(false);
                    lentRetTranDateLayout.setError(null);
                    lentRetTranDateLayout.setErrorEnabled(false);
                    lentRetRemarksLayout.setErrorEnabled(true);
                    lentRetRemarksLayout.setError("Remarks should be length of 4 to 20 alphanumeric");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if (source.equals("Others")) {
                    if ((remarks.isEmpty()) || (remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nil"))) {
                        lentRetAmountLayout.setError(null);
                        lentRetAmountLayout.setErrorEnabled(false);
                        lentRetRemAmountLayout.setError(null);
                        lentRetRemAmountLayout.setErrorEnabled(false);
                        lentRetSourceLayout.setError(null);
                        lentRetSourceLayout.setErrorEnabled(false);
                        lentRetTranDateLayout.setError(null);
                        lentRetTranDateLayout.setErrorEnabled(false);
                        lentRetRemarksLayout.setErrorEnabled(true);
                        lentRetRemarksLayout.setError("Mention From Detail");
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
        lentRetSourceLayout.setError(null);
        lentRetSourceLayout.setErrorEnabled(false);
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
            databaseReference.child("Available Balance Details " + country).child(source).child("amount").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot snapshot) {
                    String temp = snapshot.getValue(String.class);

                    if (country.equalsIgnoreCase("India")) {

                        if (temp == null) {
                            availableBalanceSubmit(String.valueOf(Double.parseDouble(amount)), updateFinder);
                        } else {
                            availableBalanceSubmit(String.valueOf(BigDecimal.valueOf(Double.parseDouble(temp)).add(BigDecimal.valueOf(Double.parseDouble(amount)))), updateFinder);
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
                public void onCancelled(@NotNull DatabaseError error) {
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(AddLentReturnDetailsActivity.this, "Available Balance Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                }
            });
        }

    }

    private void availableBalanceSubmit(String updateAmount, String updateFinder) {

        BalanceDetailsData data = new BalanceDetailsData();
        data.setSource(source);
        String avlAmount;
        if (country.equalsIgnoreCase("India")) {
            avlAmount = new DecimalFormat("0.00").format(Double.parseDouble(updateAmount));
        } else {
            avlAmount = new DecimalFormat("0.000").format(Double.parseDouble(updateAmount));
        }
        data.setAmount(avlAmount);

        databaseReference.child("Available Balance Details " + country).child(source).setValue(data).addOnCompleteListener(task -> updateLentDetails(avlAmount, updateFinder)).addOnFailureListener(e1 -> {
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(AddLentReturnDetailsActivity.this, "Available Balance Submit Error: " + e1.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });
    }

    private void updateLentDetails(String avlAmount, String updateFinder) {

        if (country.equalsIgnoreCase("India")) {

            String finalAmount = new DecimalFormat("0.00").format(Double.parseDouble(remAmount));

            if (finalAmount.equals("0.00")) {
                databaseReference.child("Lent Details India").child(updateFinder).removeValue().addOnCompleteListener(task -> addBalanceDetails(avlAmount)).addOnFailureListener(e -> {
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(AddLentReturnDetailsActivity.this, "Lent Details Delete Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                });
            } else {
                databaseReference.child("Lent Details India").child(updateFinder).child("remAmount").setValue(remAmount).
                        addOnCompleteListener(task -> addBalanceDetails(avlAmount)).addOnFailureListener(e -> {
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(AddLentReturnDetailsActivity.this, "Lent Details Update Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                });
            }
        } else {
            String finalAmount = new DecimalFormat("0.000").format(Double.parseDouble(remAmount));

            if (finalAmount.equals("0.000")) {
                databaseReference.child("Lent Details Oman").child(updateFinder).removeValue().addOnCompleteListener(task -> addBalanceDetails(avlAmount)).addOnFailureListener(e -> {
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(AddLentReturnDetailsActivity.this, "Lent Details Delete Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                });
            } else {
                databaseReference.child("Lent Details Oman").child(updateFinder).child("remAmount").setValue(remAmount).
                        addOnCompleteListener(task -> addBalanceDetails(avlAmount)).addOnFailureListener(e -> {
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(AddLentReturnDetailsActivity.this, "Lent Details Update Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                });
            }

        }
    }

    private void addBalanceDetails(String avlAmount) {

        String date1 = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.getDefault()).format(new Date());

        BalanceDetailsData data = new BalanceDetailsData();
        data.setSource(source);
        String newAmount;
        if (country.equalsIgnoreCase("India")) {
            newAmount = new DecimalFormat("0.00").format(Double.parseDouble(amount));
        } else {
            newAmount = new DecimalFormat("0.000").format(Double.parseDouble(amount));
        }
        data.setAmount(newAmount);
        data.setTransAmount(newAmount);
        data.setAvailableAmount(avlAmount);
        data.setCategory("Lent Return");
        data.setType("Income");
        data.setDate(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
        if ((remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nil"))) {
            data.setRemarks(name + "'s Lent Return Amount");
        } else {
            data.setRemarks(remarks);
        }
        data.setFilter(date.substring(3, 10).replaceAll("-", "_"));
        data.setUpdateFinder(date1);

        databaseReference.child("Balance Details " + country).child(source).child(date1).setValue(data).
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

    public boolean onOptionsItemSelected(MenuItem item) {
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