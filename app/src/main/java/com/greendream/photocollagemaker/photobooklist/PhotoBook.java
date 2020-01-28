package com.greendream.photocollagemaker.photobooklist;

/**
 * Created by ravi on 26/09/17.
 */

public class PhotoBook {
    String id;
    String format;
    String binding;
    String price;
    String pages;
    String thumbnail;

    public PhotoBook() {
    }

    public PhotoBook(String _id, String _format, String _binding, String _pages, String _price) {
        id = _id;
        format = _format;
        binding = _binding;
        price = _price;
        pages = _pages;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
