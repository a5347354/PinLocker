package net.funol.photolocker.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;

import net.funol.photolocker.R;
import net.funol.utils.ImageLoader;
import net.funol.utils.Screen;

import java.util.List;

/**
 * Created by DZY-ZWW on 02-11.
 * Comment by Fan
 */
public class PhotosGridAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;

    private List<Uri> datas;

    private int width = 0;
    //建構子
    public PhotosGridAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        width = Screen.getWidthPixels(context) / 3;
    }

    public void setDatas(List<Uri> datas) {
        this.datas = datas;
    }

    @Override
    public int getCount() {
        if (datas == null) {
            return 0;
        }
        return datas.size();
    }

    @Override
    public Uri getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //初始化GridView的layout，先把layout指給convertView再回傳
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_photos_grid, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            convertView.setLayoutParams(new GridView.LayoutParams(width, width));
            holder.mSelector.setFocusable(false);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }



        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    holder.mSelector.setChecked(!holder.mSelector.isChecked());

            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //convertView.mSelector.setVisibility(View.VISIBLE);
                holder.mSelector.setChecked(true);
                return false;
            }
        });
        //設定預設圖案
        holder.mPhoto.setImageResource(R.mipmap.icon_picture);
        ImageLoader.getInstance().loadImage(getItem(position).toString(), holder.mPhoto);

        return convertView;
    }

    private class ViewHolder {

        ImageView mPhoto;
        CheckBox mSelector;

        public ViewHolder(View view) {
            mPhoto = (ImageView) view.findViewById(R.id.photos_grid_photo);
            mSelector = (CheckBox) view.findViewById(R.id.main_selector);
        }

    }

}
