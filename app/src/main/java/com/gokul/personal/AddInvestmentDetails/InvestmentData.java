package com.gokul.personal.AddInvestmentDetails;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Objects;

public class InvestmentData {

    private String Name, Amount, FolioNumber, Term, Source, Date, Remarks, UpdateFinder;

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

    public String getFolioNumber() {
        return FolioNumber;
    }

    public void setFolioNumber(String folioNumber) {
        FolioNumber = folioNumber;
    }

    public String getTerm() {
        return Term;
    }

    public void setTerm(String term) {
        Term = term;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
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

    public static Comparator<InvestmentData> docComparatorLatest = (i1, i2) -> {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return String.valueOf(Objects.requireNonNull(dateFormat.parse(i2.getDate())).getTime()).compareToIgnoreCase(
                    String.valueOf(Objects.requireNonNull(dateFormat.parse(i1.getDate())).getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    };

    public static Comparator<InvestmentData> docComparatorOldest = (i1, i2) -> {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return String.valueOf(Objects.requireNonNull(dateFormat.parse(i1.getDate())).getTime()).compareToIgnoreCase(
                    String.valueOf(Objects.requireNonNull(dateFormat.parse(i2.getDate())).getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    };

}
