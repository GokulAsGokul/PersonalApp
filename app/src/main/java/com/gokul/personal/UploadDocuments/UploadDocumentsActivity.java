package com.gokul.personal.UploadDocuments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gokul.personal.General.ConnectionCheck;
import com.gokul.personal.General.GeneralData;
import com.gokul.personal.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.ArrayList;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class UploadDocumentsActivity extends AppCompatActivity {

    private TextInputLayout docUploadNameLayout, docUpCategoryLayout, docUploadFileLayout;
    private AutoCompleteTextView docUploadName, docUpCategory, docUploadFile;
    private Dialog dialog;
    private Vibrator vibrator;
    final String[] categoryList = {"Bank", "Certificate", "College", "Family", "Personal", "Work", "Others"};
    private String categoryDropDown = "";
    private Uri fileUri;
    private String fileName, fileCategory, fileUrl, fileExtension;

    ActivityResultLauncher<String> fileContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        fileUri = result;
                        docUploadFile.setText(docUploadName.getText());
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_documents);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.background)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        docUploadNameLayout = findViewById(R.id.docUploadNameLayout);
        docUpCategoryLayout = findViewById(R.id.docUpCategoryLayout);
        docUploadFileLayout = findViewById(R.id.docUploadFileLayout);
        docUploadName = findViewById(R.id.docUploadName);
        docUpCategory = findViewById(R.id.docUpCategory);
        docUploadFile = findViewById(R.id.docUploadFile);
        Button docUploadButton = findViewById(R.id.docUploadButton);

        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);

        dialog = new Dialog(UploadDocumentsActivity.this);
        dialog.setContentView(R.layout.uploading_dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        vibrator = (Vibrator) getSystemService((VIBRATOR_SERVICE));

        docUpCategory.setInputType(InputType.TYPE_NULL);
        docUpCategory.setClickable(true);
        docUpCategory.setFocusable(false);
        docUpCategoryLayout.setClickable(true);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.dropdown_list, categoryList);
        docUpCategory.setAdapter(categoryAdapter);
        docUpCategory.setOnItemClickListener((parent, view, position, id) -> {
            UIUtil.hideKeyboard(UploadDocumentsActivity.this);
            categoryDropDown = parent.getItemAtPosition(position).toString();
        });

        docUploadFileLayout.setClickable(true);
        docUploadFile.setInputType(InputType.TYPE_NULL);
        docUploadFile.setClickable(true);
        docUploadFile.setFocusable(false);
        docUploadFile.setOnClickListener(view -> {
            String fileName = Objects.requireNonNull(docUploadName.getText()).toString().trim();
            if (fileName.isEmpty()) {
                docUploadNameLayout.setErrorEnabled(true);
                docUploadNameLayout.setError("File Name is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                docUploadFileLayout.setErrorEnabled(false);
                docUploadFileLayout.setError(null);
                fileContent.launch("*/*");
            }
        });

        docUploadButton.setOnClickListener(view -> docUploadCall());

    }

    private void docUploadCall() {
        fileName = Objects.requireNonNull(docUploadName.getText()).toString().trim();
        fileCategory = Objects.requireNonNull(docUpCategory.getText()).toString().trim();
        String file = Objects.requireNonNull(docUploadFile.getText()).toString().trim();

        if (ConnectionCheck.getConnectionType(getApplicationContext()) == 0) {
            ConnectionCheck.connectionDialogShow(getApplicationContext(), UploadDocumentsActivity.this);
        } else {
            if (fileName.isEmpty()) {
                docUploadNameLayout.setErrorEnabled(true);
                docUploadNameLayout.setError("File Name is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (fileCategory.isEmpty()) {
                docUploadNameLayout.setErrorEnabled(false);
                docUploadNameLayout.setError(null);
                docUpCategoryLayout.setErrorEnabled(true);
                docUpCategoryLayout.setError("Category is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (file.isEmpty()) {
                docUploadNameLayout.setErrorEnabled(false);
                docUploadNameLayout.setError(null);
                docUpCategoryLayout.setErrorEnabled(false);
                docUpCategoryLayout.setError(null);
                docUploadFileLayout.setErrorEnabled(true);
                docUploadFileLayout.setError("Document is required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                docUploadNameLayout.setErrorEnabled(false);
                docUploadNameLayout.setError(null);
                docUpCategoryLayout.setErrorEnabled(false);
                docUpCategoryLayout.setError(null);
                docUploadFileLayout.setErrorEnabled(false);
                docUploadFileLayout.setError(null);
                UIUtil.hideKeyboard(UploadDocumentsActivity.this);
                dialog.show();
                docUpload();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void docUpload() {
        if (fileUri != null) {
            FirebaseStorage.getInstance().getReference().child("Documents/" + fileCategory + "/" + fileName)
                    .getDownloadUrl().addOnSuccessListener(uri -> {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(UploadDocumentsActivity.this, "Document already available", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }).addOnFailureListener(e -> {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().
                        child("Documents/" + fileCategory + "/" + fileName);
                storageReference.putFile(fileUri).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {

                    fileUrl = uri.toString();
                    fileExtension = getExtension(fileUri);

                    GeneralData data = new GeneralData();
                    data.setFileName(fileName);
                    data.setFileCategory(fileCategory);
                    data.setFileType(fileCategory);
                    data.setFileUrl(fileUrl);
                    data.setFileExtension(fileExtension);

                    FirebaseDatabase.getInstance().getReference("Documents Data").child(fileName).setValue(data).
                            addOnSuccessListener(unused -> {
                                Objects.requireNonNull(docUploadName.getText()).clear();
                                Objects.requireNonNull(docUpCategory.getText()).clear();
                                Objects.requireNonNull(docUploadFile.getText()).clear();
                                dialog.dismiss();
                                StyleableToast.makeText(UploadDocumentsActivity.this, "Uploaded Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                            }).addOnFailureListener(e1 -> {
                        docUploadFile.setText(R.string.firebaseUpdateError);
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                        StyleableToast.makeText(UploadDocumentsActivity.this, "Document Upload in Firebase Error: " + e1.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                    });

                }).addOnFailureListener(e12 -> {
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(UploadDocumentsActivity.this, "Failed to capture document URL: " + e12.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                    docUploadFile.setText(R.string.uploadError);
                })).addOnFailureListener(e1 -> {
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(UploadDocumentsActivity.this, "Document Upload Error: " + e1.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                }).addOnProgressListener(snapshot -> {
                    ProgressBar downloadDialogBar = dialog.findViewById(R.id.uploadDialogBar);
                    TextView uploadDialog2 = dialog.findViewById(R.id.uploadDialog2);
                    final int progress = (int) ((snapshot.getBytesTransferred() * 100L) / snapshot.getTotalByteCount());
                    downloadDialogBar.setProgress(progress);
                    uploadDialog2.setText((snapshot.getBytesTransferred()) / (1024L) + " KB of " + (snapshot.getTotalByteCount()) / (1024L) + " KB");
                });
            });
        } else {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(UploadDocumentsActivity.this, "Please select an document", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        }
    }

    private String getExtension(Uri fileUri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap typeMap = MimeTypeMap.getSingleton();
        return typeMap.getExtensionFromMimeType(resolver.getType(fileUri));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
    }
}