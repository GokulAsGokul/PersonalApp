package com.gokul.personal.AppUpload;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class UploadAppActivity extends AppCompatActivity {

    private TextInputLayout appUpVersionLayout, appUpFileLayout, appUpNoteLayout;
    private TextInputEditText appVersionNo, appUpNote;
    private AutoCompleteTextView appUpFile;
    private Dialog dialog;
    private Vibrator vibrator;
    private String versionNo, appVersionNote, appName, appUrl, appExtension;
    private Uri appUri;
    ActivityResultLauncher<String> appContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        appUri = result;
                        appUpFile.setText("PersonalApp_v" + appVersionNo.getText());
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_app);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.background)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        appUpVersionLayout = findViewById(R.id.appUpVersionLayout);
        appUpFileLayout = findViewById(R.id.appUpFileLayout);
        appUpNoteLayout = findViewById(R.id.appUpNoteLayout);
        Button appUploadButton = findViewById(R.id.appUploadButton);
        appVersionNo = findViewById(R.id.appVersionNo);
        appUpFile = findViewById(R.id.appUpFile);
        appUpNote = findViewById(R.id.appUpNote);

        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);

        dialog = new Dialog(UploadAppActivity.this);
        dialog.setContentView(R.layout.uploading_dialog);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        vibrator = (Vibrator) getSystemService((VIBRATOR_SERVICE));

        appUpNote.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                appUploadCall();
                return true;
            }
            return false;
        });

        appUpFileLayout.setClickable(true);
        appUpFile.setInputType(InputType.TYPE_NULL);
        appUpFile.setClickable(true);
        appUpFile.setFocusable(false);
        appUpFile.setOnClickListener(view -> {
            String versionNo = Objects.requireNonNull(appVersionNo.getText()).toString().trim();
            if (versionNo.isEmpty()) {
                appUpVersionLayout.setErrorEnabled(true);
                appUpVersionLayout.setError("Version No is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                appUpVersionLayout.setErrorEnabled(false);
                appUpVersionLayout.setError(null);
                appContent.launch("application/vnd.android.package-archive");
            }
        });
        appUploadButton.setOnClickListener(view -> appUploadCall());
    }

    private void appUploadCall() {
        versionNo = Objects.requireNonNull(appVersionNo.getText()).toString().trim();
        appName = Objects.requireNonNull(appUpFile.getText()).toString().trim();
        appVersionNote = Objects.requireNonNull(appUpNote.getText()).toString().trim();

        if (ConnectionCheck.getConnectionType(getApplicationContext()) == 0) {
            ConnectionCheck.connectionDialogShow(getApplicationContext(), UploadAppActivity.this);
        } else {
            if (versionNo.isEmpty()) {
                appUpVersionLayout.setErrorEnabled(true);
                appUpVersionLayout.setError("Version No is Required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (appName.isEmpty()) {
                appUpVersionLayout.setErrorEnabled(false);
                appUpVersionLayout.setError(null);
                appUpFileLayout.setErrorEnabled(true);
                appUpFileLayout.setError("Select an updated APK file");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (appVersionNote.isEmpty()) {
                appUpVersionLayout.setErrorEnabled(false);
                appUpVersionLayout.setError(null);
                appUpFileLayout.setErrorEnabled(false);
                appUpFileLayout.setError(null);
                appUpNoteLayout.setErrorEnabled(true);
                appUpNoteLayout.setError("New Version Note is required");
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                appUpVersionLayout.setErrorEnabled(false);
                appUpVersionLayout.setError(null);
                appUpFileLayout.setErrorEnabled(false);
                appUpFileLayout.setError(null);
                appUpNoteLayout.setErrorEnabled(false);
                appUpNoteLayout.setError(null);
                UIUtil.hideKeyboard(UploadAppActivity.this);
                uploadNewApp();
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private void uploadNewApp() {
        if (appUri != null) {
            FirebaseStorage.getInstance().getReference().child("APK Files/" + appUpFile.getText() + ".apk")
                    .getDownloadUrl().addOnSuccessListener(uri -> {
                dialog.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(UploadAppActivity.this, "Updated app already available", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
            }).addOnFailureListener(e -> {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().
                        child("APK Files/" + appUpFile.getText() + ".apk");
                storageReference.putFile(appUri).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {

                    appUrl = uri.toString();
                    appExtension = getExtension(appUri);

                    GeneralData data = new GeneralData();
                    data.setAppExtension(appExtension);
                    data.setAppVersionNo(versionNo);
                    data.setAppName(appName);
                    data.setAppUrl(appUrl);
                    data.setAppVersionNote(appVersionNote);

                    FirebaseDatabase.getInstance().getReference("Latest AppData").setValue(data).
                            addOnSuccessListener(unused -> {
                                Objects.requireNonNull(appVersionNo.getText()).clear();
                                Objects.requireNonNull(appUpFile.getText()).clear();
                                Objects.requireNonNull(appUpNote.getText()).clear();
                            }).addOnFailureListener(e1 -> {
                        appUpFile.setText(R.string.firebaseUpdateError);
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                        StyleableToast.makeText(UploadAppActivity.this, "App Data Updating in Firebase Error: " + e1.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                    });

                    dialog.dismiss();
                    StyleableToast.makeText(UploadAppActivity.this, "Uploaded Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                }).addOnFailureListener(e12 -> {
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(UploadAppActivity.this, "Failed to capture app URL: " + e12.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                    appUpFile.setText(R.string.uploadError);
                })).addOnFailureListener(e1 -> {
                    dialog.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(UploadAppActivity.this, "Updated App Upload Error: " + e1.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                }).addOnProgressListener(snapshot -> {
                    dialog.show();
                    ProgressBar downloadDialogBar = dialog.findViewById(R.id.uploadDialogBar);
                    TextView uploadDialog2 = dialog.findViewById(R.id.uploadDialog2);
                    final int progress = (int) ((snapshot.getBytesTransferred() * 100L) / snapshot.getTotalByteCount());
                    downloadDialogBar.setProgress(progress);
                    uploadDialog2.setText((snapshot.getBytesTransferred()) / (1024L) + " KB of " + (snapshot.getTotalByteCount()) / (1024L) + " KB");
                });
            });
        } else {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(UploadAppActivity.this, "Please select an updated APK file", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        }
    }

    private String getExtension(Uri appUri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap typeMap = MimeTypeMap.getSingleton();
        return typeMap.getExtensionFromMimeType(resolver.getType(appUri));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ConnectionCheck.connectionDialogShow(getApplicationContext(), this);
    }
}