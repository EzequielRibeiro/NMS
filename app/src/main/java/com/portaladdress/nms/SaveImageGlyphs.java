package com.portaladdress.nms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;


public class SaveImageGlyphs {


    public void getPrint(LinearLayout content, Context context) throws IOException {

        Activity activity = (Activity) context;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        content.setDrawingCacheEnabled(true);
        Bitmap bitmap = content.getDrawingCache();
        File file = null, f = null;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Glyphs");
            if (!file.exists()) {
                file.mkdirs();

            }
            f = new File(file.getAbsolutePath() + file.separator + "glyphs.png");
        }
        FileOutputStream ostream = new FileOutputStream(f);
        bitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream);
        ostream.close();

        Uri uri = FileProvider.getUriForFile(activity, activity.getBaseContext().
                getApplicationContext().getPackageName() + ".com.portaladdress.provider.ImageFileProvider", f);
       // Intent intent = new Intent(Intent.ACTION_SENDTO);
       // intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
       // intent.setDataAndType(uri, "image/*");
       // activity.startActivity(intent);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/*");
        content.destroyDrawingCache();
        activity.startActivity(Intent.createChooser(shareIntent, "Send to:"));

    } 
 

}
    

