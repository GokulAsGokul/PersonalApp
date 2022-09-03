package com.gokul.personal.AddLent;

import java.util.Comparator;

public class LentData {

    private String Name, Amount, From, To, Date, Remarks,UpdateFinder;

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

    public static Comparator<LentData> docComparatorLatest = (l1, l2) -> l1.getDate().compareTo(l2.getDate());

    public static Comparator<LentData> docComparatorOldest = (l1, l2) -> l2.getDate().compareTo(l1.getDate());
}
