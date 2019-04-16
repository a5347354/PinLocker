package net.funol.photolocker.listener;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import net.funol.photolocker.R;
import net.funol.photolocker.activity.MainActivity;
import net.funol.photolocker.adapter.PhotosGridAdapter;
import net.funol.utils.AlbumOnlyFileScanner;
import net.funol.utils.LockAlgorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by fan on 2015/7/20.
 */
public class BottomBarOnClickListener implements View.OnClickListener {


    private View contnetView; //存放RootView
    private PhotosGridAdapter mainActivity_photoGirdAdapter;
    private AlbumOnlyFileScanner albumOnlyFileScanner = new AlbumOnlyFileScanner();
    //手機SD卡的路徑
    private final String MYAPP_SD_PATH_STR = albumOnlyFileScanner.getfilePath();
    private String MYAPP_PATH_STR = "/DCIM/Locker";

    private Bitmap scratchBitmap = null;
    LockAlgorithm lockAlgorithm = null;


    /**
     * BottomBarOnClickListener建構子
     * @param rootView 傳回Activity的RootView
     * */
    public BottomBarOnClickListener(View rootView){
        // TODO Auto-generated method stub
        contnetView = rootView;
    }


    /**
     * BottomBarOnClickListener建構子
     * @param rootView  傳回Activity的RootView
     * @param adapter   傳回Activity的Adapter
     * */
    public BottomBarOnClickListener(View rootView,PhotosGridAdapter adapter){
    // TODO Auto-generated method stub
        contnetView = rootView;
        mainActivity_photoGirdAdapter = adapter;
    }


    /**
     * 複寫onClick事件
     * */
    @Override
    public void onClick(View v) {
        final GridView gView1 = (GridView)contnetView.findViewById(R.id.main_photos);
        final int itemCount = gView1.getAdapter().getCount();//找出在GridView項目的數量

        switch (v.getId()){
            /*-----選到全選按鈕，選取所有的Grid中所有的CheckBox-----*/
            case R.id.all_select:
                Log.e("CountItem",""+itemCount);
                for (int i = 0; i < itemCount ; i++) {
                    RelativeLayout itemLayout = (RelativeLayout)gView1.getChildAt(i); // Find by under LinearLayout

                    CheckBox checkbox = (CheckBox) itemLayout.findViewById(R.id.main_selector);
                    checkbox.setChecked(true);
                    Log.e("CheckBoxIDafter",""+i);

                }
                Toast.makeText(v.getContext(), v.getId() + "to" + R.id.all_select, Toast.LENGTH_LONG).show();
                break;
            /*-----刪除照片功能-----*/
            case R.id.delete:
                for (int i = 0; i < itemCount ; i++) {
                    RelativeLayout itemLayout = (RelativeLayout) gView1.getChildAt(i); // Find by under LinearLayout

                    CheckBox checkbox = (CheckBox) itemLayout.findViewById(R.id.main_selector);

                    //如果勾選狀態，將該路徑的圖片刪除
                    if(checkbox.isChecked()) {
                        File deleteFile = new File(gView1.getAdapter().getItem(i).toString());
                        deleteFile.delete();
                        Toast.makeText(v.getContext(), gView1.getAdapter().getItem(i).toString(), Toast.LENGTH_LONG).show();
                    }
                }
                Log.e("BottomBarClick",MYAPP_SD_PATH_STR+MYAPP_PATH_STR);
                mainActivity_photoGirdAdapter.setDatas(albumOnlyFileScanner.getImagePathFromSD(MYAPP_SD_PATH_STR+MYAPP_PATH_STR));
                mainActivity_photoGirdAdapter.notifyDataSetChanged();
                break;
            /*-----解鎖所有相簿裡的圖片-----*/
            case R.id.unlock:
                //從xml取得密碼
                final SharedPreferences settings = v.getContext().getSharedPreferences("Preference", 0);
                final int randSeed = Integer.parseInt(settings.getString("password", ""));

                //彈出是否解鎖警告視窗
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("(解鎖後的圖片會在相簿中看到)");
                builder.setTitle("是否確定解鎖圖片？");
                //確定解鎖
                AlertDialog.Builder positive_Btn = builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        try{
                            for (int i = 0; i < itemCount; i++) {
                                RelativeLayout itemLayout = (RelativeLayout) gView1.getChildAt(i); // Find by under LinearLayout
                                CheckBox checkbox = (CheckBox) itemLayout.findViewById(R.id.main_selector);
                                if (checkbox.isChecked()) {
                                    loadBitmap(gView1.getAdapter().getItem(i).toString());

                                    lockAlgorithm = new LockAlgorithm(scratchBitmap,randSeed);
                                    lockAlgorithm.UnLockBitmap();
                                    File PicPath = new File(gView1.getAdapter().getItem(i).toString());
                                    FileOutputStream out = new FileOutputStream(PicPath);
                                    // 將 Bitmap 壓縮成指定格式的圖片並寫入檔案串流
                                    scratchBitmap.compress(Bitmap.CompressFormat.JPEG , 90 , out);
                                    // 刷新並關閉檔案串流
                                    out.flush ();
                                    out.close ();
                                    scratchBitmap.recycle();


                                    /*lockAlgorithm = new LockAlgorithm(randSeed);
                                    lockAlgorithm.loadBitmap(gView1.getAdapter().getItem(i).toString());
                                    File PicPath = new File(gView1.getAdapter().getItem(i).toString());

                                    FileOutputStream out = null;
                                    try {
                                        out = new FileOutputStream(PicPath);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    // 將 Bitmap 壓縮成指定格式的圖片並寫入檔案串流
                                    lockAlgorithm.UnLockBitmap().compress(Bitmap.CompressFormat.JPEG, 90, out);
                                    // 刷新並關閉檔案串流
                                    try {
                                        out.flush();
                                        out.close();
                                    } catch (IOException e) {
                                        Log.e("BottomBarOnClickListener",e.toString());
                                    }*/
                                }
                                mainActivity_photoGirdAdapter.setDatas(albumOnlyFileScanner.getImagePathFromSD(MYAPP_SD_PATH_STR+MYAPP_PATH_STR));
                                mainActivity_photoGirdAdapter.notifyDataSetChanged();
                            }
                        }catch(Exception e){
                            Log.e("BottomBarOnClickListener",e.toString());
                        }

                    }
                });
                //取消解鎖
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

                break;
        }
    }
    /**
     * 載入圖片到scratchBitmap
     * @param filePath 檔案路徑
     * */
    @SuppressLint("NewApi")
    private void loadBitmap(String filePath) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        if(scratchBitmap != null){
            scratchBitmap.recycle();
            System.gc();
        }
        opts.inMutable = true;  // return a mutable Bitmap instead of an immutable one.
        scratchBitmap = BitmapFactory.decodeFile(filePath , opts);
    }
}
