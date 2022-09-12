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
import androidx.appcompat.widget.SwitchCompat;
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

public class ListValuesFragment extends Fragment {

    private TextInputLayout listValueLayout, listNameLayout;
    private TextInputEditText listValue;
    private AutoCompleteTextView listName;
    private Vibrator vibrator;
    private Dialog dialog;
    final String[] listsName = {"AddBalanceCategory", "AddLentTo", "InvestmentFrequency", "OmanCategory", "OmanLentTo", "OmanSource", "PersonalTypes", "Sources", "UploadDocCategory"};
    private String valueList, nameList, listsDropDown;
    private SwitchCompat valSwitch;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_values, container, false);

        listValueLayout = view.findViewById(R.id.listValueLayout);
        listNameLayout = view.findViewById(R.id.listNameLayout);
        listValue = view.findViewById(R.id.listValue);
        listName = view.findViewById(R.id.listName);
        valSwitch = view.findViewById(R.id.valSwitch);
        Button listSubmit = view.findViewById(R.id.listSubmit);

        vibrator = (Vibrator) requireActivity().getSystemService((VIBRATOR_SERVICE));

        dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.submit_dialog);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        listNameLayout.setClickable(true);
        listName.setInputType(InputType.TYPE_NULL);
        listName.setClickable(true);
        listName.setFocusable(false);
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(view.getContext(), R.layout.dropdown_list, listsName);
        listName.setAdapter(listAdapter);
        listName.setOnItemClickListener((parent, view1, position, id) -> {
            UIUtil.hideKeyboard(requireActivity());
            listsDropDown = parent.getItemAtPosition(position).toString();
        });

        listSubmit.setOnClickListener(view1 -> {
            valueList = Objects.requireNonNull(listValue.getText()).toString().trim();
            nameList = Objects.requireNonNull(listName.getText()).toString().trim();

            if (ConnectionCheck.getConnectionType(view.getContext()) == 0) {
                ConnectionCheck.connectionDialogShow(view.getContext(), requireActivity());
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
                    addListData(view);
                }
            }
        });

        return view;
    }

    private void addListData(View view) {

        AdminData adminData = new AdminData();
        adminData.setListValue(valueList);

        dialog.show();

        if (valSwitch.isChecked()) {

            databaseReference.child("DropDown Values").child(nameList).orderByChild("listValue").equalTo(valueList).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (!snapshot.exists()) {

                        dialog.dismiss();
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                        StyleableToast.makeText(view.getContext(), "List value not available", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();

                    } else {

                        databaseReference.child("DropDown Values").child(nameList).child(valueList).removeValue().addOnCompleteListener(task -> {
                            Objects.requireNonNull(listValue.getText()).clear();
                            Objects.requireNonNull(listName.getText()).clear();
                            dialog.dismiss();
                            StyleableToast.makeText(view.getContext(), "List value removed successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();

                        }).addOnFailureListener(e -> {
                            dialog.dismiss();
                            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                            StyleableToast.makeText(view.getContext(), "List value remove error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    StyleableToast.makeText(view.getContext(), "List values read error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                }
            });

        } else {

            databaseReference.child("DropDown Values").child(nameList).orderByChild("listValue").equalTo(valueList).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {

                        dialog.dismiss();
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                        StyleableToast.makeText(view.getContext(), "List value already available", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();

                    } else {

                        databaseReference.child("DropDown Values").child(nameList).child(valueList).setValue(adminData).addOnCompleteListener(task -> {
                            Objects.requireNonNull(listValue.getText()).clear();
                            Objects.requireNonNull(listName.getText()).clear();
                            dialog.dismiss();
                            StyleableToast.makeText(view.getContext(), "List value added successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();

                        }).addOnFailureListener(e -> {
                            dialog.dismiss();
                            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                            StyleableToast.makeText(view.getContext(), "List value add error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                        });

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    StyleableToast.makeText(view.getContext(), "List values read error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                }
            });

        }

    }

}