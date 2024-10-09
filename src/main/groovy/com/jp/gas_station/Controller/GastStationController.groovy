package com.jp.gas_station.Controller

import com.jp.gas_station.Models.AddFuelsOutput
import com.jp.gas_station.Models.Customer
import com.jp.gas_station.Models.FillUpOutput
import com.jp.gas_station.Models.FuelPump
import com.jp.gas_station.PumpService.CustomerService
import com.jp.gas_station.PumpService.PumpService
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
        FuelPump pump = pumpService.pumps.values().find { it.pumpId == input.pumpId }
        //adicionar validação de pumps dentro do metodo add fuel e xe a pump n for valida lançar uma exceção
        if (input.quantity <= 0 || input.quantity >= 10000 || pump == null || pump.quantity >= 10000 ||
                pump.quantity + input.quantity >= 10000) {
            return ResponseEntity.badRequest().build()
        }
//essas 2 podem ser uma só.
        AddFuelsOutput output = pumpService.addFuelToPump(input)
        pumpService.addFuelToList(input)

        return ResponseEntity.ok(output)
    }

    @PostMapping("/fill-up")
    ResponseEntity fillUp(@RequestBody Customer input) {
        FuelPump pump = pumpService.pumps.values().find { it.pumpId == input.selectedPump }
        //adicionar validação de pumps dentro do metodo fill up e xe a pump n for valida lançar uma exceção
        if (input.amountRefueled <= 0 || pump == null || input.amountRefueled > pump.quantity || pump.quantity <= 0) {
            return ResponseEntity.badRequest().build()
        }

        pump.fillUp(input.amountRefueled)
        customerService.saveCustomer(input, pump)

        FillUpOutput output = new FillUpOutput()
        output.response = "O cliente abasteceu o veiculo"
        output.amountRefueled = input.amountRefueled
        output.totalPaid = pump.sellingPrice * input.amountRefueled
        return ResponseEntity.ok(output)
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

    @GetMapping("/added-gas")
    ResponseEntity gasList() {
        return ResponseEntity.ok(pumpService.getFuelList())
    }
}