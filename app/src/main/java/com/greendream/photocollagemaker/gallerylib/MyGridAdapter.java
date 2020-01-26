package com.greendream.photocollagemaker.gallerylib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.greendream.photocollagemaker.BuildConfig;
import com.greendream.photocollagemaker.R;

import java.lang.ref.WeakReference;
import java.util.List;

public class MyGridAdapter extends BaseAdapter {
    private static final String TAG = "MyGridAdapter";
    Context context;
    GridView gridView;
    LayoutInflater inflater;
    List<GridViewItem> items;
    Bitmap placeHolder;

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            this.bitmapWorkerTaskReference = new WeakReference(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return (BitmapWorkerTask) this.bitmapWorkerTaskReference.get();
        }
    }

    class BitmapWorkerTask extends AsyncTask<Long, Void, Bitmap> {
        private long data = 0;
        private final WeakReference<ImageView> imageViewReference;
        private GridViewItem item;

        public BitmapWorkerTask(ImageView imageView, GridViewItem item) {
            this.imageViewReference = new WeakReference(imageView);
            this.item = item;
        }

        protected Bitmap doInBackground(Long... params) {
            this.data = params[0].longValue();
            return this.item.getImage();
        }

        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            if (bitmap != null) {
                ImageView imageView = (ImageView) this.imageViewReference.get();
                if (this == MyGridAdapter.getBitmapWorkerTask(imageView)) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    static class ViewHolder {
        ImageView imageView;
        TextView selectedCount;
        View textContainer;
        TextView textCount;
        TextView textPath;

        ViewHolder() {
        }
    }

    @SuppressLint({"WrongConstant"})
    public MyGridAdapter(Context context, List<GridViewItem> items, GridView gridView) {
        this.items = items;
        this.inflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.gridView = gridView;
        this.placeHolder = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_pattern);
        this.context = context;
    }

    public int getCount() {
        return this.items.size();
    }

    public Object getItem(int position) {
        return this.items.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    @SuppressLint({"NewApi", "WrongConstant"})
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.grid_item, null);
            viewHolder = new ViewHolder();
            viewHolder.textPath = (TextView) convertView.findViewById(R.id.textView_path);


            viewHolder.textPath.setSelected(true);
            viewHolder.textPath.setSingleLine(true);
            viewHolder.textPath.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            viewHolder.textPath.setHorizontallyScrolling(true);
            viewHolder.textPath.setLines(1);
            viewHolder.textPath.setMarqueeRepeatLimit(-1);

            viewHolder.textCount = (TextView) convertView.findViewById(R.id.textViewCount);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.textContainer = convertView.findViewById(R.id.grid_item_text_container);
            viewHolder.selectedCount = (TextView) convertView.findViewById(R.id.textViewSelectedItemCount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String folderName = ((GridViewItem) this.items.get(position)).getFolderName();
        if (folderName == null || folderName.length() == 0) {
            if (viewHolder.textContainer.getVisibility() == 0) {
                viewHolder.textContainer.setVisibility(8);
            }
            if (((GridViewItem) this.items.get(position)).selectedItemCount > 0) {
                viewHolder.selectedCount.setText(BuildConfig.FLAVOR + ((GridViewItem) this.items.get(position)).selectedItemCount);
                if (viewHolder.selectedCount.getVisibility() == 4) {
                    viewHolder.selectedCount.setVisibility(0);
                }
            } else if (viewHolder.selectedCount.getVisibility() == 0) {
                viewHolder.selectedCount.setVisibility(4);
            }
        } else {
            if (viewHolder.textContainer.getVisibility() == 8) {
                viewHolder.textContainer.setVisibility(0);
            }
            viewHolder.textPath.setText(((GridViewItem) this.items.get(position)).getFolderName());
            viewHolder.textCount.setText(((GridViewItem) this.items.get(position)).count);
            if (viewHolder.selectedCount.getVisibility() == 0) {
                viewHolder.selectedCount.setVisibility(4);
            }
        }
        loadBitmap((long) position, viewHolder.imageView, (GridViewItem) this.items.get(position));
        return convertView;
    }

    public void loadBitmap(long resId, ImageView imageView, GridViewItem item) {
        if (cancelPotentialWork(resId, imageView)) {
            BitmapWorkerTask task = new BitmapWorkerTask(imageView, item);
            imageView.setImageDrawable(new AsyncDrawable(this.context.getResources(), this.placeHolder, task));
            task.execute(new Long[]{Long.valueOf(resId)});
        }
    }

    public static boolean cancelPotentialWork(long data, ImageView imageView) {
        BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask == null) {
            return true;
        }
        long bitmapData = bitmapWorkerTask.data;
        if (bitmapData != 0 && bitmapData == data) {
            return false;
        }
        bitmapWorkerTask.cancel(true);
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                return ((AsyncDrawable) drawable).getBitmapWorkerTask();
            }
        }
        return null;
    }
}
