package com.greendream.photocollagemaker.gallerylib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore.Images.Media;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.greendream.photocollagemaker.BuildConfig;
import com.greendream.photocollagemaker.Glob;
import com.greendream.photocollagemaker.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment implements OnItemClickListener {
    public static int MAX_COLLAGE = 9;
    public static int MAX_MIRROR = 1;
    public static int MAX_SCRAPBOOK = 9;
    private static final String TAG = "GalleryActivity";
    int COLLAGE_IMAGE_LIMIT_MAX = 9;
    int COLLAGE_IMAGE_LIMIT_MIN = 0;
    Activity activity;
    MyGridAdapter adapter;
    List<Album> albumList;
    boolean collageSingleMode = false;
    Context context;
    TextView deleteAllTv;
    LinearLayout footer;
    GalleryListener galleryListener;
    Parcelable gridState;
    GridView gridView;
    TextView headerText;
    ImageView imageBack;
    boolean isMirror = false;
    boolean isOnBucket = true;
    public boolean isScrapBook = false;
    boolean isShape = false;
    private com.facebook.ads.InterstitialAd fbInterstitialAd;
    TextView maxTv;
    TextView nextTv;
    OnClickListener onClickListener = new OnClickListener() {
        @SuppressLint({"WrongConstant"})
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.imageBack) {
                GalleryFragment.this.backtrace();
            }
            if (id == R.id.imageView_delete) {
                View parent = (View) view.getParent();
                if (parent != null && parent.getParent() != null) {
                    int location = ((ViewGroup) parent.getParent()).indexOfChild(parent);
                    GalleryFragment.this.footer.removeView(parent);
                    GalleryFragment.this.deleteAllTv.setText("(" + GalleryFragment.this.footer.getChildCount() + ")");
                    long imageid = ((Long) GalleryFragment.this.selectedImageIdList.remove(location)).longValue();
                    GalleryFragment.this.selectedImageOrientationList.remove(location);
                    Point index = GalleryFragment.this.findItemById(imageid);
                    if (index != null) {
                        GridViewItem gridViewItem = (GridViewItem) ((Album) GalleryFragment.this.albumList.get(index.x)).gridItems.get(index.y);
                        gridViewItem.selectedItemCount--;
                        int value = ((GridViewItem) ((Album) GalleryFragment.this.albumList.get(index.x)).gridItems.get(index.y)).selectedItemCount;
                        if (((Album) GalleryFragment.this.albumList.get(index.x)).gridItems == GalleryFragment.this.adapter.items && GalleryFragment.this.gridView.getFirstVisiblePosition() <= index.y && index.y <= GalleryFragment.this.gridView.getLastVisiblePosition() && GalleryFragment.this.gridView.getChildAt(index.y) != null) {
                            TextView text = (TextView) GalleryFragment.this.gridView.getChildAt(index.y).findViewById(R.id.textViewSelectedItemCount);
                            text.setText(BuildConfig.FLAVOR + value);
                            if (value <= 0 && text.getVisibility() == 0) {
                                text.setVisibility(4);
                            }
                        }
                    }
                } else {
                    return;
                }
            }
            if (!(id != R.id.gallery_delete_all || GalleryFragment.this.footer == null || GalleryFragment.this.footer.getChildCount() == 0)) {
                GalleryFragment.this.removeAllTv.setVisibility(0);
                GalleryFragment.this.maxTv.setVisibility(4);
                GalleryFragment.this.deleteAllTv.setVisibility(4);
                GalleryFragment.this.removeAllTv.startAnimation(GalleryFragment.this.slideInLeft);
            }
            if (id == R.id.gallery_remove_all) {
                GalleryFragment.this.remoAll();
            }
            if (id == R.id.gallery_next) {
                GalleryFragment.this.photosSelectFinished();
                showFbInterstitialAd();
            }
        }
    };
    TextView removeAllTv;
    int selectedBucketId;
    List<Long> selectedImageIdList = new ArrayList();
    List<Integer> selectedImageOrientationList = new ArrayList();
    Animation slideInLeft;

    public interface GalleryListener {
        void onGalleryCancel();

        void onGalleryOkImageArray(long[] jArr, int[] iArr, boolean z, boolean z2, boolean z3);

        void onGalleryOkImageArrayRemoveFragment(long[] jArr, int[] iArr, boolean z, boolean z2);

        void onGalleryOkSingleImage(long j, int i, boolean z, boolean z2);
    }

    @SuppressLint("WrongConstant")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_gallery, container, false);

        if (Glob.isOnline(context)) {
            com.google.android.gms.ads.AdView mAdView = new com.google.android.gms.ads.AdView(context);
            mAdView.setAdSize(com.google.android.gms.ads.AdSize.SMART_BANNER);
            mAdView.setAdUnitId(getString(R.string.banner_ad_id));
            ((RelativeLayout)fragmentView.findViewById(R.id.g_adView)).addView(mAdView);
            mAdView.loadAd(new com.google.android.gms.ads.AdRequest.Builder().build());
        }

        fbInterstitialAd = new com.facebook.ads.InterstitialAd(context, getString(R.string.fb_interstitial));
        if (Glob.isOnline(context)) {
            loadFbInterstitialAd();
        }

        this.footer = (LinearLayout) fragmentView.findViewById(R.id.selected_image_linear);
        this.headerText = (TextView) fragmentView.findViewById(R.id.textView_header);
        this.imageBack = (ImageView) fragmentView.findViewById(R.id.imageBack);
        this.imageBack.setOnClickListener(this.onClickListener);
        this.maxTv = (TextView) fragmentView.findViewById(R.id.gallery_max);
        this.nextTv = (TextView) fragmentView.findViewById(R.id.gallery_next);
        this.nextTv.setOnClickListener(this.onClickListener);
        this.maxTv.setText(String.format(getString(R.string.gallery_lib_max), new Object[]{Integer.valueOf(getLimitMax())}));
        this.deleteAllTv = (TextView) fragmentView.findViewById(R.id.gallery_delete_all);
        this.removeAllTv = (TextView) fragmentView.findViewById(R.id.gallery_remove_all);
        this.slideInLeft = AnimationUtils.loadAnimation(this.context, R.anim.slide_in_left);
        this.deleteAllTv.setOnClickListener(this.onClickListener);
        this.removeAllTv.setOnClickListener(this.onClickListener);
        this.deleteAllTv.setText("(" + this.footer.getChildCount() + ")");
        return fragmentView;
    }


    void showFbInterstitialAd() {
        if (Glob.isOnline(context) && fbInterstitialAd != null) {
            if (fbInterstitialAd.isAdLoaded()) {
                fbInterstitialAd.show();
            }
        }
    }

    void loadFbInterstitialAd() {
        fbInterstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial displayed callback
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                fbInterstitialAd.loadAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Show the ad when it's done loading.

            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });
        fbInterstitialAd.loadAd();
    }

    public void onDestroy() {

        if (fbInterstitialAd != null) {
            fbInterstitialAd.destroy();
        }
        super.onDestroy();
    }
    public void setIsShape(boolean isShape) {
        this.isShape = isShape;
    }

    @SuppressLint("WrongConstant")
    void remoAll() {
        if (this.footer != null) {
            this.footer.removeAllViews();
            if (this.selectedImageIdList != null && this.selectedImageIdList.size() > 0) {
                for (int i = 0; i < this.selectedImageIdList.size(); i++) {
                    Point index = findItemById(((Long) this.selectedImageIdList.get(i)).longValue());
                    if (index != null) {
                        GridViewItem gridViewItem = (GridViewItem) ((Album) this.albumList.get(index.x)).gridItems.get(index.y);
                        gridViewItem.selectedItemCount--;
                        int value = ((GridViewItem) ((Album) this.albumList.get(index.x)).gridItems.get(index.y)).selectedItemCount;
                        if (((Album) this.albumList.get(index.x)).gridItems == this.adapter.items && this.gridView.getFirstVisiblePosition() <= index.y && index.y <= this.gridView.getLastVisiblePosition() && this.gridView.getChildAt(index.y) != null) {
                            TextView text = (TextView) this.gridView.getChildAt(index.y).findViewById(R.id.textViewSelectedItemCount);
                            text.setText(BuildConfig.FLAVOR + value);
                            if (value <= 0 && text.getVisibility() == 0) {
                                text.setVisibility(4);
                            }
                        }
                    }
                }
            }
            if (this.selectedImageIdList != null) {
                this.selectedImageIdList.clear();
            }
            this.selectedImageOrientationList.clear();
            this.deleteAllTv.setText("(" + this.footer.getChildCount() + ")");
            getView().findViewById(R.id.gallery_remove_all).setVisibility(4);
            getView().findViewById(R.id.gallery_max).setVisibility(0);
            this.deleteAllTv.setVisibility(0);
        }
    }

    public int getLimitMax() {
        return this.COLLAGE_IMAGE_LIMIT_MAX;
    }

    public void setGalleryListener(GalleryListener l) {
        this.galleryListener = l;
    }

    public void setIsScrapbook(boolean isScrapbook) {
        this.isScrapBook = isScrapbook;
        setLimitMax(MAX_SCRAPBOOK);
        if (this.selectedImageIdList != null && this.selectedImageIdList.size() > this.COLLAGE_IMAGE_LIMIT_MAX) {
            remoAll();
        } else if (this.footer != null && this.footer.getChildCount() > this.COLLAGE_IMAGE_LIMIT_MAX) {
            remoAll();
        }
    }

       public void setLimitMax(int max) {
        this.COLLAGE_IMAGE_LIMIT_MAX = max;
        if (this.maxTv != null) {
            this.maxTv.setText(String.format(getString(R.string.gallery_lib_max), new Object[]{Integer.valueOf(this.COLLAGE_IMAGE_LIMIT_MAX)}));
        }
    }



    public void onResume() {
        super.onResume();
        if (this.gridView != null) {
            try {
                this.gridState = this.gridView.onSaveInstanceState();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        logGalleryFolders();
        updateListForSelection();
        setGridAdapter();
        if (!this.isOnBucket && this.albumList != null && this.selectedBucketId >= 0 && this.selectedBucketId < this.albumList.size()) {
            this.adapter.items = ((Album) this.albumList.get(this.selectedBucketId)).gridItems;
            if (this.gridView != null) {
                this.gridView.post(new Runnable() {
                    public void run() {
                        if (GalleryFragment.this.gridState != null) {
                            Log.d(GalleryFragment.TAG, "trying to restore listview state..");
                            GalleryFragment.this.gridView.onRestoreInstanceState(GalleryFragment.this.gridState);
                        }
                    }
                });
            }
        }
        this.adapter.notifyDataSetChanged();
    }

    void updateListForSelection() {
        if (this.selectedImageIdList != null && !this.selectedImageIdList.isEmpty()) {
            for (int i = 0; i < this.selectedImageIdList.size(); i++) {
                Point localPoint = findItemById(((Long) this.selectedImageIdList.get(i)).longValue());
                if (localPoint != null) {
                    GridViewItem localGridViewItem = (GridViewItem) ((Album) this.albumList.get(localPoint.x)).gridItems.get(localPoint.y);
                    localGridViewItem.selectedItemCount++;
                }
            }
        }
    }

    public void setIsMirrorSelector(boolean isMirror) {
        this.isMirror = isMirror;
    }

    @SuppressLint("WrongConstant")
    public void setCollageSingleMode(boolean mode) {
        this.collageSingleMode = mode;
        if (mode) {
            if (this.selectedImageIdList != null) {
                for (int i = this.selectedImageIdList.size() - 1; i >= 0; i--) {
                    Point index = findItemById(((Long) this.selectedImageIdList.remove(i)).longValue());
                    if (index != null) {
                        GridViewItem gridViewItem = (GridViewItem) ((Album) this.albumList.get(index.x)).gridItems.get(index.y);
                        gridViewItem.selectedItemCount--;
                        int value = ((GridViewItem) ((Album) this.albumList.get(index.x)).gridItems.get(index.y)).selectedItemCount;
                        if (((Album) this.albumList.get(index.x)).gridItems == this.adapter.items && this.gridView.getFirstVisiblePosition() <= index.y && index.y <= this.gridView.getLastVisiblePosition() && this.gridView.getChildAt(index.y) != null) {
                            TextView text = (TextView) this.gridView.getChildAt(index.y).findViewById(R.id.textViewSelectedItemCount);
                            text.setText(value);
                            if (value <= 0 && text.getVisibility() == 0) {
                                text.setVisibility(4);
                            }
                        }
                    }
                }
            }
            if (this.selectedImageOrientationList != null) {
                this.selectedImageOrientationList.clear();
            }
            if (this.footer != null) {
                this.footer.removeAllViews();
            }
            if (this.deleteAllTv != null) {
                this.deleteAllTv.setText("(0)");
            }
        }
    }

    public void onAttach(Activity act) {
        super.onAttach(act);
        this.context = getActivity();
        this.activity = getActivity();
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        logGalleryFolders();
        setGridAdapter();
    }

    private void setGridAdapter() {
        this.gridView = (GridView) getView().findViewById(R.id.gridView);
        this.adapter = new MyGridAdapter(this.context, ((Album) this.albumList.get(this.albumList.size() - 1)).gridItems, this.gridView);
        this.gridView.setAdapter(this.adapter);
        this.gridView.setOnItemClickListener(this);
    }

    private List<GridViewItem> createGridItemsOnClick(int position) {
        List<GridViewItem> items = new ArrayList();
        Album album = (Album) this.albumList.get(position);
        List<Long> imageIdList = album.imageIdList;
        List<Integer> orientList = album.orientationList;
        for (int i = 0; i < imageIdList.size(); i++) {
            items.add(new GridViewItem(this.activity, BuildConfig.FLAVOR, BuildConfig.FLAVOR, false, ((Long) imageIdList.get(i)).longValue(), ((Integer) orientList.get(i)).intValue()));
        }
        return items;
    }

    private boolean logGalleryFolders() {
        this.albumList = new ArrayList();
        List<Integer> bucketIdList = new ArrayList();
//        Cursor cur = this.context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "bucket_display_name", "bucket_id", "_id", "orientation"}, "1) GROUP BY 1,(2", null, "date_modified DESC");
        Cursor cur = this.context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "bucket_display_name", "bucket_id", "_id", "orientation"}, null, null, "date_modified DESC");
        List<GridViewItem> items;
        int i;
        if (cur == null || !cur.moveToFirst()) {
            items = new ArrayList();
            for (i = 0; i < this.albumList.size(); i++) {
                items.add(new GridViewItem(this.activity, ((Album) this.albumList.get(i)).name, BuildConfig.FLAVOR + ((Album) this.albumList.get(i)).imageIdList.size(), true, ((Album) this.albumList.get(i)).imageIdForThumb, ((Integer) ((Album) this.albumList.get(i)).orientationList.get(0)).intValue()));
            }
            this.albumList.add(new Album());
            ((Album) this.albumList.get(this.albumList.size() - 1)).gridItems = items;
            for (i = 0; i < this.albumList.size() - 1; i++) {
                ((Album) this.albumList.get(i)).gridItems = createGridItemsOnClick(i);
            }
            return true;
        }
        int bucketColumn = cur.getColumnIndex("bucket_display_name");
        int bucketId = cur.getColumnIndex("bucket_id");
        int imageId = cur.getColumnIndex("_id");
        int orientationColumnIndex = cur.getColumnIndex("orientation");
        do {
            Album album = new Album();
            int id = cur.getInt(bucketId);
            album.ID = id;
            if (bucketIdList.contains(Integer.valueOf(id))) {
                Album albumFromList = (Album) this.albumList.get(bucketIdList.indexOf(Integer.valueOf(album.ID)));
                albumFromList.imageIdList.add(Long.valueOf(cur.getLong(imageId)));
                albumFromList.orientationList.add(Integer.valueOf(cur.getInt(orientationColumnIndex)));
            } else {
                String bucket = cur.getString(bucketColumn);
                bucketIdList.add(Integer.valueOf(id));
                album.name = bucket;
                album.imageIdForThumb = cur.getLong(imageId);
                album.imageIdList.add(Long.valueOf(album.imageIdForThumb));
                this.albumList.add(album);
                album.orientationList.add(Integer.valueOf(cur.getInt(orientationColumnIndex)));
            }
        } while (cur.moveToNext());
        items = new ArrayList();
        for (i = 0; i < this.albumList.size(); i++) {
            items.add(new GridViewItem(this.activity, ((Album) this.albumList.get(i)).name, BuildConfig.FLAVOR + ((Album) this.albumList.get(i)).imageIdList.size(), true, ((Album) this.albumList.get(i)).imageIdForThumb, ((Integer) ((Album) this.albumList.get(i)).orientationList.get(0)).intValue()));
        }
        this.albumList.add(new Album());
        ((Album) this.albumList.get(this.albumList.size() - 1)).gridItems = items;
        for (i = 0; i < this.albumList.size() - 1; i++) {
            ((Album) this.albumList.get(i)).gridItems = createGridItemsOnClick(i);
        }
        return true;
    }

    public void onBackPressed() {
        backtrace();
    }

    boolean backtrace() {
        if (this.isOnBucket) {
            if (this.galleryListener != null) {
                this.galleryListener.onGalleryCancel();
            }
            return true;
        }
        this.gridView.setNumColumns(3);  //DT 3 Grid
        this.adapter.items = ((Album) this.albumList.get(this.albumList.size() - 1)).gridItems;
        this.adapter.notifyDataSetChanged();
        this.gridView.smoothScrollToPosition(0);
        this.isOnBucket = true;
        this.headerText.setText(getString(R.string.gallery_select_an_album));
        return false;
    }

    @SuppressLint({"WrongConstant"})
    public void onItemClick(AdapterView<?> adapterView, View arg1, int location, long arg3) {
        if (this.isOnBucket) {
            this.gridView.setNumColumns(3);
            this.adapter.items = ((Album) this.albumList.get(location)).gridItems;
            this.adapter.notifyDataSetChanged();
            this.gridView.smoothScrollToPosition(0);
            this.isOnBucket = false;
            this.selectedBucketId = location;
            this.headerText.setText(((Album) this.albumList.get(location)).name);
        } else if (this.footer.getChildCount() >= this.COLLAGE_IMAGE_LIMIT_MAX) {
            @SuppressLint("StringFormatInvalid") Toast msg = Toast.makeText(this.context, String.format(getString(R.string.gallery_no_more), new Object[]{Integer.valueOf(this.COLLAGE_IMAGE_LIMIT_MAX)}), 1);
            msg.setGravity(17, msg.getXOffset() / 2, msg.getYOffset() / 2);
            msg.show();
        } else {
            View retval = LayoutInflater.from(this.context).inflate(R.layout.footer_item, null);
            retval.findViewById(R.id.imageView_delete).setOnClickListener(this.onClickListener);
            ImageView im = (ImageView) retval.findViewById(R.id.imageView);
            if (this.selectedBucketId >= 0 && this.selectedBucketId < this.albumList.size() && location >= 0 && location < ((Album) this.albumList.get(this.selectedBucketId)).imageIdList.size()) {
                long id = ((Long) ((Album) this.albumList.get(this.selectedBucketId)).imageIdList.get(location)).longValue();
                this.selectedImageIdList.add(Long.valueOf(id));
                this.selectedImageOrientationList.add(((Album) this.albumList.get(this.selectedBucketId)).orientationList.get(location));
                Bitmap bm = GalleryUtility.getThumbnailBitmap(this.context, id, ((Integer) ((Album) this.albumList.get(this.selectedBucketId)).orientationList.get(location)).intValue());
                if (bm != null) {
                    im.setImageBitmap(bm);
                }
                this.footer.addView(retval);
                this.deleteAllTv.setText("(" + this.footer.getChildCount() + ")");
                GridViewItem gridViewItem = (GridViewItem) this.adapter.items.get(location);
                gridViewItem.selectedItemCount++;
                TextView text = (TextView) arg1.findViewById(R.id.textViewSelectedItemCount);
                text.setText(BuildConfig.FLAVOR + ((GridViewItem) this.adapter.items.get(location)).selectedItemCount);
                if (text.getVisibility() == 4) {
                    text.setVisibility(0);
                }
                if (this.collageSingleMode) {
                    photosSelectFinished();
                    this.collageSingleMode = false;
                }
            }
        }
    }

    Point findItemById(long id) {
        for (int i = 0; i < this.albumList.size() - 1; i++) {
            List<GridViewItem> list = ((Album) this.albumList.get(i)).gridItems;
            for (int j = 0; j < list.size(); j++) {
                if (((GridViewItem) list.get(j)).imageIdForThumb == id) {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }

    public int getLimitMin() {
        return this.COLLAGE_IMAGE_LIMIT_MIN;
    }

    void photosSelectFinished() {
        int size = this.selectedImageIdList.size();
        if (size <= this.COLLAGE_IMAGE_LIMIT_MIN) {
            @SuppressLint({"StringFormatInvalid", "LocalSuppress"}) Toast msg = Toast.makeText(this.context, String.format(getString(R.string.gallery_select_one), new Object[]{Integer.valueOf(getLimitMin() + 1)}), 1);
            msg.setGravity(17, msg.getXOffset() / 2, msg.getYOffset() / 2);
            msg.show();
            return;
        }
        int i;
        long[] arrr = new long[size];
        for (i = 0; i < size; i++) {
            arrr[i] = ((Long) this.selectedImageIdList.get(i)).longValue();
        }
        int[] orientationArr = new int[size];
        for (i = 0; i < size; i++) {
            orientationArr[i] = ((Integer) this.selectedImageOrientationList.get(i)).intValue();
        }
        if (this.galleryListener != null) {
            this.galleryListener.onGalleryOkImageArray(arrr, orientationArr, this.isScrapBook, this.isShape, this.isMirror);
            return;
        }
        try {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
