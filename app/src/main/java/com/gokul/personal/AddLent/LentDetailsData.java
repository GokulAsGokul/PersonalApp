package com.gokul.personal.AddLent;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Objects;

public class LentDetailsData {

    private String Name, Amount, RemAmount, From, To, Date, Remarks, UpdateFinder;

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

    public String getRemAmount() {
        return RemAmount;
    }

    public void setRemAmount(String remAmount) {
        RemAmount = remAmount;
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

    public static Comparator<String> lentComparatorFilter = (f1, f2) -> {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
        try {
            if (!f2.equalsIgnoreCase("All")) {
                return String.valueOf(Objects.requireNonNull(dateFormat.parse(f2)).getTime()).compareToIgnoreCase(
                        String.valueOf(Objects.requireNonNull(dateFormat.parse(f1)).getTime()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    };

    public static Comparator<LentDetailsData> lentComparatorName = (l1, l2) -> l1.getName().compareToIgnoreCase(l2.getName());

    public static Comparator<LentDetailsData> lentComparatorLatest = (l1, l2) -> {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return String.valueOf(Objects.requireNonNull(dateFormat.parse(l2.getDate())).getTime()).compareToIgnoreCase(
                    String.valueOf(Objects.requireNonNull(dateFormat.parse(l1.getDate())).getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    };

    public static Comparator<LentDetailsData> lentComparatorOldest = (l1, l2) -> {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return String.valueOf(Objects.requireNonNull(dateFormat.parse(l1.getDate())).getTime()).compareToIgnoreCase(
                    String.valueOf(Objects.requireNonNull(dateFormat.parse(l2.getDate())).getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    };

    public static Comparator<LentDetailsData> lentComparatorLowest = (l1, l2) -> Double.compare(Double.parseDouble(l1.getAmount()), Double.parseDouble(l2.getAmount()));

    public static Comparator<LentDetailsData> lentComparatorHighest = (l1, l2) -> Double.compare(Double.parseDouble(l2.getAmount()), Double.parseDouble(l1.getAmount()));

    public static Comparator<LentDetailsData> lentComparatorRemAmount = (l1, l2) -> Double.compare(Double.parseDouble(l2.getRemAmount()), Double.parseDouble(l1.getRemAmount()));

}
