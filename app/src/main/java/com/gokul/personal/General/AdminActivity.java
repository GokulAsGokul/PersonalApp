package com.gokul.personal.General;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gokul.personal.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class AdminActivity extends AppCompatActivity {

    private TextInputLayout listValueLayout, listNameLayout;
    private TextInputEditText listValue, personalName, personalValue;
    private AutoCompleteTextView listName;
    private Vibrator vibrator;
    private Dialog dialog;
    final String[] listsName = {"Sources", "AddBalanceCategory", "UploadDocCategory"};
    private String valueList, nameList, listsDropDown, namePersonal, valuePersonal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.background)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);

        listValueLayout = findViewById(R.id.listValueLayout);
        listNameLayout = findViewById(R.id.listNameLayout);
        TextInputLayout personalNameLayout = findViewById(R.id.personalNameLayout);
        TextInputLayout personalValueLayout = findViewById(R.id.personalValueLayout);
        listValue = findViewById(R.id.listValue);
        personalName = findViewById(R.id.personalName);
        listName = findViewById(R.id.listName);
        personalValue = findViewById(R.id.personalValue);
        Button listSubmit = findViewById(R.id.listSubmit);
        Button personalSubmit = findViewById(R.id.personalSubmit);

        dialog = new Dialog(AdminActivity.this);
        dialog.setContentView(R.layout.submit_dialog);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        vibrator = (Vibrator) getSystemService((VIBRATOR_SERVICE));

        listNameLayout.setClickable(true);
        listName.setInputType(InputType.TYPE_NULL);
        listName.setClickable(true);
        listName.setFocusable(false);
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, R.layout.dropdown_list, listsName);
        listName.setAdapter(listAdapter);
        listName.setOnItemClickListener((parent, view, position, id) -> {
            UIUtil.hideKeyboard(AdminActivity.this);
            listsDropDown = parent.getItemAtPosition(position).toString();
        });

        personalValue.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                addPersonalDetails();
                return true;
            }
            return false;
        });

        listSubmit.setOnClickListener(view -> {
            valueList = Objects.requireNonNull(listValue.getText()).toString().trim();
            nameList = Objects.requireNonNull(listName.getText()).toString().trim();

            if (ConnectionCheck.getConnectionType(getApplicationContext()) == 0) {
                ConnectionCheck.connectionDialogShow(getApplicationContext(), AdminActivity.this);
            } else {
                if (valueList.isEmpty()) {
                    listValueLayout.setErrorEnabled(true);
                    listValueLayout.setError("List Value is Required");
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if (nameList.isEmpty()) {
                    listValueLayout.setErrorEnabled(false);
                    listValueLayout.setError(null);
                    listNameLayout.setErrorEnabled(true);
                    listNameLayout.setError("Select a List");
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    listValueLayout.setErrorEnabled(false);
                    listValueLayout.setError(null);
                    listNameLayout.setErrorEnabled(false);
                    listNameLayout.setError(null);

                    addListData();

                }
            }
        });

        personalSubmit.setOnClickListener(view -> {
            namePersonal = Objects.requireNonNull(personalName.getText()).toString().trim();
            valuePersonal = Objects.requireNonNull(personalValue.getText()).toString().trim();

            if (ConnectionCheck.getConnectionType(getApplicationContext()) == 0) {
                ConnectionCheck.connectionDialogShow(getApplicationContext(), AdminActivity.this);
            } else {
                if (namePersonal.isEmpty()) {
                    personalNameLayout.setErrorEnabled(true);
                    personalNameLayout.setError("Name is Required");
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if (valuePersonal.isEmpty()) {
                    personalNameLayout.setErrorEnabled(false);
                    personalNameLayout.setError(null);
                    personalValueLayout.setErrorEnabled(true);
                    personalValueLayout.setError("Value is Required");
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    personalNameLayout.setErrorEnabled(false);
                    personalNameLayout.setError(null);
                    personalValueLayout.setErrorEnabled(false);
                    personalValueLayout.setError(null);

                    addPersonalDetails();

                }
            }
        });

    }

    private void addListData() {

        AdminData adminData = new AdminData();
        adminData.setListValue(valueList);

        dialog.show();

        FirebaseDatabase.getInstance().getReference("DropDown Values")
                .child(nameList).child(valueList).setValue(adminData).addOnCompleteListener(task -> {
            Objects.requireNonNull(listValue.getText()).clear();
            Objects.requireNonNull(listName.getText()).clear();
            dialog.dismiss();
            StyleableToast.makeText(AdminActivity.this, "List value added successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();

        }).addOnFailureListener(e -> {
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(AdminActivity.this, "List value add error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });

    }

    private void addPersonalDetails() {

        AdminData adminData = new AdminData();
        adminData.setPersonalName(namePersonal);
        adminData.setPersonalValue(valuePersonal);

        dialog.show();

        FirebaseDatabase.getInstance().getReference("Personal Details")
                .child(namePersonal).setValue(adminData).addOnCompleteListener(task -> {
            Objects.requireNonNull(personalName.getText()).clear();
            Objects.requireNonNull(personalValue.getText()).clear();
            dialog.dismiss();
            StyleableToast.makeText(AdminActivity.this, "Personal detail added successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();

        }).addOnFailureListener(e -> {
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(AdminActivity.this, "Personal detail add error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });
    }
}