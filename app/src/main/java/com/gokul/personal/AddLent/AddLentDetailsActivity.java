package com.gokul.personal.AddLent;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gokul.personal.General.ConnectionCheck;
import com.gokul.personal.General.DashboardActivity;
import com.gokul.personal.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class AddLentDetailsActivity extends AppCompatActivity {

    private TextInputLayout lendDecNameLayout, lendDecAmountLayout, lendDecFromLayout, lendDecToLayout, lendDecTranDateLayout, lendDecRemarksLayout;
    private TextInputEditText lendDecName, lendDecAmount, lendDecRemarks;
    private AutoCompleteTextView lendDecFrom, lendDecTo, lendDecTranDate;
    final String[] fromList = {"Cash", "DBS Bank", "HDFC Bank", "GPay", "LVB Bank", "PhonePe", "SBI Bank", "Others"};
    final String[] toList = {"Bank", "Cash", "GPay", "PhonePe", "Others"};
    private String fromDropDown, toDropDown = "";
    private Vibrator vibrator;
    private String name, amount, from, to, date, remarks;
    private Dialog dialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lending_declaration);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.background)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        lendDecNameLayout = findViewById(R.id.lendDecNameLayout);
        lendDecAmountLayout = findViewById(R.id.lendDecAmountLayout);
        lendDecFromLayout = findViewById(R.id.lendDecFromLayout);
        lendDecToLayout = findViewById(R.id.lendDecToLayout);
        lendDecTranDateLayout = findViewById(R.id.lendDecTranDateLayout);
        lendDecRemarksLayout = findViewById(R.id.lendDecRemarksLayout);
        lendDecName = findViewById(R.id.lendDecName);
        lendDecAmount = findViewById(R.id.lendDecAmount);
        lendDecRemarks = findViewById(R.id.lendDecRemarks);
        lendDecFrom = findViewById(R.id.lendDecFrom);
        lendDecTo = findViewById(R.id.lendDecTo);
        lendDecTranDate = findViewById(R.id.lendDecTranDate);
        Button lendDecSubmit = findViewById(R.id.lendDecSubmit);

        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);

        dialog1 = new Dialog(AddLentDetailsActivity.this);
        dialog1.setContentView(R.layout.submit_dialog);
        dialog1.setCancelable(true);
        dialog1.setCanceledOnTouchOutside(false);
        if (dialog1.getWindow() != null) {
            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        vibrator = (Vibrator) getSystemService((VIBRATOR_SERVICE));

        lendDecFrom.setInputType(InputType.TYPE_NULL);
        lendDecFrom.setClickable(true);
        lendDecFrom.setFocusable(false);
        lendDecFromLayout.setClickable(true);
        ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(this, R.layout.dropdown_list, fromList);
        lendDecFrom.setAdapter(fromAdapter);
        lendDecFrom.setOnItemClickListener((parent, view, position, id) -> {
            UIUtil.hideKeyboard(AddLentDetailsActivity.this);
            fromDropDown = parent.getItemAtPosition(position).toString();
        });

        lendDecTo.setInputType(InputType.TYPE_NULL);
        lendDecTo.setClickable(true);
        lendDecTo.setFocusable(false);
        lendDecToLayout.setClickable(true);
        ArrayAdapter<String> toAdapter = new ArrayAdapter<>(this, R.layout.dropdown_list, toList);
        lendDecTo.setAdapter(toAdapter);
        lendDecTo.setOnItemClickListener((parent, view, position, id) -> {
            UIUtil.hideKeyboard(AddLentDetailsActivity.this);
            toDropDown = parent.getItemAtPosition(position).toString();
        });

        lendDecTranDate.setInputType(InputType.TYPE_NULL);
        lendDecTranDate.setClickable(true);
        lendDecTranDate.setFocusable(false);
        lendDecTranDate.setOnClickListener(v -> {
            UIUtil.hideKeyboard(AddLentDetailsActivity.this);
            AddLentDetailsActivity.this.dayPicker();
        });

        lendDecRemarks.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                lentSubmitCall();
                return true;
            }
            return false;
        });

        lendDecSubmit.setOnClickListener(view -> lentSubmitCall());

    }

    private void dayPicker() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddLentDetailsActivity.this, R.style.DateDialogTheme, (view, year1, month1, day1) -> {
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
            lendDecTranDate.setText(format2.format(formattedDate));
        }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.background));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.background));
    }

    private void lentSubmitCall() {

        if (ConnectionCheck.getConnectionType(getApplicationContext()) == 0) {
            lendDecNameLayout.setError(null);
            lendDecNameLayout.setErrorEnabled(false);
            lendDecAmountLayout.setError(null);
            lendDecAmountLayout.setErrorEnabled(false);
            lendDecFromLayout.setError(null);
            lendDecFromLayout.setErrorEnabled(false);
            lendDecToLayout.setError(null);
            lendDecToLayout.setErrorEnabled(false);
            lendDecTranDateLayout.setError(null);
            lendDecTranDateLayout.setErrorEnabled(false);
            lendDecRemarksLayout.setError(null);
            lendDecRemarksLayout.setErrorEnabled(false);
            UIUtil.hideKeyboard(AddLentDetailsActivity.this);
            ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
        } else {

            if ((Objects.requireNonNull(lendDecRemarks.getText()).toString()).equalsIgnoreCase("Mention If Any")) {
                lendDecRemarks.setText(R.string.nill);
            }

            name = Objects.requireNonNull(lendDecName.getText()).toString();
            amount = Objects.requireNonNull(lendDecAmount.getText()).toString();
            from = lendDecFrom.getText().toString().trim();
            to = lendDecTo.getText().toString().trim();
            date = lendDecTranDate.getText().toString().trim();
            remarks = lendDecRemarks.getText().toString().trim();

            String remarksPattern = "^[a-zA-Z0-9\\s]{4,20}$";

            if (name.isEmpty()) {
                lendDecNameLayout.setErrorEnabled(true);
                lendDecNameLayout.setError("Borrower Name is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (amount.isEmpty()) {
                lendDecNameLayout.setError(null);
                lendDecNameLayout.setErrorEnabled(false);
                lendDecAmountLayout.setErrorEnabled(true);
                lendDecAmountLayout.setError("Amount is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (from.isEmpty()) {
                lendDecNameLayout.setError(null);
                lendDecNameLayout.setErrorEnabled(false);
                lendDecAmountLayout.setError(null);
                lendDecAmountLayout.setErrorEnabled(false);
                lendDecFromLayout.setErrorEnabled(true);
                lendDecFromLayout.setError("From is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (to.isEmpty()) {
                lendDecNameLayout.setError(null);
                lendDecNameLayout.setErrorEnabled(false);
                lendDecAmountLayout.setError(null);
                lendDecAmountLayout.setErrorEnabled(false);
                lendDecFromLayout.setError(null);
                lendDecFromLayout.setErrorEnabled(false);
                lendDecToLayout.setErrorEnabled(true);
                lendDecToLayout.setError("To is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (date.isEmpty()) {
                lendDecNameLayout.setError(null);
                lendDecNameLayout.setErrorEnabled(false);
                lendDecAmountLayout.setError(null);
                lendDecAmountLayout.setErrorEnabled(false);
                lendDecFromLayout.setError(null);
                lendDecFromLayout.setErrorEnabled(false);
                lendDecToLayout.setError(null);
                lendDecToLayout.setErrorEnabled(false);
                lendDecTranDateLayout.setErrorEnabled(true);
                lendDecTranDateLayout.setError("Date is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (remarks.isEmpty()) {
                lendDecNameLayout.setError(null);
                lendDecNameLayout.setErrorEnabled(false);
                lendDecAmountLayout.setError(null);
                lendDecAmountLayout.setErrorEnabled(false);
                lendDecFromLayout.setError(null);
                lendDecFromLayout.setErrorEnabled(false);
                lendDecToLayout.setError(null);
                lendDecToLayout.setErrorEnabled(false);
                lendDecTranDateLayout.setError(null);
                lendDecTranDateLayout.setErrorEnabled(false);
                lendDecRemarksLayout.setErrorEnabled(true);
                lendDecRemarksLayout.setError("Remarks is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (!remarks.matches(remarksPattern)) {
                lendDecNameLayout.setError(null);
                lendDecNameLayout.setErrorEnabled(false);
                lendDecAmountLayout.setError(null);
                lendDecAmountLayout.setErrorEnabled(false);
                lendDecFromLayout.setError(null);
                lendDecFromLayout.setErrorEnabled(false);
                lendDecToLayout.setError(null);
                lendDecToLayout.setErrorEnabled(false);
                lendDecTranDateLayout.setError(null);
                lendDecTranDateLayout.setErrorEnabled(false);
                lendDecRemarksLayout.setErrorEnabled(true);
                lendDecRemarksLayout.setError("Remarks should be length of 4 to 20 alphanumeric");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (from.equals("Others")) {
                if ((remarks.isEmpty()) || (remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nill"))) {
                    lendDecNameLayout.setError(null);
                    lendDecNameLayout.setErrorEnabled(false);
                    lendDecAmountLayout.setError(null);
                    lendDecAmountLayout.setErrorEnabled(false);
                    lendDecFromLayout.setError(null);
                    lendDecFromLayout.setErrorEnabled(false);
                    lendDecToLayout.setError(null);
                    lendDecToLayout.setErrorEnabled(false);
                    lendDecTranDateLayout.setError(null);
                    lendDecTranDateLayout.setErrorEnabled(false);
                    lendDecRemarksLayout.setErrorEnabled(true);
                    lendDecRemarksLayout.setError("Mention the From Detail");
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    lentSubmit();
                }
            } else if (to.equals("Bank")) {
                if ((remarks.isEmpty()) || (remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nill"))) {
                    lendDecNameLayout.setError(null);
                    lendDecNameLayout.setErrorEnabled(false);
                    lendDecAmountLayout.setError(null);
                    lendDecAmountLayout.setErrorEnabled(false);
                    lendDecFromLayout.setError(null);
                    lendDecFromLayout.setErrorEnabled(false);
                    lendDecToLayout.setError(null);
                    lendDecToLayout.setErrorEnabled(false);
                    lendDecTranDateLayout.setError(null);
                    lendDecTranDateLayout.setErrorEnabled(false);
                    lendDecRemarksLayout.setErrorEnabled(true);
                    lendDecRemarksLayout.setError("Mention the Bank Name");
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    lentSubmit();
                }
            } else if (to.equals("Others")) {
                if ((remarks.isEmpty()) || (remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nill"))) {
                    lendDecNameLayout.setError(null);
                    lendDecNameLayout.setErrorEnabled(false);
                    lendDecAmountLayout.setError(null);
                    lendDecAmountLayout.setErrorEnabled(false);
                    lendDecFromLayout.setError(null);
                    lendDecFromLayout.setErrorEnabled(false);
                    lendDecToLayout.setError(null);
                    lendDecToLayout.setErrorEnabled(false);
                    lendDecTranDateLayout.setError(null);
                    lendDecTranDateLayout.setErrorEnabled(false);
                    lendDecRemarksLayout.setErrorEnabled(true);
                    lendDecRemarksLayout.setError("Mention the To Detail");
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    lentSubmit();
                }
            } else {
                lentSubmit();
            }

        }
    }

    private void lentSubmit() {

        lendDecNameLayout.setError(null);
        lendDecNameLayout.setErrorEnabled(false);
        lendDecAmountLayout.setError(null);
        lendDecAmountLayout.setErrorEnabled(false);
        lendDecFromLayout.setError(null);
        lendDecFromLayout.setErrorEnabled(false);
        lendDecToLayout.setError(null);
        lendDecToLayout.setErrorEnabled(false);
        lendDecTranDateLayout.setError(null);
        lendDecTranDateLayout.setErrorEnabled(false);
        lendDecRemarksLayout.setError(null);
        lendDecRemarksLayout.setErrorEnabled(false);
        UIUtil.hideKeyboard(AddLentDetailsActivity.this);
        if (ConnectionCheck.getConnectionType(getApplicationContext()) == 0) {
            ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
        } else {
            dialog1.show();

            LentData data = new LentData();

            data.setName(name);
            data.setAmount(amount);
            data.setFrom(from);
            data.setTo(to);
            data.setDate(date);
            data.setRemarks(remarks);

            FirebaseDatabase.getInstance().getReference("Lent Details").child(name).setValue(data).addOnCompleteListener(task -> {
                dialog1.dismiss();
                StyleableToast.makeText(AddLentDetailsActivity.this, "Submitted Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                Intent intent = new Intent(AddLentDetailsActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }).addOnFailureListener(e -> {
                dialog1.dismiss();
                StyleableToast.makeText(AddLentDetailsActivity.this, "Add Lent Submit Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            });
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
    }
}