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

import androidx.appcompat.app.AppCompatActivity;

import com.greendream.photocollagemaker.Glob;
import com.greendream.photocollagemaker.R;
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
    List<String> bookPages      = new LinkedList<>(Arrays.asList("16 Pages", "24 Pages", "32 Pages"));

    int nFormat = 0, nBinding = 0, nPages = 0;

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
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bookPages);
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
                startActivity( new Intent(CreatePhotoBookActivity.this, DraggableGridExampleActivity.class));
            }
        });


        updatePrice();

    }

    private void updatePrice() {
        int price = (nFormat+1) * 2 + (nBinding+1) * 3 + (nPages+1) * 5;

        TextView tvPrice = (TextView) findViewById(R.id.txt_price);
        tvPrice.setText("$ " + price);
    }

}
