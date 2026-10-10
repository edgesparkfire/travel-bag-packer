package com.example.bag.model;

import lombok.Data;

@Data
public class GearItem {
    private String name;
    private String category; // e.g., Clothing, Electronics, Documents
    private double weight;   // in kg
    private boolean isPacked;
}
