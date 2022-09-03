package com.gokul.personal.AdminActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.fragment.app.Fragment;

import com.gokul.personal.Miscellaneous.ConnectionCheck;
import com.gokul.personal.Miscellaneous.GeneralData;
import com.gokul.personal.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

import static android.content.Context.VIBRATOR_SERVICE;

public class AppUploadFragment extends Fragment {

    private TextInputLayout appUpVersionLayout, appUpFileLayout, appUpNoteLayout;
    private TextInputEditText appVersionNo, appUpNote;
    private AutoCompleteTextView appUpFile;
    private Vibrator vibrator;
    private Dialog dialog1;
    private String versionNo, appVersionNote, appName, appUrl, appExtension;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_app_upload, container, false);

        appUpVersionLayout = view.findViewById(R.id.appUpVersionLayout);
        appUpFileLayout = view.findViewById(R.id.appUpFileLayout);
        appUpNoteLayout = view.findViewById(R.id.appUpNoteLayout);
        Button appUploadButton = view.findViewById(R.id.appUploadButton);
        appVersionNo = view.findViewById(R.id.appVersionNo);
        appUpFile = view.findViewById(R.id.appUpFile);
        appUpNote = view.findViewById(R.id.appUpNote);

        vibrator = (Vibrator) requireActivity().getSystemService((VIBRATOR_SERVICE));

        dialog1 = new Dialog(view.getContext());
        dialog1.setContentView(R.layout.uploading_dialog);
        dialog1.setCanceledOnTouchOutside(false);
        if (dialog1.getWindow() != null) {
            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        appUpNote.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                appUploadCall(view);
                return true;
            }
            return false;
        });

        appUpFileLayout.setClickable(true);
        appUpFile.setInputType(InputType.TYPE_NULL);
        appUpFile.setClickable(true);
        appUpFile.setFocusable(false);
        appUpFile.setOnClickListener(view1 -> {
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
        appUploadButton.setOnClickListener(view2 -> appUploadCall(view));

        return view;
    }

    private void appUploadCall(View view) {
        versionNo = Objects.requireNonNull(appVersionNo.getText()).toString().trim();
        appName = Objects.requireNonNull(appUpFile.getText()).toString().trim();
        appVersionNote = Objects.requireNonNull(appUpNote.getText()).toString().trim();

        if (ConnectionCheck.getConnectionType(view.getContext()) == 0) {
            ConnectionCheck.connectionDialogShow(view.getContext(), requireActivity());
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
                UIUtil.hideKeyboard(requireActivity());
                uploadNewApp(view);
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private void uploadNewApp(View view) {
        if (appUri != null) {
            FirebaseStorage.getInstance().getReference().child("APK Files/" + appUpFile.getText() + ".apk")
                    .getDownloadUrl().addOnSuccessListener(uri -> {
                dialog1.dismiss();
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                StyleableToast.makeText(view.getContext(), "Updated app already available", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
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

                    databaseReference.child("Latest AppData").setValue(data).
                            addOnSuccessListener(unused -> {
                                Objects.requireNonNull(appVersionNo.getText()).clear();
                                Objects.requireNonNull(appUpFile.getText()).clear();
                                Objects.requireNonNull(appUpNote.getText()).clear();
                            }).addOnFailureListener(e1 -> {
                        appUpFile.setText(R.string.firebaseUpdateError);
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                        StyleableToast.makeText(view.getContext(), "App Data Updating in Firebase Error: " + e1.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                    });

                    dialog1.dismiss();
                    StyleableToast.makeText(view.getContext(), "Uploaded Successfully", Toast.LENGTH_LONG, R.style.CustomToast).show();
                }).addOnFailureListener(e12 -> {
                    dialog1.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(view.getContext(), "Failed to capture app URL: " + e12.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                    appUpFile.setText(R.string.uploadError);
                })).addOnFailureListener(e1 -> {
                    dialog1.dismiss();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    StyleableToast.makeText(view.getContext(), "Updated App Upload Error: " + e1.getMessage(), Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
                }).addOnProgressListener(snapshot -> {
                    dialog1.show();
                    ProgressBar downloadDialogBar = dialog1.findViewById(R.id.uploadDialogBar);
                    TextView uploadDialog2 = dialog1.findViewById(R.id.uploadDialog2);
                    final int progress = (int) ((snapshot.getBytesTransferred() * 100L) / snapshot.getTotalByteCount());
                    downloadDialogBar.setProgress(progress);
                    uploadDialog2.setText((snapshot.getBytesTransferred()) / (1024L) + " KB of " + (snapshot.getTotalByteCount()) / (1024L) + " KB");
                });
            });
        } else {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            StyleableToast.makeText(view.getContext(), "Please select an updated APK file", Toast.LENGTH_LONG, R.style.CustomErrorToast).show();
        }
    }

    private String getExtension(Uri appUri) {
        ContentResolver resolver = requireActivity().getContentResolver();
        MimeTypeMap typeMap = MimeTypeMap.getSingleton();
        return typeMap.getExtensionFromMimeType(resolver.getType(appUri));
    }

}