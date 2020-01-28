package com.greendream.photocollagemaker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Glob {

    public static String DATABASE_CART = "cart";
    public static String DATABASE_PHOTOBOOK = "photobook";


    public static String acc_link = "https://play.google.com/store/apps/developer?id=ABCD";  //REPLACE ABCD with Developer Console name
    public static String privacy_link = "https://www.google.com/";  //ADD YOUR PRIVACY POLICY LINK HERE

    public static boolean isOnline(Context ctx) {
//        iGold
//        NetworkInfo netInfo = ((ConnectivityManager) ctx.getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
//        if (netInfo == null || !netInfo.isConnected()) {
//            return false;
//        }
//        return true;

        return false;
    }
}
