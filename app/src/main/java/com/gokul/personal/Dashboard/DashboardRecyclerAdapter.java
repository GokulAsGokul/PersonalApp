package com.gokul.personal.Dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gokul.personal.AdminActivity.AdminData;
import com.gokul.personal.R;

import java.util.ArrayList;

public class DashboardRecyclerAdapter extends RecyclerView.Adapter<DashboardRecyclerAdapter.MyViewHolder> {
    private final Context context;
    private final ArrayList<AdminData> list;

    public DashboardRecyclerAdapter(Context context, ArrayList<AdminData> list) {
        this.context = context;
        this.list = list;
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView bankName, cifNo, accountNo, ifscCode, micrCode, branchName;

        public MyViewHolder(View itemView) {
            super(itemView);
            bankName = itemView.findViewById(R.id.bankName);
            cifNo = itemView.findViewById(R.id.cifNo);
            accountNo = itemView.findViewById(R.id.accountNo);
            ifscCode = itemView.findViewById(R.id.ifscCode);
            micrCode = itemView.findViewById(R.id.micrCode);
            branchName = itemView.findViewById(R.id.branchName);
        }
    }

}
