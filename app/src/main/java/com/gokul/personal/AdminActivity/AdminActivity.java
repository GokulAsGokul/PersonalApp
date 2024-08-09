package com.gokul.personal.AdminActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.R;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.background)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Admin Panel");
        actionBar.setDisplayHomeAsUpEnabled(true);

        TabLayout tabLayout1 = findViewById(R.id.tabLayout1);
        ViewPager viewPager1 = findViewById(R.id.viewPager1);

        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);

        tabLayout1.setupWithViewPager(viewPager1);
        prepareViewPager(viewPager1);

    }

    private void prepareViewPager(ViewPager viewPager) {
        MainAdapter adapter1 = new MainAdapter(getSupportFragmentManager());
        PersonalDetailsFragment personalDetailsFragment = new PersonalDetailsFragment();
        ListValuesFragment listValuesFragment = new ListValuesFragment();
        DataFragment dataFragment = new DataFragment();
        BankDetailsFragment bankDetailsFragment = new BankDetailsFragment();
        AppUploadFragment appUploadFragment = new AppUploadFragment();

        adapter1.addFragment(personalDetailsFragment, "Personal");
        adapter1.addFragment(listValuesFragment, "List");
        adapter1.addFragment(dataFragment, "Data");
        adapter1.addFragment(bankDetailsFragment, "Bank");
        adapter1.addFragment(appUploadFragment, "App");
        viewPager.setAdapter(adapter1);
    }

    private static class MainAdapter extends FragmentPagerAdapter {

        ArrayList<Fragment> fragmentArrayList1 = new ArrayList<>();
        ArrayList<String> fragmentTitleList1 = new ArrayList<>();

        private void addFragment(Fragment fragment, String title) {
            fragmentArrayList1.add(fragment);
            fragmentTitleList1.add(title);
        }

        public MainAdapter(FragmentManager supportFragmentManager1) {
            super(supportFragmentManager1);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList1.get(position);
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            return fragmentArrayList1.get(position);
        }

        @Override
        public int getCount() {
            return fragmentArrayList1.size();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
    }

}