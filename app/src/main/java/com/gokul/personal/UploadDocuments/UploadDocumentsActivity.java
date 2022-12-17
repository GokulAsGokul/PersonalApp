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
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.Miscellaneous.GeneralData;
import com.gokul.personal.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class UploadDocumentsActivity extends AppCompatActivity {

    private TextInputLayout docUploadNameLayout, docUpCategoryLayout, docUploadFileLayout;
    private AutoCompleteTextView docUploadName, docUpCategory, docUploadFile;
    private Vibrator vibrator;
    private Uri fileUri;
    private String fileName, fileCategory, fileUrl, fileExtension, categoryDropDown;
    private Dialog dialog1, dialog2;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private final ArrayList<String> categoryList = new ArrayList<>();

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

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        docUploadNameLayout = findViewById(R.id.docUploadNameLayout);
        docUpCategoryLayout = findViewById(R.id.docUpCategoryLayout);
        docUploadFileLayout = findViewById(R.id.docUploadFileLayout);
        docUploadName = findViewById(R.id.docUploadName);
        docUpCategory = findViewById(R.id.docUpCategory);
        docUploadFile = findViewById(R.id.docUploadFile);
        Button docUploadButton = findViewById(R.id.docUploadButton);

        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);

        dialog1 = new Dialog(UploadDocumentsActivity.this);
        dialog1.setContentView(R.layout.uploading_dialog);
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(false);
        if (dialog1.getWindow() != null) {
            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog2 = new Dialog(UploadDocumentsActivity.this, android.R.style.Theme_DeviceDefault_NoActionBar);
        dialog2.setContentView(R.layout.loading_dialog);
        dialog2.setCancelable(true);
        dialog2.setCanceledOnTouchOutside(false);
        if (dialog2.getWindow() != null) {
            dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog2.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
            dialog2.getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));
        }
        if (ConnectionCheck.getConnectionType(getApplicationContext()) != 0) {
            dialog2.show();
        }

        vibrator = (Vibrator) getSystemService((VIBRATOR_SERVICE));

        docUpCategory.setInputType(InputType.TYPE_NULL);
        docUpCategory.setClickable(true);
        docUpCategory.setFocusable(false);
        docUpCategoryLayout.setClickable(true);
        databaseReference.child("DropDown Values").child("DocumentCategory").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String temp = childDataSnapshot.child("listValue").getValue(String.class);
                    dialog2.show();
                    categoryList.add(temp);
                }
                ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(UploadDocumentsActivity.this, R.layout.dropdown_list, categoryList);
                sourceAdapter.sort(Comparator.naturalOrder());
                docUpCategory.setAdapter(sourceAdapter);
                dialog2.dismiss();
                docUpCategory.setOnItemClickListener((parent, view, position, id) -> {
                    UIUtil.hideKeyboard(UploadDocumentsActivity.this);
                    categoryDropDown = parent.getItemAtPosition(position).toString();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog2.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(UploadDocumentsActivity.this, "DocumentCategory Read Error: " + error.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }
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

        docUploadButton.setOnClickListener(view -> {
            dialog2.show();
            docUploadCall();
        });

    }

    private void docUploadCall() {
        fileName = Objects.requireNonNull(docUploadName.getText()).toString().trim();
        fileCategory = Objects.requireNonNull(docUpCategory.getText()).toString().trim();
        String file = Objects.requireNonNull(docUploadFile.getText()).toString().trim();

        if (ConnectionCheck.getConnectionType(getApplicationContext()) == 0) {
            dialog2.dismiss();
            ConnectionCheck.connectionDialogShow(getApplicationContext(), UploadDocumentsActivity.this);
        } else {
            if (fileName.isEmpty()) {
                docUploadNameLayout.setErrorEnabled(true);
                docUploadNameLayout.setError("File Name is Required");
                dialog2.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (fileCategory.isEmpty()) {
                docUploadNameLayout.setErrorEnabled(false);
                docUploadNameLayout.setError(null);
                docUpCategoryLayout.setErrorEnabled(true);
                docUpCategoryLayout.setError("Category is Required");
                dialog2.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (file.isEmpty()) {
                docUploadNameLayout.setErrorEnabled(false);
                docUploadNameLayout.setError(null);
                docUpCategoryLayout.setErrorEnabled(false);
                docUpCategoryLayout.setError(null);
                docUploadFileLayout.setErrorEnabled(true);
                docUploadFileLayout.setError("Document is required");
                dialog2.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                docUploadNameLayout.setErrorEnabled(false);
                docUploadNameLayout.setError(null);
                docUpCategoryLayout.setErrorEnabled(false);
                docUpCategoryLayout.setError(null);
                docUploadFileLayout.setErrorEnabled(false);
                docUploadFileLayout.setError(null);
                UIUtil.hideKeyboard(UploadDocumentsActivity.this);
                dialog2.dismiss();
                dialog1.show();
                docUpload();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void docUpload() {
        if (fileUri != null) {
            FirebaseStorage.getInstance().getReference().child("Documents/" + fileCategory + "/" + fileName)
                    .getDownloadUrl().addOnSuccessListener(uri -> {
                dialog1.dismiss();
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
                    data.setFileUrl(fileUrl);
                    data.setFileExtension(fileExtension);

                    databaseReference.child("Documents Data").child(fileName).setValue(data).
                            addOnSuccessListener(unused -> {
                                Objects.requireNonNull(docUploadName.getText()).clear();
                                Objects.requireNonNull(docUpCategory.getText()).clear();
                                Objects.requireNonNull(docUploadFile.getText()).clear();
                                dialog1.dismiss();
                                StyleableToast.makeText(UploadDocumentsActivity.this, "Uploaded Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                            }).addOnFailureListener(e1 -> {
                        docUploadFile.setText(R.string.firebaseUpdateError);
                        dialog2.dismiss();
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                        StyleableToast.makeText(UploadDocumentsActivity.this, "Document Upload in Firebase Error: " + e1.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                    });

                }).addOnFailureListener(e12 -> {
                    dialog1.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(UploadDocumentsActivity.this, "Failed to capture document URL: " + e12.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                    docUploadFile.setText(R.string.uploadError);
                })).addOnFailureListener(e1 -> {
                    dialog1.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(UploadDocumentsActivity.this, "Document Upload Error: " + e1.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                }).addOnProgressListener(snapshot -> {
                    ProgressBar downloadDialogBar = dialog1.findViewById(R.id.uploadDialogBar);
                    TextView uploadDialog2 = dialog1.findViewById(R.id.uploadDialog2);
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