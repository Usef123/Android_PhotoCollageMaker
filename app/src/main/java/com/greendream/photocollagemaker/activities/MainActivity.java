package com.greendream.photocollagemaker.activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;

import com.facebook.ads.AdSettings;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.greendream.photocollagemaker.BuildConfig;
import com.greendream.photocollagemaker.DrawerItem;
import com.greendream.photocollagemaker.DrawerItemCustomAdapter;
import com.greendream.photocollagemaker.Glob;
import com.greendream.photocollagemaker.PrivacyPolicyActivity;
import com.greendream.photocollagemaker.R;
import com.greendream.photocollagemaker.bitmap.BitmapResizer;
import com.greendream.photocollagemaker.collagelib.CollageActivity;
import com.greendream.photocollagemaker.collagelib.CollageHelper;
import com.greendream.photocollagemaker.gallerylib.GalleryFragment;
import com.greendream.photocollagemaker.imagesavelib.ImageLoader;
import com.greendream.photocollagemaker.imagesavelib.ImageLoader.ImageLoaded;
import com.greendream.photocollagemaker.share.MyCreationActivity;
import com.greendream.photocollagemaker.utils.Utility;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAd;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    String IMAGE_DIRECTORY_NAME = "PhotoCollageMaker";
    int MEDIA_TYPE_IMAGE = 1;
    int PERMISSION_CAMERA_EDITOR = 44;
    int PERMISSION_COLLAGE_EDITOR = 11;
    int PERMISSION_MIRROR_EDITOR = 55;
    int PERMISSION_SCRAPBOOK_EDITOR = 33;
    int PERMISSION_SINGLE_EDITOR = 22;
    int REQUEST_MIRROR = 3;
    Uri fileUri;
    GalleryFragment galleryFragment;
    ImageLoader imageLoader;
    CardView mCameraLayout, mMirrorLayout;
    CardView mCollegeLayout;
    InterstitialAd mInterstitialAd;
    RelativeLayout mMainLayout;
    CardView mScrapbookLayout, mSingleEditorLayout;
    private com.facebook.ads.InterstitialAd fbInterstitialAd;
    private AdView adView;

    private NativeAd nativeExitAd;
    android.app.AlertDialog alertDialog = null;
    private ProgressBar progressBarExitRefresh;
    private LinearLayout adExitChoicesContainer;
    private LinearLayout adExitView;
    public static final int RequestPermissionCode = 1;
    private LinearLayout nativeAdContainerExitAds;
    private long mBackPressed = 0;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private int navigationPosition = 0;
    ActionBarDrawerToggle mDrawerToggle;
    Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdSettings.addTestDevice("b55a7a3e1f0c63b056a49f0adbf96785");

        this.mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.mDrawerList = (ListView) findViewById(R.id.left_drawer);


        setupToolbar();
//      igold
//        DrawerItem[] drawerItem = new DrawerItem[]{new DrawerItem(R.drawable.ic_rate, getResources().getString(R.string.drawer_title_ratting)), new DrawerItem(R.drawable.ic_share, getResources().getString(R.string.drawer_title_share_friend)), new DrawerItem(R.drawable.ic_more_apps, getResources().getString(R.string.drawer_title_more_apps)), new DrawerItem(R.drawable.ic_privacy, getResources().getString(R.string.drawer_title_privacy))};
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        this.mDrawerList.addHeaderView(getLayoutInflater().inflate(R.layout.navigation_header_row, null, false));
//        this.mDrawerList.setAdapter(new DrawerItemCustomAdapter(this, drawerItem));
//        this.mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
//        this.mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        this.mDrawerLayout.setDrawerListener(this.mDrawerToggle);
//        setupDrawerToggle();

        getWindow().addFlags(1024);
        findViewbyIds();
        this.imageLoader = new ImageLoader(this);
        this.imageLoader.setListener(new ImageLoaded() {
            public void callFileSizeAlertDialogBuilder() {
                MainActivity.this.fileSizeAlertDialogBuilder();
            }
        });


        ((CardView) findViewById(R.id.ivmycreation)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MyCreationActivity.class);
                startActivity(i);
                showInterstitialAd();
            }
        });


        //Google
        if (Glob.isOnline(this)) {
            com.google.android.gms.ads.AdView mAdView = new com.google.android.gms.ads.AdView(this);
            mAdView.setAdSize(com.google.android.gms.ads.AdSize.SMART_BANNER);
            mAdView.setAdUnitId(getString(R.string.banner_ad_id));
            ((RelativeLayout) findViewById(R.id.g_adView)).addView(mAdView);

            mAdView.loadAd(new com.google.android.gms.ads.AdRequest.Builder().build());

        }

        this.mInterstitialAd = new InterstitialAd(this);
        this.mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        if (Glob.isOnline(MainActivity.this)) {
            loadInterstitialAd();
        }

        //Fb
        fbInterstitialAd = new com.facebook.ads.InterstitialAd(this, getString(R.string.fb_interstitial));
        if (Glob.isOnline(MainActivity.this)) {
            loadFbInterstitialAd();
        }

        adView = new AdView(this, getString(R.string.fb_medium_rectangle_banner), AdSize.RECTANGLE_HEIGHT_250);
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
        adContainer.addView(adView);
        adView.loadAd();
    }


    void setupToolbar() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private class DrawerItemClickListener implements AdapterView.OnItemClickListener {
        private DrawerItemClickListener() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            MainActivity.this.selectItem(position);
        }
    }

    private void selectItem(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        this.navigationPosition = position;
        switch (position) {
            case 1:
            case 2:
                ShowAfterAdNavigationOption();
                break;
            case 3:
            case 4:
//                runOnUiThread(new C04644());
                MainActivity.this.ShowAfterAdNavigationOption();
                break;
        }
        this.mDrawerLayout.closeDrawers();
    }

