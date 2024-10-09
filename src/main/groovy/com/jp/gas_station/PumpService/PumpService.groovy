package com.jp.gas_station.PumpService

import com.jp.gas_station.Models.AddFuel
import com.jp.gas_station.Models.AddFuelsOutput
import com.jp.gas_station.Models.Customer
import com.jp.gas_station.Models.FuelPump
import com.jp.gas_station.Models.ProfitPerPump
import org.springframework.stereotype.Service

@Service
class PumpService {
    private List<AddFuel> fuelList = []
    private CustomerService customerService

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

    void addPump(FuelPump pump) {
        pumps.put(pump.pumpId.toString(), pump)
    }

    AddFuel addFuelToList(FuelPump input) {
        FuelPump pump = pumps.values().find { it.pumpId == input.pumpId }
        AddFuel addFuel = new AddFuel()
        addFuel.pumpId = input.pumpId
        addFuel.type = pump.type
        addFuel.quantity = input.quantity
        addFuel.totalCost = input.quantity * pump.purchasePrice
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
                totalWasted: totalCost, totalProfit:  totalProfit)

    }

    AddFuelsOutput addFuelToPump(FuelPump input) {
        FuelPump pump = pumps.values().find { it.pumpId == input.pumpId }
        AddFuelsOutput output = new AddFuelsOutput()

        pump.addFuelToPumps(input.quantity)
        output.response = "O combustivel foi adicionado a bomba"
        output.pumpId = pump.pumpId
        output.quantityAdded = input.quantity

        return output
    }


}
