package com.gokul.personal.AddInvestmentDetails;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.VibrationEffect;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class AddInvestmentDetailsActivity extends AppCompatActivity {

    private TextInputLayout investDecNameLayout, investDecAmountLayout, investDecFreqLayout, investDecTermLayout, investDecSourceLayout, investDecTranDateLayout, investDecRemarksLayout;
    private TextInputEditText investDecName, investDecAmount, investDecTerm, investDecRemarks;
    private AutoCompleteTextView investDecFreq, investDecSource, investDecTranDate;
    private String freqDropDown, sourceDropDown;
    private Vibrator vibrator;
    private String name, amount, frequency, term, source, date, remarks;
    private Dialog dialog1, dialog2;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private final ArrayList<String> sourceList = new ArrayList<>();
    private final ArrayList<String> freqList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_investment_details);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.background)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Add Investments");
        actionBar.setDisplayHomeAsUpEnabled(true);

        investDecNameLayout = findViewById(R.id.investDecNameLayout);
        investDecAmountLayout = findViewById(R.id.investDecAmountLayout);
        investDecFreqLayout = findViewById(R.id.investDecFreqLayout);
        investDecTermLayout = findViewById(R.id.investDecTermLayout);
        investDecSourceLayout = findViewById(R.id.investDecSourceLayout);
        investDecTranDateLayout = findViewById(R.id.investDecTranDateLayout);
        investDecRemarksLayout = findViewById(R.id.investDecRemarksLayout);
        investDecName = findViewById(R.id.investDecName);
        investDecAmount = findViewById(R.id.investDecAmount);
        investDecTerm = findViewById(R.id.investDecTerm);
        investDecRemarks = findViewById(R.id.investDecRemarks);
        investDecFreq = findViewById(R.id.investDecFreq);
        investDecSource = findViewById(R.id.investDecSource);
        investDecTranDate = findViewById(R.id.investDecTranDate);
        Button investDecSubmit = findViewById(R.id.investDecSubmit);

        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);

        dialog1 = new Dialog(AddInvestmentDetailsActivity.this);
        dialog1.setContentView(R.layout.submit_dialog);
        dialog1.setCancelable(true);
        dialog1.setCanceledOnTouchOutside(false);
        if (dialog1.getWindow() != null) {
            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog2 = new Dialog(AddInvestmentDetailsActivity.this);
        dialog2.setContentView(R.layout.loading_dialog);
        dialog2.setCancelable(true);
        dialog2.setCanceledOnTouchOutside(false);
        if (dialog2.getWindow() != null) {
            dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        if (ConnectionCheck.getConnectionType(getApplicationContext()) != 0) {
            dialog2.show();
        }

        vibrator = (Vibrator) getSystemService((VIBRATOR_SERVICE));

        investDecFreq.setInputType(InputType.TYPE_NULL);
        investDecFreq.setClickable(true);
        investDecFreq.setClickable(true);
        investDecFreq.setFocusable(false);
        databaseReference.child("DropDown Values").child("InvestmentFrequency").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String temp = childDataSnapshot.child("listValue").getValue(String.class);
                    dialog2.show();
                    freqList.add(temp);
                }
                ArrayAdapter<String> freqAdapter = new ArrayAdapter<>(AddInvestmentDetailsActivity.this, R.layout.dropdown_list, freqList);
                freqAdapter.sort(Comparator.naturalOrder());
                investDecFreq.setAdapter(freqAdapter);
                dialog2.dismiss();
                investDecFreq.setOnItemClickListener((parent, view, position, id) -> {
                    UIUtil.hideKeyboard(AddInvestmentDetailsActivity.this);
                    freqDropDown = parent.getItemAtPosition(position).toString();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(AddInvestmentDetailsActivity.this, "Frequency Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

        investDecSource.setInputType(InputType.TYPE_NULL);
        investDecSource.setClickable(true);
        investDecSourceLayout.setClickable(true);
        investDecSource.setFocusable(false);
        databaseReference.child("DropDown Values").child("Sources").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String temp = childDataSnapshot.child("listValue").getValue(String.class);
                    dialog2.show();
                    sourceList.add(temp);
                }
                ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(AddInvestmentDetailsActivity.this, R.layout.dropdown_list, sourceList);
                sourceAdapter.sort(Comparator.naturalOrder());
                investDecSource.setAdapter(sourceAdapter);
                dialog2.dismiss();
                investDecSource.setOnItemClickListener((parent, view, position, id) -> {
                    UIUtil.hideKeyboard(AddInvestmentDetailsActivity.this);
                    sourceDropDown = parent.getItemAtPosition(position).toString();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(AddInvestmentDetailsActivity.this, "Source Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

        investDecTranDate.setInputType(InputType.TYPE_NULL);
        investDecTranDate.setClickable(true);
        investDecTranDate.setFocusable(false);
        investDecTranDate.setOnClickListener(v -> {
            UIUtil.hideKeyboard(AddInvestmentDetailsActivity.this);
            AddInvestmentDetailsActivity.this.dayPicker();
        });

        investDecRemarks.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                investSubmitCall();
                return true;
            }
            return false;
        });

        investDecSubmit.setOnClickListener(view -> investSubmitCall());

    }

    private void dayPicker() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddInvestmentDetailsActivity.this, R.style.DateDialogTheme, (view, year1, month1, day1) -> {
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
            investDecTranDate.setText(format2.format(formattedDate));
        }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.background));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.background));
    }

    private void investSubmitCall() {

        if (ConnectionCheck.getConnectionType(getApplicationContext()) == 0) {
            investDecNameLayout.setError(null);
            investDecNameLayout.setErrorEnabled(false);
            investDecAmountLayout.setError(null);
            investDecAmountLayout.setErrorEnabled(false);
            investDecFreqLayout.setError(null);
            investDecFreqLayout.setErrorEnabled(false);
            investDecTermLayout.setError(null);
            investDecTermLayout.setErrorEnabled(false);
            investDecSourceLayout.setError(null);
            investDecSourceLayout.setErrorEnabled(false);
            investDecTranDateLayout.setError(null);
            investDecTranDateLayout.setErrorEnabled(false);
            investDecRemarksLayout.setError(null);
            investDecRemarksLayout.setErrorEnabled(false);
            UIUtil.hideKeyboard(AddInvestmentDetailsActivity.this);
            ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
        } else {

            if ((Objects.requireNonNull(investDecRemarks.getText()).toString()).equalsIgnoreCase("Mention If Any")) {
                investDecRemarks.setText(R.string.nill);
            }

            name = Objects.requireNonNull(investDecName.getText()).toString();
            amount = Objects.requireNonNull(investDecAmount.getText()).toString();
            frequency = investDecFreq.getText().toString().trim();
            term = Objects.requireNonNull(investDecTerm.getText()).toString().trim();
            source = investDecSource.getText().toString().trim();
            date = investDecTranDate.getText().toString().trim();
            remarks = investDecRemarks.getText().toString().trim();

            String remarksPattern = "^[a-zA-Z0-9\\s]{4,20}$";

            if (name.isEmpty()) {
                investDecNameLayout.setErrorEnabled(true);
                investDecNameLayout.setError("Company Name is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (amount.isEmpty()) {
                investDecNameLayout.setError(null);
                investDecNameLayout.setErrorEnabled(false);
                investDecAmountLayout.setErrorEnabled(true);
                investDecAmountLayout.setError("Amount is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (frequency.isEmpty()) {
                investDecNameLayout.setError(null);
                investDecNameLayout.setErrorEnabled(false);
                investDecAmountLayout.setError(null);
                investDecAmountLayout.setErrorEnabled(false);
                investDecFreqLayout.setErrorEnabled(true);
                investDecFreqLayout.setError("Frequency is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (term.isEmpty()) {
                investDecNameLayout.setError(null);
                investDecNameLayout.setErrorEnabled(false);
                investDecAmountLayout.setError(null);
                investDecAmountLayout.setErrorEnabled(false);
                investDecFreqLayout.setError(null);
                investDecFreqLayout.setErrorEnabled(false);
                investDecTermLayout.setErrorEnabled(true);
                investDecTermLayout.setError("Term is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (source.isEmpty()) {
                investDecNameLayout.setError(null);
                investDecNameLayout.setErrorEnabled(false);
                investDecAmountLayout.setError(null);
                investDecAmountLayout.setErrorEnabled(false);
                investDecFreqLayout.setError(null);
                investDecFreqLayout.setErrorEnabled(false);
                investDecTermLayout.setError(null);
                investDecTermLayout.setErrorEnabled(false);
                investDecSourceLayout.setErrorEnabled(true);
                investDecSourceLayout.setError("Source is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (date.isEmpty()) {
                investDecNameLayout.setError(null);
                investDecNameLayout.setErrorEnabled(false);
                investDecAmountLayout.setError(null);
                investDecAmountLayout.setErrorEnabled(false);
                investDecFreqLayout.setError(null);
                investDecFreqLayout.setErrorEnabled(false);
                investDecTermLayout.setError(null);
                investDecTermLayout.setErrorEnabled(false);
                investDecSourceLayout.setError(null);
                investDecSourceLayout.setErrorEnabled(false);
                investDecTranDateLayout.setErrorEnabled(true);
                investDecTranDateLayout.setError("Date is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (remarks.isEmpty()) {
                investDecNameLayout.setError(null);
                investDecNameLayout.setErrorEnabled(false);
                investDecAmountLayout.setError(null);
                investDecAmountLayout.setErrorEnabled(false);
                investDecFreqLayout.setError(null);
                investDecFreqLayout.setErrorEnabled(false);
                investDecTermLayout.setError(null);
                investDecTermLayout.setErrorEnabled(false);
                investDecSourceLayout.setError(null);
                investDecSourceLayout.setErrorEnabled(false);
                investDecTranDateLayout.setError(null);
                investDecTranDateLayout.setErrorEnabled(false);
                investDecRemarksLayout.setErrorEnabled(true);
                investDecRemarksLayout.setError("Remarks is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (!remarks.matches(remarksPattern)) {
                investDecNameLayout.setError(null);
                investDecNameLayout.setErrorEnabled(false);
                investDecAmountLayout.setError(null);
                investDecAmountLayout.setErrorEnabled(false);
                investDecFreqLayout.setError(null);
                investDecFreqLayout.setErrorEnabled(false);
                investDecTermLayout.setError(null);
                investDecTermLayout.setErrorEnabled(false);
                investDecSourceLayout.setError(null);
                investDecSourceLayout.setErrorEnabled(false);
                investDecTranDateLayout.setError(null);
                investDecTranDateLayout.setErrorEnabled(false);
                investDecRemarksLayout.setErrorEnabled(true);
                investDecRemarksLayout.setError("Remarks should be length of 4 to 20 alphanumeric");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (source.equals("Others")) {
                if ((remarks.isEmpty()) || (remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nill"))) {
                    investDecNameLayout.setError(null);
                    investDecNameLayout.setErrorEnabled(false);
                    investDecAmountLayout.setError(null);
                    investDecAmountLayout.setErrorEnabled(false);
                    investDecFreqLayout.setError(null);
                    investDecFreqLayout.setErrorEnabled(false);
                    investDecTermLayout.setError(null);
                    investDecTermLayout.setErrorEnabled(false);
                    investDecSourceLayout.setError(null);
                    investDecSourceLayout.setErrorEnabled(false);
                    investDecTranDateLayout.setError(null);
                    investDecTranDateLayout.setErrorEnabled(false);
                    investDecRemarksLayout.setErrorEnabled(true);
                    investDecRemarksLayout.setError("Mention the Source Detail");
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    investSubmit();
                }
            } else if (source.equals("Bank")) {
                if ((remarks.isEmpty()) || (remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nill"))) {
                    investDecNameLayout.setError(null);
                    investDecNameLayout.setErrorEnabled(false);
                    investDecAmountLayout.setError(null);
                    investDecAmountLayout.setErrorEnabled(false);
                    investDecFreqLayout.setError(null);
                    investDecFreqLayout.setErrorEnabled(false);
                    investDecTermLayout.setError(null);
                    investDecTermLayout.setErrorEnabled(false);
                    investDecSourceLayout.setError(null);
                    investDecSourceLayout.setErrorEnabled(false);
                    investDecTranDateLayout.setError(null);
                    investDecTranDateLayout.setErrorEnabled(false);
                    investDecRemarksLayout.setErrorEnabled(true);
                    investDecRemarksLayout.setError("Mention the Bank Name");
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    investSubmit();
                }
            } else {
                investSubmit();
            }

        }
    }

    @SuppressLint("SimpleDateFormat")
    private void investSubmit() {

        investDecNameLayout.setError(null);
        investDecNameLayout.setErrorEnabled(false);
        investDecAmountLayout.setError(null);
        investDecAmountLayout.setErrorEnabled(false);
        investDecFreqLayout.setError(null);
        investDecFreqLayout.setErrorEnabled(false);
        investDecTermLayout.setError(null);
        investDecTermLayout.setErrorEnabled(false);
        investDecSourceLayout.setError(null);
        investDecSourceLayout.setErrorEnabled(false);
        investDecTranDateLayout.setError(null);
        investDecTranDateLayout.setErrorEnabled(false);
        investDecRemarksLayout.setError(null);
        investDecRemarksLayout.setErrorEnabled(false);
        UIUtil.hideKeyboard(AddInvestmentDetailsActivity.this);

        if (ConnectionCheck.getConnectionType(getApplicationContext()) == 0) {
            ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
        } else {
            dialog1.show();

            String date1 = new SimpleDateFormat("_dd_MM_yyyy_HH_mm", Locale.getDefault()).format(new Date());

            InvestmentData data = new InvestmentData();
            data.setName(name);
            data.setAmount(amount);
            data.setFrequency(frequency);
            data.setTerm(term);
            data.setSource(source);
            data.setDate(date);
            data.setRemarks(remarks);
            data.setUpdateFinder(name + date1);

            databaseReference.child("Investment Details").child(name + date1).setValue(data).addOnCompleteListener(task -> availableBalanceSubmit()).addOnFailureListener(e -> {
                dialog1.dismiss();
                StyleableToast.makeText(AddInvestmentDetailsActivity.this, "Investment Submit Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            });
        }
    }

    private void availableBalanceSubmit() {

        databaseReference.child("Available Balance Details India").child(source).child("amount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String temp = snapshot.getValue(String.class);
                String sourceAmount;
                if (temp == null) {
                    sourceAmount = String.valueOf(Integer.parseInt(amount));
                } else {
                    sourceAmount = String.valueOf(Integer.parseInt(temp) - Integer.parseInt(amount));
                }

                databaseReference.child("Available Balance Details India").child(source).child("amount").setValue(sourceAmount).addOnCompleteListener(task -> {
                    Objects.requireNonNull(investDecName.getText()).clear();
                    Objects.requireNonNull(investDecAmount.getText()).clear();
                    investDecFreq.getText().clear();
                    Objects.requireNonNull(investDecTerm.getText()).clear();
                    investDecSource.getText().clear();
                    investDecTranDate.getText().clear();
                    dialog1.dismiss();
                    StyleableToast.makeText(AddInvestmentDetailsActivity.this, "Investment Details Submitted Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                }).addOnFailureListener(e1 -> {
                    dialog1.dismiss();
                    StyleableToast.makeText(AddInvestmentDetailsActivity.this, "Available Balance Submit Error: " + e1.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(AddInvestmentDetailsActivity.this, "Available Balance Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
    }

}