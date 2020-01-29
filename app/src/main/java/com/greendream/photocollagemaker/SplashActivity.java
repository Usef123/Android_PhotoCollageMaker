package com.greendream.photocollagemaker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.greendream.photocollagemaker.activities.CreatePhotoBookActivity;
import com.greendream.photocollagemaker.activities.MainActivity;
import com.greendream.photocollagemaker.photobooklist.PhotoBookList2Activity;
import com.greendream.photocollagemaker.photobooklist.PhotoBookListActivity;

public class SplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 3000;
    ProgressBar progressBar;
    private Handler progressBarHandler = new Handler();
    private int progressBarStatus = 0;

    class C05631 implements Runnable {

        class C05611 implements Runnable {
            C05611() {
            }

            public void run() {
                SplashActivity.this.progressBar.setProgress(SplashActivity.this.progressBarStatus);
            }
        }


        C05631() {
        }

        public void run() {
            while (SplashActivity.this.progressBarStatus < 100) {
                SplashActivity.this.progressBarStatus = SplashActivity.this.progressBarStatus + 20;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SplashActivity.this.progressBarHandler.post(new C05611());
            }
            if (SplashActivity.this.progressBarStatus >= 100) {
                try {
                    Thread.sleep(100);
                    //SplashActivity.this.startActivity(new Intent(SplashActivity.this.getBaseContext(), PhotoBookListActivity.class));
                    SplashActivity.this.startActivity(new Intent(SplashActivity.this.getBaseContext(), PhotoBookList2Activity.class));
                    SplashActivity.this.finish();
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.progressBar.setProgress(0);
        this.progressBar.setSecondaryProgress(100);
        int color = getResources().getColor(R.color.yellow);
        this.progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        this.progressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        this.progressBarStatus = 0;
        new Thread(new C05631()).start();
    }
}

