package com.example.bag.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
    private String itemId;
    private String itemName;
    private Integer quantity;
    private Boolean isPacked;
    private String category;
}