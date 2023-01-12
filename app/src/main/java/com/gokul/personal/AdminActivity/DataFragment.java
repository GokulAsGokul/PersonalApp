package com.gokul.personal.AdminActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.gokul.personal.AddBalances.BalanceDetailsData;
import com.gokul.personal.AddInvestmentDetails.InvestmentData;
import com.gokul.personal.AddLent.LentData;
import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.muddz.styleabletoast.StyleableToast;

import static android.content.Context.VIBRATOR_SERVICE;

public class DataFragment extends Fragment {

    private TextInputLayout dataTableLayout, dataTypeLayout, dataUploadNameLayout, dataUploadFileLayout;
    private AutoCompleteTextView dataTable, dataType, dataUploadName, dataUploadFile;
    private Button dataUploadButton;
    private View view;
    private Vibrator vibrator;
    private Dialog dialog;
    private File pdfFile;
    private final String[] nameList = {"All", "Balance Details India", "Balance Details Oman", "Lent Details", "Personal Details"};
    private final String[] typeList = {"As PDF", "As Excel"};
    private String tableDropDown1, tableDropDown2, tableDropDown3;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private Document document;
    private ProgressBar downloadDialogBar;
    private long downloadID;
    private final Color colorBackground = new DeviceRgb(0, 74, 153);
    private final ArrayList<String> indiaBalanceNameList = new ArrayList<>();
    private final ArrayList<String> indiaBalanceList = new ArrayList<>();
    private final ArrayList<String> omanBalanceNameList = new ArrayList<>();
    private final ArrayList<String> omanBalanceList = new ArrayList<>();

