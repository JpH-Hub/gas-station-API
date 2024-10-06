package com.jp.gas_station.Controller

import com.jp.gas_station.Models.AddFuel
import com.jp.gas_station.Models.AddFuelsOutput
import com.jp.gas_station.Models.Customers
import com.jp.gas_station.Models.FillUpOutput
import com.jp.gas_station.Models.FuelPump
import com.jp.gas_station.Models.profitPerPump
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/station")
class GasStationController {
    List<AddFuel> addedGasList = []
    List<Customers> customersList = []
    private Map<String, FuelPump> pumps = [
            "1": new FuelPump(pumpId: 1, quantity: 0, type: "dieselGas", sellingPrice: 7, purchasePrice: 6),
            "2": new FuelPump(pumpId: 2, quantity: 0, type: "ethanolGas", sellingPrice: 4, purchasePrice: 3),
            "3": new FuelPump(pumpId: 3, quantity: 0, type: "additiveGas", sellingPrice: 5, purchasePrice: 4),
            "4": new FuelPump(pumpId: 4, quantity: 0, type: "commonGas", sellingPrice: 6, purchasePrice: 5)
    ]


    @GetMapping
    ResponseEntity getPumps() {
        return ResponseEntity.ok(pumps.values().toList())
    }

    @PostMapping("/createPump")
    ResponseEntity createPump(@RequestBody FuelPump newPump) {
        FuelPump pump = pumps.values().find { it.pumpId == newPump.pumpId }
        if (pump != null) {
            return ResponseEntity.badRequest().build()
        }
        if (newPump.quantity > 0) {
            newPump.quantity = 0
        }
        pumps.put(newPump.pumpId.toString(), newPump)
        return ResponseEntity.noContent().build()
    }


    @PostMapping("/addGasToPumps")
    ResponseEntity addGas(@RequestBody FuelPump input) {

        if (input.quantity <= 0 || input.quantity > 10000) {
            return ResponseEntity.badRequest().build()
        }

        FuelPump pump = pumps.values().find { it.pumpId == input.pumpId }
        if (pump == null) {
            return ResponseEntity.badRequest().build()
        }
        // por algum motivo ainda n ta funcionando 100%
        if (pump.quantity >= 1000) {
            return ResponseEntity.badRequest().build()
        }


        AddFuel newAddGas = new AddFuel()

        newAddGas.pumpId = input.pumpId
        newAddGas.type = pump.type
        newAddGas.quantity = input.quantity
        newAddGas.totalCost = input.quantity * pump.purchasePrice
        addedGasList.add(newAddGas)

        AddFuelsOutput output = new AddFuelsOutput()

        pump.addFuelToPumps(input.quantity)
        output.response = "O combustivel foi adicionado a bomba"
        output.pumpId = pump.pumpId
        output.quantityAdded = input.quantity

        return ResponseEntity.ok(output)
    }


    @PostMapping("/fill-up")
    ResponseEntity fillUp(@RequestBody Customers input) {

        if (input.amountRefueled <= 0) {
            return ResponseEntity.badRequest().build()
        }

        FuelPump pump = pumps.values().find { it.pumpId == input.selectedPump }
        if (pump == null || input.amountRefueled > pump.quantity || pump.quantity <= 0) {
            return ResponseEntity.badRequest().build()
        }

        FillUpOutput output = new FillUpOutput()
        pump.fillUp(input.amountRefueled)

        output.response = "O cliente abasteceu o carro"
        output.amountRefueled = input.amountRefueled
        output.totalPaid = pump.sellingPrice * input.amountRefueled


        Customers newCustomer = new Customers()
        newCustomer.name = input.name
        newCustomer.selectedPump = input.selectedPump
        newCustomer.amountRefueled = input.amountRefueled
        newCustomer.totalPaid = pump.sellingPrice * input.amountRefueled
        customersList.add(newCustomer)

        return ResponseEntity.ok(output)
    }

    @GetMapping("/{id}")
    ResponseEntity lucroDaBomba(@PathVariable("id") String id) {
        double totalCost = 0
        double  totalProfit = 0
        double totalWasted = 0
        FuelPump pump = pumps[id]
        profitPerPump profit = new profitPerPump()

        for (AddFuel addfuel : addedGasList) {
            if (pump.pumpId == addfuel.pumpId) {
                totalCost += addfuel.totalCost
            }
        }

            for (Customers customer : customersList) {
                if (customer.selectedPump == pump.pumpId) {
                    totalWasted += customer.totalPaid
                }
            }
            totalProfit = totalWasted - totalCost


        profit.pumpId = pump.pumpId
        profit.type = pump.type
        profit.sellingPrice = pump.sellingPrice
        profit.totalWasted = totalCost
        profit.totalProfit = totalProfit

            if (pump != null) {
                return ResponseEntity.ok(profit)
            }
        return ResponseEntity.notFound().build()
    }


    @GetMapping("/customers")
    ResponseEntity customerList() {
        return ResponseEntity.ok(customersList)
    }

    @GetMapping("/added-gas")
    ResponseEntity gasList() {
        return ResponseEntity.ok(addedGasList)
    }
}