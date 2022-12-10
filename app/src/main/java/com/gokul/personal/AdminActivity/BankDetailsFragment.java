package com.gokul.personal.AdminActivity;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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

import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

import static android.content.Context.VIBRATOR_SERVICE;

public class BankDetailsFragment extends Fragment {

    private TextInputLayout bnkNameLayout, bnkCIFLayout, bnkAcntNumberLayout, bnkIFSCLayout, bnkMICRLayout, bnkBranchLayout;
    private TextInputEditText bnkName, bnkCIF, bnkAcntNumber, bnkIFSC, bnkMICR, bnkBranch;
    private String bankName, bankCIF, bankAccount, bankIFSC, bankMICR, bankBranch;
    private Vibrator vibrator;
    private Dialog dialog;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bank_details, container, false);

        bnkNameLayout = view.findViewById(R.id.bnkNameLayout);
        bnkCIFLayout = view.findViewById(R.id.bnkCIFLayout);
        bnkAcntNumberLayout = view.findViewById(R.id.bnkAcntNumberLayout);
        bnkIFSCLayout = view.findViewById(R.id.bnkIFSCLayout);
        bnkMICRLayout = view.findViewById(R.id.bnkMICRLayout);
        bnkBranchLayout = view.findViewById(R.id.bnkBranchLayout);
        bnkName = view.findViewById(R.id.bnkName);
        bnkCIF = view.findViewById(R.id.bnkCIF);
        bnkAcntNumber = view.findViewById(R.id.bnkAcntNumber);
        bnkIFSC = view.findViewById(R.id.bnkIFSC);
        bnkMICR = view.findViewById(R.id.bnkMICR);
        bnkBranch = view.findViewById(R.id.bnkBranch);
        Button bnkSubmit = view.findViewById(R.id.bnkSubmit);

        vibrator = (Vibrator) requireActivity().getSystemService((VIBRATOR_SERVICE));

        dialog = new Dialog(view.getContext(), android.R.style.Theme_DeviceDefault_NoActionBar);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setStatusBarColor(ContextCompat.getColor(view.getContext(), R.color.background));
            dialog.getWindow().setNavigationBarColor(ContextCompat.getColor(view.getContext(), R.color.background));
        }

        bnkBranch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                bankSubmitCall(view);
                return true;
            }
            return false;
        });

        bnkSubmit.setOnClickListener(this::bankSubmitCall);

        return view;
    }

    private void bankSubmitCall(View view1) {

        bankName = Objects.requireNonNull(bnkName.getText()).toString().trim();
        bankCIF = Objects.requireNonNull(bnkCIF.getText()).toString().trim();
        bankAccount = Objects.requireNonNull(bnkAcntNumber.getText()).toString().trim();
        bankIFSC = Objects.requireNonNull(bnkIFSC.getText()).toString().trim();
        bankMICR = Objects.requireNonNull(bnkMICR.getText()).toString().trim();
        bankBranch = Objects.requireNonNull(bnkBranch.getText()).toString().trim();

        if (ConnectionCheck.getConnectionType(view1.getContext()) == 0) {
            ConnectionCheck.connectionDialogShow(view1.getContext(), requireActivity());
        } else {
            if (bankName.isEmpty()) {
                bnkNameLayout.setErrorEnabled(true);
                bnkNameLayout.setError("Bank Name is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (bankCIF.isEmpty()) {
                bnkNameLayout.setErrorEnabled(false);
                bnkNameLayout.setError(null);
                bnkCIFLayout.setErrorEnabled(true);
                bnkCIFLayout.setError("CIF is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (bankAccount.isEmpty()) {
                bnkNameLayout.setErrorEnabled(false);
                bnkNameLayout.setError(null);
                bnkCIFLayout.setErrorEnabled(false);
                bnkCIFLayout.setError(null);
                bnkAcntNumberLayout.setErrorEnabled(true);
                bnkAcntNumberLayout.setError("Account Number is required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (bankIFSC.isEmpty()) {
                bnkNameLayout.setErrorEnabled(false);
                bnkNameLayout.setError(null);
                bnkCIFLayout.setErrorEnabled(false);
                bnkCIFLayout.setError(null);
                bnkAcntNumberLayout.setErrorEnabled(false);
                bnkAcntNumberLayout.setError(null);
                bnkIFSCLayout.setErrorEnabled(true);
                bnkIFSCLayout.setError("IFSC Code is required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (bankMICR.isEmpty()) {
                bnkNameLayout.setErrorEnabled(false);
                bnkNameLayout.setError(null);
                bnkCIFLayout.setErrorEnabled(false);
                bnkCIFLayout.setError(null);
                bnkAcntNumberLayout.setErrorEnabled(false);
                bnkAcntNumberLayout.setError(null);
                bnkIFSCLayout.setErrorEnabled(false);
                bnkIFSCLayout.setError(null);
                bnkMICRLayout.setErrorEnabled(true);
                bnkMICRLayout.setError("MICR Code is required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (bankBranch.isEmpty()) {
                bnkNameLayout.setErrorEnabled(false);
                bnkNameLayout.setError(null);
                bnkCIFLayout.setErrorEnabled(false);
                bnkCIFLayout.setError(null);
                bnkAcntNumberLayout.setErrorEnabled(false);
                bnkAcntNumberLayout.setError(null);
                bnkIFSCLayout.setErrorEnabled(false);
                bnkIFSCLayout.setError(null);
                bnkMICRLayout.setErrorEnabled(false);
                bnkMICRLayout.setError(null);
                bnkBranchLayout.setErrorEnabled(true);
                bnkBranchLayout.setError("Branch Name is required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                bnkNameLayout.setErrorEnabled(false);
                bnkNameLayout.setError(null);
                bnkCIFLayout.setErrorEnabled(false);
                bnkCIFLayout.setError(null);
                bnkAcntNumberLayout.setErrorEnabled(false);
                bnkAcntNumberLayout.setError(null);
                bnkIFSCLayout.setErrorEnabled(false);
                bnkIFSCLayout.setError(null);
                bnkMICRLayout.setErrorEnabled(false);
                bnkMICRLayout.setError(null);
                bnkBranchLayout.setErrorEnabled(false);
                bnkBranchLayout.setError(null);
                UIUtil.hideKeyboard(requireActivity());
                bankSubmit(view1);
            }
        }

    }

    private void bankSubmit(View view1) {

        dialog.show();

        AdminData data = new AdminData();
        data.setBankName(bankName);
        data.setBankCIF(bankCIF);
        data.setBankAccount(bankAccount);
        data.setBankIFSC(bankIFSC);
        data.setBankMICR(bankMICR);
        data.setBankBranch(bankBranch);

        databaseReference.child("Bank Details").orderByChild("bankName").equalTo(bankName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(view1.getContext(), "Bank details already available", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();

                } else {
                    databaseReference.child("Bank Details").child(bankName).setValue(data).addOnCompleteListener(task -> {
                        Objects.requireNonNull(bnkName.getText()).clear();
                        Objects.requireNonNull(bnkCIF.getText()).clear();
                        Objects.requireNonNull(bnkAcntNumber.getText()).clear();
                        Objects.requireNonNull(bnkIFSC.getText()).clear();
                        Objects.requireNonNull(bnkMICR.getText()).clear();
                        Objects.requireNonNull(bnkBranch.getText()).clear();
                        dialog.dismiss();
                        StyleableToast.makeText(view1.getContext(), "Bank details added successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                    }).addOnFailureListener(e -> {
                        dialog.dismiss();
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                        StyleableToast.makeText(view1.getContext(), "Bank details add error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(view1.getContext(), "Bank details read error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

    }

}