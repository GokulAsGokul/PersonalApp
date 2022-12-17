package com.gokul.personal.Dashboard;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.gokul.personal.AddBalances.AddBalanceDetailsActivity;
import com.gokul.personal.AddInvestmentDetails.AddInvestmentDetailsActivity;
import com.gokul.personal.AddLent.AddLentDetailsActivity;
import com.gokul.personal.AdminActivity.AdminActivity;
import com.gokul.personal.AdminActivity.AdminData;
import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.PersonalBoard.PersonalBoardActivity;
import com.gokul.personal.R;
import com.gokul.personal.UploadDocuments.UploadDocumentsActivity;
import com.gokul.personal.ViewBalance.ViewBalanceDetailsActivity;
import com.gokul.personal.ViewDocuments.ViewDocumentsActivity;
import com.gokul.personal.ViewInvestmentDetails.ViewInvestmentDetailsActivity;
import com.gokul.personal.ViewLentDetails.ViewLentDetailsActivity;
import com.gokul.personal.ViewPersonalDetails.ViewPersonalDetailsActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.ViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class DashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String appName, appURL, appCurrentVersionName;
    private TextView downloadDialogText1, downloadDialogText2;
    private long downloadID;
    private AppCompatButton installBtn;
    private ProgressBar downloadDialogBar;
    private Dialog dialog;
    private Vibrator vibrator;
    private TextView availableAmount, availableAmountText, lentAmount, lentAmountText, investmentAmount, investmentAmountText, totalAmount, totalAmountText,
            availableAmountOM, availableAmountTextOM, lentAmountOM, lentAmountTextOM, totalAmountOM, totalAmountTextOM;
    private int totalLentAmount, totalAvailableAmount, totalInvestmentAmount;
    private double totalAvailableAmountOM, totalLentAmountOM;
    private ConstraintLayout balanceCardLayout, balanceCardLayoutOM;
    private LottieAnimationView animationView;
    private RecyclerView recyclerView;

    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadID == id) {
                downloadDialogText1.setText(R.string.download_completed);
                downloadDialogText2.setVisibility(View.GONE);
                downloadDialogBar.setVisibility(View.GONE);
                installBtn.setVisibility(View.VISIBLE);
                installAPK();
                installBtn.setOnClickListener(v -> installAPK());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.background)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);

        View headerView = navigationView.getHeaderView(0);
        headerView.findViewById(R.id.navDraProfile).setOnClickListener(view -> {

//            Executor executor = ContextCompat.getMainExecutor(this);
//
//            BiometricPrompt prompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
//                @Override
//                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
//                    super.onAuthenticationError(errorCode, errString);
//                    StyleableToast.makeText(DashboardActivity.this, "Please place registered finger on sensor", R.style.CustomErrorToast);
//                }
//
//                @Override
//                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
//                    super.onAuthenticationSucceeded(result);
//                    startActivity(new Intent(DashboardActivity.this, AdminActivity.class));
//                }
//
//                @Override
//                public void onAuthenticationFailed() {
//                    super.onAuthenticationFailed();
//                    StyleableToast.makeText(DashboardActivity.this, "Please place registered finger on sensor", R.style.CustomErrorToast);
//                }
//            });
//
//            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Admin Activity Authentication")
//                    .setNegativeButtonText("Cancel").setConfirmationRequired(false).build();
//
//            prompt.authenticate(promptInfo);

            startActivity(new Intent(DashboardActivity.this, AdminActivity.class));

        });

        headerView.findViewById(R.id.headerLayout2).setOnClickListener(view -> {

//            Executor executor = ContextCompat.getMainExecutor(this);
//
//            BiometricPrompt prompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
//                @Override
//                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
//                    super.onAuthenticationError(errorCode, errString);
//                    StyleableToast.makeText(DashboardActivity.this, "Please place registered finger on sensor", R.style.CustomErrorToast);
//                }
//
//                @Override
//                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
//                    super.onAuthenticationSucceeded(result);
//                    startActivity(new Intent(DashboardActivity.this, ViewPersonalDetailsActivity.class));
//                }
//
//                @Override
//                public void onAuthenticationFailed() {
//                    super.onAuthenticationFailed();
//                    StyleableToast.makeText(DashboardActivity.this, "Please place registered finger on sensor", R.style.CustomErrorToast);
//                }
//            });
//
//            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Personal Data View Authentication")
//                    .setNegativeButtonText("Cancel").setConfirmationRequired(false).build();
//
//            prompt.authenticate(promptInfo);

            startActivity(new Intent(DashboardActivity.this, ViewPersonalDetailsActivity.class));

        });

        PackageInfo pInfo = null;
        try {
            pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(DashboardActivity.this, "App Version Read Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        }
        assert pInfo != null;
        appCurrentVersionName = pInfo.versionName;

        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.navAppVer).setTitle("App Version: " + appCurrentVersionName);

        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        navigationView.setNavigationItemSelectedListener((MenuItem item) -> {
            if (item.getItemId() == R.id.navAddDoc) {
                startActivity(new Intent(DashboardActivity.this, UploadDocumentsActivity.class));
            } else if (item.getItemId() == R.id.navAddBal) {
                startActivity(new Intent(DashboardActivity.this, AddBalanceDetailsActivity.class));
            } else if (item.getItemId() == R.id.navAddInvest) {
                startActivity(new Intent(DashboardActivity.this, AddInvestmentDetailsActivity.class));
            } else if (item.getItemId() == R.id.navAddLend) {
                startActivity(new Intent(DashboardActivity.this, AddLentDetailsActivity.class));
            } else if (item.getItemId() == R.id.navAddPers) {
                startActivity(new Intent(DashboardActivity.this, PersonalBoardActivity.class));
            } else if (item.getItemId() == R.id.navViewDoc) {
                startActivity(new Intent(DashboardActivity.this, ViewDocumentsActivity.class));
            } else if (item.getItemId() == R.id.navViewBal) {
                startActivity(new Intent(DashboardActivity.this, ViewBalanceDetailsActivity.class));
            } else if (item.getItemId() == R.id.navViewLent) {
                startActivity(new Intent(DashboardActivity.this, ViewLentDetailsActivity.class));
            } else if (item.getItemId() == R.id.navViewInvest) {
                startActivity(new Intent(DashboardActivity.this, ViewInvestmentDetailsActivity.class));
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
                dialog.show();
                appVersionChecker();
            }
            return true;
        });

        balanceCardLayout = findViewById(R.id.balanceCardLayout1);
        availableAmount = findViewById(R.id.availableAmount);
        availableAmountText = findViewById(R.id.availableAmountText);
        lentAmount = findViewById(R.id.lentAmount);
        lentAmountText = findViewById(R.id.lentAmountText);
        investmentAmount = findViewById(R.id.investmentAmount);
        investmentAmountText = findViewById(R.id.investmentAmountText);
        totalAmount = findViewById(R.id.totalAmount);
        totalAmountText = findViewById(R.id.totalAmountText);
        balanceCardLayoutOM = findViewById(R.id.balanceCardLayoutOM);
        availableAmountOM = findViewById(R.id.availableAmountOM);
        availableAmountTextOM = findViewById(R.id.availableAmountTextOM);
        lentAmountOM = findViewById(R.id.lentAmountOM);
        lentAmountTextOM = findViewById(R.id.lentAmountTextOM);
        totalAmountOM = findViewById(R.id.totalAmountOM);
        totalAmountTextOM = findViewById(R.id.totalAmountTextOM);

        balanceCardLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        animationView = findViewById(R.id.dashAnimation);
        animationView.setVisibility(View.VISIBLE);

        vibrator = (Vibrator) getSystemService((VIBRATOR_SERVICE));

        dialog = new Dialog(DashboardActivity.this, android.R.style.Theme_DeviceDefault_NoActionBar);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
            dialog.getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));
        }

        if (ConnectionCheck.getConnectionType(getApplicationContext()) != 0) {
            dialog.show();
        }

        recyclerView = findViewById(R.id.dashDetail);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        bankDetails();

        indiaAvailableAmountCall();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
        bankDetails();
        totalLentAmount = 0;
        totalAvailableAmount = 0;
        totalInvestmentAmount = 0;
        totalAvailableAmountOM = 0;
        totalLentAmountOM = 0;
        indiaAvailableAmountCall();
    }

    public void expand1(View view) {
        int v = (availableAmount.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
        TransitionManager.beginDelayedTransition(balanceCardLayout, new AutoTransition());
        availableAmount.setVisibility(v);
        availableAmountText.setVisibility(v);
        lentAmount.setVisibility(v);
        lentAmountText.setVisibility(v);
        investmentAmount.setVisibility(v);
        investmentAmountText.setVisibility(v);
        totalAmount.setVisibility(v);
        totalAmountText.setVisibility(v);
    }

    public void expand2(View view) {
        int v = (availableAmountOM.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
        TransitionManager.beginDelayedTransition(balanceCardLayoutOM, new AutoTransition());
        availableAmountOM.setVisibility(v);
        availableAmountTextOM.setVisibility(v);
        lentAmountOM.setVisibility(v);
        lentAmountTextOM.setVisibility(v);
        totalAmountOM.setVisibility(v);
        totalAmountTextOM.setVisibility(v);
    }

    private void bankDetails() {
        ArrayList<AdminData> list = new ArrayList<>();
        DashboardRecyclerAdapter adapter = new DashboardRecyclerAdapter(this, list, DashboardActivity.this);

        databaseReference.child("Bank Details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    AdminData data1 = dataSnapshot.getValue(AdminData.class);
                    list.add(data1);
                    dialog.dismiss();
                }
                adapter.notifyDataSetChanged();
                if (list.isEmpty()) {
                    animationView.setVisibility(View.VISIBLE);
                    animationView.playAnimation();
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    animationView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(DashboardActivity.this, "Bank detail read error: " + error.getMessage(), R.style.CustomErrorToast, Toast.LENGTH_LONG);
            }
        });

        recyclerView.setAdapter(adapter);
        ScrollingPagerIndicator pagerIndicator = findViewById(R.id.dashDetailIndicator);
        pagerIndicator.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void appVersionChecker() {

        if (ConnectionCheck.getConnectionType(getApplicationContext()) == 0) {
            dialog.dismiss();
            ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
        } else {

            databaseReference.orderByChild("appExtension").equalTo("apk").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        dialog.dismiss();
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                        StyleableToast.makeText(DashboardActivity.this, "App details not available", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                    } else {
                        databaseReference.child("Latest AppData").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                appName = Objects.requireNonNull(snapshot.child("appName").getValue()).toString();
                                appURL = Objects.requireNonNull(snapshot.child("appUrl").getValue()).toString();
                                String appNewVersionName = Objects.requireNonNull(snapshot.child("appVersionNo").getValue()).toString();

                                if (!appCurrentVersionName.equals(appNewVersionName)) {
                                    DialogPlusBuilder dialogPlusBuilder = DialogPlus.newDialog(DashboardActivity.this);
                                    dialogPlusBuilder.setGravity(Gravity.CENTER);
                                    dialogPlusBuilder.setMargin(150, 0, 150, 0);
                                    dialogPlusBuilder.setContentHolder(new ViewHolder(R.layout.app_update));
                                    dialogPlusBuilder.setExpanded(false);
                                    DialogPlus dialogPlus = dialogPlusBuilder.create();
                                    View view1 = dialogPlus.getHolderView();
                                    Button downloadAppBtn = view1.findViewById(R.id.downloadAppBtn);
                                    dialog.dismiss();
                                    dialogPlus.show();
                                    downloadAppBtn.setOnClickListener(v -> {
                                        if (ConnectionCheck.getConnectionType(DashboardActivity.this) == 0) {
                                            dialogPlus.dismiss();
                                            ConnectionCheck.connectionDialogShow(DashboardActivity.this, DashboardActivity.this);
                                        } else {
                                            downloadAppCall(appURL);
                                            dialogPlus.dismiss();
                                        }
                                    });
                                } else {
                                    dialog.dismiss();
                                    StyleableToast.makeText(DashboardActivity.this, "You are using updated app", Toast.LENGTH_LONG, R.style.CustomToast).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                dialog.dismiss();
                                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                                StyleableToast.makeText(DashboardActivity.this, "App Version Read Error: ", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(DashboardActivity.this, "App details read error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                }
            });

        }
    }

    private void downloadAppCall(String url) {
        Dialog dialog = new Dialog(DashboardActivity.this);
        dialog.setContentView(R.layout.downloading_app_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        downloadDialogBar = dialog.findViewById(R.id.downloadDialogBar);
        downloadDialogText1 = dialog.findViewById(R.id.downloadDialog1);
        downloadDialogText2 = dialog.findViewById(R.id.downloadDialog2);
        dialog.findViewById(R.id.downloadDialog3).setVisibility(View.GONE);
        installBtn = dialog.findViewById(R.id.installBtn);
        installBtn.setVisibility(View.GONE);
        dialog.show();
        downloadApp(url);
    }

    @SuppressLint({"Range", "SetTextI18n"})
    private void downloadApp(String url) {
        DownloadManager downloadManager = (DownloadManager) DashboardActivity.this.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri).setTitle("Personal App Download")
                .setDescription(appName + ".apk is downloading").setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/PersonalApp/LatestApp/" + appName + ".apk");
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
                runOnUiThread(() -> {
                    downloadDialogText2.setText((bytes_downloaded / (1024L * 1024L)) + " MB of " + (bytes_total / (1024L * 1024L)) + " MB");
                    downloadDialogBar.setProgress(dl_progress);
                });
                cursor.close();
            }
        }).start();
    }

    private void installAPK() {

        File file = new File(Environment.getExternalStorageDirectory() + "/Download/PersonalApp/LatestApp/" + appName + ".apk");
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uriFromFile(getApplicationContext(), new File(file.getAbsolutePath())), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                getApplicationContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(DashboardActivity.this, "Error in apk install: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        } else {
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(DashboardActivity.this, "New apk file not available", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        }
    }

    private Uri uriFromFile(Context context, File file) {
        return FileProvider.getUriForFile(context, "com.gokul.personal.provider", file);
    }

    private void indiaAvailableAmountCall() {

        databaseReference.child("Available Balance Details India").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String amount = childDataSnapshot.child("amount").getValue(String.class);
                    assert amount != null;
                    totalAvailableAmount = totalAvailableAmount + Integer.parseInt(amount);
                }
                availableAmount.setText(String.valueOf(totalAvailableAmount));
                indiaLentAmountCall();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(DashboardActivity.this, "India Available Balance Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

    }

    private void indiaLentAmountCall() {

        databaseReference.child("Lent Details India").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String amount = childDataSnapshot.child("amount").getValue(String.class);
                    assert amount != null;
                    totalLentAmount = totalLentAmount + Integer.parseInt(amount);
                }
                lentAmount.setText(String.valueOf(totalLentAmount));
                indiaInvestmentCall();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(DashboardActivity.this, "India Lent Details Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

    }

    private void indiaInvestmentCall() {

        databaseReference.child("Investment Details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String amount = childDataSnapshot.child("amount").getValue(String.class);
                    assert amount != null;
                    totalInvestmentAmount = totalInvestmentAmount + Integer.parseInt(amount);
                }
                investmentAmount.setText(String.valueOf(totalInvestmentAmount));
                totalAmount.setText(String.valueOf(totalAvailableAmount + totalLentAmount + totalInvestmentAmount));
                omanAvailableAmountCall();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(DashboardActivity.this, "Lent Details Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

    }

    private void omanAvailableAmountCall() {

        databaseReference.child("Available Balance Details Oman").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String amount = childDataSnapshot.child("amount").getValue(String.class);
                    assert amount != null;
                    totalAvailableAmountOM = totalAvailableAmountOM + Double.parseDouble(amount);
                }
                @SuppressLint("DefaultLocale") String amount = String.format("%.3f", totalAvailableAmountOM);
                availableAmountOM.setText(amount);
                omanLentAmountCall();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(DashboardActivity.this, "Oman Available Balance Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

    }

    private void omanLentAmountCall() {

        databaseReference.child("Lent Details Oman").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String amount = childDataSnapshot.child("amount").getValue(String.class);
                    assert amount != null;
                    totalLentAmountOM = totalLentAmountOM + Double.parseDouble(amount);
                }
                @SuppressLint("DefaultLocale") String amount = String.format("%.3f", totalLentAmountOM);
                @SuppressLint("DefaultLocale") String amount1 = String.format("%.3f", (totalAvailableAmountOM + totalLentAmountOM));
                lentAmountOM.setText(amount);
                totalAmountOM.setText(amount1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(DashboardActivity.this, "Oman Lent Details Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });
    }

}