//    by iGold
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        this.mDrawerToggle.syncState();
//    }

    void setupDrawerToggle() {
        this.mDrawerToggle = new ActionBarDrawerToggle(this, this.mDrawerLayout, this.toolbar, R.string.app_name, R.string.app_name);
        this.mDrawerToggle.syncState();
    }


    private void ShowAfterAdNavigationOption() {
        switch (this.navigationPosition) {
            case 1:
                if (Glob.isOnline(MainActivity.this)) {


                    try {
                        Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                        startActivity(marketIntent);
                    } catch (ActivityNotFoundException e) {
                        Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                        startActivity(marketIntent);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Available", Toast.LENGTH_SHORT).show();
                }
                return;
            case 2:
                Intent shareIntent = new Intent("android.intent.action.SEND");
                shareIntent.setType("text/*");
                shareIntent.putExtra("android.intent.extra.TEXT", getResources().getString(R.string.app_name) + " Created By :" + "https://play.google.com/store/apps/details?id=" + getPackageName());
                startActivity(Intent.createChooser(shareIntent, "Share App"));
                return;
            case 3:
                if (!Glob.isOnline(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "No Internet Connection..", Toast.LENGTH_SHORT).show();
                    break;
                }
                try {
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse(Glob.acc_link)));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "You don't have Google Play installed", Toast.LENGTH_SHORT).show();
                }
                return;
            case 4:
                if (!Glob.isOnline(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "No Internet Connection..", Toast.LENGTH_SHORT).show();
                    break;
                }
                startActivity(new Intent(getApplicationContext(), PrivacyPolicyActivity.class));
                break;
            default:
                return;
        }
    }


    private void findViewbyIds() {
        this.mMainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        this.mSingleEditorLayout = (CardView) findViewById(R.id.layout_single_editor);
        this.mCameraLayout = (CardView) findViewById(R.id.layout_camera);
        this.mCollegeLayout = (CardView) findViewById(R.id.layout_college);
        this.mMirrorLayout = (CardView) findViewById(R.id.layout_mirror);
        this.mScrapbookLayout = (CardView) findViewById(R.id.layout_scrapbook);
        this.mSingleEditorLayout.setOnClickListener(this);
        this.mCameraLayout.setOnClickListener(this);
        this.mCollegeLayout.setOnClickListener(this);
        this.mMirrorLayout.setOnClickListener(this);
        this.mScrapbookLayout.setOnClickListener(this);
    }

    private void fileSizeAlertDialogBuilder() {
        Point p = BitmapResizer.decodeFileSize(new File(this.imageLoader.selectedImagePath), Utility.maxSizeForDimension(this, 1, 1500.0f));
        if (p == null || p.x != -1) {
            startShaderActivity();
        } else {
            startShaderActivity();
        }
    }

    private void startShaderActivity() {
        Log.e("PhotoBookListActivity.startShade", this.imageLoader.selectedImagePath);
        int maxSize = Utility.maxSizeForDimension(this, 1, 1500.0f);
        Intent shaderIntent = new Intent(getApplicationContext(), MirrorNewActivity.class);
        shaderIntent.putExtra("selectedImagePath", this.imageLoader.selectedImagePath);
        shaderIntent.putExtra("isSession", false);
        shaderIntent.putExtra("MAX_SIZE", maxSize);
        Utility.logFreeMemory(this);
        startActivity(shaderIntent);
    }

    @SuppressLint({"WrongConstant"})
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == this.PERMISSION_COLLAGE_EDITOR) {
            if (ActivityCompat.checkSelfPermission(this, permissions[0]) == 0) {
                openCollage(false, false, false);
                Toast.makeText(this, "Permission granted", 0).show();
                return;
            }
            Toast.makeText(this, "Permission denied", 0).show();
        } else if (requestCode == this.PERMISSION_SINGLE_EDITOR) {
            if (ActivityCompat.checkSelfPermission(this, permissions[0]) == 0) {
                openCollage(true, false, false);
                Toast.makeText(this, "Permission granted", 0).show();
                return;
            }
            Toast.makeText(this, "Permission denied", 0).show();
        } else if (requestCode == this.PERMISSION_SCRAPBOOK_EDITOR) {
            if (ActivityCompat.checkSelfPermission(this, permissions[0]) == 0) {
                openCollage(false, true, false);
                Toast.makeText(this, "Permission granted", 0).show();
                return;
            }
            Toast.makeText(this, "Permission denied", 0).show();
        } else if (requestCode == this.PERMISSION_CAMERA_EDITOR) {
            if (ActivityCompat.checkSelfPermission(this, permissions[0]) == 0) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                this.fileUri = getOutputMediaFileUri(this.MEDIA_TYPE_IMAGE);
                intent.putExtra("output", this.fileUri);
                startActivityForResult(intent, this.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                Toast.makeText(this, "Permission granted", 0).show();
                return;
            }
            Toast.makeText(this, "Permission denied", 0).show();
        } else if (requestCode != this.PERMISSION_MIRROR_EDITOR) {
        } else {
            if (ActivityCompat.checkSelfPermission(this, permissions[0]) == 0) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction("android.intent.action.GET_CONTENT");
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), this.REQUEST_MIRROR);
                Toast.makeText(this, "Permission granted", 0).show();
                return;
            }
            Toast.makeText(this, "Permission denied", 0).show();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("file_uri", this.fileUri);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.fileUri = (Uri) savedInstanceState.getParcelable("file_uri");
    }

    @SuppressLint("WrongConstant")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == this.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                if (resultCode == -1) {
                    Intent localIntent = new Intent(this, CollageActivity.class);
                    System.out.println("CAMERA IMAGE PATH" + this.fileUri.getPath());
                    localIntent.putExtra("selected_image_path", this.fileUri.getPath());
                    startActivity(localIntent);
                } else if (resultCode == 0) {
                    Toast.makeText(getApplicationContext(), "No Image Captured", 0).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", 0).show();
                }
            } else if (resultCode == -1 && requestCode == this.REQUEST_MIRROR) {
                try {
                    this.imageLoader.getImageFromIntent(data);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, BuildConfig.FLAVOR + getString(R.string.error_img_not_found), 0).show();
                }
            }
        } catch (NullPointerException e2) {
            e2.printStackTrace();
        }
    }

    //Google
    void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        this.mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                loadInterstitialAd();
            }
        });
    }

    void showInterstitialAd() {
        if (mInterstitialAd != null && Glob.isOnline(MainActivity.this)) {
            if (mInterstitialAd.isLoaded()) {
                Random rn = new Random();
                int answer = rn.nextInt(70) + 30;
                if (answer < 60) {
                    mInterstitialAd.show();
                }

            }
        }
    }

    //FB
    void showFbInterstitialAd() {
        if (Glob.isOnline(this) && fbInterstitialAd != null) {
            if (fbInterstitialAd.isAdLoaded()) {
                Random rn = new Random();
                int answer = rn.nextInt(70) + 30;
                if (answer < 60) {
                    fbInterstitialAd.show();
                }
            }
        }
    }

    void loadFbInterstitialAd() {
        fbInterstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                fbInterstitialAd.loadAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        });
        fbInterstitialAd.loadAd();
    }


    @SuppressLint("WrongConstant")
    public void onClick(View v) {
        if (this.mCollegeLayout == v) {
            if (VERSION.SDK_INT < 19) {
                showInterstitialAd();
                openCollage(false, false, false);
            } else if (checkAndRequestCollagePermissions()) {
                showInterstitialAd();
                openCollage(false, false, false);
            }
        }
        if (this.mSingleEditorLayout == v) {
            if (VERSION.SDK_INT < 19) {
                showFbInterstitialAd();
                openCollage(true, false, false);
            } else if (checkAndRequestSinglePermissions()) {
                showFbInterstitialAd();
                openCollage(true, false, false);
            }
        }
        if (this.mScrapbookLayout == v) {
            if (VERSION.SDK_INT < 19) {
                showInterstitialAd();
                openCollage(false, true, false);
            } else if (checkAndRequestScrapbookPermissions()) {
                showInterstitialAd();
                openCollage(false, true, false);
            }
        }
        if (this.mCameraLayout == v) {
            Intent intent;
            if (VERSION.SDK_INT < 21) {
                intent = new Intent("android.media.action.IMAGE_CAPTURE");
                this.fileUri = getOutputMediaFileUri(this.MEDIA_TYPE_IMAGE);
                intent.putExtra("output", this.fileUri);
                startActivityForResult(intent, this.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            } else if (checkAndRequestCameraPermissions()) {
                intent = new Intent("android.media.action.IMAGE_CAPTURE");
                this.fileUri = getOutputMediaFileUri(this.MEDIA_TYPE_IMAGE);
                intent.putExtra("output", this.fileUri);
                startActivityForResult(intent, this.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            }
        }
        if (this.mMirrorLayout == v) {
            if (VERSION.SDK_INT < 19) {
                showFbInterstitialAd();
                openCollage(true, true, false, true);
            } else if (checkAndRequestMirrorPermissions()) {
                showFbInterstitialAd();
                openCollage(true, true, false, true);
            }
        }
//        if (this.mRateLayout == v) {
//            if (Glob.isOnline(PhotoBookListActivity.this)) {
//                try {
//                    Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
//                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
//                    startActivity(marketIntent);
//                }catch(ActivityNotFoundException e) {
//                    Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
//                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
//                    startActivity(marketIntent);
//                }
//            } else {
//                Toast.makeText(getApplicationContext(), "No Internet Available", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//        if (this.mMoreappsLayout == v) {
//            if (!Glob.isOnline(PhotoBookListActivity.this)) {
//                Toast.makeText(PhotoBookListActivity.this, "No Internet Connection..", 0).show();
//                return;
//            }
//            try {
//                startActivity(new Intent("android.intent.action.VIEW", Uri.parse(Glob.acc_link)));
//            } catch (ActivityNotFoundException e) {
//                Toast.makeText(this, "You don't have Google Play installed", 1).show();
//            }
//        }
//        if (this.mShareLayout == v) {
//            Intent shareIntent = new Intent("android.intent.action.SEND");
//            shareIntent.setType("text/*");
//            shareIntent.putExtra("android.intent.extra.TEXT", getResources().getString(R.string.app_name) + " Created By :" + "https://play.google.com/store/apps/details?id=" + getPackageName());
//            startActivity(Intent.createChooser(shareIntent, "Share App"));
//        }
//        if (mPrivacyLayout == v) {
//            if (!Glob.isOnline(PhotoBookListActivity.this)) {
//                Toast.makeText(PhotoBookListActivity.this, "No Internet Connection..", 0).show();
//                return;
//            }
//            startActivity(new Intent(getApplicationContext(), PrivacyPolicyActivity.class));
//            showInterstitialAd();
//            return;
//        }
    }

    @SuppressLint({"WrongConstant"})
    public boolean isAvailable(Intent intent) {
        return getPackageManager().queryIntentActivities(intent, 65536).size() > 0;
    }

    public void openCollage(boolean isblur, boolean isScrapBook, boolean isShape) {
        openCollage(isblur, isScrapBook, isShape, false);
    }

    public void openCollage(boolean isblur, boolean isScrapBook, boolean isShape, boolean isMirror) {
        this.galleryFragment = CollageHelper.addGalleryFragment(this, R.id.gallery_fragment_container, null, true, null);
        this.galleryFragment.setCollageSingleMode(isblur);
        this.galleryFragment.setIsMirrorSelector(isMirror);
        this.galleryFragment.setIsScrapbook(isScrapBook);
        this.galleryFragment.setIsShape(isShape);
        if (!isScrapBook) {
            this.galleryFragment.setLimitMax(GalleryFragment.MAX_COLLAGE);
        }
    }

    public void onBackPressed() {
        GalleryFragment localGalleryFragment = CollageHelper.getGalleryFragment(this);
        if (localGalleryFragment == null || !localGalleryFragment.isVisible()) {
//            if (!Glob.isOnline(this)) {
            if (this.mBackPressed + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
            } else {
                Snackbar.make(this.mMainLayout, getString(R.string.txt_press_again_to_exit), -1).show();
            }
            this.mBackPressed = System.currentTimeMillis();
//            } else {
//                showExitDialog();
//            }
        } else {
            localGalleryFragment.onBackPressed();
        }
    }

    public Uri getOutputMediaFileUri(int type) {
        try {
            return Uri.fromFile(/*getOutputMediaFile*/createImageFile(type));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;  //DT return null
    }

    private File createImageFile(int type) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
//        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

//    public File getOutputMediaFile(int type) {
//
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), this.IMAGE_DIRECTORY_NAME);
//
//
//        if (!mediaStorageDir.exists()) {
//            mediaStorageDir.mkdir();
//        }
//
//        if (mediaStorageDir.exists()/* || mediaStorageDir.mkdirs()*/) {
//            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//            if (type == this.MEDIA_TYPE_IMAGE) {
//                return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
//            }
//            return null;
//        }
//        Log.d(this.IMAGE_DIRECTORY_NAME, "Oops! Failed create " + this.IMAGE_DIRECTORY_NAME + " directory");
//        return null;
//    }

    private boolean checkAndRequestCollagePermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this, "android.permission.CAMERA");
        int storagePermission = ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE");
        int storagePermission1 = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
        List<String> listPermissionsNeeded = new ArrayList();
        if (storagePermission != 0) {
            listPermissionsNeeded.add("android.permission.READ_EXTERNAL_STORAGE");
        }
        if (storagePermission1 != 0) {
            listPermissionsNeeded.add("android.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (permissionCAMERA != 0) {
            listPermissionsNeeded.add("android.permission.CAMERA");
        }
        if (listPermissionsNeeded.isEmpty()) {
            return true;
        }
        ActivityCompat.requestPermissions(this, (String[]) listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), this.PERMISSION_COLLAGE_EDITOR);
        return false;
    }

    private boolean checkAndRequestSinglePermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this, "android.permission.CAMERA");
        int storagePermission = ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE");
        int storagePermission1 = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
        List<String> listPermissionsNeeded = new ArrayList();
        if (storagePermission != 0) {
            listPermissionsNeeded.add("android.permission.READ_EXTERNAL_STORAGE");
        }
        if (storagePermission1 != 0) {
            listPermissionsNeeded.add("android.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (permissionCAMERA != 0) {
            listPermissionsNeeded.add("android.permission.CAMERA");
        }
        if (listPermissionsNeeded.isEmpty()) {
            return true;
        }
        ActivityCompat.requestPermissions(this, (String[]) listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), this.PERMISSION_SINGLE_EDITOR);
        return false;
    }

    private boolean checkAndRequestScrapbookPermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this, "android.permission.CAMERA");
        int storagePermission = ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE");
        int storagePermission1 = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
        List<String> listPermissionsNeeded = new ArrayList();
        if (storagePermission != 0) {
            listPermissionsNeeded.add("android.permission.READ_EXTERNAL_STORAGE");
        }if (storagePermission1 != 0) {
            listPermissionsNeeded.add("android.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (permissionCAMERA != 0) {
            listPermissionsNeeded.add("android.permission.CAMERA");
        }
        if (listPermissionsNeeded.isEmpty()) {
            return true;
        }
        ActivityCompat.requestPermissions(this, (String[]) listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), this.PERMISSION_SCRAPBOOK_EDITOR);
        return false;
    }

    private boolean checkAndRequestCameraPermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this, "android.permission.CAMERA");
        int storagePermission = ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE");
        int storagePermission1 = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
        List<String> listPermissionsNeeded = new ArrayList();
        if (storagePermission != 0) {
            listPermissionsNeeded.add("android.permission.READ_EXTERNAL_STORAGE");
        }if (storagePermission1 != 0) {
            listPermissionsNeeded.add("android.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (permissionCAMERA != 0) {
            listPermissionsNeeded.add("android.permission.CAMERA");
        }
        if (listPermissionsNeeded.isEmpty()) {
            return true;
        }
        ActivityCompat.requestPermissions(this, (String[]) listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), this.PERMISSION_CAMERA_EDITOR);
        return false;
    }

    private boolean checkAndRequestMirrorPermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this, "android.permission.CAMERA");
        int storagePermission = ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE");
        int storagePermission1 = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
        List<String> listPermissionsNeeded = new ArrayList();
        if (storagePermission != 0) {
            listPermissionsNeeded.add("android.permission.READ_EXTERNAL_STORAGE");
        }if (storagePermission1 != 0) {
            listPermissionsNeeded.add("android.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (permissionCAMERA != 0) {
            listPermissionsNeeded.add("android.permission.CAMERA");
        }
        if (listPermissionsNeeded.isEmpty()) {
            return true;
        }
        ActivityCompat.requestPermissions(this, (String[]) listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), this.PERMISSION_MIRROR_EDITOR);
        return false;
    }

    protected void onDestroy() {

        if (fbInterstitialAd != null) {
            fbInterstitialAd.destroy();
        }
        super.onDestroy();
    }
}
