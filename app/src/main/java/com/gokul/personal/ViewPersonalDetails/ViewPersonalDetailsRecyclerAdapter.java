package com.gokul.personal.ViewPersonalDetails;

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
import com.gokul.personal.AdminActivity.AdminData;
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

public class ViewPersonalDetailsRecyclerAdapter extends RecyclerView.Adapter<ViewPersonalDetailsRecyclerAdapter.MyViewHolder> {

    private final Context context;
    private ArrayList<AdminData> list;
    private final Activity activity;
    private DialogPlus dialog;
    private TextInputEditText personalValueUpdate;
    private TextInputLayout personalValueUpdateLayout;

    public ViewPersonalDetailsRecyclerAdapter(Context context, ArrayList<AdminData> list, Activity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
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
        holder.viewPerDetText1.setText(data.getPersonalName());
        holder.viewPerDetText2.setText(data.getPersonalValue());

        holder.viewPerDetDelete.setOnClickListener(view -> {
            if (ConnectionCheck.getConnectionType(context) == 0) {
                ConnectionCheck.connectionDialogShow(context, activity);
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Delete Details");
                builder.setMessage("Are you sure want to delete this ?");

                builder.setPositiveButton("Delete", (dialog, which) -> FirebaseDatabase.getInstance().getReference("Personal Details").child(data.getPersonalName()).removeValue().
                        addOnSuccessListener(unused -> {
                            list.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            if (list.isEmpty()) {
                                LottieAnimationView animationView = activity.findViewById(R.id.detailsAnimation);
                                animationView.setVisibility(View.VISIBLE);
                                animationView.playAnimation();
                                dialog.dismiss();
                            }
                            StyleableToast.makeText(activity, "Details Deleted Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
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

        UIUtil.hideKeyboard(activity);

        String value = Objects.requireNonNull(personalValueUpdate.getText()).toString().trim();

        if (value.isEmpty()) {
            personalValueUpdateLayout.setErrorEnabled(true);
            personalValueUpdateLayout.setError("Amount is required");
        } else {
            personalValueUpdateLayout.setError(null);
            personalValueUpdateLayout.setErrorEnabled(false);

            FirebaseDatabase.getInstance().getReference("Personal Details").child(updater).child("personalValue").setValue(value).
                    addOnCompleteListener(task -> {
                        dialog.dismiss();
                        notifyDataSetChanged();
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

        TextView viewPerDetText1, viewPerDetText2;
        ImageView viewPerDetDelete, viewPerDetEdit;

        public MyViewHolder(View itemView) {
            super(itemView);
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
