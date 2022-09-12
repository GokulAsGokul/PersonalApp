package com.gokul.personal.AddLent;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

import static android.content.Context.VIBRATOR_SERVICE;

public class AddLentIndiaFragment extends Fragment {

    private TextInputLayout lendDecNameLayout, lendDecAmountLayout, lendDecFromLayout, lendDecToLayout, lendDecTranDateLayout, lendDecRemarksLayout;
    private TextInputEditText lendDecName, lendDecAmount, lendDecRemarks;
    private AutoCompleteTextView lendDecFrom, lendDecTo, lendDecTranDate;
    private String fromDropDown, toDropDown = "";
    private Vibrator vibrator;
    private String name, amount, from, to, date, remarks;
    private Dialog dialog1, dialog2;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private final ArrayList<String> fromList = new ArrayList<>();
    private final ArrayList<String> toList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_lent_india, container, false);

        lendDecNameLayout = view.findViewById(R.id.lendDecNameLayout);
        lendDecAmountLayout = view.findViewById(R.id.lendDecAmountLayout);
        lendDecFromLayout = view.findViewById(R.id.lendDecFromLayout);
        lendDecToLayout = view.findViewById(R.id.lendDecToLayout);
        lendDecTranDateLayout = view.findViewById(R.id.lendDecTranDateLayout);
        lendDecRemarksLayout = view.findViewById(R.id.lendDecRemarksLayout);
        lendDecName = view.findViewById(R.id.lendDecName);
        lendDecAmount = view.findViewById(R.id.lendDecAmount);
        lendDecRemarks = view.findViewById(R.id.lendDecRemarks);
        lendDecFrom = view.findViewById(R.id.lendDecFrom);
        lendDecTo = view.findViewById(R.id.lendDecTo);
        lendDecTranDate = view.findViewById(R.id.lendDecTranDate);
        Button lendDecSubmit = view.findViewById(R.id.lendDecSubmit);

        ConnectionCheck.connectionDialogShow(view.getContext(), requireActivity());

        dialog1 = new Dialog(view.getContext());
        dialog1.setContentView(R.layout.submit_dialog);
        dialog1.setCancelable(true);
        dialog1.setCanceledOnTouchOutside(false);
        if (dialog1.getWindow() != null) {
            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog2 = new Dialog(view.getContext());
        dialog2.setContentView(R.layout.loading_dialog);
        dialog2.setCancelable(true);
        dialog2.setCanceledOnTouchOutside(false);
        if (dialog2.getWindow() != null) {
            dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        if (ConnectionCheck.getConnectionType(view.getContext()) != 0) {
            dialog2.show();
        }

        vibrator = (Vibrator) requireActivity().getSystemService((VIBRATOR_SERVICE));

        lendDecFrom.setInputType(InputType.TYPE_NULL);
        lendDecFrom.setClickable(true);
        lendDecFrom.setFocusable(false);
        lendDecFromLayout.setClickable(true);
        databaseReference.child("DropDown Values").child("Sources").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String temp = childDataSnapshot.child("listValue").getValue(String.class);
                    dialog2.show();
                    fromList.add(temp);
                }
                ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(view.getContext(), R.layout.dropdown_list, fromList);
                sourceAdapter.sort(Comparator.naturalOrder());
                lendDecFrom.setAdapter(sourceAdapter);
                dialog2.dismiss();
                lendDecFrom.setOnItemClickListener((parent, view, position, id) -> {
                    UIUtil.hideKeyboard(requireActivity());
                    fromDropDown = parent.getItemAtPosition(position).toString();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(view.getContext(), "From Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

        lendDecTo.setInputType(InputType.TYPE_NULL);
        lendDecTo.setClickable(true);
        lendDecTo.setFocusable(false);
        lendDecToLayout.setClickable(true);
        databaseReference.child("DropDown Values").child("AddLentTo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String temp = childDataSnapshot.child("listValue").getValue(String.class);
                    dialog2.show();
                    toList.add(temp);
                }
                ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(view.getContext(), R.layout.dropdown_list, toList);
                sourceAdapter.sort(Comparator.naturalOrder());
                lendDecTo.setAdapter(sourceAdapter);
                dialog2.dismiss();
                lendDecTo.setOnItemClickListener((parent, view, position, id) -> {
                    UIUtil.hideKeyboard(requireActivity());
                    toDropDown = parent.getItemAtPosition(position).toString();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(view.getContext(), "From Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

        lendDecTranDate.setInputType(InputType.TYPE_NULL);
        lendDecTranDate.setClickable(true);
        lendDecTranDate.setFocusable(false);
        lendDecTranDate.setOnClickListener(v -> {
            UIUtil.hideKeyboard(requireActivity());
            dayPicker(view);
        });

        lendDecRemarks.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                lentSubmitCall(view);
                return true;
            }
            return false;
        });

        lendDecSubmit.setOnClickListener(view1 -> lentSubmitCall(view));

        return view;
    }

    private void dayPicker(View view) {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                view.getContext(), R.style.DateDialogTheme, (view1, year1, month1, day1) -> {
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
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.white));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.white));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(view.getContext(), R.color.background));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(view.getContext(), R.color.background));
    }

    private void lentSubmitCall(View view) {

        if (ConnectionCheck.getConnectionType(view.getContext()) == 0) {
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
            UIUtil.hideKeyboard(requireActivity());
            ConnectionCheck.connectionDialogShow(view.getContext(), requireActivity());
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
                    lentSubmit(view);
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
                    lentSubmit(view);
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
                    lentSubmit(view);
                }
            } else {
                lentSubmit(view);
            }

        }
    }

    @SuppressLint("SimpleDateFormat")
    private void lentSubmit(View view) {

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
        UIUtil.hideKeyboard(requireActivity());
        if (ConnectionCheck.getConnectionType(view.getContext()) == 0) {
            ConnectionCheck.connectionDialogShow(view.getContext(), requireActivity());
        } else {
            dialog1.show();

            String date1 = new SimpleDateFormat("_dd_MM_yyyy_HH_mm_ss", Locale.getDefault()).format(new Date());

            LentData data = new LentData();
            data.setName(name);
            data.setAmount(amount);
            data.setFrom(from);
            data.setTo(to);
            data.setDate(date);
            data.setRemarks(remarks);
            data.setUpdateFinder(name + date1);

            databaseReference.child("Lent Details India").child(name + date1).setValue(data).addOnCompleteListener(task -> availableBalanceSubmit(view)).addOnFailureListener(e -> {
                dialog1.dismiss();
                StyleableToast.makeText(view.getContext(), "Lend Submit Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            });
        }
    }

    private void availableBalanceSubmit(View view) {

        databaseReference.child("Available Balance Details India").child(from).child("amount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String temp = snapshot.getValue(String.class);
                String sourceAmount;
                if (temp == null) {
                    sourceAmount = String.valueOf(Integer.parseInt(amount));
                } else {
                    sourceAmount = String.valueOf(Integer.parseInt(temp) - Integer.parseInt(amount));
                }

                databaseReference.child("Available Balance Details India").child(from).child("amount").setValue(sourceAmount).addOnCompleteListener(task -> {
                    Objects.requireNonNull(lendDecName.getText()).clear();
                    Objects.requireNonNull(lendDecAmount.getText()).clear();
                    lendDecFrom.getText().clear();
                    lendDecTo.getText().clear();
                    lendDecTranDate.getText().clear();
                    dialog1.dismiss();
                    StyleableToast.makeText(view.getContext(), "Lent Details Submitted Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                }).addOnFailureListener(e1 -> {
                    dialog1.dismiss();
                    StyleableToast.makeText(view.getContext(), "Available Balance Submit Error: " + e1.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(view.getContext(), "Available Balance Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });
    }

}