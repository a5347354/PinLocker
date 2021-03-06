package net.funol.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import net.funol.bean.ImageFloder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by DZY-ZWW on 02-13.
 */
public class AlbumScanner {

    private Context context;

    private EventHandler mEventHandler;

    private Map<String, ImageFloder> mFolderMap;
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mScannedPaths = new HashSet<String>();
    /**
     * 存在文件中的圖片數量
     */
    private int mImageCountOfFolder;
    /**
     * 图片数量最多的文件夹
     */
    private File mFolderWithMostImages;

    private int totalImagesCount = 0;

    public AlbumScanner(Context context) {
        this.context = context;
    }

    public void start() {

        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            // 没有檢測到記憶卡
            if (mEventHandler != null) {
                mEventHandler.onError();
            }
            return;
        }

        // 開始掃瞄
        if (mEventHandler != null) {
            mEventHandler.onStart();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                String firstImage = null;

                mFolderMap = new HashMap<String, ImageFloder>();

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = context.getContentResolver();

                // 設定格式
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);

                L.i(mCursor.getCount() + "");
                while (mCursor.moveToNext()) {
                    try {
                        // get image path
                        String path = mCursor.getString(mCursor
                                .getColumnIndex(MediaStore.Images.Media.DATA));

                        L.i(path);
                        // 取出第一張圖片的路徑
                        if (firstImage == null)
                            firstImage = path;
                        // 獲取文件路徑
                        File parentFile = new File(path).getParentFile();
                        if (parentFile == null)
                            continue;
                        String dirPath = parentFile.getAbsolutePath();
                        ImageFloder imageFloder = null;
                        // 利用HashSet防止多次掃描同一個文件夾（不加这个判断，图片多起来还是相当恐怖的~~）
                        if (mScannedPaths.contains(dirPath)) {
                            mFolderMap.get(dirPath).addImage(path);
                            continue;
                        } else {

                            mScannedPaths.add(dirPath);

                            imageFloder = new ImageFloder();
                            imageFloder.setDir(dirPath);
                            imageFloder.setFirstImagePath(path);
                            imageFloder.addImage(path);
                            mFolderMap.put(dirPath, imageFloder);
                        }

                        int picSize = parentFile.list(new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String filename) {
                                if (filename.endsWith(".jpg")
                                        || filename.endsWith(".png")
                                        || filename.endsWith(".jpeg"))
                                    return true;
                                return false;
                            }
                        }).length;
                        totalImagesCount += picSize;

                        if (picSize > mImageCountOfFolder) {
                            mImageCountOfFolder = picSize;
                            mFolderWithMostImages = parentFile;
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                mCursor.close();

                // 掃描完成，HashSet釋放內存
                mScannedPaths = null;

                // 掃描結束
                mEventHandler.onFinish(mFolderMap);

            }
        }).start();
    }

    public void setEventHandler(EventHandler handler) {
        mEventHandler = handler;
    }

    public interface EventHandler {

        /**
         * 掃描開始
         */
        public void onStart();

        /**
         * 掃描出錯
         */
        public void onError();

        /**
         * 掃描結束
         *
         * @param result 掃描結束
         */
        public void onFinish(Map<String, ImageFloder> result);

    }

}
