package com.gokul.personal.AddLent;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Objects;

public class LentData {

    private String Name, Amount, From, To, Date, Remarks, UpdateFinder;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getUpdateFinder() {
        return UpdateFinder;
    }

    public void setUpdateFinder(String updateFinder) {
        UpdateFinder = updateFinder;
    }

    public static Comparator<LentData> lentComparatorLatest = (l1, l2) -> {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return String.valueOf(Objects.requireNonNull(dateFormat.parse(l2.getDate())).getTime()).compareToIgnoreCase(
                    String.valueOf(Objects.requireNonNull(dateFormat.parse(l1.getDate())).getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    };

    public static Comparator<LentData> lentComparatorOldest = (l1, l2) -> {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return String.valueOf(Objects.requireNonNull(dateFormat.parse(l1.getDate())).getTime()).compareToIgnoreCase(
                    String.valueOf(Objects.requireNonNull(dateFormat.parse(l2.getDate())).getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    };

}
