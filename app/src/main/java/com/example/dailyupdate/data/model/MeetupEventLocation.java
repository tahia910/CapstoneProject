package com.example.dailyupdate.data.model;

import com.google.gson.annotations.SerializedName;

public class MeetupEventLocation {

    @SerializedName("name")
    private String placeName;
    @SerializedName("address_1")
    private String address;
    @SerializedName("city")
    private String city;
    @SerializedName("localized_country_name")
    private String country;

    public MeetupEventLocation(String placeName, String address, String city, String country){
        this.placeName = placeName;
        this.address = address;
        this.city = city;
        this.country = country;
    }

    public String getPlaceName(){return placeName;}
    public String getAddress(){return address;}
    public String getCity(){return city;}
    public String getCountry(){return country;}

}
