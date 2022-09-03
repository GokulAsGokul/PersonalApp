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

import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

import static android.content.Context.VIBRATOR_SERVICE;

public class PersonalDetailsFragment extends Fragment {

    private TextInputLayout personalNameLayout, personalValueLayout;
    private TextInputEditText personalName, personalValue;
    private Vibrator vibrator;
    private Dialog dialog;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_details, container, false);

        personalNameLayout = view.findViewById(R.id.personalNameLayout);
        personalValueLayout = view.findViewById(R.id.personalValueLayout);
        personalName = view.findViewById(R.id.personalName);
        personalValue = view.findViewById(R.id.personalValue);
        Button personalSubmit = view.findViewById(R.id.personalSubmit);

        vibrator = (Vibrator) requireActivity().getSystemService((VIBRATOR_SERVICE));

        dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.submit_dialog);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        personalValue.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                addPersonalDetails(view);
                return true;
            }
            return false;
        });

        personalSubmit.setOnClickListener((View view1) -> addPersonalDetails(view));

        return view;
    }

    private void addPersonalDetails(View view) {

        String namePersonal = Objects.requireNonNull(personalName.getText()).toString().trim();
        String valuePersonal = Objects.requireNonNull(personalValue.getText()).toString().trim();

        if (ConnectionCheck.getConnectionType(view.getContext()) == 0) {
            ConnectionCheck.connectionDialogShow(view.getContext(), requireActivity());
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

                AdminData adminData = new AdminData();
                adminData.setPersonalName(namePersonal);
                adminData.setPersonalValue(valuePersonal);

                dialog.show();

                databaseReference.child("Personal Details").orderByChild("personalName").equalTo(namePersonal).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            dialog.dismiss();
                            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                            StyleableToast.makeText(view.getContext(), "Personal details already available", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();

                        } else {

                            databaseReference.child("Personal Details").child(namePersonal).setValue(adminData).addOnCompleteListener(task -> {
                                Objects.requireNonNull(personalName.getText()).clear();
                                Objects.requireNonNull(personalValue.getText()).clear();
                                dialog.dismiss();
                                StyleableToast.makeText(view.getContext(), "Personal detail added successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();

                            }).addOnFailureListener(e -> {
                                dialog.dismiss();
                                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                                StyleableToast.makeText(view.getContext(), "Personal detail add error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        StyleableToast.makeText(view.getContext(), "Personal details read error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                    }
                });

            }
        }
    }

}