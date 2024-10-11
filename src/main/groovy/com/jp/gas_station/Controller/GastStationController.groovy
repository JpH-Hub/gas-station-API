package com.jp.gas_station.Controller


import com.jp.gas_station.Models.Customer
import com.jp.gas_station.Models.FillUpOutput
import com.jp.gas_station.Models.FuelPump
import com.jp.gas_station.Models.GenericOutput
import com.jp.gas_station.PumpService.CustomerService
import com.jp.gas_station.PumpService.PumpService
import org.springframework.http.HttpStatus
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

    private CustomerService customerService
    private PumpService pumpService

    GasStationController(PumpService pumpService, CustomerService customerService) {
        this.pumpService = pumpService
        this.customerService = customerService
    }

    @GetMapping
    ResponseEntity getPumps() {
        return ResponseEntity.ok(pumpService.getPumps())
    }

    @PostMapping("/createPump")
    ResponseEntity createPump(@RequestBody FuelPump newPump) {
        //essa logica abaixo tem q esstar dentro de um metodo
        FuelPump pump = pumpService.pumps.values().find { it.pumpId == newPump.pumpId }
        if (pump != null) {
            return ResponseEntity.badRequest().build()
        }
        if (newPump.quantity > 0) {
            newPump.quantity = 0
        }
        pumpService.addPump(newPump)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/addFuelToPump")
    ResponseEntity addFuel(@RequestBody FuelPump input) {
        try {
            GenericOutput output = pumpService.dad(input)
            return ResponseEntity.ok(output)
        }
        catch (RuntimeException e) {
            GenericOutput output = new GenericOutput(response: e.getMessage())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(output)
        }


    }


    @PostMapping("/fill-up")
    ResponseEntity fillUp(@RequestBody Customer input) {
        try {
            FillUpOutput output = pumpService.fillUp(input)
            return ResponseEntity.ok(output)
        }
        catch (RuntimeException e){
            GenericOutput output = new GenericOutput(response: e.getMessage())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(output)
        }

    }

    @GetMapping("/{id}")
    ResponseEntity pumpProfit(@PathVariable("id") String id) {
        FuelPump pump = pumpService.pumps[id]
        if (pump != null) {
            return ResponseEntity.ok(pumpService.calculateTheProfitPerPump(id))
        }
        return ResponseEntity.notFound().build()
    }

    @GetMapping("/customers")
    ResponseEntity customerList() {
        return ResponseEntity.ok(customerService.customers)
    }

    @GetMapping("/added_fuel")
    ResponseEntity fuelList() {
        return ResponseEntity.ok(pumpService.getFuelList())
    }
}