package net.funol.utils;

import android.app.Activity;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by fan on 2015/7/15.
 * 掃描一個資料夾下面的檔案，並抓出路徑
 * */
public class AlbumOnlyFileScanner {


    //得到該資料夾下檔案所有的路徑
    public List<Uri> getImagePathFromSD(String filePath) {
        //圖片列表
        List<Uri> imagePathList = new ArrayList<Uri>();
        //得到SD卡內image文件夾的路徑   File.separator(/)
        //String filePath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Locker/";
        //String filePath = getfilePath() + "/DCIM/Locker";
        // 得到該路徑文件下的所有文件
        File fileAll = new File(filePath);
        File[] files = fileAll.listFiles();
        // 將所有的文件存入ArrayList，並過濾所有圖片格式的文件
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (checkIsImageFile(file.getPath())) {
                imagePathList.add(Uri.parse(file.getPath()));
            }
        }
        // 返回得到的圖片列表
        return imagePathList;
    }


    /**
     * 檢查副檔名
     * @param fName 檔案名稱
     * @return 回傳是否為jpg,png等的副檔名
     * */
    private boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        // 獲取副檔名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif")
                || FileEnd.equals("jpeg")|| FileEnd.equals("bmp") ) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }


    /**
     * 取得可使用在App的路徑(SD卡或內存)
     * @return  回傳一個路徑String
     */
    
    public String getfilePath(){
        String path_SD = null;
        for (String path : getExtSDCardPath()){
            path_SD = path;
            Log.e("SD_Path",path_SD.toString());
        }

        if( path_SD != null)
            return path_SD;
        else
            return Environment.getExternalStorageDirectory().getPath();
    }


    /**
     * 取得外接SD卡路徑
     * @return  回傳一條紀錄或著為空值
     */

    public List<String> getExtSDCardPath()
    {
        List<String> lResult = new ArrayList<String>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("extSdCard"))
                {
                    String [] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory())
                    {
                        lResult.add(path);
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
            Log.e("AblumOnlyFileScanner-getExtSDCardPath",e.toString());
        }
        return lResult;
    }
}