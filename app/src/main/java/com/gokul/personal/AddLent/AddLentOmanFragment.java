package com.gokul.personal.AddLent;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.gokul.personal.AddBalances.BalanceDetailsData;
import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.Miscellaneous.DecimalDigitsInputFilter;
import com.gokul.personal.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.muddz.styleabletoast.StyleableToast;

import static android.content.Context.VIBRATOR_SERVICE;

public class AddLentOmanFragment extends Fragment {

    private TextInputLayout lendDecNameLayout, lendDecAmountLayout, lendDecFromLayout, lendDecToLayout, lendDecTranDateLayout, lendDecRemarksLayout;
    private TextInputEditText lendDecName, lendDecAmount, lendDecRemarks;
    private AutoCompleteTextView lendDecFrom, lendDecTo, lendDecTranDate;
    private Vibrator vibrator;
    private String name, amount, from, to, date, remarks, fromDropDown, toDropDown;
    private Dialog dialog;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private final ArrayList<String> fromList = new ArrayList<>();
    private final ArrayList<String> toList = new ArrayList<>();
    private final DecimalFormat decimalFormat = new DecimalFormat("0.000");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_lent_oman, container, false);

        lendDecNameLayout = view.findViewById(R.id.lendDecNameLayoutOM);
        lendDecAmountLayout = view.findViewById(R.id.lendDecAmountLayoutOM);
        lendDecFromLayout = view.findViewById(R.id.lendDecFromLayoutOM);
        lendDecToLayout = view.findViewById(R.id.lendDecToLayoutOM);
        lendDecTranDateLayout = view.findViewById(R.id.lendDecTranDateLayoutOM);
        lendDecRemarksLayout = view.findViewById(R.id.lendDecRemarksLayoutOM);
        lendDecName = view.findViewById(R.id.lendDecNameOM);
        lendDecAmount = view.findViewById(R.id.lendDecAmountOM);
        lendDecAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3)});
        lendDecRemarks = view.findViewById(R.id.lendDecRemarksOM);
        lendDecFrom = view.findViewById(R.id.lendDecFromOM);
        lendDecTo = view.findViewById(R.id.lendDecToOM);
        lendDecTranDate = view.findViewById(R.id.lendDecTranDateOM);
        Button lendDecSubmit = view.findViewById(R.id.lendDecSubmitOM);

        ConnectionCheck.connectionDialogShow(view.getContext(), requireActivity());

        dialog = new Dialog(view.getContext(), android.R.style.Theme_DeviceDefault_NoActionBar);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setStatusBarColor(ContextCompat.getColor(view.getContext(), R.color.background));
            dialog.getWindow().setNavigationBarColor(ContextCompat.getColor(view.getContext(), R.color.background));
        }
        if (ConnectionCheck.getConnectionType(view.getContext()) != 0) {
            dialog.show();
        }

        vibrator = (Vibrator) requireActivity().getSystemService((VIBRATOR_SERVICE));

        lendDecFrom.setInputType(InputType.TYPE_NULL);
        lendDecFrom.setClickable(true);
        lendDecFrom.setFocusable(false);
        lendDecFromLayout.setClickable(true);
        databaseReference.child("DropDown Values").child("OmanSource").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String temp = childDataSnapshot.child("listValue").getValue(String.class);
                    dialog.show();
                    fromList.add(temp);
                }
                ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(view.getContext(), R.layout.dropdown_list, fromList);
                sourceAdapter.sort(Comparator.naturalOrder());
                lendDecFrom.setAdapter(sourceAdapter);
                dialog.dismiss();
                lendDecFrom.setOnItemClickListener((parent, view, position, id) -> {
                    UIUtil.hideKeyboard(requireActivity());
                    fromDropDown = parent.getItemAtPosition(position).toString();
                });
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                dialog.dismiss();
                StyleableToast.makeText(view.getContext(), "From Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

        lendDecTo.setInputType(InputType.TYPE_NULL);
        lendDecTo.setClickable(true);
        lendDecTo.setFocusable(false);
        lendDecToLayout.setClickable(true);
        databaseReference.child("DropDown Values").child("OmanLentTo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String temp = childDataSnapshot.child("listValue").getValue(String.class);
                    dialog.show();
                    toList.add(temp);
                }
                ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(view.getContext(), R.layout.dropdown_list, toList);
                sourceAdapter.sort(Comparator.naturalOrder());
                lendDecTo.setAdapter(sourceAdapter);
                dialog.dismiss();
                lendDecTo.setOnItemClickListener((parent, view, position, id) -> {
                    UIUtil.hideKeyboard(requireActivity());
                    toDropDown = parent.getItemAtPosition(position).toString();
                });
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                dialog.dismiss();
                StyleableToast.makeText(view.getContext(), "To Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
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
                dialog.setCancelable(false);
                dialog.show();
                lentSubmitCall(view);
                return true;
            }
            return false;
        });

        lendDecSubmit.setOnClickListener(view1 -> {
            dialog.setCancelable(false);
            dialog.show();
            lentSubmitCall(view);
        });

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
            dialog.dismiss();
            ConnectionCheck.connectionDialogShow(view.getContext(), requireActivity());
        } else {

            if ((Objects.requireNonNull(lendDecRemarks.getText()).toString()).equalsIgnoreCase("Mention If Any")) {
                lendDecRemarks.setText(R.string.nill);
            }

            name = capitalizeAll(Objects.requireNonNull(lendDecName.getText()).toString());
            amount = decimalFormat.format(Double.parseDouble(Objects.requireNonNull(lendDecAmount.getText()).toString().trim()));
            from = lendDecFrom.getText().toString().trim();
            to = lendDecTo.getText().toString().trim();
            date = lendDecTranDate.getText().toString().trim();
            remarks = lendDecRemarks.getText().toString().trim();

            String remarksPattern = "^[a-zA-Z0-9\\s]{4,20}$";

            if (name.isEmpty()) {
                lendDecNameLayout.setErrorEnabled(true);
                lendDecNameLayout.setError("Borrower Name is Required");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (amount.isEmpty()) {
                lendDecNameLayout.setError(null);
                lendDecNameLayout.setErrorEnabled(false);
                lendDecAmountLayout.setErrorEnabled(true);
                lendDecAmountLayout.setError("Amount is Required");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (amount.equals("0") || amount.equals("0.0") || amount.equals("0.00") || amount.equals("0.000")) {
                lendDecNameLayout.setError(null);
                lendDecNameLayout.setErrorEnabled(false);
                lendDecAmountLayout.setErrorEnabled(true);
                lendDecAmountLayout.setError("Amount is Required");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (from.isEmpty()) {
                lendDecNameLayout.setError(null);
                lendDecNameLayout.setErrorEnabled(false);
                lendDecAmountLayout.setError(null);
                lendDecAmountLayout.setErrorEnabled(false);
                lendDecFromLayout.setErrorEnabled(true);
                lendDecFromLayout.setError("From is Required");
                dialog.dismiss();
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
                dialog.dismiss();
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
                dialog.dismiss();
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
                dialog.dismiss();
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
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (from.equals("Others")) {
                if ((remarks.isEmpty()) || (remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nil"))) {
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
                    lendDecRemarksLayout.setError("Mention From Detail");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    availableBalanceSubmitCall(view);
                }
            } else if (to.equals("Bank")) {
                if ((remarks.isEmpty()) || (remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nil"))) {
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
                    lendDecRemarksLayout.setError("Mention Bank Name");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    availableBalanceSubmitCall(view);
                }
            } else if (to.equals("Others")) {
                if ((remarks.isEmpty()) || (remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nil"))) {
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
                    lendDecRemarksLayout.setError("Mention To Detail");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    availableBalanceSubmitCall(view);
                }
            } else {
                availableBalanceSubmitCall(view);
            }

        }
    }

    private void availableBalanceSubmitCall(View view) {
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
            dialog.dismiss();
            ConnectionCheck.connectionDialogShow(view.getContext(), requireActivity());
        } else {
            databaseReference.child("Available Balance Details Oman").child(from).child("amount").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot snapshot) {
                    String temp = snapshot.getValue(String.class);
                    if (temp == null) {
                        dialog.dismiss();
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                        StyleableToast.makeText(view.getContext(), "Available balance is less than entered amount", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                    } else {
                        if (Double.parseDouble(amount) <= Double.parseDouble(temp)) {
                            availableBalanceSubmit(String.valueOf(Double.parseDouble(temp) - Double.parseDouble(amount)), view);
                        } else {
                            dialog.dismiss();
                            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                            StyleableToast.makeText(view.getContext(), "Available balance is less than entered amount", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                        }
                    }

                }

                @Override
                public void onCancelled(@NotNull DatabaseError error) {
                    StyleableToast.makeText(view.getContext(), "Available Balance Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                }
            });
        }

    }

    private void availableBalanceSubmit(String sourceAmount, View view) {

        BalanceDetailsData data = new BalanceDetailsData();
        data.setSource(from);
        String avlAmount = decimalFormat.format(Double.parseDouble(sourceAmount));
        data.setAmount(avlAmount);

        databaseReference.child("Available Balance Details Oman").child(from).setValue(data).addOnCompleteListener(task -> lentSubmit(avlAmount, view)).addOnFailureListener(e1 -> {
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(view.getContext(), "Available Balance Submit Error: " + e1.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });

    }

    @SuppressLint("SimpleDateFormat")
    private void lentSubmit(String avlAmount, View view) {

        String finder = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.getDefault()).format(new Date());

        LentDetailsData data = new LentDetailsData();
        data.setName(name);
        data.setAmount(decimalFormat.format(Double.parseDouble(amount)));
        data.setRemAmount(decimalFormat.format(Double.parseDouble(amount)));
        data.setFrom(from);
        data.setTo(to);
        data.setDate(date);
        data.setRemarks(remarks);
        data.setUpdateFinder(finder);

        BalanceDetailsData balanceDetailsData = new BalanceDetailsData();
        balanceDetailsData.setAmount(decimalFormat.format(Double.parseDouble(amount)));
        balanceDetailsData.setTransAmount(decimalFormat.format(Double.parseDouble(amount)));
        balanceDetailsData.setAvailableAmount(avlAmount);
        balanceDetailsData.setCategory("Lent");
        balanceDetailsData.setDate(date);
        balanceDetailsData.setFilter(date.substring(3, 10).replaceAll("-", "_"));
        balanceDetailsData.setRemarks("To " + name);
        balanceDetailsData.setSource(from);
        balanceDetailsData.setType("Expense");
        balanceDetailsData.setUpdateFinder(finder);

        databaseReference.child("Lent Details Oman").child(finder).setValue(data).addOnCompleteListener(task -> balanceDetailsSubmit(view, balanceDetailsData, finder)).addOnFailureListener(e -> {
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(view.getContext(), "Lend Submit Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });
    }

    private void balanceDetailsSubmit(View view, BalanceDetailsData balanceDetailsData, String date) {

        databaseReference.child("Balance Details Oman").child(from).child(date).setValue(balanceDetailsData).addOnCompleteListener(task -> {
            Objects.requireNonNull(lendDecName.getText()).clear();
            Objects.requireNonNull(lendDecAmount.getText()).clear();
            lendDecFrom.getText().clear();
            lendDecTo.getText().clear();
            lendDecTranDate.getText().clear();
            lendDecRemarks.setText(R.string.mention_if_any);
            dialog.dismiss();
            StyleableToast.makeText(view.getContext(), "Lent Details Submitted Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(view.getContext(), "Balance Details Submit Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });

    }

    private static String capitalizeAll(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return Arrays.stream(str.split("\\s+"))
                .map(t -> t.substring(0, 1).toUpperCase() + t.substring(1))
                .collect(Collectors.joining(" "));
    }

}