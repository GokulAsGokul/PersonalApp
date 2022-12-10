package com.gokul.personal.ViewBalance;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.gokul.personal.AddBalances.BalanceDetailsData;
import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.github.muddz.styleabletoast.StyleableToast;

public class ViewAllOmanBalanceDetailsRecyclerAdapter extends RecyclerView.Adapter<ViewAllOmanBalanceDetailsRecyclerAdapter.MyViewHolder> {
    private final Context context;
    private ArrayList<BalanceDetailsData> list;
    private final Activity activity;
    private final LottieAnimationView animationView;
    private final Vibrator vibrator;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private Dialog loadingDialog;

    public ViewAllOmanBalanceDetailsRecyclerAdapter(Context context, ArrayList<BalanceDetailsData> list, Activity activity, LottieAnimationView animationView, Vibrator vibrator) {
        this.context = context;
        this.list = list;
        this.activity = activity;
        this.animationView = animationView;
        this.vibrator = vibrator;
    }

    @NonNull
    @Override
    public ViewAllOmanBalanceDetailsRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_all_balance_details, parent, false);
        return new ViewAllOmanBalanceDetailsRecyclerAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAllOmanBalanceDetailsRecyclerAdapter.MyViewHolder holder, int position) {
        BalanceDetailsData data = list.get(position);
        holder.allBalViewSource.setText(data.getSource());
        holder.allBalViewCategory.setText(data.getCategory());
        holder.allBalViewAmount.setText(String.format("Ro %s", data.getAmount()));
        holder.allBalViewTransAmount.setText(String.format("Ro %s", data.getTransAmount()));
        holder.allBalViewMode.setText(data.getType());
        holder.allBalViewDate.setText(data.getDate());
        holder.allBalViewRemarks.setText(data.getRemarks());

        holder.allBalViewDeleteBtn.setOnClickListener(view -> {
            if (ConnectionCheck.getConnectionType(context) == 0) {
                ConnectionCheck.connectionDialogShow(context, activity);
            } else {

                loadingDialog = new Dialog(view.getContext(), android.R.style.Theme_DeviceDefault_NoActionBar);
                loadingDialog.setContentView(R.layout.loading_dialog);
                loadingDialog.setCancelable(true);
                loadingDialog.setCanceledOnTouchOutside(false);
                if (loadingDialog.getWindow() != null) {
                    loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    loadingDialog.getWindow().setStatusBarColor(ContextCompat.getColor(view.getContext(), R.color.background));
                    loadingDialog.getWindow().setNavigationBarColor(ContextCompat.getColor(view.getContext(), R.color.background));
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Delete Details");
                builder.setMessage("Are you sure want to delete this ?");

                builder.setPositiveButton("Delete", (dialog, which) -> {
                    loadingDialog.show();
                    databaseReference.child("Balance Details Oman").child(data.getSource())
                            .child(list.get(holder.getAdapterPosition()).getUpdateFinder()).removeValue().
                            addOnSuccessListener(unused -> {
                                list.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                availableBalanceUpdateCall(data.getSource(), data.getType(), data.getTransAmount(), data.getCategory(), data.getUpdateFinder(), loadingDialog);
                            }).
                            addOnFailureListener(e -> {
                                loadingDialog.dismiss();
                                StyleableToast.makeText(activity, "Details Delete Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                            });
                });
                builder.setNeutralButton("Cancel", (DialogInterface dialog, int which) -> dialog.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setBackgroundColor(Color.rgb(0, 74, 153));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.rgb(0, 74, 153));
            }

        });

    }

    private void availableBalanceUpdateCall(String balName, String balType, String amount, String category, String updateFinder, Dialog dialogCall) {

        databaseReference.child("Available Balance Details Oman").child(balName).child("amount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String temp = snapshot.getValue(String.class);

                if (balType.equalsIgnoreCase("Income")) {
                    if (temp == null) {
                        availableBalanceSubmit(balName, (String.valueOf(Double.parseDouble(amount))), category, updateFinder, dialogCall);
                    } else {
                        if (Double.parseDouble(amount) <= Double.parseDouble(temp)) {
                            availableBalanceSubmit(balName, (String.valueOf(Double.parseDouble(temp) - Double.parseDouble(amount))), category, updateFinder, dialogCall);
                        } else {
                            if (list.isEmpty()) {
                                animationView.setVisibility(View.VISIBLE);
                                animationView.playAnimation();
                            }
                            dialogCall.dismiss();
                            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                            StyleableToast.makeText(context, "Available balance is less than entered amount", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                        }
                    }
                } else {
                    if (temp == null) {
                        availableBalanceSubmit(balName, (String.valueOf(Double.parseDouble(amount))), category, updateFinder, dialogCall);
                    } else {
                        availableBalanceSubmit(balName, String.valueOf(Double.parseDouble(temp) + Double.parseDouble(amount)), category, updateFinder, dialogCall);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (list.isEmpty()) {
                    animationView.setVisibility(View.VISIBLE);
                    animationView.playAnimation();
                }
                dialogCall.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(activity, "Available Balance Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

    }

    private void availableBalanceSubmit(String balName, String sourceAmount, String category, String updateFinder, Dialog dialogCall) {
        BalanceDetailsData data = new BalanceDetailsData();
        data.setSource(balName);
        data.setAmount(sourceAmount);

        databaseReference.child("Available Balance Details Oman").child(balName).setValue(data).addOnCompleteListener(task -> {
            if (category.equals("Lent")) {
                lentDetailsDelete(updateFinder, dialogCall);
            } else if (category.equalsIgnoreCase("Investment")) {
                investmentDetailsDelete(updateFinder, dialogCall);
            } else {
                if (list.isEmpty()) {
                    animationView.setVisibility(View.VISIBLE);
                    animationView.playAnimation();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                }
                dialogCall.dismiss();
            }
        }).addOnFailureListener(e1 -> {
            if (list.isEmpty()) {
                animationView.setVisibility(View.VISIBLE);
                animationView.playAnimation();
            }
            dialogCall.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(context, "Available Balance Submit Error: " + e1.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });
    }

    private void lentDetailsDelete(String updateFinder, Dialog dialogCall) {
        databaseReference.child("Lent Details Oman").child(updateFinder).removeValue().
                addOnSuccessListener(unused -> {
                    if (list.isEmpty()) {
                        animationView.setVisibility(View.VISIBLE);
                        animationView.playAnimation();
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    }
                    dialogCall.dismiss();
                    StyleableToast.makeText(activity, "Details Deleted Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                }).
                addOnFailureListener(e -> {
                    if (list.isEmpty()) {
                        animationView.setVisibility(View.VISIBLE);
                        animationView.playAnimation();
                    }
                    dialogCall.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(activity, "Details Delete Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                });
    }

    private void investmentDetailsDelete(String updateFinder, Dialog dialogCall) {

        databaseReference.child("Investment Details").child(updateFinder).removeValue().
                addOnSuccessListener(unused -> {
                    if (list.isEmpty()) {
                        animationView.setVisibility(View.VISIBLE);
                        animationView.playAnimation();
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    }
                    dialogCall.dismiss();
                }).
                addOnFailureListener(e -> {
                    dialogCall.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(activity, "Details Delete Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView allBalViewSource, allBalViewCategory, allBalViewAmount, allBalViewTransAmount, allBalViewMode, allBalViewDate, allBalViewRemarks;
        ImageView allBalViewDeleteBtn;

        public MyViewHolder(View itemView) {
            super(itemView);
            allBalViewSource = itemView.findViewById(R.id.allallBalViewSource);
            allBalViewCategory = itemView.findViewById(R.id.allBalViewCategory);
            allBalViewAmount = itemView.findViewById(R.id.allBalViewAmount);
            allBalViewTransAmount = itemView.findViewById(R.id.allBalViewTransAmount);
            allBalViewMode = itemView.findViewById(R.id.allBalViewMode);
            allBalViewDate = itemView.findViewById(R.id.allBalViewDate);
            allBalViewRemarks = itemView.findViewById(R.id.allBalViewRemarks);
            allBalViewDeleteBtn = itemView.findViewById(R.id.allBalViewDeleteBtn);
        }
    }

    public void updateList(ArrayList<BalanceDetailsData> listData) {
        list = listData;
        notifyDataSetChanged();
    }

}