    float[] columnWidth1 = {520};
    float[] columnWidth2 = {135, 85, 128, 94, 78};
    float[] columnWidth3 = {234, 56, 76, 74, 80};
    float[] columnWidth4 = {103, 50, 90, 85, 77, 115};
    float[] columnWidth5 = {70, 50, 50, 90, 87, 53, 120};
    float[] columnWidth6 = {300};
    float[] columnWidth7 = {150, 150};
    float[] columnWidth8 = {220, 220, 80};
    private final Table table1 = new Table(columnWidth1);
    private final Table table2 = new Table(columnWidth2);
    private final Table table3 = new Table(columnWidth1);
    private final Table table4 = new Table(columnWidth2);
    private final Table table5 = new Table(columnWidth1);
    private final Table table6 = new Table(columnWidth3);
    private final Table table7 = new Table(columnWidth1);
    private final Table table8 = new Table(columnWidth3);
    private final Table table9 = new Table(columnWidth1);
    private final Table table10 = new Table(columnWidth4);
    private final Table table11 = new Table(columnWidth1);
    private final Table table12 = new Table(columnWidth4);
    private final Table table13 = new Table(columnWidth1);
    private final Table table14 = new Table(columnWidth4);
    private final Table table15 = new Table(columnWidth1);
    private final Table table16 = new Table(columnWidth4);
    private final Table table17 = new Table(columnWidth1);
    private final Table table18 = new Table(columnWidth5);
    private final Table table19 = new Table(columnWidth1);
    private final Table table20 = new Table(columnWidth5);
    private final Table table21 = new Table(columnWidth1);
    private final Table table22 = new Table(columnWidth5);
    private final Table table23 = new Table(columnWidth5);
    private final Table table24 = new Table(columnWidth1);
    private final Table table25 = new Table(columnWidth6);
    private final Table table26 = new Table(columnWidth7);
    private final Table table27 = new Table(columnWidth6);
    private final Table table28 = new Table(columnWidth7);
    private final Table table29 = new Table(columnWidth6);
    private final Table table30 = new Table(columnWidth7);
    private final Table table31 = new Table(columnWidth6);
    private final Table table32 = new Table(columnWidth7);
    private final Table table33 = new Table(columnWidth1);
    private final Table table34 = new Table(columnWidth8);
    private final Table table35 = new Table(columnWidth1);
    private final Table table36 = new Table(columnWidth8);

    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_data, container, false);

        dataTableLayout = view.findViewById(R.id.dataTableLayout);
        dataTypeLayout = view.findViewById(R.id.dataTypeLayout);
        dataUploadNameLayout = view.findViewById(R.id.dataUploadNameLayout);
        dataUploadFileLayout = view.findViewById(R.id.dataUploadFileLayout);
        dataTable = view.findViewById(R.id.dataTable);
        dataType = view.findViewById(R.id.dataType);
        dataUploadName = view.findViewById(R.id.dataUploadName);
        dataUploadFile = view.findViewById(R.id.dataUploadFile);
        Button dataDownloadButton = view.findViewById(R.id.dataDownloadButton);
        dataUploadButton = view.findViewById(R.id.dataUploadButton);

        vibrator = (Vibrator) requireActivity().getSystemService((VIBRATOR_SERVICE));

        dialog = new Dialog(view.getContext(), android.R.style.Theme_DeviceDefault_NoActionBar);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setStatusBarColor(ContextCompat.getColor(view.getContext(), R.color.background));
            dialog.getWindow().setNavigationBarColor(ContextCompat.getColor(view.getContext(), R.color.background));
        }

        dataTableLayout.setClickable(true);
        dataTable.setInputType(InputType.TYPE_NULL);
        dataTable.setClickable(true);
        dataTable.setFocusable(false);
        ArrayAdapter<String> listAdapter1 = new ArrayAdapter<>(view.getContext(), R.layout.dropdown_list, nameList);
        dataTable.setAdapter(listAdapter1);
        dataTable.setOnItemClickListener((parent, view1, position, id) -> {
            UIUtil.hideKeyboard(requireActivity());
            tableDropDown1 = parent.getItemAtPosition(position).toString();
        });

        dataTypeLayout.setClickable(true);
        dataType.setInputType(InputType.TYPE_NULL);
        dataType.setClickable(true);
        dataType.setFocusable(false);
        ArrayAdapter<String> listAdapter2 = new ArrayAdapter<>(view.getContext(), R.layout.dropdown_list, typeList);
        dataType.setAdapter(listAdapter2);
        dataType.setOnItemClickListener((parent, view1, position, id) -> {
            UIUtil.hideKeyboard(requireActivity());
            tableDropDown2 = parent.getItemAtPosition(position).toString();
        });

        dataUploadNameLayout.setClickable(true);
        dataUploadName.setInputType(InputType.TYPE_NULL);
        dataUploadName.setClickable(true);
        dataUploadName.setFocusable(false);
        ArrayAdapter<String> listAdapter3 = new ArrayAdapter<>(view.getContext(), R.layout.dropdown_list, nameList);
        dataUploadName.setAdapter(listAdapter3);
        dataUploadName.setOnItemClickListener((parent, view1, position, id) -> {
            UIUtil.hideKeyboard(requireActivity());
            tableDropDown3 = parent.getItemAtPosition(position).toString();
        });

        dataUploadFileLayout.setClickable(true);
        dataUploadFile.setInputType(InputType.TYPE_NULL);
        dataUploadFile.setClickable(true);
        dataUploadFile.setFocusable(false);

        dataDownloadButton.setOnClickListener(v -> {
            if (ConnectionCheck.getConnectionType(view.getContext()) == 0) {
                ConnectionCheck.connectionDialogShow(view.getContext(), requireActivity());
            } else {
                String tableName = dataTable.getText().toString();
                String fileType = dataType.getText().toString();
                if (tableName.isEmpty()) {
                    dataTableLayout.setErrorEnabled(true);
                    dataTableLayout.setError("Select Data Table Name");
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if (fileType.isEmpty()) {
                    dataTableLayout.setErrorEnabled(false);
                    dataTableLayout.setError(null);
                    dataTypeLayout.setErrorEnabled(true);
                    dataTypeLayout.setError("Select File Type");
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    dataTableLayout.setErrorEnabled(false);
                    dataTableLayout.setError(null);
                    dataTypeLayout.setErrorEnabled(false);
                    dataTypeLayout.setError(null);
                    dialog.show();
                    if (fileType.equalsIgnoreCase("As PDF")) {
                        pdfDataCall(tableDropDown1);
                    } else if (fileType.equalsIgnoreCase("As Excel")) {
                        excelDataCall(tableDropDown1);
                    }
                }
            }
        });

        return view;
    }

    private void pdfDataCall(String function) {

        databaseReference.child("Available Balance Details India").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    indiaBalanceNameList.add(childDataSnapshot.child("source").getValue(String.class));
                    indiaBalanceList.add(childDataSnapshot.child("amount").getValue(String.class));
                }

                databaseReference.child("Available Balance Details Oman").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                        for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                            omanBalanceNameList.add(childDataSnapshot.child("source").getValue(String.class));
                            omanBalanceList.add(childDataSnapshot.child("amount").getValue(String.class));
                        }

                        pdfGenerationCall(function);

                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                        dialog.dismiss();
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                        StyleableToast.makeText(view.getContext(), "Oman Balance Names Fetch Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                    }
                });

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(view.getContext(), "India Balance Names Fetch Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

    }

    @SuppressLint("SimpleDateFormat")
    private void pdfGenerationCall(String function) {

        try {
            File filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/PersonalApp/Statement/");
            filePath.mkdirs();
            pdfFile = new File(filePath, "PersonalAppStatement" + new SimpleDateFormat("ddMMyyhhmmss").format(Calendar.getInstance().getTime()) + ".pdf");
            WriterProperties writerProperties = new WriterProperties();
            writerProperties.setStandardEncryption("333085620gG@".getBytes(), "856203330gG@".getBytes(), EncryptionConstants.ALLOW_PRINTING, EncryptionConstants.ENCRYPTION_AES_256);
            PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile), writerProperties);
            PdfDocument pdfDocument = new PdfDocument(writer);
            pdfDocument.setDefaultPageSize(PageSize.A4);
            document = new Document(pdfDocument);

            Drawable drawable = ContextCompat.getDrawable(requireActivity(), R.drawable.profilepic);
            assert drawable != null;
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            Image image = new Image(ImageDataFactory.create(outputStream.toByteArray()));
            image.scaleToFit(100, 100);
            image.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(image);
            pdfSpace(2);
            document.add(new Paragraph("GOKUL'S").setFontColor(colorBackground).setBold().setItalic().setFontSize(25).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("PERSONAL APP STATEMENT").setFontColor(colorBackground).setBold().setItalic().setFontSize(25).setTextAlignment(TextAlignment.CENTER));

            pdfSpace(10);
            table25.setHorizontalAlignment(HorizontalAlignment.CENTER);
            table25.setVerticalAlignment(VerticalAlignment.MIDDLE);
            document.add(table25.addCell("Available Balances - India").setBackgroundColor(colorBackground).setFontSize(15).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));

            table26.setHorizontalAlignment(HorizontalAlignment.CENTER);
            table26.setVerticalAlignment(VerticalAlignment.MIDDLE);
            table26.addHeaderCell("Name").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.LEFT);
            table26.addHeaderCell("Amount (Rs)").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);

            databaseReference.child("Available Balance Details India").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getChildrenCount() == 0) {
                        table27.setHorizontalAlignment(HorizontalAlignment.CENTER);
                        table27.setVerticalAlignment(VerticalAlignment.MIDDLE);
                        table27.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph("Balance Details Not Available")));
                        document.add(table27);
                    } else {
                        document.add(table26);
                        int count = 0;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            int count1 = (int) snapshot.getChildrenCount();
                            count++;
                            BalanceDetailsData data = dataSnapshot.getValue(BalanceDetailsData.class);
                            assert data != null;
                            table28.setHorizontalAlignment(HorizontalAlignment.CENTER);
                            table28.setVerticalAlignment(VerticalAlignment.MIDDLE);
                            table28.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(data.getSource())));
                            table28.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(data.getAmount())));
                            if (count1 == count) {
                                document.add(table28);
                            }
                        }
                    }

                    pdfSpace(5);
                    table29.setHorizontalAlignment(HorizontalAlignment.CENTER);
                    table29.setVerticalAlignment(VerticalAlignment.MIDDLE);
                    document.add(table29.addCell("Available Balances - Oman").setBackgroundColor(colorBackground).setFontSize(15).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));

                    table30.setHorizontalAlignment(HorizontalAlignment.CENTER);
                    table30.setVerticalAlignment(VerticalAlignment.MIDDLE);
                    table30.addHeaderCell("Name").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.LEFT);
                    table30.addHeaderCell("Amount (Ro)").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);

                    databaseReference.child("Available Balance Details Oman").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getChildrenCount() == 0) {
                                table31.setHorizontalAlignment(HorizontalAlignment.CENTER);
                                table31.setVerticalAlignment(VerticalAlignment.MIDDLE);
                                table31.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph("Balance Details Not Available")));
                                document.add(table31);
                            } else {
                                document.add(table30);
                                int count = 0;
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    int count1 = (int) snapshot.getChildrenCount();
                                    count++;
                                    BalanceDetailsData data = dataSnapshot.getValue(BalanceDetailsData.class);
                                    assert data != null;
                                    table32.setHorizontalAlignment(HorizontalAlignment.CENTER);
                                    table32.setVerticalAlignment(VerticalAlignment.MIDDLE);
                                    table32.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(data.getSource())));
                                    table32.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(data.getAmount())));
                                    if (count1 == count) {
                                        document.add(table32);
                                    }
                                }
                            }

                            if (function.equalsIgnoreCase("All")) {
                                bankDetails(function);
                            } else if (function.equalsIgnoreCase("Lent Details")) {
                                lentDetailsIndia(function);
                            } else if (function.equalsIgnoreCase("Balance Details India")) {
                                balanceDetailsIndia(function);
                            } else if (function.equalsIgnoreCase("Balance Details Oman")) {
                                balanceDetailsOman();
                            } else if (function.equalsIgnoreCase("Personal Details")) {
                                personalDetails(function);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            dialog.dismiss();
                            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                            StyleableToast.makeText(view.getContext(), "Available Balance Oman Details Fetch Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(view.getContext(), "Available Balance India Details Fetch Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(view.getContext(), "PDF Generation Call Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        }

    }

    private void excelDataCall(String function) {

        dialog.show();

        databaseReference.child("Available Balance Details India").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    indiaBalanceNameList.add(childDataSnapshot.child("source").getValue(String.class));
                    indiaBalanceList.add(childDataSnapshot.child("amount").getValue(String.class));
                }

                databaseReference.child("Available Balance Details Oman").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                        for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                            omanBalanceNameList.add(childDataSnapshot.child("source").getValue(String.class));
                            omanBalanceList.add(childDataSnapshot.child("amount").getValue(String.class));
                        }

                        excelGenerationCall(function);

                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                        dialog.dismiss();
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                        StyleableToast.makeText(view.getContext(), "Oman Balance Names Fetch Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                    }
                });

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(view.getContext(), "India Balance Names Fetch Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

    }

    @SuppressLint("SimpleDateFormat")
    private void excelGenerationCall(String function) {

        File filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/PersonalApp/Statement/"
                + "PersonalAppStatement" + new SimpleDateFormat("ddMMyyhhmmss").format(Calendar.getInstance().getTime()) + ".xls");

        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet("Overview");

        HSSFRow row1 = hssfSheet.createRow(1);
        HSSFCell cell1 = row1.createCell(7);
        HSSFRow row2 = hssfSheet.createRow(6);
        HSSFCell cell2 = row2.createCell(6);
        HSSFRow row3 = hssfSheet.createRow(6);
        HSSFCell cell3 = row3.createCell(11);
        HSSFRow row4 = hssfSheet.createRow(8);
        HSSFCell cell4 = row4.createCell(6);
        HSSFRow row5 = hssfSheet.createRow(8);
        HSSFCell cell5 = row5.createCell(7);
        HSSFRow row6 = hssfSheet.createRow(8);
        HSSFCell cell6 = row6.createCell(11);
        HSSFRow row7 = hssfSheet.createRow(8);
        HSSFCell cell7 = row7.createCell(12);

        cell1.setCellValue("Gokul'S Personal App Statement");
        cell2.setCellValue("Available Balance - India");
        cell3.setCellValue("Available Balance - Oman");
        cell4.setCellValue("Name");
        cell5.setCellValue("Amount");
        cell6.setCellValue("Name");
        cell7.setCellValue("Amount");

        for (int i = 0; i < indiaBalanceNameList.size(); i++) {
            HSSFRow row = hssfSheet.createRow(9 + i);
            HSSFCell cell = row.createCell(6);
            cell.setCellValue(indiaBalanceNameList.get(i));
        }
