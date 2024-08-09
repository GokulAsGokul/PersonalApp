package com.gokul.personal.AddBalances;

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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

import static android.content.Context.VIBRATOR_SERVICE;

public class IndiaFragment extends Fragment {

    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private final ArrayList<String> sourceList = new ArrayList<>();
    private final ArrayList<String> categoryList = new ArrayList<>();
    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private TextInputLayout balSourceNameLayout, balAmountLayout, balTransAmountLayout, balCategoryLayout, balTranDateLayout, balDecRemarksLayout;
    private TextInputEditText balAmount, balTransAmount, balDecRemarks;
    private AutoCompleteTextView balSourceName, balCategory, balTranDate;
    private Dialog dialog;
    private String source, amount, transAmount, category, date, remarks, sourceDropDown, categoryDropDown;
    private SwitchCompat balSwitch;
    private Vibrator vibrator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_india_balance, container, false);

        balSourceNameLayout = view.findViewById(R.id.balSourceNameLayout);
        balAmountLayout = view.findViewById(R.id.balAmountLayout);
        balTransAmountLayout = view.findViewById(R.id.balTransAmountLayout);
        balCategoryLayout = view.findViewById(R.id.balCategoryLayout);
        balTranDateLayout = view.findViewById(R.id.balTranDateLayout);
        balSourceName = view.findViewById(R.id.balSourceName);
        balAmount = view.findViewById(R.id.balAmount);
        balAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
        balTransAmount = view.findViewById(R.id.balTransAmount);
        balTransAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
        balCategory = view.findViewById(R.id.balCategory);
        balTranDate = view.findViewById(R.id.balTranDate);
        balDecRemarksLayout = view.findViewById(R.id.balDecRemarksLayout);
        balDecRemarks = view.findViewById(R.id.balDecRemarks);
        balSwitch = view.findViewById(R.id.balSwitch);
        Button balDecSubmit = view.findViewById(R.id.balDecSubmit);

        ConnectionCheck.connectionDialogShow(view.getContext(), getActivity());

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

        balSourceName.setInputType(InputType.TYPE_NULL);
        balSourceName.setClickable(true);
        balSourceName.setFocusable(false);
        balSourceNameLayout.setClickable(true);
        databaseReference.child("DropDown Values").child("IndiaSource").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String temp = childDataSnapshot.child("listValue").getValue(String.class);
                    dialog.show();
                    sourceList.add(temp);
                }
                ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_list, sourceList);
                sourceAdapter.sort(Comparator.naturalOrder());
                balSourceName.setAdapter(sourceAdapter);
                dialog.dismiss();
                balSourceName.setOnItemClickListener((parent, view, position, id) -> {
                    UIUtil.hideKeyboard(requireActivity());
                    sourceDropDown = parent.getItemAtPosition(position).toString();
                });
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                dialog.dismiss();
                StyleableToast.makeText(view.getContext(), "Sources Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

        balCategory.setInputType(InputType.TYPE_NULL);
        balCategory.setClickable(true);
        balCategory.setFocusable(false);
        balCategoryLayout.setClickable(true);
        databaseReference.child("DropDown Values").child("IndiaBalanceCategory").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String temp = childDataSnapshot.child("listValue").getValue(String.class);
                    dialog.show();
                    categoryList.add(temp);
                }
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_list, categoryList);
                categoryAdapter.sort(Comparator.naturalOrder());
                balCategory.setAdapter(categoryAdapter);
                dialog.dismiss();
                balCategory.setOnItemClickListener((parent, view, position, id) -> {
                    UIUtil.hideKeyboard(requireActivity());
                    categoryDropDown = parent.getItemAtPosition(position).toString();
                });
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                dialog.dismiss();
                StyleableToast.makeText(view.getContext(), "Category Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

        balTranDate.setInputType(InputType.TYPE_NULL);
        balTranDate.setClickable(true);
        balTranDate.setFocusable(false);
        balTranDate.setOnClickListener(v -> {
            UIUtil.hideKeyboard(requireActivity());
            IndiaFragment.this.dayPicker(view);
        });

        balDecSubmit.setOnClickListener(view1 -> {
            dialog.setCancelable(false);
            dialog.show();
            submitBalanceCall(view);
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
            balTranDate.setText(format2.format(formattedDate));
        }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.white));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.white));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(view.getContext(), R.color.background));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(view.getContext(), R.color.background));
    }

    private void submitBalanceCall(View view) {

        if (ConnectionCheck.getConnectionType(view.getContext()) == 0) {
            balSourceNameLayout.setError(null);
            balSourceNameLayout.setErrorEnabled(false);
            balAmountLayout.setError(null);
            balAmountLayout.setErrorEnabled(false);
            balTransAmountLayout.setError(null);
            balTransAmountLayout.setErrorEnabled(false);
            balCategoryLayout.setError(null);
            balCategoryLayout.setErrorEnabled(false);
            balTranDateLayout.setError(null);
            balTranDateLayout.setErrorEnabled(false);
            balDecRemarksLayout.setError(null);
            balDecRemarksLayout.setErrorEnabled(false);
            UIUtil.hideKeyboard(requireActivity());
            dialog.dismiss();
            ConnectionCheck.connectionDialogShow(view.getContext(), getActivity());
        } else {

            if ((Objects.requireNonNull(balDecRemarks.getText()).toString()).equalsIgnoreCase("Mention If Any")) {
                balDecRemarks.setText(R.string.nill);
            }

            source = balSourceName.getText().toString();
            amount = Objects.requireNonNull(balAmount.getText()).toString().trim();
            transAmount = Objects.requireNonNull(balTransAmount.getText()).toString().trim();
            category = Objects.requireNonNull(balCategory.getText()).toString().trim();
            date = Objects.requireNonNull(balTranDate.getText()).toString().trim();
            remarks = balDecRemarks.getText().toString().trim();

            String remarksPattern = "^[a-zA-Z0-9\\s]{4,20}$";

            if (source.isEmpty()) {
                balSourceNameLayout.setErrorEnabled(true);
                balSourceNameLayout.setError("Source Name is Required");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if ((amount.startsWith(".") || amount.isEmpty() || amount.equals("0") || amount.equals("0.0") || amount.equals("0.00")
            ) && !category.equalsIgnoreCase("Zenovic Trust")) {
                balSourceNameLayout.setError(null);
                balSourceNameLayout.setErrorEnabled(false);
                balAmountLayout.setErrorEnabled(true);
                balAmountLayout.setError("Amount is Required");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (transAmount.startsWith(".") || transAmount.isEmpty() || transAmount.equals("0") || transAmount.equals("0.0")
                    || transAmount.equals("0.00")) {
                balSourceNameLayout.setError(null);
                balSourceNameLayout.setErrorEnabled(false);
                balAmountLayout.setError(null);
                balAmountLayout.setErrorEnabled(false);
                balTransAmountLayout.setErrorEnabled(true);
                balTransAmountLayout.setError("Amount is Required");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (category.isEmpty()) {
                balSourceNameLayout.setError(null);
                balSourceNameLayout.setErrorEnabled(false);
                balAmountLayout.setError(null);
                balAmountLayout.setErrorEnabled(false);
                balTransAmountLayout.setError(null);
                balTransAmountLayout.setErrorEnabled(false);
                balCategoryLayout.setErrorEnabled(true);
                balCategoryLayout.setError("Category is Required");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (date.isEmpty()) {
                balSourceNameLayout.setError(null);
                balSourceNameLayout.setErrorEnabled(false);
                balAmountLayout.setError(null);
                balAmountLayout.setErrorEnabled(false);
                balTransAmountLayout.setError(null);
                balTransAmountLayout.setErrorEnabled(false);
                balCategoryLayout.setError(null);
                balCategoryLayout.setErrorEnabled(false);
                balTranDateLayout.setErrorEnabled(true);
                balTranDateLayout.setError("Date is Required");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (remarks.isEmpty()) {
                balSourceNameLayout.setError(null);
                balSourceNameLayout.setErrorEnabled(false);
                balAmountLayout.setError(null);
                balAmountLayout.setErrorEnabled(false);
                balTransAmountLayout.setError(null);
                balTransAmountLayout.setErrorEnabled(false);
                balCategoryLayout.setError(null);
                balCategoryLayout.setErrorEnabled(false);
                balTranDateLayout.setError(null);
                balTranDateLayout.setErrorEnabled(false);
                balDecRemarksLayout.setErrorEnabled(true);
                balDecRemarksLayout.setError("Remarks is Required");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (!remarks.matches(remarksPattern)) {
                balSourceNameLayout.setError(null);
                balSourceNameLayout.setErrorEnabled(false);
                balAmountLayout.setError(null);
                balAmountLayout.setErrorEnabled(false);
                balTransAmountLayout.setError(null);
                balTransAmountLayout.setErrorEnabled(false);
                balCategoryLayout.setError(null);
                balCategoryLayout.setErrorEnabled(false);
                balTranDateLayout.setError(null);
                balTranDateLayout.setErrorEnabled(false);
                balDecRemarksLayout.setErrorEnabled(true);
                balDecRemarksLayout.setError("Remarks should be length of 4 to 20 alphanumeric");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (source.equals("Others")) {
                if ((remarks.isEmpty()) || (remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nill"))) {
                    balSourceNameLayout.setError(null);
                    balSourceNameLayout.setErrorEnabled(false);
                    balAmountLayout.setError(null);
                    balAmountLayout.setErrorEnabled(false);
                    balTransAmountLayout.setError(null);
                    balTransAmountLayout.setErrorEnabled(false);
                    balCategoryLayout.setError(null);
                    balCategoryLayout.setErrorEnabled(false);
                    balTranDateLayout.setError(null);
                    balTranDateLayout.setErrorEnabled(false);
                    balDecRemarksLayout.setErrorEnabled(true);
                    balDecRemarksLayout.setError("Mention Source of Amount");
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    availableBalanceSubmitCall(view);
                }
            } else if (category.equals("Others")) {
                if ((remarks.isEmpty()) || (remarks.equalsIgnoreCase("Mention If Any")) || (remarks.equalsIgnoreCase("Nill"))) {
                    balSourceNameLayout.setError(null);
                    balSourceNameLayout.setErrorEnabled(false);
                    balAmountLayout.setError(null);
                    balAmountLayout.setErrorEnabled(false);
                    balTransAmountLayout.setError(null);
                    balTransAmountLayout.setErrorEnabled(false);
                    balCategoryLayout.setError(null);
                    balCategoryLayout.setErrorEnabled(false);
                    balTranDateLayout.setError(null);
                    balTranDateLayout.setErrorEnabled(false);
                    balDecRemarksLayout.setErrorEnabled(true);
                    balDecRemarksLayout.setError("Mention Category of Amount");
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

        amount = decimalFormat.format(Double.parseDouble(Objects.requireNonNull(balAmount.getText()).toString().trim()));
        transAmount = decimalFormat.format(Double.parseDouble(Objects.requireNonNull(balTransAmount.getText()).toString().trim()));

        balSourceNameLayout.setError(null);
        balSourceNameLayout.setErrorEnabled(false);
        balAmountLayout.setError(null);
        balAmountLayout.setErrorEnabled(false);
        balTransAmountLayout.setError(null);
        balTransAmountLayout.setErrorEnabled(false);
        balCategoryLayout.setError(null);
        balCategoryLayout.setErrorEnabled(false);
        balTranDateLayout.setError(null);
        balTranDateLayout.setErrorEnabled(false);
        balDecRemarksLayout.setError(null);
        balDecRemarksLayout.setErrorEnabled(false);
        UIUtil.hideKeyboard(requireActivity());

        if (ConnectionCheck.getConnectionType(view.getContext()) == 0) {
            dialog.dismiss();
            ConnectionCheck.connectionDialogShow(view.getContext(), requireActivity());
        } else {

            databaseReference.child("Available Balance Details India").child(source).child("amount").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot snapshot) {
                    String temp = snapshot.getValue(String.class);
                    if (balSwitch.isChecked()) {
                        if (temp == null) {
                            availableBalanceSubmit(transAmount, view);
                            dialog.dismiss();
                        } else {
                            if (Double.parseDouble(transAmount) <= Double.parseDouble(temp)) {
                                availableBalanceSubmit(String.valueOf(BigDecimal.valueOf(Double.parseDouble(temp)).subtract(BigDecimal.valueOf(Double.parseDouble(transAmount)))), view);
                            } else {
                                dialog.dismiss();
                                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                                StyleableToast.makeText(view.getContext(), "Available balance is less than entered amount", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                            }
                        }
                    } else {
                        if (temp == null) {
                            availableBalanceSubmit(transAmount, view);
                        } else {
                            availableBalanceSubmit(String.valueOf(BigDecimal.valueOf(Double.parseDouble(temp)).add(BigDecimal.valueOf(Double.parseDouble(transAmount)))), view);
                        }
                    }
                }

                @Override
                public void onCancelled(@NotNull DatabaseError error) {
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(view.getContext(), "Available Balance Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                }
            });
        }
    }

    private void availableBalanceSubmit(String sourceAmount, View view) {

        BalanceDetailsData data = new BalanceDetailsData();
        data.setSource(source);
        String avlAmount = decimalFormat.format(Double.parseDouble(sourceAmount));
        data.setAmount(avlAmount);

        databaseReference.child("Available Balance Details India").child(source).setValue(data).addOnCompleteListener(task -> submitBalance(avlAmount, view)).addOnFailureListener(e1 -> {
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(view.getContext(), "Available Balance Submit Error: " + e1.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });
    }

    private void submitBalance(String avlAmount, View view) {

        String date1 = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.getDefault()).format(new Date());

        BalanceDetailsData data = new BalanceDetailsData();
        data.setSource(source);
        data.setAmount(decimalFormat.format(Double.parseDouble(amount)));
        data.setTransAmount(decimalFormat.format(Double.parseDouble(transAmount)));
        data.setAvailableAmount(avlAmount);
        data.setCategory(category);
        if (balSwitch.isChecked()) {
            data.setType("Expense");
        } else {
            data.setType("Income");
        }
        data.setDate(date);
        data.setRemarks(remarks);
        data.setFilter(date.substring(3, 10).replaceAll("-", "_"));
        data.setUpdateFinder(date1);

        databaseReference.child("Balance Details India").child(source).child(date1).setValue(data).addOnCompleteListener(task -> {
            balSourceName.getText().clear();
            Objects.requireNonNull(balAmount.getText()).clear();
            Objects.requireNonNull(balTransAmount.getText()).clear();
            balCategory.getText().clear();
            balTranDate.getText().clear();
            balDecRemarks.setText(R.string.mention_if_any);
            dialog.dismiss();
            StyleableToast.makeText(view.getContext(), "Balance Details Submitted Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(view.getContext(), "Balance Details Submit Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });
    }

}