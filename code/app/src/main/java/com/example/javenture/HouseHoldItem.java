package com.example.javenture;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains information about a household item
 */
public class HouseHoldItem implements Parcelable {
    private String id;
    private String description;
    private String make;
    private LocalDate datePurchased;
    private double price;
    private String serialNumber;
    private String comment;
    private String model;
    private List<ImageItem> imageItems;
    private List<String> tags;

    public HouseHoldItem() {
        imageItems = new ArrayList<>();
        tags = new ArrayList<>();
    }

    public HouseHoldItem(String id, String description, String make, LocalDate datePurchased, double price, String serialNumber, String comment, String model, List<ImageItem> imageItems, List<String> tags) {
        this.id = id;
        this.description = description;
        this.make = make;
        this.datePurchased = datePurchased;
        this.price = price;
        this.serialNumber = serialNumber;
        this.comment = comment;
        this.model = model;
        this.imageItems = imageItems;
        this.tags = tags;
    }

    protected HouseHoldItem(Parcel in) {
        id = in.readString();
        description = in.readString();
        make = in.readString();
        datePurchased = in.readSerializable(LocalDate.class.getClassLoader(), LocalDate.class);
        price = in.readDouble();
        serialNumber = in.readString();
        comment = in.readString();
        model = in.readString();

        imageItems = new ArrayList<>();
        in.readTypedList(imageItems, ImageItem.CREATOR); // Make sure ImageItem is Parcelable

        tags = in.createStringArrayList();
    }

    /**
     * Convert the HouseHoldItem object to a map for storing in the db
     * @return map of the HouseHoldItem object
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("description", description);
        map.put("make", make);
        map.put("datePurchased", getFormattedDatePurchased());
        map.put("price", price);
        map.put("serialNumber", serialNumber);
        map.put("comment", comment);
        map.put("model", model);
        map.put("tags", tags);
        // convert photoURIs to a list of strings
        List<String> photoUrls = new ArrayList<>();
        for (ImageItem imageItem : imageItems) {
            assert !imageItem.isLocal();
            photoUrls.add(imageItem.getRemoteUrl());
        }
        map.put("photoUrls", photoUrls);
        return map;
    }

    public String getDescription() {
        return description;
    }

    public String getMake() {
        return make;
    }

    public LocalDate getDatePurchased() {
        return datePurchased;
    }

    public String getFormattedDatePurchased() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
        return datePurchased.format(formatter);
    }

    public double getPrice() {
        return price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public void setDatePurchased(LocalDate datePurchased) {
        this.datePurchased = datePurchased;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<ImageItem> getImageItems() {
        return imageItems;
    }

    public void setImageItems(List<ImageItem> imageItems) {
        this.imageItems = imageItems;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * Check if the item has a given tag
     * @param tag tag to check
     * @return true if the item has the tag, false otherwise
     */
    public boolean hasTag(String tag) {
        if (tags == null) {
            return false;
        }
        for (String t : tags) {
            if (t.equals(tag)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Assign a tag to the item
     * @param tag tag to assign
     */
    public void addTag(String tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        if (hasTag(tag)) {
            return;
        }
        tags.add(tag);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static final Creator<HouseHoldItem> CREATOR = new Creator<HouseHoldItem>() {
        @Override
        public HouseHoldItem createFromParcel(Parcel in) {
            return new HouseHoldItem(in);
        }

        @Override
        public HouseHoldItem[] newArray(int size) {
            return new HouseHoldItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(description);
        parcel.writeString(make);
        parcel.writeSerializable(datePurchased); // For LocalDate
        parcel.writeDouble(price);
        parcel.writeString(serialNumber);
        parcel.writeString(comment);
        parcel.writeString(model);
        parcel.writeTypedList(imageItems);
        parcel.writeStringList(tags);
    }
}