//        for (int i = 0; i < indiaBalanceList.size(); i++) {
//            HSSFRow row = hssfSheet.createRow(9 + i);
//            HSSFCell cell = row.createCell(7);
//            cell.setCellValue(indiaBalanceList.get(i));
//        }

        FileOutputStream fileOutputStream = null;

        try {
            if (!filePath.exists()) {
                filePath.createNewFile();
            }

            fileOutputStream = new FileOutputStream(filePath);
            hssfWorkbook.write(fileOutputStream);

        } catch (Exception e) {
            e.printStackTrace();
            dialog.dismiss();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(view.getContext(), "Excel Generation Call Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        } finally {
            try {
                assert fileOutputStream != null;
                fileOutputStream.flush();
                fileOutputStream.close();
                dialog.dismiss();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void pdfSpace(int count) {
        for (int i = 0; i < count; i++) {
            document.add(new Paragraph(""));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int STORAGE_PERMISSION_CODE = 1;
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                StyleableToast.makeText(view.getContext(), "Storage Permission Granted", Toast.LENGTH_LONG, R.style.CustomToast).show();
            } else {
                StyleableToast.makeText(view.getContext(), "Storage Permission Denied", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        }
    }

    private void bankDetails(String function) {

        AreaBreak nextPage = new AreaBreak(AreaBreakType.NEXT_PAGE);
        document.add(nextPage);

        table1.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table1.setVerticalAlignment(VerticalAlignment.MIDDLE);
        document.add(table1.addCell("Bank Details").setBackgroundColor(colorBackground).setFontSize(15).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));

        table2.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table2.setVerticalAlignment(VerticalAlignment.MIDDLE);
        table2.addHeaderCell("Name").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.LEFT);
        table2.addHeaderCell("CIF").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table2.addHeaderCell("Account No").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table2.addHeaderCell("IFSC").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table2.addHeaderCell("MICR").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);

        databaseReference.child("Bank Details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0) {
                    table3.setHorizontalAlignment(HorizontalAlignment.CENTER);
                    table3.setVerticalAlignment(VerticalAlignment.MIDDLE);
                    table3.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph("Bank Details Not Available")));
                    document.add(table3);
                } else {
                    document.add(table2);
                    int count = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        int count1 = (int) snapshot.getChildrenCount();
                        count++;
                        AdminData data = dataSnapshot.getValue(AdminData.class);
                        assert data != null;
                        table4.setHorizontalAlignment(HorizontalAlignment.CENTER);
                        table4.setVerticalAlignment(VerticalAlignment.MIDDLE);
                        table4.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(data.getBankName())));
                        table4.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(data.getBankCIF())));
                        table4.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(data.getBankAccount())));
                        table4.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(data.getBankIFSC())));
                        table4.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(data.getBankMICR())));
                        if (count1 == count) {
                            document.add(table4);
                        }
                    }
                }

                investmentDetails(function);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(view.getContext(), "Bank Details Fetch Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

    }

    private void investmentDetails(String function) {

        pdfSpace(7);
        table5.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table5.setVerticalAlignment(VerticalAlignment.MIDDLE);
        document.add(table5.addCell("Investment Details").setBackgroundColor(colorBackground).setFontSize(15).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));

        table6.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table6.setVerticalAlignment(VerticalAlignment.MIDDLE);
        table6.addHeaderCell("Name And Folio No").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table6.addHeaderCell("Amount").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table6.addHeaderCell("Source").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table6.addHeaderCell("Date").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table6.addHeaderCell("Remarks").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);

        databaseReference.child("Investment Details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0) {
                    table7.setHorizontalAlignment(HorizontalAlignment.CENTER);
                    table7.setVerticalAlignment(VerticalAlignment.MIDDLE);
                    table7.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph("Bank Details Not Available")));
                    document.add(table7);
                } else {
                    List<InvestmentData> investList1 = new ArrayList<>();
                    document.add(table6);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        InvestmentData data = dataSnapshot.getValue(InvestmentData.class);
                        investList1.add(data);
                    }
                    List<InvestmentData> investList2 = investList1.stream().distinct().sorted((i1, i2) -> {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            return String.valueOf(Objects.requireNonNull(dateFormat.parse(i1.getDate())).getTime()).compareTo(
                                    String.valueOf(Objects.requireNonNull(dateFormat.parse(i2.getDate())).getTime()));
                        } catch (ParseException e) {
                            StyleableToast.makeText(view.getContext(), "Investment List Sort Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                        }
                        return 0;
                    }).collect(Collectors.toList());

                    int count = 0;
                    for (int i = 0; i < investList2.size(); i++) {
                        int count1 = investList2.size();
                        count++;
                        table8.setHorizontalAlignment(HorizontalAlignment.CENTER);
                        table8.setVerticalAlignment(VerticalAlignment.MIDDLE);
                        table8.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.LEFT).add(new Paragraph(investList2.get(i).getName() + " - " + investList2.get(i).getFolioNumber())));
                        table8.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(investList2.get(i).getAmount())));
                        table8.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(investList2.get(i).getSource())));
                        table8.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(investList2.get(i).getDate())));
                        table8.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(investList2.get(i).getRemarks())));
                        if (count1 == count) {
                            document.add(table8);
                        }
                    }

                }

                personalDetails(function);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(view.getContext(), "Investment Details Fetch Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

    }

    private void personalDetails(String function) {

        AreaBreak nextPage = new AreaBreak(AreaBreakType.NEXT_PAGE);
        document.add(nextPage);

        table33.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table33.setVerticalAlignment(VerticalAlignment.MIDDLE);
        document.add(table33.addCell("Personal Details").setBackgroundColor(colorBackground).setFontSize(15).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));

        table34.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table34.setVerticalAlignment(VerticalAlignment.MIDDLE);
        table34.addHeaderCell("Name").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table34.addHeaderCell("Value").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table34.addHeaderCell("Type").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);

        databaseReference.child("Personal Details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0) {
                    table35.setHorizontalAlignment(HorizontalAlignment.CENTER);
                    table35.setVerticalAlignment(VerticalAlignment.MIDDLE);
                    table35.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph("Personal Details Not Available")));
                    document.add(table15);
                } else {
                    List<AdminData> perList1 = new ArrayList<>();
                    document.add(table34);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        AdminData data = dataSnapshot.getValue(AdminData.class);
                        perList1.add(data);
                    }
                    List<AdminData> perList2 = perList1.stream().distinct().sorted((a1, a2) -> a1.getPersonalName().compareToIgnoreCase(a2.getPersonalName())).collect(Collectors.toList());

                    int count = 0;
                    for (int i = 0; i < perList2.size(); i++) {
                        int count1 = perList2.size();
                        count++;
                        table36.setHorizontalAlignment(HorizontalAlignment.CENTER);
                        table36.setVerticalAlignment(VerticalAlignment.MIDDLE);
                        table36.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.LEFT).add(new Paragraph(perList2.get(i).getPersonalName())));
                        table36.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.LEFT).add(new Paragraph(perList2.get(i).getPersonalValue())));
                        table36.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(perList2.get(i).getPersonalType())));
                        if (count1 == count) {
                            document.add(table36);
                        }
                    }
                }
                if (function.equalsIgnoreCase("All")) {
                    lentDetailsIndia(function);
                } else {
                    pdfClose();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(view.getContext(), "Personal Details Fetch Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });
    }

    private void lentDetailsIndia(String function) {

        AreaBreak nextPage = new AreaBreak(AreaBreakType.NEXT_PAGE);
        document.add(nextPage);

        table9.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table9.setVerticalAlignment(VerticalAlignment.MIDDLE);
        document.add(table9.addCell("Lent Details - India").setBackgroundColor(colorBackground).setFontSize(15).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));

        table10.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table10.setVerticalAlignment(VerticalAlignment.MIDDLE);
        table10.addHeaderCell("Name").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table10.addHeaderCell("Amount").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table10.addHeaderCell("From").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table10.addHeaderCell("To").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table10.addHeaderCell("Date").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table10.addHeaderCell("Remarks").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);

        databaseReference.child("Lent Details India").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0) {
                    table11.setHorizontalAlignment(HorizontalAlignment.CENTER);
                    table11.setVerticalAlignment(VerticalAlignment.MIDDLE);
                    table11.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph("Lent Details Not Available")));
                    document.add(table11);
                } else {
                    List<LentData> lentList1 = new ArrayList<>();
                    document.add(table10);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        LentData data = dataSnapshot.getValue(LentData.class);
                        lentList1.add(data);
                    }
                    List<LentData> lentList2 = lentList1.stream().distinct().sorted((l1, l2) -> {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            return String.valueOf(Objects.requireNonNull(dateFormat.parse(l1.getDate())).getTime()).compareTo(
                                    String.valueOf(Objects.requireNonNull(dateFormat.parse(l2.getDate())).getTime()));
                        } catch (ParseException e) {
                            StyleableToast.makeText(view.getContext(), "India Lent List Sort Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                        }
                        return 0;
                    }).collect(Collectors.toList());

                    int count = 0;
                    for (int i = 0; i < lentList2.size(); i++) {
                        int count1 = lentList2.size();
                        count++;
                        table12.setHorizontalAlignment(HorizontalAlignment.CENTER);
                        table12.setVerticalAlignment(VerticalAlignment.MIDDLE);
                        table12.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.LEFT).add(new Paragraph(lentList2.get(i).getName())));
                        table12.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(lentList2.get(i).getAmount())));
                        table12.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(lentList2.get(i).getFrom())));
                        table12.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(lentList2.get(i).getTo())));
                        table12.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(lentList2.get(i).getDate())));
                        table12.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(lentList2.get(i).getRemarks())));
                        if (count1 == count) {
                            document.add(table12);
                        }
                    }
                }
                lentDetailsOman(function);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(view.getContext(), "India Lent Details Fetch Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

    }

    private void lentDetailsOman(String function) {

        pdfSpace(5);
        table13.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table13.setVerticalAlignment(VerticalAlignment.MIDDLE);
        document.add(table13.addCell("Lent Details - Oman").setBackgroundColor(colorBackground).setFontSize(15).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));

        table14.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table14.setVerticalAlignment(VerticalAlignment.MIDDLE);
        table14.addHeaderCell("Name").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table14.addHeaderCell("Amount").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table14.addHeaderCell("From").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table14.addHeaderCell("To").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table14.addHeaderCell("Date").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table14.addHeaderCell("Remarks").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);

        databaseReference.child("Lent Details Oman").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0) {
                    table15.setHorizontalAlignment(HorizontalAlignment.CENTER);
                    table15.setVerticalAlignment(VerticalAlignment.MIDDLE);
                    table15.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph("Lent Details Not Available")));
                    document.add(table15);
                } else {
                    List<LentData> lentList1 = new ArrayList<>();
                    document.add(table14);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        LentData data = dataSnapshot.getValue(LentData.class);
                        lentList1.add(data);
                    }
                    List<LentData> lentList2 = lentList1.stream().distinct().sorted((l1, l2) -> {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            return String.valueOf(Objects.requireNonNull(dateFormat.parse(l1.getDate())).getTime()).compareTo(
                                    String.valueOf(Objects.requireNonNull(dateFormat.parse(l2.getDate())).getTime()));
                        } catch (ParseException e) {
                            StyleableToast.makeText(view.getContext(), "Oman Lent List Sort Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                        }
                        return 0;
                    }).collect(Collectors.toList());

                    int count = 0;
                    for (int i = 0; i < lentList2.size(); i++) {
                        int count1 = lentList2.size();
                        count++;
                        table16.setHorizontalAlignment(HorizontalAlignment.CENTER);
                        table16.setVerticalAlignment(VerticalAlignment.MIDDLE);
                        table16.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.LEFT).add(new Paragraph(lentList2.get(i).getName())));
                        table16.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(lentList2.get(i).getAmount())));
                        table16.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(lentList2.get(i).getFrom())));
                        table16.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(lentList2.get(i).getTo())));
                        table16.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(lentList2.get(i).getDate())));
                        table16.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(lentList2.get(i).getRemarks())));
                        if (count1 == count) {
                            document.add(table16);
                        }
                    }
                }
                if (function.equalsIgnoreCase("All")) {
                    balanceDetailsIndia(function);
                } else {
                    pdfClose();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(view.getContext(), "Oman Lent Details Fetch Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
        });

    }

    private void balanceDetailsIndia(String function) {

        AreaBreak nextPage = new AreaBreak(AreaBreakType.NEXT_PAGE);
        document.add(nextPage);

        table17.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table17.setVerticalAlignment(VerticalAlignment.MIDDLE);
        document.add(table17.addCell("Balance Details - India").setBackgroundColor(colorBackground).setFontSize(15).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));

        table18.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table18.setVerticalAlignment(VerticalAlignment.MIDDLE);
        table18.addHeaderCell("Date").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table18.addHeaderCell("Amount").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table18.addHeaderCell("Txn Amt").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table18.addHeaderCell("Category").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table18.addHeaderCell("Source").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table18.addHeaderCell("Mode").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table18.addHeaderCell("Remarks").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);

        List<BalanceDetailsData> balList1 = new ArrayList<>();
        for (int i = 0; i < indiaBalanceNameList.size(); i++) {
            int finalI = i;
            databaseReference.child("Balance Details India").child(indiaBalanceNameList.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<BalanceDetailsData> balList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.getChildrenCount() != 0) {
                            BalanceDetailsData data = dataSnapshot.getValue(BalanceDetailsData.class);
                            balList.add(data);
                        }
                    }
                    balList1.addAll(balList);
                    balList.clear();
                    if (finalI == (indiaBalanceNameList.size() - 1)) {
                        if (balList1.size() == 0) {
                            table19.setHorizontalAlignment(HorizontalAlignment.CENTER);
                            table19.setVerticalAlignment(VerticalAlignment.MIDDLE);
                            table19.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph("Balance Details Not Available")));
                            document.add(table19);
                        } else {
                            List<BalanceDetailsData> balList2 = balList1.stream().distinct().sorted((b1, b2) -> {
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                try {
                                    return String.valueOf(Objects.requireNonNull(dateFormat.parse(b2.getDate())).getTime()).compareTo(
                                            String.valueOf(Objects.requireNonNull(dateFormat.parse(b1.getDate())).getTime()));
                                } catch (ParseException e) {
                                    StyleableToast.makeText(view.getContext(), "India Balance List Sort Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                                }
                                return 0;
                            }).collect(Collectors.toList());
                            document.add(table18);
                            int count = 0;
                            for (int i = 0; i < balList2.size(); i++) {
                                int count1 = balList2.size();
                                count++;
                                table20.setHorizontalAlignment(HorizontalAlignment.CENTER);
                                table20.setVerticalAlignment(VerticalAlignment.MIDDLE);
                                table20.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(balList2.get(i).getDate())));
                                table20.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(balList2.get(i).getAmount())));
                                table20.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(balList2.get(i).getTransAmount())));
                                table20.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(balList2.get(i).getCategory())));
                                table20.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(balList2.get(i).getSource())));
                                table20.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(balList2.get(i).getType())));
                                table20.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(balList2.get(i).getRemarks())));
                                if (count1 == count) {
                                    document.add(table20);
                                    if (function.equalsIgnoreCase("All")) {
                                        balanceDetailsOman();
                                    } else {
                                        pdfClose();
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(view.getContext(), "India Balance Details Fetch Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                }
            });
        }

    }

    private void balanceDetailsOman() {

        AreaBreak nextPage = new AreaBreak(AreaBreakType.NEXT_PAGE);
        document.add(nextPage);

        table21.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table21.setVerticalAlignment(VerticalAlignment.MIDDLE);
        document.add(table21.addCell("Balance Details - Oman").setBackgroundColor(colorBackground).setFontSize(15).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));

        table22.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table22.setVerticalAlignment(VerticalAlignment.MIDDLE);
        table22.addHeaderCell("Date").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table22.addHeaderCell("Amount").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table22.addHeaderCell("Txn Amt").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table22.addHeaderCell("Category").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table22.addHeaderCell("Source").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table22.addHeaderCell("Mode").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);
        table22.addHeaderCell("Remarks").setBackgroundColor(colorBackground).setFontSize(12).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER);

        List<BalanceDetailsData> balList1 = new ArrayList<>();
        for (int i = 0; i < omanBalanceNameList.size(); i++) {
            int finalI = i;
            databaseReference.child("Balance Details Oman").child(omanBalanceNameList.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<BalanceDetailsData> balList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.getChildrenCount() != 0) {
                            BalanceDetailsData data = dataSnapshot.getValue(BalanceDetailsData.class);
                            balList.add(data);
                        }
                    }
                    balList1.addAll(balList);
                    balList.clear();
                    if (finalI == (omanBalanceNameList.size() - 1)) {
                        if (balList1.size() == 0) {
                            table24.setHorizontalAlignment(HorizontalAlignment.CENTER);
                            table24.setVerticalAlignment(VerticalAlignment.MIDDLE);
                            table24.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph("Balance Details Not Available")));
                            document.add(table24);
                        } else {
                            List<BalanceDetailsData> balList2 = balList1.stream().distinct().sorted((b1, b2) -> {
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                try {
                                    return String.valueOf(Objects.requireNonNull(dateFormat.parse(b2.getDate())).getTime()).compareTo(
                                            String.valueOf(Objects.requireNonNull(dateFormat.parse(b1.getDate())).getTime()));
                                } catch (ParseException e) {
                                    StyleableToast.makeText(view.getContext(), "Oman Balance List Sort Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                                }
                                return 0;
                            }).collect(Collectors.toList());
                            document.add(table22);
                            int count = 0;
                            for (int i = 0; i < balList2.size(); i++) {
                                int count1 = balList2.size();
                                count++;
                                table23.setHorizontalAlignment(HorizontalAlignment.CENTER);
                                table23.setVerticalAlignment(VerticalAlignment.MIDDLE);
                                table23.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(balList2.get(i).getDate())));
                                table23.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(balList2.get(i).getAmount())));
                                table23.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(balList2.get(i).getTransAmount())));
                                table23.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(balList2.get(i).getCategory())));
                                table23.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(balList2.get(i).getSource())));
                                table23.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(balList2.get(i).getType())));
                                table23.addCell(new Cell().setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER).add(new Paragraph(balList2.get(i).getRemarks())));
                                if (count1 == count) {
                                    document.add(table23);
                                    pdfClose();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(view.getContext(), "Oman Balance Details Fetch Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                }
            });
        }

    }

    @SuppressLint("SimpleDateFormat")
    private void pdfClose() {
        AreaBreak nextPage = new AreaBreak(AreaBreakType.NEXT_PAGE);
        document.add(nextPage);
        pdfSpace(30);
        document.add(new Paragraph("Thank You").setFontColor(colorBackground).setBold().setItalic().setFontSize(25).setTextAlignment(TextAlignment.CENTER));
        pdfSpace(5);
        document.add(new Paragraph("Saving a small amount soon builds").setFontColor(colorBackground).setBold().setItalic().setFontSize(15).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("up to a large amount").setFontColor(colorBackground).setBold().setItalic().setFontSize(15).setTextAlignment(TextAlignment.CENTER));
        pdfSpace(42);
        document.add(new Paragraph(String.format("Print Date: %s", new SimpleDateFormat("dd-MMM-yy hh:mm:ss aa").format(Calendar.getInstance().getTime()).toUpperCase())).setFontSize(10).setTextAlignment(TextAlignment.CENTER));
        document.close();
        dialog.dismiss();
        StyleableToast.makeText(view.getContext(), "File Downloaded In: Download/PersonalApp/Statement", Toast.LENGTH_LONG, R.style.CustomToast).show();
        viewPdf(pdfFile);
    }

    private void viewPdf(File pdfFile) {

        Uri path = FileProvider.getUriForFile(view.getContext(), requireContext().getApplicationContext().getPackageName() + ".provider", pdfFile);

        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            StyleableToast.makeText(view.getContext(), "PDF Open Error: " + e.getMessage(), Toast.LENGTH_LONG, R.style.CustomToast).show();

        }
    }

}