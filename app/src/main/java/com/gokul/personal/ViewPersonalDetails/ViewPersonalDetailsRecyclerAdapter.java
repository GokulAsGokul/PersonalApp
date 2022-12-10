package com.gokul.personal.ViewPersonalDetails;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.gokul.personal.AdminActivity.AdminData;
import com.gokul.personal.Dashboard.DashboardActivity;
import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.ArrayList;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class ViewPersonalDetailsRecyclerAdapter extends RecyclerView.Adapter<ViewPersonalDetailsRecyclerAdapter.MyViewHolder> {

    private final Context context;
    private ArrayList<AdminData> list;
    private final Activity activity;
    private DialogPlus dialog;
    private TextInputEditText personalValueUpdate;
    private TextInputLayout personalValueUpdateLayout;
    private Dialog loadingDialog;
    private final Vibrator vibrator;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public ViewPersonalDetailsRecyclerAdapter(Context context, ArrayList<AdminData> list, Activity activity, Vibrator vibrator) {
        this.context = context;
        this.list = list;
        this.activity = activity;
        this.vibrator = vibrator;
    }

    @NonNull
    @Override
    public ViewPersonalDetailsRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_personal_details_view, parent, false);
        return new ViewPersonalDetailsRecyclerAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPersonalDetailsRecyclerAdapter.MyViewHolder holder, int position) {
        AdminData data = list.get(position);
        holder.viewPersonalType.setText(data.getPersonalType());
        holder.viewPerDetText1.setText(data.getPersonalName());
        holder.viewPerDetText2.setText(data.getPersonalValue());

        loadingDialog = new Dialog(holder.itemView.getContext(), android.R.style.Theme_DeviceDefault_NoActionBar);
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.setCancelable(true);
        loadingDialog.setCanceledOnTouchOutside(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            loadingDialog.getWindow().setStatusBarColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.background));
            loadingDialog.getWindow().setNavigationBarColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.background));
        }

        holder.viewPerDetDelete.setOnClickListener(view -> {
            if (ConnectionCheck.getConnectionType(context) == 0) {
                ConnectionCheck.connectionDialogShow(context, activity);
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Delete Details");
                builder.setMessage("Are you sure want to delete this ?");

                builder.setPositiveButton("Delete", (dialog, which) -> {
                    loadingDialog.show();
                    databaseReference.child("Personal Details").child(data.getPersonalName()).removeValue().
                            addOnSuccessListener(unused -> {
                                list.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                if (list.isEmpty()) {
                                    LottieAnimationView animationView = activity.findViewById(R.id.detailsAnimation);
                                    animationView.setVisibility(View.VISIBLE);
                                    animationView.playAnimation();
                                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                                }
                                loadingDialog.dismiss();
                                StyleableToast.makeText(activity, "Details Deleted Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                            }).
                            addOnFailureListener(e -> {
                                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                                StyleableToast.makeText(activity, "Details Delete Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                            });
                });
                builder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setBackgroundColor(Color.rgb(0, 74, 153));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.rgb(0, 74, 153));
            }

        });

        holder.viewPerDetEdit.setOnClickListener(view -> {
            dialog = DialogPlus.newDialog(context).setGravity(Gravity.CENTER)
                    .setMargin(50, 0, 50, 0)
                    .setContentHolder(new ViewHolder(R.layout.personal_details_updater))
                    .setExpanded(false).setCancelable(true).create();

            View view1 = dialog.getHolderView();

            personalValueUpdateLayout = view1.findViewById(R.id.personalValueUpdateLayout);
            personalValueUpdate = view1.findViewById(R.id.personalValueUpdate);
            AppCompatButton personalUpdateBtn = view1.findViewById(R.id.personalUpdateBtn);

            personalValueUpdate.setText(data.getPersonalValue());

            personalUpdateBtn.setOnClickListener(view2 -> updatePersonalDetails(data.getPersonalName()));

            personalValueUpdate.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    updatePersonalDetails(data.getPersonalName());
                    return true;
                }
                return false;
            });

            dialog.show();

        });

    }

    private void updatePersonalDetails(String updater) {

        loadingDialog.show();
        UIUtil.hideKeyboard(activity);

        String value = Objects.requireNonNull(personalValueUpdate.getText()).toString().trim();

        if (value.isEmpty()) {
            personalValueUpdateLayout.setErrorEnabled(true);
            personalValueUpdateLayout.setError("Amount is required");
        } else {
            personalValueUpdateLayout.setError(null);
            personalValueUpdateLayout.setErrorEnabled(false);
            dialog.dismiss();

            databaseReference.child("Personal Details").child(updater).child("personalValue").setValue(value).
                    addOnCompleteListener(task -> {
                        context.startActivity(new Intent(context, DashboardActivity.class));
                        context.startActivity(new Intent(context, ViewPersonalDetailsActivity.class));
                        loadingDialog.dismiss();
                        StyleableToast.makeText(context, "Updated Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                    }).addOnFailureListener(e -> {
                loadingDialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(context, "Updated Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            });

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView viewPerDetText1, viewPerDetText2, viewPersonalType;
        ImageView viewPerDetDelete, viewPerDetEdit;

        public MyViewHolder(View itemView) {
            super(itemView);
            viewPersonalType = itemView.findViewById(R.id.viewPersonalType);
            viewPerDetText1 = itemView.findViewById(R.id.viewPerDetText1);
            viewPerDetText2 = itemView.findViewById(R.id.viewPerDetText2);
            viewPerDetDelete = itemView.findViewById(R.id.viewPerDetDelete);
            viewPerDetEdit = itemView.findViewById(R.id.viewPerDetEdit);
        }
    }

    public void updateList(ArrayList<AdminData> listData) {
        list = listData;
        notifyDataSetChanged();
    }

}
