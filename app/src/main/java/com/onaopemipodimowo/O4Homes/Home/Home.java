package com.onaopemipodimowo.O4Homes.Home;


public class Home  {
    private String Listing_id, Name, City, State_code, Line, Property_type, List_price_min, List_price_max, Primary_photo_href, Image1_href, Image2_href, Image3_href;
    private boolean Cats_policy, Dogs_policy;
    private int Baths_min, Baths_max, Beds_min, Beds_max;



    public Home( String listing_id, String name, String city, String state_code, String line, String property_type, String list_price_min, String list_price_max, String primary_photo_href, String image1_href, String image2_href, String image3_href, boolean cats_policy, boolean dogs_policy, int baths_min, int baths_max, int beds_min, int beds_max) {
        Listing_id = listing_id;
        Name = name;
        City = city;
        State_code = state_code;
        Line = line;
        Property_type = property_type;
        List_price_min = list_price_min;
        List_price_max = list_price_max;
        Primary_photo_href = primary_photo_href;
        Image1_href = image1_href;
        Image2_href = image2_href;
        Image3_href = image3_href;
        Cats_policy = cats_policy;
        Dogs_policy = dogs_policy;
        Baths_min = baths_min;
        Baths_max = baths_max;
        Beds_min = beds_min;
        Beds_max = beds_max;

    }

    public String getListing_id(){return Listing_id;}

    public String getName(){return Name;}

    public String getCity(){
        return City;
    }

    public String getState_code(){return State_code;}

    public String getLine(){
        return Line;
    }

    public String getProperty_type(){
        return Property_type;
    }

    public String getList_price_min(){return List_price_min;}

    public String getList_price_max(){return List_price_max;}

    public String getPrimary_photo_href(){return Primary_photo_href;}

    public String getImage1_href(){return Image1_href;}

    public String getImage2_href(){return Image2_href;}

    public String getImage3_href(){return Image3_href;}
    public String getCats_policy() {

        if (Cats_policy == true) {
            return "Yes";
        } else {
            return "No";
        }
    }

    public String getDogs_policy(){
        if(Dogs_policy == true){
            return "Yes";
        } else{
            return "No";
        }

    }

    public int getBaths_min(){
        return Baths_min;
    }

    public int getBaths_max(){
        return Baths_max;
    }

    public int getBeds_min(){
        return Beds_min;
    }

    public int getBeds_max(){
        return Beds_max;
    }

}
