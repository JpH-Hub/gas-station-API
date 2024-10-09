package com.jp.gas_station.Models

import org.springframework.http.HttpStatus

class ProfitPerPump {
    Integer pumpId
    String type
    Double sellingPrice
    Double totalWasted
    Double totalProfit
}
