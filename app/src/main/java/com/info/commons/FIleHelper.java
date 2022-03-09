package com.info.commons;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import android.util.Log;
import android.webkit.MimeTypeMap;

import com.info.tradewyse.TradwyseApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

/**
 *Class for handling File related operations
 *@author Dinesh Bagvan
 * @version 1.9
 * @since  22/5/17
 */
public class FIleHelper {

    private static final String TAG = FIleHelper.class.getName();
    /**
     * Method for converting Uri into File path
     * @param context
     * @param uri Selected file Uri
     * @return
     */
    public static String getPath(final Context context, final Uri uri) {
        if (uri == null || context == null) {
            return null;
        }

        //final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);

                    String[] contentUriPrefixesToTry = new String[]{
                            "content://downloads/public_downloads",
                            "content://downloads/my_downloads"
                    };

                    for (String contentUriPrefix : contentUriPrefixesToTry) {
                        try {
                            if (id.startsWith("raw:")) {
                                return id.substring(4);
                            }
                            Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));
                            String path = getDataColumn(context, contentUri, null, null);
                            if (path != null) {
                                return path;
                            }
                        } catch (Exception e) {}
                    }
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
        }
        // MediaStore (and general)
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    @Nullable
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        String fileName = null;
        final String _display_name_column = "_display_name";
        final String[] projection = {
                column, _display_name_column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                final int column_name_index = cursor.getColumnIndexOrThrow(_display_name_column);
                fileName = cursor.getString(column_name_index);
                String filePath = cursor.getString(column_index);
                if (!StringHelper.isEmpty(filePath)) {
                    return filePath;
                }
            }
        } /*catch (Exception e) {
           Log.e("Exception",e.toString());
        } */finally {
            if (cursor != null)
                cursor.close();
        }

        if (fileName != null) {
            InputStream input;
            try {
                input = context.getContentResolver().openInputStream(uri);


                File file = FIleHelper.createNewFile(context, fileName);//new File(Environment.getExternalStorageDirectory() + "/" + fileName);
                try {
                    OutputStream output = new FileOutputStream(file);
                    try {
                        byte[] buffer = new byte[4 * 1024]; // or other buffer size
                        int read;
                        while ((read = input.read(buffer)) != -1) {
                            output.write(buffer, 0, read);
                        }
                        output.flush();
                        return file.getAbsolutePath();
                    } finally {
                        output.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (input != null) {
                        input.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Method for creating new File with @params fileName
     * @param context
     * @param fileName New File Name
     * @return New created File
     * @throws IOException
     */
    public static File createNewFile(Context context, String fileName) throws IOException {
        File storageDir = context.getExternalFilesDir("TradeWyse");
        File file = new File(storageDir, fileName);
        Log.e("newFile", file.getAbsolutePath());
        return file;
    }

    public static File createNewTempFile(Context context, String fileName) throws IOException {
        if(context==null){
            return null;
        }
        File storageDir = context.getCacheDir();
        File file = new File(storageDir, fileName);
        Log.e("newFile", file.getAbsolutePath());
        return file;
    }

    public static String createFileName(String prefix){
        return prefix+"_"+System.currentTimeMillis()+".jpg";
    }

    /**
     * Method for checking selected file is PDF or Not
     * @param name Selected File Name
     * @return true, when selected file is PDF
     */
    public static boolean isFilePdf(String name) {
        String picName;
        picName = name.substring(name.lastIndexOf(".") + 1);
        return picName.equalsIgnoreCase("pdf");
    }

    public static boolean isFileJpgOrJpegOrPng(String picName) {

        return picName.equalsIgnoreCase("jpg") || picName.equalsIgnoreCase("jpeg") || picName.equalsIgnoreCase("png");
    }

    /**
     * Method for checking file is exists or Not
     * @param filePath the File path of file to be check
     * @return true , when file is exists
     */
    public static boolean checkImageIsCorrupted(String filePath, Context context) {
        Uri photoFIleUri = Uri.parse(filePath);
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(photoFIleUri), null, o);

            final int REQUIRED_SIZE = 50;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap outputBitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(photoFIleUri), null, o2);
            return !(outputBitmap == null || outputBitmap.getWidth() > -1);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Method for checking selected file is Image/PDF or Not
     * @param fileType Selected File type
     * @return true, when selected file is Image or PDF
     */
    public static boolean isInValidFile(String fileType) {
        if (StringHelper.isEmpty(fileType)) {
            return false;
        }
        return !(fileType.equalsIgnoreCase("jpg")||fileType.equalsIgnoreCase("gif") || fileType.equalsIgnoreCase("jpeg") || fileType.equalsIgnoreCase("png"));
    }

    /**
     * Method for MimeType for uri
     * @param context the Application context
     * @param uri the selected URI
     * @return the Mime type
     */
    public static String getMimeType(Context context, Uri uri) {
        String extension = "";
        try {
            //Check uri format to avoid null
            if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                //If scheme is a content
                final MimeTypeMap mime = MimeTypeMap.getSingleton();
                extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
            } else {
                //If scheme is a File
                //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
                extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

            }

        } catch (Exception e) {
            Log.v(TAG, "" + e.getMessage());
        }

        return extension;
    }

    public static File writeResponseBodyToDisk(ResponseBody body, Context context,String fileName,String fileType) {
        try {

            File futureStudioIconFile = createNewTempFile(context,fileName+"."+fileType);
            if(futureStudioIconFile.length()>0){
                return  futureStudioIconFile;
            }
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("File Download", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return futureStudioIconFile;
            } catch (IOException e) {
                return null;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return null;
        }
    }

    public static Uri getProfileUri(Context context,String imageName){
        File profileFile = null;
        try {
            profileFile = FIleHelper.createNewTempFile(context,imageName+".jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", profileFile);
    }
}
