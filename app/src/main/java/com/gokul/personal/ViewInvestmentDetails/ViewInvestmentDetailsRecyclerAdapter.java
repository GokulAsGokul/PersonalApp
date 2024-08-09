package com.gokul.personal.ViewInvestmentDetails;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.gokul.personal.AddBalances.BalanceDetailsData;
import com.gokul.personal.AddInvestmentDetails.InvestmentData;
import com.gokul.personal.AddInvestmentDetails.MatureInvestmentDetailsActivity;
import com.gokul.personal.AddInvestmentDetails.UpdateInvestmentDetailsActivity;
import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;

import io.github.muddz.styleabletoast.StyleableToast;

public class ViewInvestmentDetailsRecyclerAdapter extends RecyclerView.Adapter<ViewInvestmentDetailsRecyclerAdapter.MyViewHolder> {
    private final Context context;
    private final Activity activity;
    private final LottieAnimationView animationView;
    private final Vibrator vibrator;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ArrayList<InvestmentData> list;
    private Dialog loadingDialog;

    public ViewInvestmentDetailsRecyclerAdapter(Context context, ArrayList<InvestmentData> list, Activity activity, LottieAnimationView animationView, Vibrator vibrator) {
        this.context = context;
        this.list = list;
        this.activity = activity;
        this.animationView = animationView;
        this.vibrator = vibrator;
    }

    @NotNull
    @Override
    public ViewInvestmentDetailsRecyclerAdapter.MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_investment_details_view, parent, false);
        return new ViewInvestmentDetailsRecyclerAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewInvestmentDetailsRecyclerAdapter.MyViewHolder holder, int position) {
        InvestmentData data = list.get(position);
        holder.invDetCompany.setText(data.getName());
        holder.invDetAmount.setText(data.getAmount());
        holder.invDetRemAmount.setText(data.getRemAmount());
        holder.invDetFolio.setText(data.getFolioNumber());
        holder.invDetTerm.setText(data.getTerm());
        holder.invDetSource.setText(data.getSource());
        holder.invDetDate.setText(data.getDate());
        holder.invDetRemarks.setText(data.getRemarks());

        loadingDialog = new Dialog(holder.itemView.getContext(), android.R.style.Theme_DeviceDefault_NoActionBar);
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.setCancelable(true);
        loadingDialog.setCanceledOnTouchOutside(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            loadingDialog.getWindow().setStatusBarColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.background));
            loadingDialog.getWindow().setNavigationBarColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.background));
        }

        holder.investDetDeleteBtn.setOnClickListener(view -> {
            if (ConnectionCheck.getConnectionType(context) == 0) {
                ConnectionCheck.connectionDialogShow(context, activity);
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Delete Details");
                builder.setMessage("Are you sure want to delete this ?");

                builder.setPositiveButton("Delete", (dialog, which) -> databaseReference.child("Investment Details").child(data.getUpdateFinder()).removeValue().
                        addOnSuccessListener(unused -> {
                            loadingDialog.show();
                            list.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            deleteBalanceDetails(data.getSource(), data.getAmount(), data.getUpdateFinder(), loadingDialog);
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
            if (ConnectionCheck.getConnectionType(context) == 0) {
                ConnectionCheck.connectionDialogShow(context, activity);
            } else {
                Intent intent = new Intent(context, UpdateInvestmentDetailsActivity.class);
                intent.putExtra("name", data.getName());
                intent.putExtra("amount", data.getAmount());
                intent.putExtra("remAmount", data.getRemAmount());
                intent.putExtra("folio", data.getFolioNumber());
                intent.putExtra("term", data.getTerm());
                intent.putExtra("source", data.getSource());
                intent.putExtra("date", data.getDate());
                intent.putExtra("remarks", data.getRemarks());
                intent.putExtra("updateFinder", data.getUpdateFinder());
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });

        holder.investDetMatureBtn.setOnClickListener(view -> {
            if (ConnectionCheck.getConnectionType(context) == 0) {
                ConnectionCheck.connectionDialogShow(context, activity);
            } else {
                Intent intent = new Intent(context, MatureInvestmentDetailsActivity.class);
                intent.putExtra("folio", data.getFolioNumber());
                intent.putExtra("amount", data.getRemAmount());
                intent.putExtra("updateFinder", data.getUpdateFinder());
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });

    }

    private void deleteBalanceDetails(String source, String amount, String updateFinder, Dialog dialogCall) {
        databaseReference.child("Balance Details India").child(source).child(updateFinder).removeValue().
                addOnCompleteListener(task -> availableBalanceUpdateCall(source, amount, dialogCall)).addOnFailureListener(e -> {
            dialogCall.dismiss();
            StyleableToast.makeText(activity, "Balance Details Delete Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });
    }

    private void availableBalanceUpdateCall(String source, String amount, Dialog dialogCall) {

        databaseReference.child("Available Balance Details India").child(source).child("amount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                String temp = snapshot.getValue(String.class);
                if (temp == null) {
                    availableBalanceSubmit(source, String.valueOf(Integer.parseInt(amount)), dialogCall);
                } else {
                    availableBalanceSubmit(source, String.valueOf(BigDecimal.valueOf(Double.parseDouble(temp)).add(BigDecimal.valueOf(Double.parseDouble(amount)))), dialogCall);
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                dialogCall.dismiss();
                if (list.isEmpty()) {
                    animationView.setVisibility(View.VISIBLE);
                    animationView.playAnimation();
                }
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(activity, "Available Balance Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

    }

    private void availableBalanceSubmit(String source, String sourceAmount, Dialog dialogCall) {

        BalanceDetailsData data = new BalanceDetailsData();
        data.setSource(source);
        data.setAmount(sourceAmount);

        databaseReference.child("Available Balance Details India").child(source).setValue(data).addOnCompleteListener(task -> {
            if (list.isEmpty()) {
                animationView.setVisibility(View.VISIBLE);
                animationView.playAnimation();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            }
            dialogCall.dismiss();
            StyleableToast.makeText(activity, "Investment Details Deleted Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
        }).addOnFailureListener(e1 -> {
            dialogCall.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(context, "Available Balance Submit Error: " + e1.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(ArrayList<InvestmentData> listData) {
        list = listData;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView invDetCompany, invDetAmount, invDetRemAmount, invDetFolio, invDetTerm, invDetSource, invDetDate, invDetRemarks;
        ImageView investDetEditBtn, investDetDeleteBtn, investDetMatureBtn;

        public MyViewHolder(View itemView) {
            super(itemView);
            invDetCompany = itemView.findViewById(R.id.invDetCompany);
            invDetAmount = itemView.findViewById(R.id.invDetAmount);
            invDetRemAmount = itemView.findViewById(R.id.invDetRemAmount);
            invDetFolio = itemView.findViewById(R.id.invDetFolio);
            invDetTerm = itemView.findViewById(R.id.invDetTerm);
            invDetSource = itemView.findViewById(R.id.invDetSource);
            invDetDate = itemView.findViewById(R.id.invDetDate);
            invDetRemarks = itemView.findViewById(R.id.invDetRemarks);
            investDetEditBtn = itemView.findViewById(R.id.investDetEditBtn);
            investDetDeleteBtn = itemView.findViewById(R.id.investDetDeleteBtn);
            investDetMatureBtn = itemView.findViewById(R.id.investDetMatureBtn);

        }
    }

}