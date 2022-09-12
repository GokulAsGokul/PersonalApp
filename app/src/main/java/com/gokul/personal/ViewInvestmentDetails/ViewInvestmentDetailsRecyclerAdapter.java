package com.gokul.personal.ViewInvestmentDetails;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
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
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.gokul.personal.AddInvestmentDetails.InvestmentData;
import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.ArrayList;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class ViewInvestmentDetailsRecyclerAdapter extends RecyclerView.Adapter<ViewInvestmentDetailsRecyclerAdapter.MyViewHolder> {
    private final Context context;
    private ArrayList<InvestmentData> list;
    private final Activity activity;
    private DialogPlus dialog;
    private TextInputEditText investUpdateAmount;
    private TextInputLayout investUpdateAmountLayout;
    private final LottieAnimationView animationView;
    private final Vibrator vibrator;

    public ViewInvestmentDetailsRecyclerAdapter(Context context, ArrayList<InvestmentData> list, Activity activity, LottieAnimationView animationView, Vibrator vibrator) {
        this.context = context;
        this.list = list;
        this.activity = activity;
        this.animationView = animationView;
        this.vibrator = vibrator;
    }

    @NonNull
    @Override
    public ViewInvestmentDetailsRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_investment_details_view, parent, false);
        return new ViewInvestmentDetailsRecyclerAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewInvestmentDetailsRecyclerAdapter.MyViewHolder holder, int position) {
        InvestmentData data = list.get(position);
        holder.invDetCompany.setText(data.getName());
        holder.invDetAmount.setText(data.getAmount());
        holder.invDetFrequency.setText(data.getFrequency());
        holder.invDetTerm.setText(data.getTerm());
        holder.invDetSource.setText(data.getSource());
        holder.invDetDate.setText(data.getDate());
        holder.invDetRemarks.setText(data.getRemarks());

        holder.investDetDeleteBtn.setOnClickListener(view -> {
            if (ConnectionCheck.getConnectionType(context) == 0) {
                ConnectionCheck.connectionDialogShow(context, activity);
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Delete Details");
                builder.setMessage("Are you sure want to delete this ?");

                builder.setPositiveButton("Delete", (dialog, which) -> FirebaseDatabase.getInstance().getReference("Investment Details").child(data.getUpdateFinder()).removeValue().
                        addOnSuccessListener(unused -> {
                            list.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            if (list.isEmpty()) {
                                animationView.setVisibility(View.VISIBLE);
                                animationView.playAnimation();
                                dialog.dismiss();
                                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                StyleableToast.makeText(activity, "Details Deleted Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                            }
                        }).
                        addOnFailureListener(e ->
                                StyleableToast.makeText(activity, "Details Delete Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show()));
                builder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setBackgroundColor(Color.rgb(0, 74, 153));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.rgb(0, 74, 153));
            }

        });

        holder.investDetEditBtn.setOnClickListener(view -> {
            dialog = DialogPlus.newDialog(context).setGravity(Gravity.CENTER)
                    .setMargin(50, 0, 50, 0)
                    .setContentHolder(new ViewHolder(R.layout.investment_details_updater))
                    .setExpanded(false).setCancelable(true).create();

            View view1 = dialog.getHolderView();

            investUpdateAmountLayout = view1.findViewById(R.id.investUpdateAmountLayout);
            investUpdateAmount = view1.findViewById(R.id.investUpdateAmount);
            AppCompatButton investUpdateBtn = view1.findViewById(R.id.investUpdateBtn);

            investUpdateAmount.setText(data.getAmount());

            investUpdateBtn.setOnClickListener(view2 -> updateLentDetails(data.getUpdateFinder()));

            investUpdateAmount.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    updateLentDetails(data.getUpdateFinder());
                    return true;
                }
                return false;
            });

            dialog.show();

        });

    }

    private void updateLentDetails(String updater) {

        UIUtil.hideKeyboard(activity);

        String amount = Objects.requireNonNull(investUpdateAmount.getText()).toString().trim();

        if (amount.isEmpty()) {
            investUpdateAmountLayout.setErrorEnabled(true);
            investUpdateAmountLayout.setError("Amount is required");
        } else {
            investUpdateAmountLayout.setError(null);
            investUpdateAmountLayout.setErrorEnabled(false);

            FirebaseDatabase.getInstance().getReference("Investment Details").child(updater).child("amount").setValue(amount).
                    addOnCompleteListener(task -> {
                        dialog.dismiss();
                        StyleableToast.makeText(context, "Updated Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                    }).addOnFailureListener(e -> {
                dialog.dismiss();
                StyleableToast.makeText(context, "Updated Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView invDetCompany, invDetAmount, invDetFrequency, invDetTerm, invDetSource, invDetDate, invDetRemarks;
        ImageView investDetEditBtn, investDetDeleteBtn;

        public MyViewHolder(View itemView) {
            super(itemView);
            invDetCompany = itemView.findViewById(R.id.invDetCompany);
            invDetAmount = itemView.findViewById(R.id.invDetAmount);
            invDetFrequency = itemView.findViewById(R.id.invDetFrequency);
            invDetTerm = itemView.findViewById(R.id.invDetTerm);
            invDetSource = itemView.findViewById(R.id.invDetSource);
            invDetDate = itemView.findViewById(R.id.invDetDate);
            invDetRemarks = itemView.findViewById(R.id.invDetRemarks);
            investDetEditBtn = itemView.findViewById(R.id.investDetEditBtn);
            investDetDeleteBtn = itemView.findViewById(R.id.investDetDeleteBtn);

        }
    }

    public void updateList(ArrayList<InvestmentData> listData) {
        list = listData;
        notifyDataSetChanged();
    }

}
