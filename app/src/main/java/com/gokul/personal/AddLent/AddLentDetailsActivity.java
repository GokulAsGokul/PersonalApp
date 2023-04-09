package com.gokul.personal.AddLent;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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

import java.util.ArrayList;
import java.util.Objects;

public class AddLentDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lending_declaration);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.background)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        TabLayout tabLayoutOM = findViewById(R.id.tabLayoutOM);
        ViewPager viewPagerOM = findViewById(R.id.viewPagerOM);

        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);

        tabLayoutOM.setupWithViewPager(viewPagerOM);
        prepareViewPager(viewPagerOM);

    }

    private void prepareViewPager(ViewPager viewPager) {
        AddLentDetailsActivity.MainAdapter adapter = new AddLentDetailsActivity.MainAdapter(getSupportFragmentManager());
        AddLentIndiaFragment lentIndiaFragment = new AddLentIndiaFragment();
        AddLentOmanFragment lentOmanFragment = new AddLentOmanFragment();
        adapter.addFragment(lentIndiaFragment);
        adapter.addFragment(lentOmanFragment);
        viewPager.setAdapter(adapter);
    }

    private class MainAdapter extends FragmentPagerAdapter {

        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
        int[] imageList = {R.drawable.ic_india_flag, R.drawable.ic_oman_flag};

        private void addFragment(Fragment fragment) {
            fragmentArrayList.add(fragment);
        }

        public MainAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentArrayList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentArrayList.size();
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), imageList[position]);
            assert drawable != null;
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            SpannableString spannableString = new SpannableString(" ");
            ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_CENTER);
            spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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