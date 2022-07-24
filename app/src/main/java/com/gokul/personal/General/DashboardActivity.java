package com.gokul.personal.General;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.gokul.personal.AddBalances.AddBalanceDetailsActivity;
import com.gokul.personal.AddLent.AddLentDetailsActivity;
import com.gokul.personal.AppUpload.UploadAppActivity;
import com.gokul.personal.R;
import com.gokul.personal.UploadDocuments.UploadDocumentsActivity;
import com.gokul.personal.ViewDocuments.ViewDocumentsActivity;
import com.gokul.personal.ViewPersonalDetails.ViewPersonalDetailsActivity;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class DashboardActivity extends AppCompatActivity {

    private FloatingActionsMenu fabBtn;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.background)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        fabBtn = findViewById(R.id.fabBtn);
        FloatingActionButton addLendBtn = findViewById(R.id.addLendBtn);
        FloatingActionButton addBalBtn = findViewById(R.id.addBalBtn);
        FloatingActionButton addDocBtn = findViewById(R.id.addDocBtn);
        FloatingActionButton addAppBtn = findViewById(R.id.addAppBtn);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View headerView = navigationView.getHeaderView(0);
        headerView.findViewById(R.id.navDraProfile).setOnClickListener(view -> startActivity(new Intent(DashboardActivity.this, AdminActivity.class)));
        headerView.findViewById(R.id.headerLayout2).setOnClickListener(view -> startActivity(new Intent(DashboardActivity.this, ViewPersonalDetailsActivity.class)));

        PackageInfo pInfo = null;
        try {
            pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            StyleableToast.makeText(DashboardActivity.this, "App Version Read Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        }
        assert pInfo != null;
        String appCurrentVersionName = pInfo.versionName;

        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.navAppVer).setTitle("App Version: " + appCurrentVersionName);

        navigationView.setNavigationItemSelectedListener((MenuItem item) -> {
            if (item.getItemId() == R.id.navAddApp) {
                startActivity(new Intent(DashboardActivity.this, UploadAppActivity.class));
            } else if (item.getItemId() == R.id.navAddDoc) {
                startActivity(new Intent(DashboardActivity.this, UploadDocumentsActivity.class));
            } else if (item.getItemId() == R.id.navAddBal) {
                startActivity(new Intent(DashboardActivity.this, AddBalanceDetailsActivity.class));
            } else if (item.getItemId() == R.id.navAddLend) {
                startActivity(new Intent(DashboardActivity.this, AddLentDetailsActivity.class));
            } else if (item.getItemId() == R.id.navViewDash) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (item.getItemId() == R.id.navViewDoc) {
                startActivity(new Intent(DashboardActivity.this, ViewDocumentsActivity.class));
            } else if (item.getItemId() == R.id.navViewBal) {
                startActivity(new Intent(DashboardActivity.this, ViewBalanceDetailsActivity.class));
            } else if (item.getItemId() == R.id.navViewLent) {
                Toast.makeText(DashboardActivity.this, "View Lent Clicked", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        addLendBtn.setOnClickListener(view -> {
            fabBtn.collapse();
            startActivity(new Intent(DashboardActivity.this, AddLentDetailsActivity.class));
        });

        addBalBtn.setOnClickListener(view -> {
            fabBtn.collapse();
            startActivity(new Intent(DashboardActivity.this, AddBalanceDetailsActivity.class));
        });

        addDocBtn.setOnClickListener(view -> {
            fabBtn.collapse();
            startActivity(new Intent(DashboardActivity.this, UploadDocumentsActivity.class));
        });

        addAppBtn.setOnClickListener(view -> {
            fabBtn.collapse();
            startActivity(new Intent(DashboardActivity.this, UploadAppActivity.class));
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (fabBtn.isExpanded()) {
                Rect outRect = new Rect();
                fabBtn.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    fabBtn.collapse();
                    return false;
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

}
