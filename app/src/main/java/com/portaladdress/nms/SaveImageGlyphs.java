package com.portaladdress.nms;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.widget.LinearLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import androidx.core.content.FileProvider;


public class SaveImageGlyphs {


    public void getPrint(LinearLayout content, Context context) throws IOException {


        content.setDrawingCacheEnabled(true);
        content.buildDrawingCache(true);
        Bitmap bitmap = content.getDrawingCache(true).copy(Bitmap.Config.RGB_565, false);

        content.destroyDrawingCache();
        File file;

        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Glyphs/");

        }else{
            file = new File(Environment.DIRECTORY_PICTURES,"Glyphs/");

         }
        file.mkdirs();


        //create png file
        File fileGlyphs = new File(file, "glyphs.png");
        FileOutputStream fileOutputStream;
        try
        {
            fileOutputStream = new FileOutputStream(fileGlyphs);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        Uri uri =  FileProvider.getUriForFile(Objects.requireNonNull(context),
                BuildConfig.APPLICATION_ID+".provider", fileGlyphs);


        Intent shareIntent = new Intent();
        shareIntent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/*");

        Intent chooser = Intent.createChooser(shareIntent, "Share Glyphs");

        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        context.startActivity(chooser);


    } 
 

}
    

