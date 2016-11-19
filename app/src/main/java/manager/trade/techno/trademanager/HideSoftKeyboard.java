package manager.trade.techno.trademanager;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;


public class HideSoftKeyboard {

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
