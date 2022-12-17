package com.gokul.personal.ViewLentDetails;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.gokul.personal.AddLent.LentData;
import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

import static android.content.Context.VIBRATOR_SERVICE;

public class ViewOmanLentFragment extends Fragment {

    private ArrayList<LentData> list;
    private Dialog dialog;
    private ViewLentDetailsRecyclerAdapter adapter;
    private Vibrator vibrator;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_oman_lent, container, false);

        setHasOptionsMenu(true);

        RecyclerView recyclerView = view.findViewById(R.id.lentDetailOM);
        LottieAnimationView animationView = view.findViewById(R.id.lentAnimationOM);
        animationView.setVisibility(View.GONE);

        vibrator = (Vibrator) requireActivity().getSystemService((VIBRATOR_SERVICE));

        list = new ArrayList<>();

        ConnectionCheck.connectionDialogShow(view.getContext(), getActivity());

        dialog = new Dialog(view.getContext(), android.R.style.Theme_DeviceDefault_NoActionBar);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setStatusBarColor(ContextCompat.getColor(view.getContext(), R.color.background));
            dialog.getWindow().setNavigationBarColor(ContextCompat.getColor(view.getContext(), R.color.background));
        }
        if (ConnectionCheck.getConnectionType(view.getContext()) != 0) {
            dialog.show();
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        FirebaseDatabase.getInstance().getReference("Lent Details Oman").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    LentData data1 = dataSnapshot.getValue(LentData.class);
                    list.add(data1);
                }
                adapter.notifyDataSetChanged();
                if (list.isEmpty()) {
                    animationView.setVisibility(View.VISIBLE);
                    animationView.playAnimation();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    list.sort((l1, l2) -> {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            return String.valueOf(Objects.requireNonNull(dateFormat.parse(l2.getDate())).getTime()).compareTo(
                                    String.valueOf(Objects.requireNonNull(dateFormat.parse(l1.getDate())).getTime()));
                        } catch (ParseException e) {
                            StyleableToast.makeText(view.getContext(), "List Sort Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                        }
                        return 0;
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(view.getContext(), "Oman Lent Details Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

        adapter = new ViewLentDetailsRecyclerAdapter(view.getContext(), list, requireActivity(), animationView, vibrator, "Oman");
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        requireActivity().getMenuInflater().inflate(R.menu.view_lent_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.lentSearch);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search By Name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
        try {
            @SuppressLint("SoonBlockedPrivateApi") Field mCursorDrawableRes = TextView.class.getDeclaredField("mDrawableForCursor");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchView, R.drawable.color_cursor);
        } catch (Exception ignored) {
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void filter(String text) {
        ArrayList<LentData> temp = new ArrayList<>();
        for (LentData data : list) {
            if ((data.getName().toLowerCase()).contains(text)) {
                temp.add(data);
            }
        }
        adapter.updateList(temp);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                requireActivity().finish();
                return true;
            case R.id.sortByLatest:
                list.sort(LentData.lentComparatorLatest);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.sortByOldest:
                list.sort(LentData.lentComparatorOldest);
                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}