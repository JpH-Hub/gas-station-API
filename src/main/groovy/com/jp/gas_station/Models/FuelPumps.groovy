package com.jp.gas_station.Models

abstract class FuelPumps {
    int id
    String type
    Integer gasQuantity = 10

    void addFuelToPumps(Integer quantity){
    gasQuantity += quantity
    }

    void fillUp(Integer quantityToFillUp){
        gasQuantity -= quantityToFillUp
    }
}
