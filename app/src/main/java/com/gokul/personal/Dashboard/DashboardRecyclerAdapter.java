package com.gokul.personal.Dashboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gokul.personal.AdminActivity.AdminData;
import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.R;

import java.util.ArrayList;

public class DashboardRecyclerAdapter extends RecyclerView.Adapter<DashboardRecyclerAdapter.MyViewHolder> {
    private final Context context;
    private final ArrayList<AdminData> list;
    private final Activity activity;

    public DashboardRecyclerAdapter(Context context, ArrayList<AdminData> list, DashboardActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public DashboardRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_dash_details_view, parent, false);
        return new DashboardRecyclerAdapter.MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DashboardRecyclerAdapter.MyViewHolder holder, int position) {
        AdminData data = list.get(position);
        holder.bankName.setText(data.getBankName());
        holder.cifNo.setText(data.getBankCIF());
        holder.accountNo.setText(data.getBankAccount());
        holder.micrCode.setText(data.getBankMICR());
        holder.ifscCode.setText(data.getBankIFSC());
        holder.branchName.setText(data.getBankBranch());

        holder.dashShareBtn.setOnClickListener(view -> {
            if (ConnectionCheck.getConnectionType(context) == 0) {
                ConnectionCheck.connectionDialogShow(context, activity);
            } else {
                String shareTextBody = "***** *Bank Details* *****" + "\n" + "\n" +
                        "_Name_               : " + "*Gokul P*" + "\n" +
                        "_Bank Name_     : " + "*" + list.get(position).getBankName() + "*" + "\n" +
                        "_Account No_     : " + "*" + list.get(position).getBankAccount() + "*" + "\n" +
                        "_IFSC Code_       : " + "*" + list.get(position).getBankIFSC() + "*" + "\n" +
                        "_MICR Code_      : " + "*" + list.get(position).getBankMICR() + "*" + "\n" +
                        "_Branch_              : " + "*" + list.get(position).getBankBranch() + "*" + "\n" + "\n" +
                        "*********************************";
                shareContDet(position, shareTextBody);
            }
        });

    }

    private void shareContDet(int position, String shareTextBody) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Gokul's " + list.get(position).getBankName() + " Details");
        intent.putExtra(Intent.EXTRA_TEXT, shareTextBody);
        context.startActivity(Intent.createChooser(intent, "Bank Details Sharing"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView bankName, cifNo, accountNo, ifscCode, micrCode, branchName;
        ImageView dashShareBtn;

        public MyViewHolder(View itemView) {
            super(itemView);
            bankName = itemView.findViewById(R.id.bankName);
            cifNo = itemView.findViewById(R.id.cifNo);
            accountNo = itemView.findViewById(R.id.accountNo);
            ifscCode = itemView.findViewById(R.id.ifscCode);
            micrCode = itemView.findViewById(R.id.micrCode);
            branchName = itemView.findViewById(R.id.branchName);
            dashShareBtn = itemView.findViewById(R.id.dashShareBtn);
        }
    }

}
