package net.funol.photolocker.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.capricorn.ArcMenu;

import net.funol.photolocker.R;
import net.funol.photolocker.adapter.PhotosGridAdapter;
import net.funol.photolocker.listener.BottomBarOnClickListener;
import net.funol.utils.AlbumOnlyFileScanner;
import net.funol.utils.LockAlgorithm;
import net.funol.utils.Screen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static net.funol.utils.RecycleBitmap.recycleAbsList;

/**
 * 繼承自CustomTitleBarActivity(自行定義)
 *  */

public class MainActivity extends CustomTitleBarActivity {

    //private Button mButton;
    private GridView mPhotosGrid;

    private PhotosGridAdapter adapter;
    private RelativeLayout mFolderListContainer;
    private LinearLayout mainAct_add_LinearLay;
    private CheckBox mainActivity_Check_Chk;
    private static final int REQUEST_CODE = 0;
    //加號item圖片來源
    private static final int[] ITEM_DRAWABLES = { R.drawable.composer_camera,
            R.drawable.composer_photo};
    //自行定義類別，用於掃描特定資料夾下的所有檔案(圖片)
    private AlbumOnlyFileScanner albumOnlyFileScanner = new AlbumOnlyFileScanner();
    //手機SD卡或內存的路徑
    private final String MYAPP_SD_PATH_STR = albumOnlyFileScanner.getfilePath();
    private String MYAPP_PATH_STR = "/DCIM/Locker/";
    private ImageButton mainAct_selectAll_ImgBut;
    private ImageButton mainAct_deleteItem_ImgBut;
    private ImageButton mainAct_unlock_ImgBut;
    private Bitmap scratchBitmap = null;
    private LockAlgorithm lockAlgorithm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        //初始化存取圖片的路徑
        initPath_String();
        Log.e("SD_PATH",MYAPP_SD_PATH_STR);

        //載入GridView我們資料夾的所有圖片
        adapter.setDatas(albumOnlyFileScanner.getImagePathFromSD(MYAPP_SD_PATH_STR+MYAPP_PATH_STR));


