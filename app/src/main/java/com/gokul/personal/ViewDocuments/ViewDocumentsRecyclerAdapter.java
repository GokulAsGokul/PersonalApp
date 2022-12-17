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
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.Miscellaneous.GeneralData;
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
    private final Vibrator vibrator;
    private Dialog dialog;

    public ViewDocumentsRecyclerAdapter(Context context, ArrayList<GeneralData> list, Activity activity, Vibrator vibrator) {
        this.context = context;
        this.list = list;
        this.activity = activity;
        this.vibrator = vibrator;
    }

    @NonNull
    @Override
    public ViewDocumentsRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_documents_view, parent, false);
        return new ViewDocumentsRecyclerAdapter.MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewDocumentsRecyclerAdapter.MyViewHolder holder, int position) {
        GeneralData data = list.get(position);
        holder.viewDocsCategory.setText(data.getFileCategory().toUpperCase());
        holder.viewDocsType.setText(data.getFileExtension().toUpperCase());
        holder.serialNo.setText((position + 1) + ".");
        holder.viewDocsText.setText(data.getFileName());

        holder.viewDocsDownload.setOnClickListener(view -> {
            if (ConnectionCheck.getConnectionType(context) == 0) {
                ConnectionCheck.connectionDialogShow(context, activity);
            } else {
                dialog = new Dialog(holder.itemView.getRootView().getContext());
                dialog.setContentView(R.layout.downloading_dialog);
                dialog.setCancelable(false);
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

                Dialog loadingDialog = new Dialog(view.getContext(), android.R.style.Theme_DeviceDefault_NoActionBar);
                loadingDialog.setContentView(R.layout.loading_dialog);
                loadingDialog.setCancelable(true);
                loadingDialog.setCanceledOnTouchOutside(false);
                if (loadingDialog.getWindow() != null) {
                    loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    loadingDialog.getWindow().setStatusBarColor(ContextCompat.getColor(view.getContext(), R.color.background));
                    loadingDialog.getWindow().setNavigationBarColor(ContextCompat.getColor(view.getContext(), R.color.background));
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Delete Document");
                builder.setMessage("Are you sure want to delete this ?");

                builder.setPositiveButton("Delete", (dialog, which) -> {
                    loadingDialog.show();
                    FirebaseStorage.getInstance().getReferenceFromUrl(data.getFileUrl()).delete().addOnSuccessListener(unuse -> FirebaseDatabase.getInstance().getReference("Documents Data").child(data.getFileName()).removeValue().
                            addOnSuccessListener(unused -> {
                                list.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                if (list.isEmpty()) {
                                    LottieAnimationView animationView = activity.findViewById(R.id.docsAnimation);
                                    animationView.setVisibility(View.VISIBLE);
                                    animationView.playAnimation();
                                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                                }
                                loadingDialog.dismiss();
                                StyleableToast.makeText(activity, "Document Deleted Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                            }).
                            addOnFailureListener(e -> {
                                loadingDialog.dismiss();
                                StyleableToast.makeText(activity, "Document Data Delete Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                            })).addOnFailureListener(e -> StyleableToast.makeText(activity, "Document Delete Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show());
                });
                builder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setBackgroundColor(Color.rgb(0, 74, 153));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.rgb(0, 74, 153));

            }

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView viewDocsCategory, viewDocsText, serialNo, viewDocsType;
        ImageView viewDocsDownload, viewDocsDelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            viewDocsCategory = itemView.findViewById(R.id.viewDocsCategory);
            viewDocsType = itemView.findViewById(R.id.viewDocsType);
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
                .setDestinationInExternalPublicDir(destination, "PersonalApp/Documents/" + (fileName + "." + extension));
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
                        dialog.setCancelable(true);
                    }
                });
                cursor.close();
            }
        }).start();
    }

}