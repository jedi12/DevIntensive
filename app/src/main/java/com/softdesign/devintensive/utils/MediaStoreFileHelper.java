package com.softdesign.devintensive.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.io.File;

public class MediaStoreFileHelper {
    public static File getFileByUri(@NonNull Context context, @NonNull Uri contentUri) {
        Cursor cursor = null;

        if (contentUri.getScheme().equals("file")) {
            return new File(contentUri.getPath());
        }

        try {
            String[] mediaData = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri,  mediaData, null, null, null);
            int column = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            return new File(cursor.getString(column));

        } catch (Exception ex) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
