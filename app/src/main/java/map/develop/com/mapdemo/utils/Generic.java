package map.develop.com.mapdemo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Generic {

  public static boolean isNetworkAvailable(Context _context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) _context.getApplicationContext()
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = null;
    if (connectivityManager != null) {
      activeNetworkInfo = connectivityManager
          .getActiveNetworkInfo();
    }
    return activeNetworkInfo != null;
  }
}
