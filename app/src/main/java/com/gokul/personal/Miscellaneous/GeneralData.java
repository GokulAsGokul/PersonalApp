package com.gokul.personal.Miscellaneous;

import java.util.Comparator;

public class GeneralData {

    private String Name, Status, AppVersionNo, AppName, AppUrl, AppExtension, AppVersionNote, FileName, FileCategory, FileUrl, FileExtension;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getAppVersionNo() {
        return AppVersionNo;
    }

    public void setAppVersionNo(String appVersionNo) {
        AppVersionNo = appVersionNo;
    }

    public String getAppName() {
        return AppName;
    }

    public void setAppName(String appName) {
        AppName = appName;
    }

    public String getAppUrl() {
        return AppUrl;
    }

    public void setAppUrl(String appUrl) {
        AppUrl = appUrl;
    }

    public String getAppExtension() {
        return AppExtension;
    }

    public void setAppExtension(String appExtension) {
        AppExtension = appExtension;
    }

    public String getAppVersionNote() {
        return AppVersionNote;
    }

    public void setAppVersionNote(String appVersionNote) {
        AppVersionNote = appVersionNote;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileCategory() {
        return FileCategory;
    }

    public void setFileCategory(String fileCategory) {
        FileCategory = fileCategory;
    }

    public String getFileUrl() {
        return FileUrl;
    }

    public void setFileUrl(String fileUrl) {
        FileUrl = fileUrl;
    }

    public String getFileExtension() {
        return FileExtension;
    }

    public void setFileExtension(String fileExtension) {
        FileExtension = fileExtension;
    }

    public static Comparator<GeneralData> docComparatorAZ = (g1, g2) -> g1.getFileName().compareToIgnoreCase(g2.getFileName());

    public static Comparator<GeneralData> docComparatorZA = (g1, g2) -> g2.getFileName().compareToIgnoreCase(g1.getFileName());

    public static Comparator<GeneralData> docComparatorCategory = (g1, g2) -> g1.getFileCategory().compareToIgnoreCase(g2.getFileCategory());

    public static Comparator<GeneralData> docComparatorType = (g1, g2) -> g1.getFileExtension().compareToIgnoreCase(g2.getFileExtension());

}
