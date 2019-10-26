package com.wangky.video.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public final class FileUtils {

    public static String getFilePathByUri(Context context, Uri uri) {
        String path = null;
        // 以 file:// 开头的
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            path = uri.getPath();
            return path;
        }
        // 以 content:// 开头的，比如 content://media/extenral/images/media/17766
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
            return path;
        }
        // 4.4及之后的 是以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        path = Environment.getExternalStorageDirectory() + "/" + split[1];
                        return path;
                    }
                } else if (isDownloadsDocument(uri)) {
                    // DownloadsProvider
                    final String id = DocumentsContract.getDocumentId(uri);

                    Cursor cursor = context.getContentResolver().query(uri,null,null,null,null);

                    String fileName ="";
                    //获取文件名
                    if(cursor !=null && cursor.moveToFirst()){
                        int columnIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME); //_display_name
                        fileName = cursor.getString(columnIndex); //returns file n
                    }

                    if(fileName != "" && fileName !=null){
                        path = Uri.withAppendedPath(Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() ),fileName).toString();
                    }

//                    Uri contentUri = uri;
//                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//                            contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
//                            Long.valueOf(id));
//                        }
//                    path = getDataColumn(context, contentUri, null, null);

                    return path;
                } else if (isMediaDocument(uri)) {
                    // MediaProvider
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
                    final String[] selectionArgs = new String[]{split[1]};
                    path = getDataColumn(context, contentUri, selection, selectionArgs);
                    return path;
                }
            }
        }
        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
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



    public static String getFileName(String path){
        String name = path;
        int start = path.lastIndexOf("/");
        int end = path.lastIndexOf(".");
        if(start != -1 && end != -1){
            name = path.substring(start+1,end);
        }

        return name;
    }

    /**
     * 获取文件大小 kb,mb,gb
     * @param size
     * @return
     */
    public static String getFileSize(long size){
        long value = size;
        if(value < 1024){
            return size +"B";
        }else {
            value = new BigDecimal(value / 1024).setScale(2,BigDecimal.ROUND_DOWN).longValue();
        }

        if(value < 1024){//kb
            return value + "KB";
        }else {
            value = new BigDecimal(value / 1024).setScale(2,BigDecimal.ROUND_DOWN).longValue();
        }

        if(value < 1024){ // MB
            return value +"MB";
        }else {
            value = new BigDecimal(value / 1024).setScale(2,BigDecimal.ROUND_DOWN).longValue();
            return value + "GB";
        }
    }



    public static boolean isVideoFile(String fileName){
        int start = fileName.lastIndexOf(".");
        String suffix = fileName.substring(start + 1);
        String[] common={"mp4","avi","rmvb","mkv"};
        List names = Arrays.asList(common);
        return names.contains(suffix);
    }



}