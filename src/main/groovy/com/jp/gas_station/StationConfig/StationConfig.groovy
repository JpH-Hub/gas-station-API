package com.jp.gas_station.StationConfig

import com.jp.gas_station.Controller.GasStationController
import com.jp.gas_station.PumpService.CustomerService
import com.jp.gas_station.PumpService.PumpService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StationConfig {
    @Bean
    PumpService pumpService() {
        return new PumpService(customerService())
    }

    @Bean
    CustomerService customerService() {
        return new CustomerService()
    }

    @Bean
    GasStationController gasStationController() {
        return new GasStationController(pumpService(), customerService())
    }

}
