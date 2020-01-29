package com.greendream.photocollagemaker.photobooklist;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greendream.photocollagemaker.Glob;
import com.greendream.photocollagemaker.R;
import com.greendream.photocollagemaker.activities.CreatePhotoBookActivity;
import com.greendream.photocollagemaker.photogrid.DraggableGridExampleActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * SwipeMenuListView
 * Created by baoyz on 15/6/29.
 */
public class PhotoBookList2Activity extends Activity {

    private SwipeRefreshLayout swipeContainer;

    private List<PhotoBook> cartList = new ArrayList<>();

    private AppAdapter mAdapter;
    private SwipeMenuListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photobook_list_2);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                prepareCart(new PhotoBookListActivity.CallBackListener() {
                    @Override
                    public void onCallbackDone() {
                        swipeContainer.setRefreshing(false);
                    }
                });
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mListView = findViewById(R.id.swipemenulistview);

        mAdapter = new AppAdapter();
        mListView.setAdapter(mAdapter);

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("Order");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                //deleteItem.setIcon(R.drawable.ic_delete);
                deleteItem.setTitle("Delete");
                // set item title fontsize
                deleteItem.setTitleSize(18);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);

                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mListView.setMenuCreator(creator);

        // step 2. listener item click event
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                PhotoBook item = cartList.get(position);

                switch (index) {
                    case 0:
                        // order
                        order(item);
                        break;
                    case 1:
                        // delete
					    delete(item);

					    cartList.remove(position);
                        mAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });

        // set SwipeListener
        mListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });

        // set MenuStateChangeListener
        mListView.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
            @Override
            public void onMenuOpen(int position) {
            }

            @Override
            public void onMenuClose(int position) {
            }
        });

        // other setting
        mListView.setCloseInterpolator(new BounceInterpolator());

        // test item long click
//        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view,
//                                           int position, long id) {
//                Toast.makeText(getApplicationContext(), position + " long click", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });

        mListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PhotoBook item = cartList.get(position);
                open(item);
            }
        });



        FloatingActionButton btnAdd = findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(PhotoBookList2Activity.this, CreatePhotoBookActivity.class));
            }
        });

        prepareCart(null);
    }

    private void open(PhotoBook item) {
        // open app
        Intent intent = new Intent(PhotoBookList2Activity.this, DraggableGridExampleActivity.class);
        intent.putExtra("id", item.getId());
        startActivity(intent);
    }
    private void delete(PhotoBook item) {
        // delete app
        DatabaseReference databaseCart = FirebaseDatabase.getInstance().getReference(Glob.DATABASE_CART).child(item.getId());
        databaseCart.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Snackbar.make(getWindow().getDecorView().getRootView(), "removed successfully", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void order(PhotoBook item) {
        // order app

    }

    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cartList.size();
        }

        @Override
        public PhotoBook getItem(int position) {
            return cartList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.cart_list_item, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();

            final PhotoBook item = cartList.get(position);

            holder.name.setText(item.getFormat());
            holder.description.setText(item.getBinding());
            holder.pages.setText("" + item.getPages());
            holder.price.setText( "$ " + item.getPrice());

            Glide.with(getBaseContext())
                    .load(item.getThumbnail())
                    .into(holder.thumbnail);


            return convertView;
        }

        class ViewHolder {
            public TextView name, description, pages, price;
            public ImageView thumbnail;

            public ViewHolder(View view) {

                name = view.findViewById(R.id.format);
                description = view.findViewById(R.id.binding);
                pages = view.findViewById(R.id.pages);
                price = view.findViewById(R.id.price);
                thumbnail = view.findViewById(R.id.thumbnail);

                view.setTag(this);
            }
        }

    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }


    private void prepareCart(final PhotoBookListActivity.CallBackListener listener) {

        DatabaseReference databaseCart = FirebaseDatabase.getInstance().getReference(Glob.DATABASE_CART);

        databaseCart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                // adding items to cart list
                cartList.clear();

                for (DataSnapshot property :dataSnapshot.getChildren()) {


                    PhotoBook item = property.getValue(PhotoBook.class);

                    cartList.add(item);
                }

                // refreshing recycler view
                mAdapter.notifyDataSetChanged();

                if (listener != null) {
                    listener.onCallbackDone();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}