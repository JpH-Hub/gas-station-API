package com.jp.gas_station.Controller

import com.jp.gas_station.Models.AddFuel
import com.jp.gas_station.Models.AddFuelsOutput
import com.jp.gas_station.Models.Customers
import com.jp.gas_station.Models.FillUpOutput
import com.jp.gas_station.Models.FuelPump
import com.jp.gas_station.Models.profitPerPump
import com.jp.gas_station.PumpService.CustomerService
import com.jp.gas_station.PumpService.PumpService
import org.springframework.beans.factory.annotation.Autowired
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
    List<Customers> customersList = []
    List<AddFuel> FuelList = []
    CustomerService customerService
    PumpService pumpService

    @Autowired
    GasStationController(PumpService pumpService) {
        this.pumpService = pumpService

    }

    @GetMapping
    ResponseEntity getPumps() {
        return ResponseEntity.ok(pumpService.getPumps())
    }

    @PostMapping("/createPump")
    ResponseEntity createPump(@RequestBody FuelPump newPump) {
        FuelPump pump = pumpService.pumps.values().find { it.pumpId == newPump.pumpId }
        if (pump != null) {
            return ResponseEntity.badRequest().build()
        }
        if (newPump.quantity > 0) {
            newPump.quantity = 0
        }
        pumpService.addPumps(newPump)
        return ResponseEntity.noContent().build()
    }


    @PostMapping("/addFuelToPumps")
    ResponseEntity addFuel(@RequestBody FuelPump input) {
        if (input.quantity <= 0 || input.quantity >= 10000) {
            return ResponseEntity.badRequest().build()
        }

        FuelPump pump = pumpService.pumps.values().find { it.pumpId == input.pumpId }

        if (pump == null) {
            return ResponseEntity.badRequest().build()
        }

        if (pump.quantity >= 1000) {
            return ResponseEntity.badRequest().build()
        }

        AddFuelsOutput output = pumpService.addFuelToPump(input)

//preciso desaclopar tambem essa lista
        AddFuel addFuel = new AddFuel()
        addFuel.pumpId = input.pumpId
        addFuel.type = pump.type
        addFuel.quantity = input.quantity
        addFuel.totalCost = input.quantity * pump.purchasePrice
        FuelList.add(addFuel)


        return ResponseEntity.ok(output)
    }


    @PostMapping("/fill-up")
    ResponseEntity fillUp(@RequestBody Customers input) {
// tambem desaclopar o customers para o customerSErvice
        if (input.amountRefueled <= 0) {
            return ResponseEntity.badRequest().build()
        }

        FuelPump pump = pumpService.pumps.values().find { it.pumpId == input.selectedPump }
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
    ResponseEntity pumpProfit(@PathVariable("id") String id) {
        // e preciso fazer metodos para calcular o profit para ficar mais legivel, e tambem desaclopar
        double totalCost = 0
        double totalProfit = 0
        double totalWasted = 0
        FuelPump pump = pumpService.pumps[id]
        profitPerPump profit = new profitPerPump()

        for (AddFuel addfuel : FuelList) {
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
        return ResponseEntity.ok(FuelList)
    }
}