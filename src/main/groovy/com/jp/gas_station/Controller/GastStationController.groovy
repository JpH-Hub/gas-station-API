package com.jp.gas_station.Controller

import com.jp.gas_station.Models.AddFuels
import com.jp.gas_station.Models.AddFuelsOutput
import com.jp.gas_station.Models.AdditiveFuelPump
import com.jp.gas_station.Models.CommonFuelPump
import com.jp.gas_station.Models.Customers
import com.jp.gas_station.Models.DieselFuelPump
import com.jp.gas_station.Models.EthanolFuelPump
import com.jp.gas_station.Models.FillUpOutput
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
    List<Customers> customersList = []
    List<FuelPumps> addedGasList = []
    private Map<String, FuelPumps> pumps = [
            "1": new DieselFuelPump(id: 1, gasQuantity: 10, type: "dieselGas"),
            "2": new EthanolFuelPump(id: 2, gasQuantity: 10, type: "ethanolGas"),
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
        if (input.quantity <= 0){
            return ResponseEntity.badRequest().build()
        }

        AddFuels newAddGas = new AddFuels()
        newAddGas.type = input.type
        newAddGas.quantity = input.quantity
        addedGasList.add(newAddGas)

        if (pump) {
            pump.addFuelToPumps(input.quantity)
            output.response = " A gasolina foi adicionada"
            output.quantityAdded = input.quantity
            return ResponseEntity.ok(output)
        } else {
            return ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/fill-up")
    ResponseEntity fillUp(@RequestBody Customers input) {
        FillUpOutput output = new FillUpOutput()
        FuelPumps pump = pumps.values().find { it.type == input.carGasType }
        if (input.amountRefueled <= 0){
            return ResponseEntity.badRequest().build()
        }

        if (pump) {
            if (input.amountRefueled > pump.gasQuantity || pump.gasQuantity <= 0){
                return ResponseEntity.badRequest().build()
            }
            pump.fillUp(input.amountRefueled)
            output.response = "O cliente abasteceu o carro"
            output.amountRefueled = input.amountRefueled
        } else {
            return ResponseEntity.badRequest().build()
        }

        Customers newCustomer = new Customers()
        newCustomer.name = input.name
        newCustomer.carGasType = input.carGasType
        newCustomer.amountRefueled = input.amountRefueled
        customersList.add(newCustomer)

        return ResponseEntity.ok(output)
    }

    @GetMapping("/customers")
    ResponseEntity customerList() {
        return ResponseEntity.ok(customersList)
    }
    @GetMapping("/added-gas")
    ResponseEntity gasList(){
        return ResponseEntity.ok(addedGasList)
    }
}