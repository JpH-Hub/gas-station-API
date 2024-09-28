package com.jp.gas_station.Models

abstract class FuelPumps {
    int id
    String type
    Integer gasQuantity = 10

    void addFuel(Integer quantity){
    gasQuantity += quantity
    }
}
