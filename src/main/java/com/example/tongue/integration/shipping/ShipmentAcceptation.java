package com.example.tongue.integration.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShipmentAcceptation {
    private String artifactId;
    private String shippingId;
    private String driverUsername;
}