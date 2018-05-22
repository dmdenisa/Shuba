package shuba.shuba;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import shuba.shuba.model.Contract;


public class Utils {


    public static void LogOut(Activity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        preferences.edit().remove(Contract.Preferences.AUTH_HASH).apply();

        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.finish();
    }
    
    public static Dialog ShowLogOutDialog(Activity activity) {
        Dialog dialog = Dialog.NewInstance(
                activity.getString(R.string.ok),
                activity.getString(R.string.cancel),
                activity.getString(R.string.message_logout),
                null
        );
        dialog.show(activity.getFragmentManager(), "logout");
        return dialog;
    }

}
