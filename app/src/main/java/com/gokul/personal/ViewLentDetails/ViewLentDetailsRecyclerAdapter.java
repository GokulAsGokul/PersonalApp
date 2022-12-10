package com.gokul.personal.ViewLentDetails;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.gokul.personal.AddBalances.BalanceDetailsData;
import com.gokul.personal.AddLent.AddLentReturnDetailsActivity;
import com.gokul.personal.AddLent.LentData;
import com.gokul.personal.AddLent.UpdateLentDetailsActivity;
import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import io.github.muddz.styleabletoast.StyleableToast;

public class ViewLentDetailsRecyclerAdapter extends RecyclerView.Adapter<ViewLentDetailsRecyclerAdapter.MyViewHolder> {
    private final Context context;
    private ArrayList<LentData> list;
    private final Activity activity;
    private final LottieAnimationView animationView;
    private final Vibrator vibrator;
    private final String country;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private Dialog loadingDialog;
    private DialogPlus dialogPlus;
    private TextInputLayout lentReturnSourceLayout;
    private AutoCompleteTextView lentReturnSource;
    private final ArrayList<String> sourceList = new ArrayList<>();
    private String sourceDropDown;

    public ViewLentDetailsRecyclerAdapter(Context context, ArrayList<LentData> list, Activity activity, LottieAnimationView animationView, Vibrator vibrator, String country) {
        this.context = context;
        this.list = list;
        this.activity = activity;
        this.animationView = animationView;
        this.vibrator = vibrator;
        this.country = country;
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
        if (country.equalsIgnoreCase("India")) {
            holder.lentDetAmount.setText(String.format("Rs %s", data.getAmount()));
        } else {
            holder.lentDetAmount.setText(String.format("Ro %s", data.getAmount()));
        }
        holder.lentDetFrom.setText(data.getFrom());
        holder.lentDetTo.setText(data.getTo());
        holder.lentDetDate.setText(data.getDate());
        holder.lentDetRemarks.setText(data.getRemarks());
        try {
            calculateDays(data.getDate(), holder);
        } catch (ParseException e) {
            Toast.makeText(context, "No of Days Calculation Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        loadingDialog = new Dialog(holder.itemView.getContext(), android.R.style.Theme_DeviceDefault_NoActionBar);
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.setCancelable(true);
        loadingDialog.setCanceledOnTouchOutside(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            loadingDialog.getWindow().setStatusBarColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.background));
            loadingDialog.getWindow().setNavigationBarColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.background));
        }

        holder.lentDetDeleteBtn.setOnClickListener(view -> {
            if (ConnectionCheck.getConnectionType(context) == 0) {
                ConnectionCheck.connectionDialogShow(context, activity);
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Delete Details");
                builder.setMessage("Are you sure want to delete this ?");

                builder.setPositiveButton("Delete", (dialog, which) -> {
                    dialogPlus = DialogPlus.newDialog(context).setGravity(Gravity.CENTER)
                            .setMargin(50, 0, 50, 0)
                            .setContentHolder(new ViewHolder(R.layout.lent_details_delete_updater))
                            .setExpanded(false).setCancelable(true).create();

                    View view1 = dialogPlus.getHolderView();
                    dialogPlus.show();
                    lentReturnSourceLayout = view1.findViewById(R.id.lentReturnSourceLayout);
                    lentReturnSource = view1.findViewById(R.id.lentReturnSource);
                    AppCompatButton lentReturnUpdateBtn = view1.findViewById(R.id.lentReturnUpdateBtn);

                    lentReturnSource.setInputType(InputType.TYPE_NULL);
                    lentReturnSource.setClickable(true);
                    lentReturnSourceLayout.setClickable(true);
                    lentReturnSource.setFocusable(false);
                    databaseReference.child("DropDown Values").child("IndiaSource").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                                String temp = childDataSnapshot.child("listValue").getValue(String.class);
                                loadingDialog.show();
                                sourceList.add(temp);
                            }
                            ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(context, R.layout.dropdown_list, sourceList);
                            sourceAdapter.sort(Comparator.naturalOrder());
                            lentReturnSource.setAdapter(sourceAdapter);
                            loadingDialog.dismiss();
                            lentReturnSource.setOnItemClickListener((parent, view, position, id) -> {
                                UIUtil.hideKeyboard(activity);
                                sourceDropDown = parent.getItemAtPosition(position).toString();
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            loadingDialog.dismiss();
                            StyleableToast.makeText(context, "Source Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                        }
                    });

                    lentReturnUpdateBtn.setOnClickListener(view2 -> {
                        String source = lentReturnSource.getText().toString();

                        if (source.isEmpty()) {
                            lentReturnSourceLayout.setErrorEnabled(true);
                            lentReturnSourceLayout.setError("Source is required");
                        } else {
                            lentReturnSourceLayout.setError(null);
                            lentReturnSourceLayout.setErrorEnabled(false);
                            dialogPlus.dismiss();
                            loadingDialog.show();

                            databaseReference.child("Lent Details " + country).child(data.getUpdateFinder()).removeValue().
                                    addOnSuccessListener(unused -> {
                                        list.remove(holder.getAdapterPosition());
                                        notifyItemRemoved(holder.getAdapterPosition());
                                        addBalanceDetails(data.getFrom(), data.getAmount(), loadingDialog);
                                    }).
                                    addOnFailureListener(e -> {
                                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                                        StyleableToast.makeText(activity, "Lent Details Delete Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                                    });
                        }
                    });
                });
                builder.setNeutralButton("Delete All", (dialog, which) -> {
                    loadingDialog.show();
                    databaseReference.child("Lent Details " + country).child(data.getUpdateFinder()).removeValue().
                            addOnSuccessListener(unused -> {
                                list.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                deleteBalanceDetails(data.getFrom(), data.getAmount(), data.getUpdateFinder(), loadingDialog);
                            }).
                            addOnFailureListener(e -> {
                                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                                StyleableToast.makeText(activity, "Lent Details Delete Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                            });
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setBackgroundColor(Color.rgb(0, 74, 153));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.rgb(0, 74, 153));
            }

        });

        holder.lentDetEditBtn.setOnClickListener(view -> {
            if (ConnectionCheck.getConnectionType(context) == 0) {
                ConnectionCheck.connectionDialogShow(context, activity);
            } else {
                Intent intent = new Intent(context, UpdateLentDetailsActivity.class);
                intent.putExtra("name", data.getName());
                intent.putExtra("amount", data.getAmount());
                intent.putExtra("from", data.getFrom());
                intent.putExtra("to", data.getTo());
                intent.putExtra("date", data.getDate());
                intent.putExtra("remarks", data.getRemarks());
                intent.putExtra("country", country);
                intent.putExtra("updateFinder", data.getUpdateFinder());
                context.startActivity(intent);
            }
        });

        holder.lentDetReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ConnectionCheck.getConnectionType(context) == 0) {
                    ConnectionCheck.connectionDialogShow(context, activity);
                } else {
                    Intent intent = new Intent(context, AddLentReturnDetailsActivity.class);
                    intent.putExtra("country", country);
                    intent.putExtra("updateFinder", data.getUpdateFinder());
                    context.startActivity(intent);
                }
            }
        });

    }

    private void deleteBalanceDetails(String source, String amount, String updateFinder, Dialog dialogCall) {
        databaseReference.child("Balance Details " + country).child(source).child(updateFinder).removeValue().
                addOnCompleteListener(task -> availableBalanceUpdateCall(source, amount, dialogCall)).addOnFailureListener(e -> {
            dialogCall.dismiss();
            StyleableToast.makeText(activity, "Balance Details Delete Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });
    }

    private void addBalanceDetails(String source, String amount, Dialog dialogCall) {

        String date1 = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.getDefault()).format(new Date());

        BalanceDetailsData data = new BalanceDetailsData();
        data.setSource(source);
        data.setAmount(amount);
        data.setTransAmount(amount);
        data.setCategory("Lent");
        data.setType("Income");
        data.setDate(new SimpleDateFormat("dd-MM-yyyy_HH", Locale.getDefault()).format(new Date()));
        data.setRemarks("Lent Amount Returned");
        data.setUpdateFinder(date1);

        databaseReference.child("Balance Details " + country).child(source).child(date1).setValue(data).
                addOnCompleteListener(task -> availableBalanceUpdateCall(source, amount, loadingDialog)).addOnFailureListener(e -> {
            dialogCall.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(context, "Balance Details Submit Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });
    }

    private void availableBalanceUpdateCall(String source, String amount, Dialog dialogCall) {

        databaseReference.child("Available Balance Details " + country).child(source).child("amount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String temp = snapshot.getValue(String.class);

                if (country.equalsIgnoreCase("India")) {

                    if (temp == null) {
                        availableBalanceSubmit(source, String.valueOf(Integer.parseInt(amount)), dialogCall);
                    } else {
                        availableBalanceSubmit(source, String.valueOf(Integer.parseInt(temp) + Integer.parseInt(amount)), dialogCall);
                    }
                } else if (country.equalsIgnoreCase("Oman")) {

                    if (temp == null) {
                        availableBalanceSubmit(source, String.valueOf(Double.parseDouble(amount)), dialogCall);
                    } else {
                        availableBalanceSubmit(source, String.valueOf(Double.parseDouble(temp) + Double.parseDouble(amount)), dialogCall);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
        if (country.equalsIgnoreCase("India")) {
            data.setAmount(sourceAmount);
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#.000");
            data.setAmount(decimalFormat.format(Double.parseDouble(sourceAmount)));
        }

        databaseReference.child("Available Balance Details " + country).child(source).setValue(data).addOnCompleteListener(task -> {
            if (list.isEmpty()) {
                animationView.setVisibility(View.VISIBLE);
                animationView.playAnimation();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            }
            dialogCall.dismiss();
            StyleableToast.makeText(context, "Lent Details Deleted Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView lentDetName, lentDetAmount, lentDetFrom, lentDetTo, lentDetDate, lentDetRemarks, lentDetDays;
        ImageView lentDetDeleteBtn, lentDetEditBtn, lentDetReturnBtn;

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
            lentDetReturnBtn = itemView.findViewById(R.id.lentDetReturnBtn);

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
