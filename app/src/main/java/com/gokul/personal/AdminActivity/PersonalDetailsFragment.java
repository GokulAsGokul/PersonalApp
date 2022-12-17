package com.gokul.personal.AdminActivity;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

import static android.content.Context.VIBRATOR_SERVICE;

public class PersonalDetailsFragment extends Fragment {

    private TextInputLayout personalNameLayout, personalValueLayout, personalTypeLayout;
    private TextInputEditText personalName, personalValue;
    private AutoCompleteTextView personalType;
    private Vibrator vibrator;
    private Dialog dialog;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private final ArrayList<String> typeList = new ArrayList<>();
    private String typeDropDown;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_details, container, false);

        personalNameLayout = view.findViewById(R.id.personalNameLayout);
        personalValueLayout = view.findViewById(R.id.personalValueLayout);
        personalTypeLayout = view.findViewById(R.id.personalTypeLayout);
        personalName = view.findViewById(R.id.personalName);
        personalValue = view.findViewById(R.id.personalValue);
        personalType = view.findViewById(R.id.personalType);
        Button personalSubmit = view.findViewById(R.id.personalSubmit);

        vibrator = (Vibrator) requireActivity().getSystemService((VIBRATOR_SERVICE));

        dialog = new Dialog(view.getContext(), android.R.style.Theme_DeviceDefault_NoActionBar);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setStatusBarColor(ContextCompat.getColor(view.getContext(), R.color.background));
            dialog.getWindow().setNavigationBarColor(ContextCompat.getColor(view.getContext(), R.color.background));
        }

        personalType.setInputType(InputType.TYPE_NULL);
        personalType.setClickable(true);
        personalTypeLayout.setClickable(true);
        personalType.setFocusable(false);
        databaseReference.child("DropDown Values").child("PersonalTypes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String temp = childDataSnapshot.child("listValue").getValue(String.class);
                    dialog.show();
                    typeList.add(temp);
                }
                ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(view.getContext(), R.layout.dropdown_list, typeList);
                sourceAdapter.sort(Comparator.naturalOrder());
                personalType.setAdapter(sourceAdapter);
                dialog.dismiss();
                personalType.setOnItemClickListener((parent, view, position, id) -> {
                    UIUtil.hideKeyboard(requireActivity());
                    typeDropDown = parent.getItemAtPosition(position).toString();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(view.getContext(), "Type Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

        personalSubmit.setOnClickListener((View view1) -> {
            dialog.show();
            dialog.setCancelable(true);
            addPersonalDetails(view);
        });

        return view;
    }

    private void addPersonalDetails(View view) {

        String namePersonal = Objects.requireNonNull(personalName.getText()).toString().trim();
        String valuePersonal = Objects.requireNonNull(personalValue.getText()).toString().trim();
        String typePersonal = Objects.requireNonNull(personalType.getText()).toString().trim();

        if (ConnectionCheck.getConnectionType(view.getContext()) == 0) {
            personalNameLayout.setErrorEnabled(false);
            personalNameLayout.setError(null);
            personalValueLayout.setErrorEnabled(false);
            personalValueLayout.setError(null);
            personalTypeLayout.setErrorEnabled(false);
            personalTypeLayout.setError(null);
            dialog.dismiss();
            ConnectionCheck.connectionDialogShow(view.getContext(), requireActivity());
        } else {
            if (namePersonal.isEmpty()) {
                personalNameLayout.setErrorEnabled(true);
                personalNameLayout.setError("Name is Required");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (valuePersonal.isEmpty()) {
                personalNameLayout.setErrorEnabled(false);
                personalNameLayout.setError(null);
                personalValueLayout.setErrorEnabled(true);
                personalValueLayout.setError("Value is Required");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (typePersonal.isEmpty()) {
                personalNameLayout.setErrorEnabled(false);
                personalNameLayout.setError(null);
                personalValueLayout.setErrorEnabled(false);
                personalValueLayout.setError(null);
                personalTypeLayout.setErrorEnabled(true);
                personalTypeLayout.setError("Type is Required");
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                personalNameLayout.setErrorEnabled(false);
                personalNameLayout.setError(null);
                personalValueLayout.setErrorEnabled(false);
                personalValueLayout.setError(null);
                personalTypeLayout.setErrorEnabled(false);
                personalTypeLayout.setError(null);

                AdminData adminData = new AdminData();
                adminData.setPersonalName(namePersonal);
                adminData.setPersonalValue(valuePersonal);
                adminData.setPersonalType(typePersonal);

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
                        dialog.dismiss();
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                        StyleableToast.makeText(view.getContext(), "Personal details read error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                    }
                });

            }
        }
    }

}