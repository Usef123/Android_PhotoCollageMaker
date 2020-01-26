package com.greendream.photocollagemaker.share;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.greendream.photocollagemaker.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.util.ArrayList;

public class MyCreationActivity extends AppCompatActivity {
    public static ArrayList<String> IMAGEALLARY = new ArrayList();
    public static int pos;
    private ImageView Iv_back_creation;
    MyCreationAdapter myCreationAdapter;
    private GridView grid_crea;

    InterstitialAd mInterstitialAd;

    class C14711 implements OnClickListener {
        C14711() {
        }

        public void onClick(View v) {
            MyCreationActivity.super.onBackPressed();
            finish();
//            showInterstitial();
//            MyCreationActivity.this.startActivity(new Intent(MyCreationActivity.this, MainActivity.class));
//            MyCreationActivity.this.finish();
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_my_creation);

        this.mInterstitialAd = new InterstitialAd(this);
        this.mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        this.mInterstitialAd.loadAd(new Builder().build());

        if (Glob.isOnline(this)) {

            AdView adview = new AdView(this);
            adview.setAdSize(AdSize.SMART_BANNER);
            adview.setAdUnitId(getString(R.string.banner_ad_id));
            ((RelativeLayout) findViewById(R.id.adContainer_creation)).addView(adview);

            adview.loadAd(new Builder().build());

        }

        if (MyCreationAdapter.imagegallary.size() == 0) {
            findViewById(R.id.text_noimage).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.text_noimage).setVisibility(View.GONE);
        }


        this.grid_crea = (GridView) findViewById(R.id.grid_crea);
        this.myCreationAdapter = new MyCreationAdapter(this, IMAGEALLARY);
        IMAGEALLARY.clear();
        listAllImages(new File(Environment.getExternalStorageDirectory().getPath() + "/" + Glob.app_name));
        this.grid_crea.setAdapter(this.myCreationAdapter);
        this.Iv_back_creation = (ImageView) findViewById(R.id.back_click_iv);
        this.Iv_back_creation.setOnClickListener(new C14711());
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void showInterstitial() {
        if (this.mInterstitialAd.isLoaded()) {
            this.mInterstitialAd.show();
        }
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdClosed() {
                MyCreationActivity.this.loadAdmobAd();
            }

            public void onAdLoaded() {
            }

            public void onAdOpened() {
            }
        });
    }

    private void loadAdmobAd() {

        this.mInterstitialAd.loadAd(new Builder().build());

    }

    private void listAllImages(File filepath) {
        File[] files = filepath.listFiles();
        if (MyCreationAdapter.imagegallary.size() == 0) {
            findViewById(R.id.text_noimage).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.text_noimage).setVisibility(View.GONE);
        }
        if (files != null) {
            for (int j = files.length - 1; j >= 0; j--) {
                String ss = files[j].toString();
                File check = new File(ss);
                Log.d("" + check.length(), "" + check.length());
                if (check.length() <= 1024) {
                    Log.e("Invalid Image", "Delete Image");
                } else if (check.toString().contains(".jpg") || check.toString().contains(".png") || check.toString().contains(".jpeg")) {
                    IMAGEALLARY.add(ss);
                }
                System.out.println(ss);
            }
            return;
        }


        System.out.println("Empty Folder");
    }


    protected void onResume() {
        super.onResume();
        loadAdmobAd();
        if (MyCreationAdapter.imagegallary.size() == 0) {
            findViewById(R.id.text_noimage).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.text_noimage).setVisibility(View.GONE);
        }
    }

}
