package com.gokul.personal.ViewDocuments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.gokul.personal.General.ConnectionCheck;
import com.gokul.personal.General.GeneralData;
import com.gokul.personal.R;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import io.github.muddz.styleabletoast.StyleableToast;

public class ViewDocumentsRecyclerAdapter extends RecyclerView.Adapter<ViewDocumentsRecyclerAdapter.MyViewHolder> {
    private final Context context;
    private ArrayList<GeneralData> list;
    private long downloadID;
    private TextView downloadDialogText1, downloadDialogText2, downloadDialogText3;
    private ProgressBar downloadDialogBar;
    private final Activity activity;
    private Menu myMenu;

    public ViewDocumentsRecyclerAdapter(Context context, ArrayList<GeneralData> list, Activity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewDocumentsRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_documents_view, parent, false);
        return new ViewDocumentsRecyclerAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewDocumentsRecyclerAdapter.MyViewHolder holder, int position) {
        GeneralData data = list.get(position);
        holder.serialNo.setText(String.valueOf(position + 1));
        holder.viewDocsText.setText(data.getFileName());

        holder.viewDocsDownload.setOnClickListener(view -> {
            if (ConnectionCheck.getConnectionType(context) == 0) {
                ConnectionCheck.connectionDialogShow(context, activity);
            } else {
                Dialog dialog = new Dialog(holder.itemView.getRootView().getContext());
                dialog.setContentView(R.layout.downloading_dialog);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(false);
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                downloadDialogBar = dialog.findViewById(R.id.downloadDialogBar);
                downloadDialogText1 = dialog.findViewById(R.id.downloadDialogText1);
                downloadDialogText2 = dialog.findViewById(R.id.downloadDialogText2);
                downloadDialogText3 = dialog.findViewById(R.id.downloadDialogText3);
                downloadDialogBar.setVisibility(View.VISIBLE);
                dialog.show();
                downloadFile(list.get(position).getFileName(), list.get(position).getFileExtension(), Environment.DIRECTORY_DOWNLOADS,
                        list.get(position).getFileUrl());
            }
        });

        holder.viewDocsDelete.setOnClickListener(view -> {
            if (ConnectionCheck.getConnectionType(context) == 0) {
                ConnectionCheck.connectionDialogShow(context, activity);
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Delete Document");
                builder.setMessage("Are you sure want to delete this ?");

                builder.setPositiveButton("Delete", (dialog, which) -> FirebaseStorage.getInstance().getReferenceFromUrl(data.getFileUrl()).delete().addOnSuccessListener(unuse -> FirebaseDatabase.getInstance().getReference("Documents Data").child(data.getFileName()).removeValue().
                        addOnSuccessListener(unused -> {
                            list.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            if (list.isEmpty()) {
                                myMenu.findItem(R.id.search).setVisible(false);
                                myMenu.findItem(R.id.sortByAZ).setVisible(false);
                                myMenu.findItem(R.id.sortByZA).setVisible(false);
                                LottieAnimationView animationView = activity.findViewById(R.id.docsAnimation);
                                animationView.setVisibility(View.VISIBLE);
                                animationView.playAnimation();
                                dialog.dismiss();
                            }
                            StyleableToast.makeText(activity, "Document Deleted Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                        }).
                        addOnFailureListener(e ->
                                StyleableToast.makeText(activity, "Document Data Delete Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show())).addOnFailureListener(e -> StyleableToast.makeText(activity, "Document Delete Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show()));
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

        TextView viewDocsText, serialNo;
        ImageView viewDocsDownload, viewDocsDelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            serialNo = itemView.findViewById(R.id.serialNo);
            viewDocsText = itemView.findViewById(R.id.viewDocsText);
            viewDocsDownload = itemView.findViewById(R.id.viewDocsDownload);
            viewDocsDelete = itemView.findViewById(R.id.viewDocsDelete);

        }
    }

    public void updateList(ArrayList<GeneralData> listData) {
        list = listData;
        notifyDataSetChanged();
    }

    @SuppressLint({"Range", "SetTextI18n"})
    private void downloadFile(String fileName, String extension, String destination, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri).setTitle("Document Download")
                .setDescription(fileName + " is downloading").setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(destination, "Personal/Documents/" + (fileName + "." + extension));
        downloadID = downloadManager.enqueue(request);

        new Thread(() -> {
            boolean downloading = true;
            while (downloading) {
                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(downloadID);
                Cursor cursor = downloadManager.query(q);
                cursor.moveToFirst();
                @SuppressLint("Range") int bytes_downloaded = cursor.getInt(cursor
                        .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                @SuppressLint("Range") int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false;
                }
                final int dl_progress = (int) ((bytes_downloaded * 100L) / bytes_total);
                activity.runOnUiThread(() -> {
                    downloadDialogText2.setText((bytes_downloaded / 1024L) + " KB of " + (bytes_total / 1024L) + " KB");
                    downloadDialogBar.setProgress(dl_progress);
                    if (bytes_downloaded == bytes_total) {
                        downloadDialogText2.setVisibility(View.GONE);
                        downloadDialogBar.setVisibility(View.GONE);
                        downloadDialogText1.setText("Completed");
                        downloadDialogText3.setText("Path: Download/Personal/Documents");
                    }
                });
                cursor.close();
            }
        }).start();
    }

}
