package net.funol.photolocker.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import net.funol.bean.ImageFloder;
import net.funol.photolocker.R;
import net.funol.photolocker.adapter.FolderListAdapter;
import net.funol.photolocker.adapter.MultiPhotoPickerAdapter;
import net.funol.utils.AlbumScanner;
import net.funol.utils.L;
import net.funol.utils.Screen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * 繼承自CustomTitleBarActivity(自行定義)
 *  */
public class MultiPhotoPickerActivity extends CustomTitleBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener, MultiPhotoPickerAdapter.ImageSelectChangeListener {

    private MultiPhotoPickerAdapter adapter;
    private GridView mPhotoGrid;

    private LinearLayout mFolderListContainer;
    private ListView mFolderList;

    private TextView mAlbumPicker;
    private TextView mPreview;

    private List<ImageFloder> mFolderDataList;
    private Map<String, ImageFloder> mImageFolderMap;
    private final int REQUEST_PREVIEW_IMAGES = 1;

    private int currentFolderPosition = 0;

    private int MAX_IMAGES = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_photo_picker);

        String action = getIntent().getAction();
        if (getResources().getString(R.string.action_photo_picker).equals(action)) {
            MAX_IMAGES = 1;
        } else if (getResources().getString(R.string.action_multi_photo_picker).equals(action)) {
            MAX_IMAGES = 9;
        } else {
            Uri data = getIntent().getData();
            String path = data.getPath();
            try {
                MAX_IMAGES = Integer.parseInt(path.split("/")[2]);
            } catch (Exception e) {
                L.e("图片数量参数传入错误，使用默认数值");
                MAX_IMAGES = 9;
            }
        }

        initViews();
    }

    private void initViews() {
        mTitleBarTitle.setVisibility(View.GONE);
        mTitleBarTitle1.setVisibility(View.VISIBLE);
        mTitleBarTitle1.setText("所有照片");
        mTitleBarFunction.setEnabled(false);

        mPhotoGrid = (GridView) findViewById(R.id.multi_photo_picker_photo_grid);
        mFolderListContainer = (LinearLayout) findViewById(R.id.multi_photo_picker_photo_folder_list_container);
        mFolderList = (ListView) findViewById(R.id.multi_photo_picker_photo_folder_list);
        mFolderList.setOnItemClickListener(this);

        mAlbumPicker = (TextView) findViewById(R.id.multi_photo_picker_album_picker);
        mAlbumPicker.setOnClickListener(this);
        mPreview = (TextView) findViewById(R.id.multi_photo_preview);
        mPreview.setOnClickListener(this);

        adapter = new MultiPhotoPickerAdapter(this);
        adapter.setMaxImages(MAX_IMAGES);
        adapter.setOnImageSelectChangeListener(this);
        mPhotoGrid.setAdapter(adapter);
        mPhotoGrid.setColumnWidth(Screen.getWidthPixels(this) / 3);

        AlbumScanner scanner = new AlbumScanner(this);
        scanner.setEventHandler(new AlbumScanner.EventHandler() {
            @Override
            public void onStart() {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onFinish(Map<String, ImageFloder> result) {
                mImageFolderMap = result;
                mAlbumScanFinishHandler.sendEmptyMessage(0);
            }
        });
        scanner.start();
    }

    private Handler mAlbumScanFinishHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            mFolderDataList = new ArrayList<ImageFloder>(mImageFolderMap.values());

            FolderListAdapter mFolderListAdapter = new FolderListAdapter(MultiPhotoPickerActivity.this);
            mFolderListAdapter.setDatas(mFolderDataList);
            mFolderList.setAdapter(mFolderListAdapter);

            currentFolderPosition = 0;
            refreshImages();
        }

    };
    //打開資料夾清單
    private void openFolderList() {
        //顯示ListView，預設為gone
        mFolderListContainer.setVisibility(View.VISIBLE);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        translateAnimation.setDuration(200);
        mFolderList.startAnimation(translateAnimation);
    }

    private void closeFolderList() {
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        translateAnimation.setDuration(200);
        mFolderList.startAnimation(translateAnimation);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFolderListContainer.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void refreshImages() {
        mAlbumPicker.setText(mFolderDataList.get(currentFolderPosition).getName());
        adapter.setDatas(mFolderDataList.get(currentFolderPosition));
        adapter.notifyDataSetChanged();
        mPhotoGrid.smoothScrollToPosition(0);
    }

    @Override
    protected void onBackClicked(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onTitleClicked(View view) {

    }

    @Override
    protected void onFunctionClicked(View view) {
        retrunImages(new ArrayList<Uri>(adapter.getSelectedImages()));
    }

    private void retrunImages(ArrayList<Uri> images) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("photos", images);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.multi_photo_picker_album_picker:
                if (mFolderListContainer.getVisibility() == View.GONE) {
                    openFolderList();
                } else {
                    closeFolderList();
                }
                break;
            //預覽按鈕
            case R.id.multi_photo_preview:
                Intent intent = new Intent();
                intent.setClass(this, PreviewImagesActivity.class);
                intent.putParcelableArrayListExtra("photos", new ArrayList<Uri>(adapter.getSelectedImages()));
                startActivityForResult(intent, REQUEST_PREVIEW_IMAGES);
                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PREVIEW_IMAGES && data != null) {
            ArrayList<Uri> uris = data.getParcelableArrayListExtra("photos");
            if (resultCode == RESULT_OK) {
                retrunImages(uris);
            } else {
                updateFolderData(uris);
                refreshImages();
                onSelectChanged(null, -1);
            }
        }
    }

    private void updateFolderData(List<Uri> uris) {

        Set<String> selectedImages = new HashSet<String>();
        for (Uri uri : uris) {
            selectedImages.add(uri.toString());
        }

        Set<String> keys = mImageFolderMap.keySet();
        for (String key : keys) {
            ImageFloder floder = mImageFolderMap.get(key);
            List<Uri> images = floder.getImages();
            int i = 0;
            for (Uri image : images) {
                if (selectedImages.contains(image.toString())) {
                    floder.selectImage(i);
                } else {
                    floder.unSelectedImage(i);
                    adapter.unSelectedImage(image);
                }
                i++;
            }
            mImageFolderMap.put(key, floder);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currentFolderPosition = position;
        closeFolderList();
        refreshImages();
    }

    //當一個項目被選到則button改成「發送(1/20)」
    @Override
    public void onSelectChanged(CheckBox cb, int position) {
        if (adapter.getSelectedImages().size() == 0) {
            mTitleBarFunction.setText("發送");
            mTitleBarFunction.setEnabled(false);
        } else {
            mTitleBarFunction.setText("發送(" + adapter.getSelectedImages().size() + "/" + MAX_IMAGES + ")");
            mTitleBarFunction.setEnabled(true);
        }
    }
}
