package com.jp.gas_station.StationConfig

import com.jp.gas_station.PumpService.PumpService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StationConfig {
    @Bean
    PumpService pumpService(){
        return new PumpService()

    }
}
