package com.info.commons;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.info.tradewyse.BaseActivity;
import com.info.tradewyse.BuildConfig;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static java.security.AccessController.getContext;

public abstract class ProfileImageAbstractActivity extends BaseActivity {

    private static final String IMAGE_DIRECTORY_NAME = "TradeTips";

    protected static final int REQUEST_CODE_GALLERY = 2004;
    protected static final int REQUEST_CODE_TAKE_PICTURE = 2005;
    private static final int STORAGE_PERMISSION_CODE = 34;
    public static final int CAMERA_PERMISSION_CODE = 39;
    public static final int BARCODE_SCANNER_CODE_MK_TICKET_ADD = 1001;
    public static final int BARCODE_SCANNER_CODE_OTHER_TICKET = 1002;
    public static final int BARCODE_SCANNER_CODE_CASH_VOUCHER = 1003;
    public static final int BARCODE_SCANNER_CODE_COUPON = 1004;
    public static final int BARCODE_SCANNER_CODE_WELFARE = 1005;
    public static final int BARCODE_SCANNER_CODE_MK_TICKET_EDIT = 1006;

    private static String initialPath;
    protected Uri mImageCaptureUri;
    protected Uri cropImageUri;
    private String imageUri;
    private String imagePath = null;
    private boolean doCrop = true;

    private static File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            boolean isCreated = mediaStorageDir.mkdirs();
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;

        initialPath = mediaStorageDir.getPath();

        mediaFile = new File(initialPath + File.separator
                + "IMG_" + timeStamp + ".jpg");


        return mediaFile;
    }

    protected void selectProfileImage(boolean doCrop) {

        this.doCrop = doCrop;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            showProfileSelector();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    private void showProfileSelector() {
        CharSequence[] opts = null;

        if (doCrop) {
            opts = new String[]{"Camera", "Photo Library", "Remove Image",
                    "Cancel"};
        } else {
            opts = new String[]{"From Camera", "From Gallery", "Cancel"};
        }

        final CharSequence[] options = opts;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Document Image Source: ");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].toString().contains("Camera")) {
                    checkCameraPermission();
                } else if (options[item].toString().contains("Library") || options[item].toString().contains("Gallery")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_CODE_GALLERY);

                } else if (options[item].equals("Remove Image")) {
                    //removeCapturedImage();

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePicture();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    public void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            takePicture();
        }
    }


    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                mImageCaptureUri = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        getImageFile(this.getExternalFilesDir(Environment.DIRECTORY_DCIM), null));

            } else {
                mImageCaptureUri = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        getOutputMediaFile());
            }
        } else {
            mImageCaptureUri = Uri.fromFile(getOutputMediaFile());
        }
        Log.d("captureImage", String.valueOf(mImageCaptureUri));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {

                    boolean granted = true;

                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED)
                            granted = false;
                    }

                    if (granted) {
                        //showProfileSelector();
                    } else {
                        Toast.makeText(getApplicationContext(), "External storage permission not granted!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "External storage permission not granted!", Toast.LENGTH_SHORT).show();
                }

                break;
            }

            case CAMERA_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {

                    boolean granted = true;

                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED)
                            granted = false;
                    }

                    if (granted) {
                        takePicture();
                    } else {
                        Toast.makeText(getApplicationContext(), "Camera permission not granted!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Camera permission not granted!", Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PICTURE && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ImageUtils.normalizeImageForUri(this, mImageCaptureUri, getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath());
                imageUri = String.valueOf(mImageCaptureUri);
                imagePath = getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath() + "/" + mImageCaptureUri.getLastPathSegment();

            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P)
            {
                ImageUtils.normalizeImageForUri(this, mImageCaptureUri, getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath());
                imageUri = String.valueOf(mImageCaptureUri);
                imagePath = getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath() + "/" + mImageCaptureUri.getLastPathSegment();

            }
            }
            else {
                ImageUtils.normalizeImageForUri(this, mImageCaptureUri, initialPath);
                imageUri = String.valueOf(mImageCaptureUri);
                imagePath = initialPath + "/" + mImageCaptureUri.getLastPathSegment();
            }
            previewCapturedImage();

        } else if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            /*if (doCrop) {
                getOutputMediaFile();
                String path = ImageUtils.getFilePath(this, initialPath, data.getData());
                beginCrop(Uri.parse(path));
            }  else {*/
            imageUri = String.valueOf(data.getData());
            imagePath = getPath(data.getData());
            previewCapturedImage();
            //}
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);

        }
    }

    private void beginCrop(Uri source) {
        cropImageUri = FileProvider.getUriForFile(this,
                BuildConfig.APPLICATION_ID + ".provider",
                getOutputMediaFile());
//        if (this instanceof CreateContentActivity)
//            Crop.of(source, cropImageUri).start(this);
//        else
//        Crop.of(source, cropImageUri).start(this);

    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            imageUri = cropImageUri.toString();
            imagePath = initialPath + "/" + cropImageUri.getLastPathSegment();
            previewCapturedImage();
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }


    public String getPath(Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    protected abstract void previewCapturedImage();

    /*protected abstract void removeCapturedImage();

    protected abstract void showImageError(String error);*/

    protected void removeImage() {
        imagePath = null;
        imageUri = null;
        cropImageUri = null;
        mImageCaptureUri = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mImageCaptureUri != null)
            outState.putString("cameraImageUri", mImageCaptureUri.toString());
        if (cropImageUri != null)
            outState.putString("cropImageUri", cropImageUri.toString());
    }

    public void openCameraAfterCameraPermission() {
        takePicture();
    }


    /*@Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("cameraImageUri"))
            mImageCaptureUri = Uri.parse(savedInstanceState.getString("cameraImageUri"));
        if (savedInstanceState.containsKey("cropImageUri"))
            cropImageUri = Uri.parse(savedInstanceState.getString("cropImageUri"));
    }*/

    /**
     * Gets image file.
     *
     * @param fileDir   the file dir
     * @param extension the extension
     * @return the image file
     */
    public static File getImageFile(File fileDir, String extension) {
        try {
            // Create an image file name
            String ext = extension != null ? "." + extension : ".jpg";
            String fileName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.getDefault()).format(new Date());
            String imageFileName = fileName + ext;

            // Create Directory If not exist
            if (!fileDir.exists()) fileDir.mkdirs();

            // Create File Object
            File file = new File(fileDir, imageFileName);

            // Create empty file
            file.createNewFile();
            return file;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    protected boolean permissionsAvailable(String[] permissions) {
        boolean granted = true;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                granted = false;
                break;
            }
        }
        return granted;
    }

}
