package com.example.javenture;

import java.io.Serializable;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HouseHoldItem implements Serializable {
    private String id;
    private String description;
    private String make;
    private LocalDate datePurchased;
    private double price;
    private String serialNumber;
    private String comment;
    private String model;
    private List<URI> photoURIs;
    private List<Tag> tags;

    public HouseHoldItem() { }
    public HouseHoldItem(String id, String description, String make, LocalDate datePurchased, double price, String serialNumber, String comment, String model, List<URI> photoURIs, List<Tag> tags) {
        this.id = id;
        this.description = description;
        this.make = make;
        this.datePurchased = datePurchased;
        this.price = price;
        this.serialNumber = serialNumber;
        this.comment = comment;
        this.model = model;
        this.photoURIs = photoURIs;
        this.tags = tags;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("description", description);
        map.put("make", make);
        map.put("datePurchased", getFormattedDatePurchased());
        map.put("price", price);
        map.put("serialNumber", serialNumber);
        map.put("comment", comment);
        map.put("model", model);
        ArrayList<String> tagNames = new ArrayList<>();
        for (Tag tag : tags) {
            tagNames.add(tag.getName());
        }
        map.put("tags", tagNames);
        // TODO photos
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

    public List<URI> getPhotoURIs() {
        return photoURIs;
    }

    public void setPhotoURIs(List<URI> photoURIs) {
        this.photoURIs = photoURIs;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
