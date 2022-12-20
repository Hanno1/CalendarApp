package com.example.projectcalendarapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;
import com.example.projectcalendarapp.ViewModel.MainView;

import java.util.Arrays;
import java.util.List;

public class Constants {
    /*
    this holds all constants as static variables
     */
    // Colors for the mainView
    public static final int NOTTHISMONTHCOLOR = 0, THISMONTHCOLOR = 1, THISMONTHBUTGRAYCOLOR = 3;
    // Borders for the mainView
    public static final int TODAYBORDER = 0, TODAYEVENTBORDER = 1, TODAYSELECTEDBORDER = 2,
            TODAYSELECTEDEVENTBORDER= 3,
            NORMALBORDER = 4, NORMALEVENTBORDER = 5, NORMALSELECTEDBORDER = 6,
            NORMALSELECTEDEVENTBORDER = 7;
    // Icon ids for the mainView
    public static final int ICON_ID_NORMAL = 0, ICON_ID_BIRTHDAY = 1, ICON_ID_MEETING = 2,
            ICON_ID_MAIL = 3;

    // MainController for the mainView - this is not a good solution but the only one i could find
    public static MainController mainController;

    public static void setBorder(MainView view, int borderType, TextView tv){
        /*
        set the border of a textview to the border type
         */
        switch (borderType){
            case Constants.NORMALBORDER: tv.setBackground(ContextCompat.getDrawable(view,
                    R.drawable.normal_border)); break;
            case Constants.NORMALEVENTBORDER: tv.setBackground(ContextCompat.getDrawable(view,
                    R.drawable.normal_event_border)); break;
            case Constants.NORMALSELECTEDBORDER: tv.setBackground(ContextCompat.getDrawable(view,
                    R.drawable.normal_selected_border)); break;
            case Constants.NORMALSELECTEDEVENTBORDER: tv.setBackground(ContextCompat.getDrawable(view,
                    R.drawable.normal_selected_event_border)); break;
            case Constants.TODAYBORDER: tv.setBackground(ContextCompat.getDrawable(view,
                    R.drawable.today_border)); break;
            case Constants.TODAYEVENTBORDER: tv.setBackground(ContextCompat.getDrawable(view,
                    R.drawable.today_event_border)); break;
            case Constants.TODAYSELECTEDBORDER: tv.setBackground(ContextCompat.getDrawable(view,
                    R.drawable.today_selected_border)); break;
            case Constants.TODAYSELECTEDEVENTBORDER: tv.setBackground(ContextCompat.getDrawable(view,
                    R.drawable.today_selected_event_border)); break;
            default:
                System.out.println("Error in Constants (setBorder), Unknown Bordertype: " + borderType);
                System.exit(1);
        }
    }

    public static int setUnselectedTextview(MainView view, int borderType, TextView tv){
        /*
        for a given Textview, reset the border to unselected textview
         */
        switch (borderType){
            case Constants.NORMALSELECTEDBORDER: tv.setBackground(ContextCompat.getDrawable(view,
                    R.drawable.normal_border)); return Constants.NORMALBORDER;
            case Constants.NORMALSELECTEDEVENTBORDER: tv.setBackground(ContextCompat.getDrawable(view,
                    R.drawable.normal_event_border)); return Constants.NORMALEVENTBORDER;
            case Constants.TODAYSELECTEDBORDER: tv.setBackground(ContextCompat.getDrawable(view,
                    R.drawable.today_border)); return Constants.TODAYBORDER;
            case Constants.TODAYSELECTEDEVENTBORDER: tv.setBackground(ContextCompat.getDrawable(view,
                    R.drawable.today_event_border)); return Constants.TODAYEVENTBORDER;
            default:
                System.out.println("Error in Constants (setUnselectedTexview), Unknown Bordertype: " + borderType);
                System.exit(1);
                return -1;
        }
    }

