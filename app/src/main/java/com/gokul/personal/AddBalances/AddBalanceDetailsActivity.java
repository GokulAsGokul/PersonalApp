package com.gokul.personal.AddBalances;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.gokul.personal.General.ConnectionCheck;
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

public class AddBalanceDetailsActivity extends AppCompatActivity {

    private TextInputLayout balSourceNameLayout, balAmountLayout, balCategoryLayout, balTranDateLayout, balDecRemarksLayout;
    private TextInputEditText balAmount, balDecRemarks;
    private AutoCompleteTextView balSourceName, balCategory, balTranDate;
    final String[] sourceName = {"Cash", "DBS Bank", "HDFC Bank", "LVB Bank", "SBI Bank", "Others"};
    final String[] categoryList = {"Cash Withdrawal", "Salary", "Lent Return", "Others"};
    private Dialog dialog1;
    private String sourceDropDown, categoryDropDown = "";
    private String source, amount, category, date, remarks;
    private String switchValue = "Income";
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_balance);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.background)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        balSourceNameLayout = findViewById(R.id.balSourceNameLayout);
        balAmountLayout = findViewById(R.id.balAmountLayout);
        balCategoryLayout = findViewById(R.id.balCategoryLayout);
        balTranDateLayout = findViewById(R.id.balTranDateLayout);
        balSourceName = findViewById(R.id.balSourceName);
        balAmount = findViewById(R.id.balAmount);
        balCategory = findViewById(R.id.balCategory);
        balTranDate = findViewById(R.id.balTranDate);
        balDecRemarksLayout = findViewById(R.id.balDecRemarksLayout);
        balDecRemarks = findViewById(R.id.balDecRemarks);
        SwitchCompat balSwitch = findViewById(R.id.balSwitch);
        Button balDecSubmit = findViewById(R.id.balDecSubmit);

        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);

        dialog1 = new Dialog(AddBalanceDetailsActivity.this);
        dialog1.setContentView(R.layout.submit_dialog);
        dialog1.setCancelable(true);
        dialog1.setCanceledOnTouchOutside(false);
        if (dialog1.getWindow() != null) {
            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        vibrator = (Vibrator) getSystemService((VIBRATOR_SERVICE));

        balSourceName.setInputType(InputType.TYPE_NULL);
        balSourceName.setClickable(true);
        balSourceName.setFocusable(false);
        balSourceNameLayout.setClickable(true);
        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(this, R.layout.dropdown_list, sourceName);
        balSourceName.setAdapter(sourceAdapter);
        balSourceName.setOnItemClickListener((parent, view, position, id) -> {
            UIUtil.hideKeyboard(AddBalanceDetailsActivity.this);
            sourceDropDown = parent.getItemAtPosition(position).toString();
        });

        balCategory.setInputType(InputType.TYPE_NULL);
        balCategory.setClickable(true);
        balCategory.setFocusable(false);
        balCategoryLayout.setClickable(true);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.dropdown_list, categoryList);
        balCategory.setAdapter(categoryAdapter);
        balCategory.setOnItemClickListener((parent, view, position, id) -> {
            UIUtil.hideKeyboard(AddBalanceDetailsActivity.this);
            categoryDropDown = parent.getItemAtPosition(position).toString();
        });

        balTranDate.setInputType(InputType.TYPE_NULL);
        balTranDate.setClickable(true);
        balTranDate.setFocusable(false);
        balTranDate.setOnClickListener(v -> {
            UIUtil.hideKeyboard(AddBalanceDetailsActivity.this);
            AddBalanceDetailsActivity.this.dayPicker();
        });

        balSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                switchValue = "Expense";
            } else {
                switchValue = "Income";
            }
        });

        balDecSubmit.setOnClickListener(view -> submitBalanceCall());

    }

    private void dayPicker() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddBalanceDetailsActivity.this, R.style.DateDialogTheme, (view, year1, month1, day1) -> {
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
            balTranDate.setText(format2.format(formattedDate));
        }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.background));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.background));
    }

    private void submitBalanceCall() {

        if (ConnectionCheck.getConnectionType(getApplicationContext()) == 0) {
            balSourceNameLayout.setError(null);
            balSourceNameLayout.setErrorEnabled(false);
            balAmountLayout.setError(null);
            balAmountLayout.setErrorEnabled(false);
            balCategoryLayout.setError(null);
            balCategoryLayout.setErrorEnabled(false);
            balTranDateLayout.setError(null);
            balTranDateLayout.setErrorEnabled(false);
            balDecRemarksLayout.setError(null);
            balDecRemarksLayout.setErrorEnabled(false);
            UIUtil.hideKeyboard(AddBalanceDetailsActivity.this);
            ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
        } else {

            if ((Objects.requireNonNull(balDecRemarks.getText()).toString()).equalsIgnoreCase("Mention If Any")) {
                balDecRemarks.setText(R.string.nill);
            }

            source = balSourceName.getText().toString();
            amount = Objects.requireNonNull(balAmount.getText()).toString().trim();
            category = Objects.requireNonNull(balCategory.getText()).toString().trim();
            date = Objects.requireNonNull(balTranDate.getText()).toString().trim();
            remarks = balDecRemarks.getText().toString().trim();

            String remarksPattern = "^[a-zA-Z0-9\\s]{4,20}$";

            if (source.isEmpty()) {
                balSourceNameLayout.setErrorEnabled(true);
                balSourceNameLayout.setError("Source Name is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (amount.isEmpty()) {
                balSourceNameLayout.setError(null);
                balSourceNameLayout.setErrorEnabled(false);
                balAmountLayout.setErrorEnabled(true);
                balAmountLayout.setError("Amount is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (category.isEmpty()) {
                balSourceNameLayout.setError(null);
                balSourceNameLayout.setErrorEnabled(false);
                balAmountLayout.setError(null);
                balAmountLayout.setErrorEnabled(false);
                balCategoryLayout.setErrorEnabled(true);
                balCategoryLayout.setError("Category is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (date.isEmpty()) {
                balSourceNameLayout.setError(null);
                balSourceNameLayout.setErrorEnabled(false);
                balAmountLayout.setError(null);
                balAmountLayout.setErrorEnabled(false);
                balCategoryLayout.setError(null);
                balCategoryLayout.setErrorEnabled(false);
                balTranDateLayout.setErrorEnabled(true);
                balTranDateLayout.setError("Date is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (remarks.isEmpty()) {
                balSourceNameLayout.setError(null);
                balSourceNameLayout.setErrorEnabled(false);
                balAmountLayout.setError(null);
                balAmountLayout.setErrorEnabled(false);
                balCategoryLayout.setError(null);
                balCategoryLayout.setErrorEnabled(false);
                balTranDateLayout.setError(null);
                balTranDateLayout.setErrorEnabled(false);
                balDecRemarksLayout.setErrorEnabled(true);
                balDecRemarksLayout.setError("Remarks is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (!remarks.matches(remarksPattern)) {
                balSourceNameLayout.setError(null);
                balSourceNameLayout.setErrorEnabled(false);
                balAmountLayout.setError(null);
                balAmountLayout.setErrorEnabled(false);
                balCategoryLayout.setError(null);
                balCategoryLayout.setErrorEnabled(false);
                balTranDateLayout.setError(null);
                balTranDateLayout.setErrorEnabled(false);
                balDecRemarksLayout.setErrorEnabled(true);
                balDecRemarksLayout.setError("Remarks should be length of 4 to 20 alphanumeric");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (source.equals("Others")) {
                if ((remarks.isEmpty()) || (remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nill"))) {
                    balSourceNameLayout.setError(null);
                    balSourceNameLayout.setErrorEnabled(false);
                    balAmountLayout.setError(null);
                    balAmountLayout.setErrorEnabled(false);
                    balCategoryLayout.setError(null);
                    balCategoryLayout.setErrorEnabled(false);
                    balTranDateLayout.setError(null);
                    balTranDateLayout.setErrorEnabled(false);
                    balDecRemarksLayout.setErrorEnabled(true);
                    balDecRemarksLayout.setError("Mention the Source of Amount");
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    submitBalance();
                }
            } else if (category.equals("Others")) {
                if ((remarks.isEmpty()) || (remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nill"))) {
                    balSourceNameLayout.setError(null);
                    balSourceNameLayout.setErrorEnabled(false);
                    balAmountLayout.setError(null);
                    balAmountLayout.setErrorEnabled(false);
                    balCategoryLayout.setError(null);
                    balCategoryLayout.setErrorEnabled(false);
                    balTranDateLayout.setError(null);
                    balTranDateLayout.setErrorEnabled(false);
                    balDecRemarksLayout.setErrorEnabled(true);
                    balDecRemarksLayout.setError("Mention the Category of Amount");
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    submitBalance();
                }
            } else {
                submitBalance();
            }

        }
    }

    private void submitBalance() {

        balSourceNameLayout.setError(null);
        balSourceNameLayout.setErrorEnabled(false);
        balAmountLayout.setError(null);
        balAmountLayout.setErrorEnabled(false);
        balCategoryLayout.setError(null);
        balCategoryLayout.setErrorEnabled(false);
        balTranDateLayout.setError(null);
        balTranDateLayout.setErrorEnabled(false);
        balDecRemarksLayout.setError(null);
        balDecRemarksLayout.setErrorEnabled(false);
        UIUtil.hideKeyboard(AddBalanceDetailsActivity.this);
        if (ConnectionCheck.getConnectionType(getApplicationContext()) == 0) {
            ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
        } else {
            dialog1.show();

            BalanceDetailsData data = new BalanceDetailsData();

            data.setSource(source);
            data.setAmount(amount);
            data.setCategory(category);
            data.setType(switchValue);
            data.setDate(date);
            data.setRemarks(remarks);

            FirebaseDatabase.getInstance().getReference("Balance Details").child(source).setValue(data).addOnCompleteListener(task -> {
                dialog1.dismiss();
                StyleableToast.makeText(AddBalanceDetailsActivity.this, "Submitted Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
            }).addOnFailureListener(e -> {
                dialog1.dismiss();
                StyleableToast.makeText(AddBalanceDetailsActivity.this, "Add Balance Submit Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            });
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
    }
}
