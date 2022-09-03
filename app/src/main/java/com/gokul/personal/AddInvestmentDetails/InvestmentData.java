package com.gokul.personal.AddInvestmentDetails;

import com.gokul.personal.AddLent.LentData;

import java.util.Comparator;

public class InvestmentData {

    private String Name, Amount, Frequency, Term, Source, Date, Remarks, UpdateFinder;

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

    public String getFrequency() {
        return Frequency;
    }

    public void setFrequency(String frequency) {
        Frequency = frequency;
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

    public static Comparator<InvestmentData> docComparatorLatest = (l1, l2) -> l1.getDate().compareTo(l2.getDate());

    public static Comparator<InvestmentData> docComparatorOldest = (l1, l2) -> l2.getDate().compareTo(l1.getDate());

}
