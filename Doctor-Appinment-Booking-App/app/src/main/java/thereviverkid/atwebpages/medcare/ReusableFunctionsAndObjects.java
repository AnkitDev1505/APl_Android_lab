package thereviverkid.atwebpages.medcare;

import android.content.Context;
import android.content.DialogInterface;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ReusableFunctionsAndObjects {

    public static String Name, Email, MobileNo;

    public static void setValues(String name, String email, String mobileNo) {
        Name = name;
        Email = email;
        MobileNo = mobileNo;
    }

    public static void showMessageAlert(Context context, String title, String message, String buttonstring, byte type) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.Theme_DoctorsAppointment);
        builder.setTitle(title);
        builder.setMessage(message);
        
        if (type == 1) {
            builder.setIcon(android.R.drawable.ic_dialog_info);
        } else {
            builder.setIcon(android.R.drawable.ic_dialog_alert);
        }
        
        builder.setPositiveButton(buttonstring, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        
        builder.show();
    }
}
