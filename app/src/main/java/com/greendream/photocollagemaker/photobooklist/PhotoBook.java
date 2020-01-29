package com.greendream.photocollagemaker.photobooklist;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ravi on 26/09/17.
 */

@IgnoreExtraProperties
public class PhotoBook implements Serializable {
    String id;
    String format;
    String binding;
    double price;
    int    pages;

    List<String> images;


    public PhotoBook() {
    }

    public PhotoBook(String _id, String _format, String _binding, int _pages, double _price) {
        id = _id;
        format = _format;
        binding = _binding;
        price = _price;
        pages = _pages;

        images = new LinkedList<>();
        for (int i = 0; i < _pages ; i ++) {
            images.add("");
        }

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getThumbnail() {
        try {
            return images.get(0);
        } catch (Exception e) {
            return "";
        }
    }


    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<String > getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
