package com.fangxu.dota2helper.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.math.BigDecimal;

/**
 * Created by dear33 on 2016/8/6.
 */
public class DataClearManager {
    /**
     * * �����Ӧ�����е����� * *
     *
     * @param context
     * @param filepath
     */
    public static void cleanApplicationData(Context context, String... filepath) {
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanFiles(context);
        if (filepath == null) {
            return;
        }
        for (String filePath : filepath) {
            cleanCustomCache(filePath);
        }
    }

    public static String getApplicationDataSize(Context context, String... filepath) {
        long totalSize = 0;
        totalSize += getInternalCacheSize(context);
        totalSize += getExternalCacheSize(context);
        totalSize += getFileSize(context);
        if (filepath != null) {
            for (String file : filepath) {
                totalSize += getCustomCacheSize(file);
            }
        }
        return getFormatSize(totalSize);
    }

    /**
     * * �����Ӧ���ڲ�����(/data/data/com.xxx.xxx/cache) * *
     *
     * @param context
     */
    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    public static long getInternalCacheSize(Context context) {
        return getFolderSize(context.getCacheDir());
    }

    /**
     * * �����Ӧ���������ݿ�(/data/data/com.xxx.xxx/databases) * *
     *
     * @param context
     */
    public static void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File("/data/data/" + context.getPackageName() + "/databases"));
    }

    public static long getDatabaseSize(Context context) {
        return getFolderSize(new File("/data/data/" + context.getPackageName() + "/databases"));
    }

    /**
     * * �����������Ӧ�����ݿ� * *
     *
     * @param context
     * @param dbName
     */
    public static void cleanDatabaseByName(Context context, String dbName) {
        context.deleteDatabase(dbName);
    }

    /**
     * * �����Ӧ��SharedPreference(/data/data/com.xxx.xxx/shared_prefs) *
     *
     * @param context
     */
    public static void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File("/data/data/" + context.getPackageName() + "/shared_prefs"));
    }

    public static long getSharedPreferenceSize(Context context) {
        return getFolderSize(new File("/data/data/" + context.getPackageName() + "/shared_prefs"));
    }

    /**
     * * ���/data/data/com.xxx.xxx/files�µ����� * *
     *
     * @param context
     */
    public static void cleanFiles(Context context) {
        deleteFilesByDirectory(context.getFilesDir());
    }

    public static long getFileSize(Context context) {
        return getFolderSize(context.getFilesDir());
    }

    /**
     * * ����ⲿcache�µ�����(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     *
     * @param context
     */
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    public static long getExternalCacheSize(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            getFolderSize(context.getExternalCacheDir());
        }
        return 0;
    }

    /**
     * * ����Զ���·���µ��ļ���ʹ����С�ģ��벻Ҫ��ɾ������ֻ֧��Ŀ¼�µ��ļ�ɾ�� * *
     *
     * @param filePath
     */
    public static void cleanCustomCache(String filePath) {
        deleteFilesByDirectory(new File(filePath));
    }

    public static long getCustomCacheSize(String filePath) {
        return getFolderSize(new File(filePath));
    }

    /**
     * * ɾ������ ����ֻ��ɾ��ĳ���ļ����µ��ļ�����������directory�Ǹ��ļ������������� * *
     *
     * @param directory
     */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                if (item.isFile()) {
                    item.delete();
                } else {
                    deleteFilesByDirectory(item);
                }
            }
        }
    }

    // ��ȡ�ļ�
    //Context.getExternalFilesDir() --> SDCard/Android/data/���Ӧ�õİ���/files/ Ŀ¼��һ���һЩ��ʱ�䱣�������
    //Context.getExternalCacheDir() --> SDCard/Android/data/���Ӧ�ð���/cache/Ŀ¼��һ������ʱ��������
    public static long getFolderSize(File file) {
        long size = 0;
        File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            // ������滹���ļ�
            if (fileList[i].isDirectory()) {
                size = size + getFolderSize(fileList[i]);
            } else {
                size = size + fileList[i].length();
            }
        }

        return size;
    }

    /**
     * ɾ��ָ��Ŀ¼���ļ���Ŀ¼
     *
     * @param deleteThisPath
     * @param filepath
     * @return
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// ������滹���ļ�
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// ������ļ���ɾ��
                        file.delete();
                    } else {// Ŀ¼
                        if (file.listFiles().length == 0) {// Ŀ¼��û���ļ�����Ŀ¼��ɾ��
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * ��ʽ����λ
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "B";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

}