        //GridView長按事件發生
        mPhotosGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
                Toast.makeText(view.getContext(), "長按", Toast.LENGTH_LONG).show();
                //加號addMenu隱藏、功能bar顯示
                if(mFolderListContainer.getVisibility() == View.GONE ) {
                    mFolderListContainer.setVisibility(View.VISIBLE);
                    mainAct_add_LinearLay.setVisibility(View.GONE);
                }
                //MainActivity_Check_Chk.setVisibility(View.VISIBLE);
                try {
                    mainActivity_Check_Chk.setChecked(true);
                }catch(Exception e){
                    //Toast.makeText(view.getContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
                   return false;

            }
        });
    }

    /**
     * 當使用者按下動作
     * @param keyCode 按下的按鈕的ASCII
     * */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_SEARCH:
                break;
            case KeyEvent.KEYCODE_BACK:
                //如果下方bar正在顯示，則隱藏，並把加號顯示    否則返回上一頁
                if (mFolderListContainer.getVisibility() == View.VISIBLE) {
                    mFolderListContainer.setVisibility(View.GONE);
                    mainAct_add_LinearLay.setVisibility(View.VISIBLE);
                } else {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                break;
            default:
                return false;
        }
        return false;
    }



    /**
     * 抓取傳回來的URI清單，並丟給適配器
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            List<Uri> uris = data.getParcelableArrayListExtra("photos");
            //檔案名稱
            String fileName_Str = null;
            //將圖片從原本的地方備份到我們的資料夾DCIM/Locker
            try {
                for(int i = 0; i < uris.size() ; i++){
                    fileName_Str = new File(uris.get(i).toString()).getName();
                    copyFile(uris.get(i).toString(),MYAPP_SD_PATH_STR+MYAPP_PATH_STR+fileName_Str);
                    loadBitmap(uris.get(i).toString());
                    //密碼設定
                    //從xml取得密碼
                    final SharedPreferences settings = getSharedPreferences("Preference",0);
                    int randSeed = Integer.parseInt(settings.getString("password", ""));
                    lockAlgorithm = new LockAlgorithm(scratchBitmap,randSeed);
                    lockAlgorithm.LockBitmap();
                    File PicPath = new File(MYAPP_SD_PATH_STR+MYAPP_PATH_STR+fileName_Str);
                    FileOutputStream out = new FileOutputStream(PicPath);
                    // 將 Bitmap 壓縮成指定格式的圖片並寫入檔案串流
                    scratchBitmap.compress(Bitmap.CompressFormat.JPEG , 90 , out);
                    // 刷新並關閉檔案串流
                    out.flush ();
                    out.close ();
                    scratchBitmap.recycle();
                }
            } catch (IOException e) {
                Toast.makeText(MainActivity.this,fileName_Str , Toast.LENGTH_LONG).show();
            }
            Toast.makeText(getApplicationContext(), getSharedPreferences("Preference",0).getString("password", ""), Toast.LENGTH_SHORT).show();
            //設定資料來源，資料來源為我們的資料夾
            adapter.setDatas(albumOnlyFileScanner.getImagePathFromSD(MYAPP_SD_PATH_STR + MYAPP_PATH_STR));
            //更新
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 釋放掉暫存Bitmap(scrathBitmap)
     * */
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if(scratchBitmap != null){
            scratchBitmap.recycle();
            System.gc();
        }

        scratchBitmap = null;

        super.onDestroy();
    }

    @Override
    protected void onBackClicked(View view) {}

    @Override
    protected void onTitleClicked(View view) {}

    @Override
    protected void onFunctionClicked(View view) {}







    /**
     * 初始化一開始的物件(layout)
     * */
    private void initViews() {

        /*初始化加號addMenu清單*/
        ArcMenu mainAct_AddMenu_ArcMenu = (ArcMenu) findViewById(R.id.arc_menu);
        initArcMenu(mainAct_AddMenu_ArcMenu, ITEM_DRAWABLES);

        mTitleBarFunction.setVisibility(View.GONE);
        mTitleBarBack.setVisibility(View.GONE);
        mTitleBarTitle1.setVisibility(View.GONE);
        mTitleBarTitle.setText("AppName");

        //mButton = (Button) findViewById(R.id.main_pick_photo_button);
        //mButton.setOnClickListener(this);

        mPhotosGrid = (GridView) findViewById(R.id.main_photos);
        mPhotosGrid.setColumnWidth(Screen.getWidthPixels(this) / 3);


        //MainActivity下方功能Bar宣告
        mFolderListContainer = (RelativeLayout) findViewById(R.id.main_function_bar_bottom);
        mainAct_add_LinearLay = (LinearLayout) findViewById(R.id.main_add_photo_bottom);

        adapter = new PhotosGridAdapter(this);
        mPhotosGrid.setAdapter(adapter);

        mainActivity_Check_Chk = (CheckBox)findViewById(R.id.main_selector);

        mainAct_selectAll_ImgBut = (ImageButton)findViewById(R.id.all_select);
        mainAct_selectAll_ImgBut.setOnClickListener(new BottomBarOnClickListener(findViewById(android.R.id.content)));
        mainAct_deleteItem_ImgBut = (ImageButton)findViewById(R.id.delete);
        mainAct_deleteItem_ImgBut.setOnClickListener(new BottomBarOnClickListener(findViewById(android.R.id.content),adapter));
        mainAct_unlock_ImgBut = (ImageButton)findViewById(R.id.unlock);
        mainAct_unlock_ImgBut.setOnClickListener(new BottomBarOnClickListener(findViewById(android.R.id.content),adapter));
        //MainActivity_Check_Chk.setVisibility(View.VISIBLE);
    }

    /**
     * 初始化加號清單按鈕
     * @param menu
     * @param itemDrawables 圖片來源ImageSourse
     * */
    private void initArcMenu(ArcMenu menu, int[] itemDrawables) {
        final int itemCount = itemDrawables.length;
        for (int i = 0; i < itemCount; i++) {
            ImageView item = new ImageView(this);
            item.setImageResource(itemDrawables[i]);

            final int position = i;
            menu.addItem(item, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position) {
                        //拍照
                        case 0:
                            Intent intent_camera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent_camera, REQUEST_CODE);
                            break;
                        //圖片瀏覽
                        case 1:
                            Intent intent = new Intent();
                            // intent.setAction(getResources().getString(R.string.action_photo_picker));
                            // intent.setAction(getResources().getString(R.string.action_multi_photo_picker));
                            intent.setData(Uri.parse("pick://images.funol.net/number/20"));
                            startActivityForResult(intent, REQUEST_CODE);
                            Toast.makeText(MainActivity.this, "position:" + position, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
        }
    }


    /**
     * 初始化創建並取得SD卡路徑或內存路徑
     * */
    private void initPath_String(){
        //取得SD卡路徑
        final String FILE_PATH = "/DCIM/Locker";
        File mainAct_FilePath_File;
        mainAct_FilePath_File = new File(MYAPP_SD_PATH_STR+FILE_PATH);
        //如果記憶卡路徑或內存的/DCIM/Locker目錄不存在則創建目錄
        if(!mainAct_FilePath_File.exists())
            mainAct_FilePath_File.mkdirs();
    }


    /**
     * 將點選的圖片複製到我們的資料夾下(String需包含檔名及副檔名)
     * @param rawPath_Str 舊的圖片路徑
     * @param newPath_Str 想複製到的新圖片路徑
     * */
    public void copyFile(String rawPath_Str, String newPath_Str) throws IOException {
        try {

            File rawFile = new File(rawPath_Str);
            File newFile = new File(newPath_Str);
            newFile.createNewFile();
            InputStream in = new FileInputStream(rawFile);
            OutputStream out = new FileOutputStream(newFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            Log.e("copy file error", e.toString());
        }
    }




    @SuppressLint("NewApi")
    private void loadBitmap(String FilePath) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        if(scratchBitmap != null){
            scratchBitmap.recycle();
            System.gc();
        }
        opts.inMutable = true;  // return a mutable Bitmap instead of an immutable one.
        scratchBitmap = BitmapFactory.decodeFile(FilePath , opts);
    }


}
