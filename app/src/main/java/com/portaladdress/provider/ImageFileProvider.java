package com.portaladdress.provider;

import androidx.core.content.FileProvider;

public class ImageFileProvider extends FileProvider {
   public ImageFileProvider() {
       super(R.xml.filepath)
   }
}
