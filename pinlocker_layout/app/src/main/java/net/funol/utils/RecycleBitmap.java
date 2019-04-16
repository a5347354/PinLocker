package net.funol.utils;

/**
 * Created by fan on 2015/7/25.
 */
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;

public class RecycleBitmap {

    /**
     * 清理map中的bitmap;
     * @param imgCache ImageCacheMap
     * @param maxSize 允許增加加的最大圖片數量
     * @param freeSize 釋放掉圖片的數量;
     */
    public static void recycleMapCache(LinkedHashMap<String, Bitmap> imgCache,int maxSize,int freeSize){
        //超出最大容量時清理
        if (imgCache.values().size() > maxSize) {
            synchronized(imgCache){
                Iterator<String> it = imgCache.keySet().iterator();
                while( it.hasNext()&&(imgCache.keySet().size()>freeSize)){
                    Bitmap bmp=imgCache.get(it.next());
                    if(bmp!=null&&!bmp.isRecycled()) {
                        bmp.recycle();
                        bmp=null;
                    }
                    it.remove();
                }
            }
            System.gc();
        }
    }


    /**
     * 清理View中的ImagView被BitMap佔用的內存;
     * @param mapViews 一個View的合集
     */
    public static void recycle(Map<View,int[]> mapViews){
        synchronized(mapViews){
            Iterator<View> it = mapViews.keySet().iterator();
            while(it.hasNext()){
                //獲取佈局
                View view=it.next();
                if(view==null) return;
                //獲取要佈局內要回收的ids;
                int [] recycleIds=mapViews.get(view);

                //如果是listView,先找到每個佈局文件.重要提示:每個ImagView在佈局文件的第一層;
                if ((view instanceof AbsListView)) {
                    recycleAbsList((AbsListView)view, recycleIds);
                }
                //如果是ImageView,直接回收;
                else if(view instanceof ImageView){
                    recycleImageView(view);
                }
                //如果是ViewGroup,重要提示:每個ImagView在ViewGroup的第二層;
                else if ((view instanceof ViewGroup)) {
                    recycleViewGroup((ViewGroup)view, recycleIds);
                }
            }
        }
        System.gc();
    }


    /**
     * 回收繼承自AbsListView的類,如GridView,ListView等
     * @param absView
     * @param recycleIds 要清理的Id的集合;
     */
    public static void recycleAbsList(AbsListView absView,int[]recycleIds){
        if(absView==null) return;
        synchronized(absView){
            //回收當前顯示的區域
            for (int index = absView.getFirstVisiblePosition(); index <= absView.getLastVisiblePosition(); index++) {
                //獲取每一個顯示區域的具體ItemView
                ViewGroup views = (ViewGroup) absView.getAdapter().getView(index, null, absView);
                for(int count=0;count<recycleIds.length;count++){
                    recycleImageView(views.findViewById(recycleIds[count]));
                }
            }
        }
    }


    /**
     * 回收繼承自AbsListView的類,如GridView,ListView等
     * @param absView
     * @param recycleIds 要清理的Id的集合;
     */
    public static void recycleViewGroup(ViewGroup layout,int[]recycleIds){
        if(layout==null) return;
        synchronized(layout){
            for (int i = 0; i < layout.getChildCount(); i++) {
                View subView = layout.getChildAt(i);
                if (subView instanceof ViewGroup) {
                    for(int count=0;count<recycleIds.length;count++){
                        recycleImageView(subView.findViewById(recycleIds[count]));
                    }
                } else {
                    if (subView instanceof ImageView) {
                        recycleImageView((ImageView)subView);
                    }
                }
            }
        }
    }


    /**
     * 回收ImageView佔用的圖像內存;
     * @param view
     */
    public static void recycleImageView(View view){
        if(view==null) return;
        if(view instanceof ImageView){
            Drawable drawable=((ImageView) view).getDrawable();
            if(drawable instanceof BitmapDrawable){
                Bitmap bmp = ((BitmapDrawable)drawable).getBitmap();
                if (bmp != null && !bmp.isRecycled()){
                    ((ImageView) view).setImageBitmap(null);
                    bmp.recycle();
                    bmp=null;
                }
            }
        }
    }
}