package com.jp.gas_station.Controller

import com.jp.gas_station.Models.AddFuels
import com.jp.gas_station.Models.AddFuelsOutput
import com.jp.gas_station.Models.AdditiveFuelPump
import com.jp.gas_station.Models.CommonFuelPump
import com.jp.gas_station.Models.DieselFuelPump
import com.jp.gas_station.Models.EthanolFuelPump
import com.jp.gas_station.Models.FuelPumps
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/station")
class GastStationController {
    private Map<String, FuelPumps> pumps = [
            "1": new DieselFuelPump(id: 1, gasQuantity: 10, type: "dieselGas"),
            "2": new EthanolFuelPump(id: 2, gasQuantity: 10, type: "ethanolGas "),
            "3": new AdditiveFuelPump(id: 3, gasQuantity: 10, type: "additiveGas"),
            "4": new CommonFuelPump(id: 4, gasQuantity: 10, type: "commonGas")
    ]

    @GetMapping
    ResponseEntity getPumps() {
        return ResponseEntity.ok(pumps.values().toList())
    }


    @PostMapping("/addGas")
    ResponseEntity addGas(@RequestBody AddFuels input) {
        AddFuelsOutput output = new AddFuelsOutput()
        FuelPumps pump = pumps.values().find { it.type == input.type }

        if (pump) {
            pump.addFuel(input.quantity)
            output.response = " A gasolina foi adicionada:"
            output.quantityAdded = input.quantity
            return ResponseEntity.ok(output)
        } else {
            return ResponseEntity.badRequest().build()
        }
    }
}