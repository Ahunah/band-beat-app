package com.example.bandbeat;

import java.io.Serializable;

public class Event implements Serializable {
    public int id;
    public String name;
    public String band;
    public String date;  // YYYY-MM-DD
    public String time;  // HH:mm
    public String venue;
    public double price;
    public int createdBy; // admin id
    public String imageName; // Add this field
}