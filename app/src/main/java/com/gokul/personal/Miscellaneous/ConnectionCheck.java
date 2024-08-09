package com.gokul.personal.Miscellaneous;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.gokul.personal.R;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.ViewHolder;

public class ConnectionCheck {

    public static int getConnectionType(Context context) {
        int result = 0; // Returns connection type. 0: none; 1: mobile data; 2: wifi; 3: vpn
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    result = 2;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    result = 1;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    result = 3;
                }
            }
        }
        return result;
    }

    public static void connectionDialogShow(Context context, Activity activity) {

        DialogPlusBuilder dialogPlusBuilder = DialogPlus.newDialog(activity);
        dialogPlusBuilder.setGravity(Gravity.CENTER);
        dialogPlusBuilder.setMargin(150, 0, 150, 0);
        dialogPlusBuilder.setContentHolder(new ViewHolder(R.layout.no_internet));
        dialogPlusBuilder.setExpanded(false);
        DialogPlus dialog = dialogPlusBuilder.create();
        View view1 = dialog.getHolderView();
        Button retryBtn = view1.findViewById(R.id.retry);

        int type = getConnectionType(context);

        if (type == 0) {
            dialog.show();
            retryBtn.setOnClickListener(v -> {
                int type1 = getConnectionType(context);
                if (type1 == 1 || type1 == 2 || type1 == 3) {
                    dialog.dismiss();
                } else {
                    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            });
        }
    }
}