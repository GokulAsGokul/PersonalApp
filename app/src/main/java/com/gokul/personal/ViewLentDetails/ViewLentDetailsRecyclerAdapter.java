package com.gokul.personal.ViewLentDetails;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
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
import com.gokul.personal.AddLent.LentData;
import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class ViewLentDetailsRecyclerAdapter extends RecyclerView.Adapter<ViewLentDetailsRecyclerAdapter.MyViewHolder> {
    private final Context context;
    private ArrayList<LentData> list;
    private final Activity activity;
    private DialogPlus dialog;
    private TextInputEditText lentUpdateAmount;
    private TextInputLayout lentUpdateAmountLayout;
    private LottieAnimationView animationView;

    public ViewLentDetailsRecyclerAdapter(Context context, ArrayList<LentData> list, Activity activity, LottieAnimationView animationView) {
        this.context = context;
        this.list = list;
        this.activity = activity;
        this.animationView = animationView;
    }

    @NonNull
    @Override
    public ViewLentDetailsRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_lent_details_view, parent, false);
        return new ViewLentDetailsRecyclerAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewLentDetailsRecyclerAdapter.MyViewHolder holder, int position) {
        LentData data = list.get(position);
        holder.lentDetName.setText(data.getName());
        holder.lentDetAmount.setText(data.getAmount());
        holder.lentDetFrom.setText(data.getFrom());
        holder.lentDetTo.setText(data.getTo());
        holder.lentDetDate.setText(data.getDate());
        holder.lentDetRemarks.setText(data.getRemarks());
        try {
            calculateDays(data.getDate(), holder);
        } catch (ParseException e) {
            Toast.makeText(context, "No of Days Calculation Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        holder.lentDetDeleteBtn.setOnClickListener(view -> {
            if (ConnectionCheck.getConnectionType(context) == 0) {
                ConnectionCheck.connectionDialogShow(context, activity);
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Delete Details");
                builder.setMessage("Are you sure want to delete this ?");

                builder.setPositiveButton("Delete", (dialog, which) -> FirebaseDatabase.getInstance().getReference("Lent Details").child(data.getUpdateFinder()).removeValue().
                        addOnSuccessListener(unused -> {
                            list.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            if (list.isEmpty()) {
                                animationView.setVisibility(View.VISIBLE);
                                animationView.playAnimation();
                                dialog.dismiss();
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

        holder.lentDetEditBtn.setOnClickListener(view -> {
            dialog = DialogPlus.newDialog(context).setGravity(Gravity.CENTER)
                    .setMargin(50, 0, 50, 0)
                    .setContentHolder(new ViewHolder(R.layout.lent_details_updater))
                    .setExpanded(false).setCancelable(true).create();

            View view1 = dialog.getHolderView();

            lentUpdateAmountLayout = view1.findViewById(R.id.lentUpdateAmountLayout);
            lentUpdateAmount = view1.findViewById(R.id.lentUpdateAmount);
            AppCompatButton lentUpdateBtn = view1.findViewById(R.id.lentUpdateBtn);

            lentUpdateAmount.setText(data.getAmount());

            lentUpdateBtn.setOnClickListener(view2 -> updateLentDetails(data.getUpdateFinder()));

            lentUpdateAmount.setOnEditorActionListener((v, actionId, event) -> {
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

        String amount = Objects.requireNonNull(lentUpdateAmount.getText()).toString().trim();

        if (amount.isEmpty()) {
            lentUpdateAmountLayout.setErrorEnabled(true);
            lentUpdateAmountLayout.setError("Amount is required");
        } else {
            lentUpdateAmountLayout.setError(null);
            lentUpdateAmountLayout.setErrorEnabled(false);

            FirebaseDatabase.getInstance().getReference("Lent Details").child(updater).child("amount").setValue(amount).
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

        TextView lentDetName, lentDetAmount, lentDetFrom, lentDetTo, lentDetDate, lentDetRemarks, lentDetDays;
        ImageView lentDetDeleteBtn, lentDetEditBtn;

        public MyViewHolder(View itemView) {
            super(itemView);
            lentDetName = itemView.findViewById(R.id.lentDetName);
            lentDetAmount = itemView.findViewById(R.id.lentDetAmount);
            lentDetFrom = itemView.findViewById(R.id.lentDetFrom);
            lentDetTo = itemView.findViewById(R.id.lentDetTo);
            lentDetDate = itemView.findViewById(R.id.lentDetDate);
            lentDetRemarks = itemView.findViewById(R.id.lentDetRemarks);
            lentDetDays = itemView.findViewById(R.id.lentDetDays);
            lentDetDeleteBtn = itemView.findViewById(R.id.lentDetDeleteBtn);
            lentDetEditBtn = itemView.findViewById(R.id.lentDetEditBtn);

        }
    }

    public void updateList(ArrayList<LentData> listData) {
        list = listData;
        notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    private void calculateDays(String dueDate, MyViewHolder holder) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dates = new SimpleDateFormat("dd-MM-yyyy");
        Date date1 = dates.parse(dueDate);
        Date date2 = dates.parse(dates.format(Calendar.getInstance().getTime()));
        assert date1 != null;
        assert date2 != null;
        long difference = Math.abs(date1.getTime() - date2.getTime());
        long differenceDates = difference / (24 * 60 * 60 * 1000);
        holder.lentDetDays.setText(differenceDates + " Days Completed");

    }

}
