package ssar.smartcloset.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ssyed on 11/10/14.
 */
public class ToastMessage {

    public static void displayShortToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void displayLongToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
