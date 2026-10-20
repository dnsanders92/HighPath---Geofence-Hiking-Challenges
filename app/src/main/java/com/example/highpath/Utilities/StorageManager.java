package com.example.highpath.Utilities;

import android.content.Context;
import android.util.Log;

import java.io.File;

public class StorageManager {

    private static final String TAG = "HighPathStorage";
    private static final String PARENT_DIRECTORY = "HighPathUser";

    public static final String GPX_FILES = "gpxFiles";
    public static final String USER_IMAGES = "userImages";

    public static void initialiseFolders(Context context) {
        File internalFilesDirectory = context.getFilesDir();
        String[] subFolders = {GPX_FILES, USER_IMAGES};

        for (String folderName : subFolders) {
            File folder = new File(internalFilesDirectory, PARENT_DIRECTORY + File.separator + folderName);
            if (!folder.exists()) {
                if (folder.mkdirs()) {
                    Log.d(TAG, "Successfully created folder: " + folderName);
                }
            }
        }
    }

    public static File getFolder(Context context, String folderName) {
        File internalFilesDirectory = context.getFilesDir();
        return new File(internalFilesDirectory, PARENT_DIRECTORY + File.separator + folderName);
    }

    public static String getGpxPath(Context context) {
        return getFolder(context, GPX_FILES).getAbsolutePath();
    }

}
