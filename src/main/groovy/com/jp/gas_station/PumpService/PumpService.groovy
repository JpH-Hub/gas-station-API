package com.jp.gas_station.PumpService

import com.jp.gas_station.Models.AddFuel
import com.jp.gas_station.Models.Customer
import com.jp.gas_station.Models.FillUpOutput
import com.jp.gas_station.Models.FuelPump
import com.jp.gas_station.Models.GenericOutput
import com.jp.gas_station.Models.ProfitPerPump
import org.springframework.stereotype.Service

@Service
class PumpService {
    private List<AddFuel> fuelList = []
    private CustomerService customerService

    void addFuelToPump(Integer quantity, FuelPump input) {
        FuelPump pump = pumps.values().find() {it.pumpId == input.pumpId}
        pump.quantity += quantity
    }




    PumpService(CustomerService customerService) {
        this.customerService = customerService
    }

    private Map<String, FuelPump> pumps = [
            "1": new FuelPump(pumpId: 1, quantity: 0, type: "dieselGas", sellingPrice: 7.62, purchasePrice: 6.35),
            "2": new FuelPump(pumpId: 2, quantity: 0, type: "ethanolGas", sellingPrice: 4.58, purchasePrice: 3.43),
            "3": new FuelPump(pumpId: 3, quantity: 0, type: "additiveGas", sellingPrice: 5.35, purchasePrice: 4.30),
            "4": new FuelPump(pumpId: 4, quantity: 0, type: "commonGas", sellingPrice: 6.34, purchasePrice: 5.10)
    ]

    Map<String, FuelPump> getPumps() {
        return pumps
    }


    FillUpOutput fillUp(Customer input) {
        FuelPump pump = pumps.values().find { it.pumpId == input.selectedPump }
        FillUpOutput output = new FillUpOutput()
        if (input.amountRefueled <= 0) {
            throw new RuntimeException("ERROR: insira um valor válido")
        } else if (pump == null) {
            throw new RuntimeException("ERROR: essa bomba de combustivel não existe, insira um id válido")
        } else if (input.amountRefueled > pump.quantity) {
            throw new RuntimeException("ERROR: a bomba de combustivel não possui essa quantidade de combustivel")
        } else if (pump.quantity <= 0) {
            throw new RuntimeException("ERROR: a bomba de combustivel está vazia")
        }
        customerService.saveCustomer(input, pump)
        output.response = "O Cliente abasteceu o carro"
        output.amountRefueled = input.amountRefueled
        output.totalPaid = pump.sellingPrice * input.amountRefueled
        pump.quantity -= input.amountRefueled
        return output
    }


    void addPump(FuelPump pump) {
        pumps.put(pump.pumpId.toString(), pump)
    }

    AddFuel addFuelToList(FuelPump input) {
        FuelPump pump = pumps.values().find { it.pumpId == input.pumpId }
        AddFuel addFuel = new AddFuel()
        addFuel.pumpId = pump.pumpId
        addFuel.totalCost = input.quantity * pump.purchasePrice
        addFuel.quantity = input.quantity
        addFuel.type = pump.type
        fuelList.add(addFuel)
        return addFuel
    }

    List<AddFuel> getFuelList() {
        return fuelList
    }

    ProfitPerPump calculateTheProfitPerPump(String id) {
        FuelPump pump = pumps[id]

        double totalCost = 0
        double totalProfit = 0
        double totalWasted = 0

        for (AddFuel addfuel : fuelList) {
            if (pump.pumpId == addfuel.pumpId) {
                totalCost += addfuel.totalCost
            }
        }

        for (Customer customer : customerService.customers) {
            if (customer.selectedPump == pump.pumpId) {
                totalWasted += customer.totalPaid
            }
        }
        totalProfit = totalWasted - totalCost

        return new ProfitPerPump(pumpId: pump.pumpId, type: pump.type, sellingPrice: pump.sellingPrice,
                totalWasted: totalCost, totalProfit: totalProfit)

    }

    GenericOutput dad(FuelPump input) {
        FuelPump pump = pumps.values().find { it.pumpId == input.pumpId }
        GenericOutput output = new GenericOutput()
        if (input.quantity <= 0) {
            throw new RuntimeException("ERROR: quantidade de combustivel inválida, insira uma quantidade positiva")
        } else if (input.quantity >= 10000) {
            throw new RuntimeException("ERROR: quantidade maior do que as bombas de combustivel podem conter")
        } else if (pump == null) {
            throw new RuntimeException("ERROR: essa bomba de combustivel não existe, insira um id válido")
        } else if (pump.quantity >= 10000 || pump.quantity + input.quantity >= 10000) {
            throw new RuntimeException("ERROR: a bomba de combustivel não suporta mais")
        }
        addFuelToPump(input.quantity, pump)
        addFuelToList(input)
        output.response = "O combustivel foi adicionado a bomba"

        return output
    }


}