    public static int setSelectedTextview(MainView view, int borderType, TextView tv){
        /*
        for a given Textview, reset the border to unselected textview
         */
        switch (borderType){
            case Constants.NORMALBORDER: tv.setBackground(ContextCompat.getDrawable(view,
                    R.drawable.normal_selected_border)); return Constants.NORMALSELECTEDBORDER;
            case Constants.NORMALEVENTBORDER: tv.setBackground(ContextCompat.getDrawable(view,
                    R.drawable.normal_selected_event_border)); return Constants.NORMALSELECTEDEVENTBORDER;
            case Constants.TODAYBORDER: tv.setBackground(ContextCompat.getDrawable(view,
                    R.drawable.today_selected_border)); return Constants.TODAYSELECTEDBORDER;
            case Constants.TODAYEVENTBORDER: tv.setBackground(ContextCompat.getDrawable(view,
                    R.drawable.today_selected_event_border)); return Constants.TODAYSELECTEDEVENTBORDER;
            default:
                System.out.println("Error in Constants (setSelectedTextView), Unknown Bordertype: " + borderType);
                System.exit(1);
                return -1;
        }
    }

    public static int setEventBorder(MainView view, int borderType, TextView tv){
        /*
        sets the border of a textview to eventBorder
         */
        switch (borderType){
            case Constants.NORMALSELECTEDBORDER:
            case Constants.NORMALSELECTEDEVENTBORDER:
                tv.setBackground(ContextCompat.getDrawable(view,
                    R.drawable.normal_selected_event_border)); return Constants.NORMALSELECTEDEVENTBORDER;
            case Constants.TODAYSELECTEDBORDER:
            case Constants.TODAYSELECTEDEVENTBORDER:
                tv.setBackground(ContextCompat.getDrawable(view,
                    R.drawable.today_selected_event_border)); return Constants.TODAYSELECTEDEVENTBORDER;
            default:
                System.out.println("Error in Constants (setEventBorder), Unknown Bordertype: " + borderType);
                System.exit(1);
                return -1;
        }
    }

    public static List<Intent> POWER_MANAGER_INTENTS = Arrays.asList(
            /*
            needed for the autostart permission
             */
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")).setData(android.net.Uri.parse("mobilemanager://function/entry/AutoStart"))
    );


    public static void startPowerSaverIntent(Context context) {
        /*
        on some devices the app needs a autostart permission - this dialog will ask for it
         */
        SharedPreferences settings = context.getSharedPreferences("ProtectedApps", Context.MODE_PRIVATE);
        boolean skipMessage = settings.getBoolean("skipProtectedAppCheck", false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            boolean foundCorrectIntent = false;
            for (Intent intent : POWER_MANAGER_INTENTS) {
                if (isCallable(context, intent)) {
                    foundCorrectIntent = true;

                    // build the custom autostart dialog
                    Dialog autostartDialog = new Dialog(context);
                    autostartDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    autostartDialog.setCancelable(true);

                    autostartDialog.setContentView(R.layout.layout_autostart_permission);
                    autostartDialog.setTitle(Build.MANUFACTURER + " Autostart Apps");

                    AppCompatButton submit_btn = autostartDialog.findViewById(R.id.autostart_dialog_submit_btn);
                    AppCompatButton cancel_btn = autostartDialog.findViewById(R.id.autostart_dialog_cancel_btn);

                    CheckBox doNotShowAgain = autostartDialog.findViewById(R.id.cb_dont_show_again);
                    doNotShowAgain.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        editor.putBoolean("skipProtectedAppCheck", isChecked);
                        editor.apply();
                    });

                    submit_btn.setOnClickListener(v -> {
                        context.startActivity(intent);
                        autostartDialog.dismiss();
                    });
                    cancel_btn.setOnClickListener(v -> autostartDialog.dismiss());

                    autostartDialog.show();
                    break;
                }
            }
            if (!foundCorrectIntent) {
                editor.putBoolean("skipProtectedAppCheck", true);
                editor.apply();
            }
        }
    }


    private static boolean isCallable(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
