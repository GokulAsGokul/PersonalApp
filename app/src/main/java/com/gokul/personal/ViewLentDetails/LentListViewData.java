package com.gokul.personal.ViewLentDetails;

import java.util.Comparator;

public class LentListViewData {

    String name, remAmount;

    public LentListViewData(String name, String remAmount) {
        this.name = name;
        this.remAmount = remAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemAmount() {
        return remAmount;
    }

    public void setRemAmount(String remAmount) {
        this.remAmount = remAmount;
    }

    public static Comparator<LentListViewData> listSorter = (l1, l2) -> l1.getName().compareToIgnoreCase(l2.getName());

}
