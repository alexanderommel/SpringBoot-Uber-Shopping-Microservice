package com.example.tongue.integration.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Distance;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingSummary {

    private BigDecimal fee;
    private LocalTime arrivalTime;
    private Distance distance;

}
