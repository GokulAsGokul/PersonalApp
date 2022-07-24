package com.gokul.personal.ViewPersonalDetails;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.gokul.personal.General.AdminData;
import com.gokul.personal.General.ConnectionCheck;
import com.gokul.personal.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import io.github.muddz.styleabletoast.StyleableToast;

public class ViewPersonalDetailsRecyclerAdapter extends RecyclerView.Adapter<ViewPersonalDetailsRecyclerAdapter.MyViewHolder> {

    private final Context context;
    private ArrayList<AdminData> list;
    private final Activity activity;
    private Menu myMenu;

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
        holder.perDetSerialNo.setText(String.valueOf(position + 1));
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
                                myMenu.findItem(R.id.search).setVisible(false);
                                myMenu.findItem(R.id.sortByAZ).setVisible(false);
                                myMenu.findItem(R.id.sortByZA).setVisible(false);
                                LottieAnimationView animationView = activity.findViewById(R.id.detailsAnimation);
                                animationView.setVisibility(View.VISIBLE);
                                animationView.playAnimation();
                                dialog.dismiss();
                            }
                            StyleableToast.makeText(activity, "Details Deleted Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                        }).
                        addOnFailureListener(e ->
                                StyleableToast.makeText(activity, "Details Delete Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show()));
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.rgb(0, 74, 153));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(0, 74, 153));

            }

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView perDetSerialNo, viewPerDetText1, viewPerDetText2;
        ImageView viewPerDetDelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            perDetSerialNo = itemView.findViewById(R.id.perDetSerialNo);
            viewPerDetText1 = itemView.findViewById(R.id.viewPerDetText1);
            viewPerDetText2 = itemView.findViewById(R.id.viewPerDetText2);
            viewPerDetDelete = itemView.findViewById(R.id.viewPerDetDelete);
        }
    }

    public void updateList(ArrayList<AdminData> listData) {
        list = listData;
        notifyDataSetChanged();
    }
}
