package com.jp.gas_station.PumpService

import com.jp.gas_station.Models.Customer
import com.jp.gas_station.Models.FuelPump
import org.springframework.stereotype.Service

@Service
class CustomerService {
    private List<Customer> customersList = []

    Customer saveCustomer(Customer input, FuelPump pump) {
        input.totalPaid = pump.sellingPrice * input.amountRefueled
        customersList.add(input)
        return input
    }

    List<Customer> getCustomers() {
        return customersList
    }

}
