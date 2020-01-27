/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.greendream.photocollagemaker.photogrid;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.greendream.photocollagemaker.R;
import com.greendream.photocollagemaker.photogrid.common.data.AbstractDataProvider;
import com.greendream.photocollagemaker.photogrid.common.data.ExampleDataProvider;
import com.greendream.photocollagemaker.photogrid.common.fragment.ExampleDataProviderFragment;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.LinkedList;
import java.util.List;

public class DraggableGridExampleActivity extends AppCompatActivity {
    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";

    ExampleDataProviderFragment     mExampleDataProviderFragment;
    DraggableGridExampleFragment    mDraggableGridExampleFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photolist);


        // hearder
        ImageView btnBack = findViewById(R.id.imageBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button btnPreview = findViewById(R.id.btnPreview);
        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<ExampleDataProvider.ConcreteData> mData = ((ExampleDataProvider)(getDataProvider())).getDataProvider();
                for (ExampleDataProvider.ConcreteData data : mData) {
                    Log.e("iGold", data.getText());
                }
            }
        });


        if (savedInstanceState == null) {

            mExampleDataProviderFragment = new ExampleDataProviderFragment();
            mDraggableGridExampleFragment = new DraggableGridExampleFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(mExampleDataProviderFragment, FRAGMENT_TAG_DATA_PROVIDER)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mDraggableGridExampleFragment, FRAGMENT_LIST_VIEW)
                    .commit();
        }



    }

    public AbstractDataProvider getDataProvider() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DATA_PROVIDER);
        return ((ExampleDataProviderFragment) fragment).getDataProvider();
    }

    @Override
    protected void onResume() {
        super.onResume();


        final String atoz = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        List<ExampleDataProvider.ConcreteData> mData;
        mData = new LinkedList<>();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < atoz.length(); j++) {
                final long id = mData.size();
                final int viewType = 0;
                final String text = Character.toString(atoz.charAt(j));
                final int swipeReaction = RecyclerViewSwipeManager.REACTION_CAN_SWIPE_UP | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_DOWN;
                mData.add(new ExampleDataProvider.ConcreteData(id, viewType, text, swipeReaction));
            }
        }

        ((ExampleDataProvider) (mExampleDataProviderFragment.getDataProvider())).setDataProvider(mData);


        if (mDraggableGridExampleFragment != null) {
            mDraggableGridExampleFragment.onRefresh();
        }

    }

}
