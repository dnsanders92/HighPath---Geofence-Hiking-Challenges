package com.example.highpath.Utilities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.highpath.Database.Entities.UserEntity;
import com.example.highpath.Database.LocalDatabase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class UploadImage {

    // image type constants
    public static final String TYPE_PROFILE = "profile";
    public static final String TYPE_BANNER = "banner";

    private final Fragment fragment;
    private final String type;
    private ActivityResultLauncher<Intent> launcher;

    // callback fired after image is picked and saved
    public interface UploadCallback {
        void onComplete(byte[] imageBytes);
    }

    public UploadImage(Fragment fragment, String type, UploadCallback callback) {
        this.fragment = fragment;
        this.type = type;

        // register gallery result
        launcher = fragment.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            // convert selected image to compressed bytes
                            byte[] bytes = uriToBytes(uri);
                            if (bytes != null) {
                                // save to local db
                                saveToDb(bytes);
                                // update ui via callback
                                if (callback != null) callback.onComplete(bytes);
                            }
                        }
                    }
                }
        );
    }

    // opens the device gallery
    public void launch() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    // converts gallery uri to compressed jpeg bytes
    // scales to max 800px to prevent canvas and db blob size issues
    private byte[] uriToBytes(Uri uri) {
        try {
            Bitmap bitmap;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.Source source = ImageDecoder.createSource(
                        fragment.requireContext().getContentResolver(), uri);
                bitmap = ImageDecoder.decodeBitmap(source);
            } else {
                InputStream inputStream = fragment.requireContext()
                        .getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(inputStream);
            }

            if (bitmap == null) return null;

            // scale down to max 800px on longest side to keep blob size manageable
            int maxSize = 800;
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            if (width > maxSize || height > maxSize) {
                float scale = Math.min((float) maxSize / width, (float) maxSize / height);
                int newWidth = Math.round(width * scale);
                int newHeight = Math.round(height * scale);
                bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
            }

            // compress to jpeg at 60% quality
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            return stream.toByteArray();

        } catch (Exception e) {
            Log.e("UploadImage", "Failed to convert URI to bytes", e);
            return null;
        }
    }

    // save to database and updates profileImage or bannerImage
    private void saveToDb(byte[] bytes) {
        new Thread(() -> {
            LocalDatabase db = LocalDatabase.getInstance(fragment.requireContext());
            UserEntity user = db.userDao().getSingleUser();
            if (user != null) {
                // set correct field based on type
                if (TYPE_PROFILE.equals(type)) {
                    user.profileImage = bytes;
                } else if (TYPE_BANNER.equals(type)) {
                    user.bannerImage = bytes;
                }
                db.userDao().update(user);
                Log.d("UploadImage", type + " image saved to DB.");
            }
        }).start();
    }
}