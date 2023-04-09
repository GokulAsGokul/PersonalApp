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
import com.gokul.personal.AddLent.LentDetailsData;
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

import java.math.BigDecimal;
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
    private ArrayList<LentDetailsData> list;
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
    private String sourceDropDown, name;

    public ViewLentDetailsRecyclerAdapter(Context context, ArrayList<LentDetailsData> list, Activity activity, LottieAnimationView animationView, Vibrator vibrator, String country) {
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

        LentDetailsData data = list.get(position);
        holder.lentDetName.setText(data.getName());
        if (country.equalsIgnoreCase("India")) {
            holder.lentDetAmount.setText(String.format("Rs %s", data.getAmount()));
            holder.lentDetRemAmount.setText(String.format("Rs %s", data.getRemAmount()));
        } else {
            holder.lentDetAmount.setText(String.format("OMR %s", data.getAmount()));
            holder.lentDetRemAmount.setText(String.format("OMR %s", data.getRemAmount()));
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
                    if (country.equalsIgnoreCase("India")) {
                        sourceList.clear();
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
                    } else {
                        sourceList.clear();
                        databaseReference.child("DropDown Values").child("OmanSource").addListenerForSingleValueEvent(new ValueEventListener() {
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
                    }

                    lentReturnUpdateBtn.setOnClickListener(view2 -> {
                        String source = lentReturnSource.getText().toString();

                        loadingDialog.show();

                        if (source.isEmpty()) {
                            lentReturnSourceLayout.setErrorEnabled(true);
                            lentReturnSourceLayout.setError("Source is required");
                            loadingDialog.dismiss();
                            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            lentReturnSourceLayout.setError(null);
                            lentReturnSourceLayout.setErrorEnabled(false);
                            dialogPlus.dismiss();

                            databaseReference.child("Lent Details " + country).child(data.getUpdateFinder()).removeValue().
                                    addOnSuccessListener(unused -> {
                                        list.remove(holder.getAdapterPosition());
                                        notifyItemRemoved(holder.getAdapterPosition());
                                        availableBalanceUpdateCall(source, data.getRemAmount(), loadingDialog, "Delete");
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
                intent.putExtra("remAmount", data.getRemAmount());
                intent.putExtra("from", data.getFrom());
                intent.putExtra("to", data.getTo());
                intent.putExtra("date", data.getDate());
                intent.putExtra("remarks", data.getRemarks());
                intent.putExtra("country", country);
                intent.putExtra("updateFinder", data.getUpdateFinder());
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });

        holder.lentDetReturnBtn.setOnClickListener(view -> {
            if (ConnectionCheck.getConnectionType(context) == 0) {
                ConnectionCheck.connectionDialogShow(context, activity);
            } else {
                Intent intent = new Intent(context, AddLentReturnDetailsActivity.class);
                intent.putExtra("name", data.getName());
                intent.putExtra("country", country);
                intent.putExtra("amount", data.getRemAmount());
                intent.putExtra("updateFinder", data.getUpdateFinder());
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });

    }

    private void deleteBalanceDetails(String source, String amount, String updateFinder, Dialog loadingDialog) {
        databaseReference.child("Balance Details " + country).child(source).child(updateFinder).removeValue().
                addOnCompleteListener(task -> availableBalanceUpdateCall(source, amount, loadingDialog, "Delete All")).addOnFailureListener(e -> {
            loadingDialog.dismiss();
            StyleableToast.makeText(activity, "Balance Details Delete Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });
    }

    private void availableBalanceUpdateCall(String source, String amount, Dialog loadingDialog, String action) {

        databaseReference.child("Available Balance Details " + country).child(source).child("amount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String temp = snapshot.getValue(String.class);

                if (temp == null) {
                    availableBalanceSubmit(source, String.valueOf(BigDecimal.valueOf(Double.parseDouble(amount))), loadingDialog, action, amount);
                } else {
                    availableBalanceSubmit(source, String.valueOf(BigDecimal.valueOf(Double.parseDouble(temp)).add(BigDecimal.valueOf(Double.parseDouble(amount)))), loadingDialog, action, amount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                if (list.isEmpty()) {
                    animationView.setVisibility(View.VISIBLE);
                    animationView.playAnimation();
                }
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(activity, "Available Balance Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

    }

    private void availableBalanceSubmit(String source, String sourceAmount, Dialog loadingDialog, String action, String txnAmount) {

        BalanceDetailsData data = new BalanceDetailsData();
        data.setSource(source);
        String avlAmount;
        if (country.equalsIgnoreCase("India")) {
            avlAmount = new DecimalFormat("0.00").format(Double.parseDouble(sourceAmount));
        } else {
            avlAmount = new DecimalFormat("0.000").format(Double.parseDouble(sourceAmount));
        }
        data.setAmount(avlAmount);

        databaseReference.child("Available Balance Details " + country).child(source).setValue(data).addOnSuccessListener(task -> {
            if (action.equalsIgnoreCase("Delete")) {
                addBalanceDetails(source, txnAmount, loadingDialog, avlAmount);
            } else {
                loadingDialog.dismiss();
                StyleableToast.makeText(context, "Lent Details Deleted Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                if (list.isEmpty()) {
                    animationView.setVisibility(View.VISIBLE);
                    animationView.playAnimation();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            }
        }).addOnFailureListener(e1 -> {
            loadingDialog.dismiss();
            if (list.isEmpty()) {
                animationView.setVisibility(View.VISIBLE);
                animationView.playAnimation();
            }
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(context, "Available Balance Submit Error: " + e1.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });
    }

    private void addBalanceDetails(String source, String amount, Dialog loadingDialog, String avlAmount) {

        String date1 = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.getDefault()).format(new Date());

        BalanceDetailsData data = new BalanceDetailsData();
        data.setSource(source);
        data.setAmount(amount);
        data.setTransAmount(amount);
        data.setAvailableAmount(avlAmount);
        data.setCategory("Lent Return");
        data.setType("Income");
        data.setDate(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
        data.setRemarks("Rem Amount Returned");
        data.setUpdateFinder(date1);
        data.setFilter(new SimpleDateFormat("MM_yyyy", Locale.getDefault()).format(new Date()));

        databaseReference.child("Balance Details " + country).child(source).child(date1).setValue(data).
                addOnCompleteListener(task -> {
                    loadingDialog.dismiss();
                    StyleableToast.makeText(context, "Lent Details Deleted Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                    if (list.isEmpty()) {
                        animationView.setVisibility(View.VISIBLE);
                        animationView.playAnimation();
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    }
                }).addOnFailureListener(e -> {
            loadingDialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(context, "Balance Details Submit Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView lentDetName, lentDetAmount, lentDetRemAmount, lentDetFrom, lentDetTo, lentDetDate, lentDetRemarks, lentDetDays;
        ImageView lentDetDeleteBtn, lentDetEditBtn, lentDetReturnBtn;

        public MyViewHolder(View itemView) {
            super(itemView);
            lentDetName = itemView.findViewById(R.id.lentDetName);
            lentDetAmount = itemView.findViewById(R.id.lentDetAmount);
            lentDetRemAmount = itemView.findViewById(R.id.lentDetRemAmount);
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

    public void updateList(ArrayList<LentDetailsData> listData) {
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
