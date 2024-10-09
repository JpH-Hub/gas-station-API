package com.jp.gas_station.Models

class FuelPump {
    Integer pumpId
    String type
    Double sellingPrice
    Double purchasePrice
    Integer quantity = 0

    void addFuelToPumps(Integer quantity) {
        this.quantity += quantity
    }

    void fillUp(Integer quantityToFillUp) {
        quantity -= quantityToFillUp
    }

}
