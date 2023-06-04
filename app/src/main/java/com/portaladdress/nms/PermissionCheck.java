package com.portaladdress.nms;
import static com.portaladdress.nms.MainActivity.PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import java.util.ArrayList;
import java.util.List;

public class PermissionCheck {

    static List<String> permissions = null;

    public static void requestPermission(Activity context){
        if (permissions != null) {
            String[] permissionArray = new String[permissions.size()];
            permissions.toArray(permissionArray);
            ActivityCompat.requestPermissions(context, permissionArray, PERMISSION_REQUEST_CODE);
        }
    }

    public static boolean checkPermission(Activity context) {

    //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

           /* if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                permissions = new ArrayList<>();
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (permissions == null) {
                    permissions = new ArrayList<>();
                }
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (permissions == null) {
                    permissions = new ArrayList<>();
                }
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
              if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (permissions == null) {
                    permissions = new ArrayList<>();
                }
                permissions.add(Manifest.permission.CAMERA);
            }*/

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        context.checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED &&
                        context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (permissions == null) {
                        permissions = new ArrayList<>();
                    }
                    permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                    permissions.add(Manifest.permission.READ_MEDIA_IMAGES);

                } else {
                    return true;
                }
            }

      //  }

        return false;
    }
}

