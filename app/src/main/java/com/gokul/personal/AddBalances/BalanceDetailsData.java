package com.gokul.personal.AddBalances;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Objects;

public class BalanceDetailsData {

    private String Source, Amount, TransAmount, Category, Date, Remarks, Type, Filter, UpdateFinder;

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getTransAmount() {
        return TransAmount;
    }

    public void setTransAmount(String transAmount) {
        TransAmount = transAmount;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
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

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getFilter() {
        return Filter;
    }

    public void setFilter(String filter) {
        Filter = filter;
    }

    public String getUpdateFinder() {
        return UpdateFinder;
    }

    public void setUpdateFinder(String updateFinder) {
        UpdateFinder = updateFinder;
    }

    public static Comparator<String> balComparatorFilter = (f1, f2) -> {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM_yyyy");
        try {
            return String.valueOf(Objects.requireNonNull(dateFormat.parse(f2)).getTime()).compareToIgnoreCase(
                    String.valueOf(Objects.requireNonNull(dateFormat.parse(f1)).getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    };

    public static Comparator<BalanceDetailsData> balComparatorLatest = (l1, l2) -> {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return String.valueOf(Objects.requireNonNull(dateFormat.parse(l2.getDate())).getTime()).compareToIgnoreCase(
                    String.valueOf(Objects.requireNonNull(dateFormat.parse(l1.getDate())).getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    };

    public static Comparator<BalanceDetailsData> balComparatorOldest = (l1, l2) -> {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return String.valueOf(Objects.requireNonNull(dateFormat.parse(l1.getDate())).getTime()).compareToIgnoreCase(
                    String.valueOf(Objects.requireNonNull(dateFormat.parse(l2.getDate())).getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    };

    public static Comparator<BalanceDetailsData> balComparatorCategory = (l1, l2) -> l1.getCategory().compareToIgnoreCase(l2.getCategory());

    public static Comparator<BalanceDetailsData> balComparatorMode = (l1, l2) -> l1.getType().compareToIgnoreCase(l2.getType());

    public static Comparator<BalanceDetailsData> balComparatorLowest = (l1, l2) -> Double.compare(Double.parseDouble(l1.getAmount()), Double.parseDouble(l2.getAmount()));

    public static Comparator<BalanceDetailsData> balComparatorHighest = (l1, l2) -> Double.compare(Double.parseDouble(l2.getAmount()), Double.parseDouble(l1.getAmount()));

    public static Comparator<BalanceDetailsData> balComparatorSource = (l1, l2) -> l1.getSource().compareToIgnoreCase(l2.getSource());

}
