package com.example.highpath.Utilities;

import android.content.Context;
import android.util.Log;

import java.io.File;

public class StorageManager {

    private static final String TAG = "StrideStorage";
    private static final String PARENT_DIRECTORY = "strideUser";

    public static final String RAW_GPX = "rawGPX";
    public static final String CORRECTED_GPX = "correctedGPX";
    public static final String USER_IMAGES = "userImages";

    public static void initialiseFolders(Context context) {
        File internalFilesDirectory = context.getFilesDir();
        String[] subFolders = {RAW_GPX, CORRECTED_GPX, USER_IMAGES};

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

    public static String getRawGpxPath(Context context) {
        return getFolder(context, RAW_GPX).getAbsolutePath();
    }

    public static String getCorrectedGpxPath(Context context) {
        return getFolder(context, CORRECTED_GPX).getAbsolutePath();
    }
}
