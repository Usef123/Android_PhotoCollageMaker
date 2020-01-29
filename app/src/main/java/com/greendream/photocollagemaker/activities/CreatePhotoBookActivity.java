package com.greendream.photocollagemaker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.greendream.photocollagemaker.Glob;
import com.greendream.photocollagemaker.R;
import com.greendream.photocollagemaker.photobooklist.PhotoBook;
import com.greendream.photocollagemaker.photogrid.DraggableGridExampleActivity;
import com.tiper.MaterialSpinner;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class CreatePhotoBookActivity extends AppCompatActivity {

    List<String> bookFormat     = new LinkedList<>(Arrays.asList("A5 (148 * 210 mm)", "A4 (210 * 297 mm)", "Square (200 * 200 mm)"));
    List<String> bookBinding    = new LinkedList<>(Arrays.asList("Soft Cover", "Hard Cover"));
    int[] bookPages             = {16, 24, 32};


    int nFormat = 0, nBinding = 0, nPages = 0;
    double price = 0.0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_photobook);

/*
        final NiceSpinner spinFormat = (NiceSpinner) findViewById(R.id.spinner_format);
        spinFormat.attachDataSource(bookFormat);
        spinFormat.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                // This example uses String, but your type can be any
                String item = (String) parent.getItemAtPosition(position);

                nFormat = position;
                updatePrice();
            }
        });


        NiceSpinner spinBinding = (NiceSpinner) findViewById(R.id.spinner_binding);
        spinBinding.attachDataSource(bookBinding);
        spinBinding.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                nBinding = position;
                updatePrice();
            }
        });


        NiceSpinner spinPages = (NiceSpinner) findViewById(R.id.spinner_pages);
        spinPages.attachDataSource(bookPages);
        spinPages.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                nPages = position;
                updatePrice();
            }
        });

 */
        MaterialSpinner spinFormat = (MaterialSpinner ) findViewById(R.id.spinner_format);
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bookFormat);
        spinFormat.setAdapter(itemsAdapter);
        spinFormat.setOnItemClickListener(new MaterialSpinner.OnItemClickListener() {
            @Override
            public void onItemClick(MaterialSpinner materialSpinner, View view, int i, long l) {
                nFormat = i;
                updatePrice();
            }
        });
        spinFormat.setSelection(0);

        MaterialSpinner spinBinding = (MaterialSpinner ) findViewById(R.id.spinner_binding);
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bookBinding);
        spinBinding.setAdapter(itemsAdapter);
        spinBinding.setOnItemClickListener(new MaterialSpinner.OnItemClickListener() {
            @Override
            public void onItemClick(MaterialSpinner materialSpinner, View view, int i, long l) {
                nBinding = i;
                updatePrice();
            }
        });
        spinBinding.setSelection(0);


        MaterialSpinner spinPages = (MaterialSpinner ) findViewById(R.id.spinner_pages);
        String[] sPages = Arrays.toString(bookPages).split("[\\[\\]]")[1].split(", ");
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Arrays.<String>asList(sPages));
        spinPages.setAdapter(itemsAdapter);
        spinPages.setOnItemClickListener(new MaterialSpinner.OnItemClickListener() {
            @Override
            public void onItemClick(MaterialSpinner materialSpinner, View view, int i, long l) {
                nPages = i;
                updatePrice();
            }
        });
        spinPages.setSelection(0);

        Button btnCreate = (Button) findViewById(R.id.btn_create);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatabaseReference databaseCart = FirebaseDatabase.getInstance().getReference(Glob.DATABASE_CART);

                final String id = databaseCart.push().getKey();
                PhotoBook item = new PhotoBook(id, bookFormat.get(nFormat), bookBinding.get(nBinding), bookPages[nPages], price);
                databaseCart.child(id).setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Intent intent = new Intent(CreatePhotoBookActivity.this, DraggableGridExampleActivity.class);
                        intent.putExtra("id", id);
                        CreatePhotoBookActivity.this.startActivity( intent );

                    }
                });




            }
        });


        updatePrice();

    }

    private void updatePrice() {
        price = (nFormat+1) * 2 + (nBinding+1) * 3 + (nPages+1) * 5;

        TextView tvPrice = (TextView) findViewById(R.id.txt_price);
        tvPrice.setText("$ " + price);
    }

}
