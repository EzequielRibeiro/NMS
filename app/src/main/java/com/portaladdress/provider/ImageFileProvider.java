package com.portaladdress.provider;

import androidx.core.content.FileProvider;

import com.portaladdress.nms.R;

public class ImageFileProvider extends FileProvider {
   public ImageFileProvider() {
       super(R.xml.filepath);
   }
}
