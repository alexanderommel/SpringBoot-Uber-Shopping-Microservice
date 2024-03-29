package com.example.tongue.domain.checkout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutPrice implements Serializable {
    
    private BigDecimal cartTotal;
    private BigDecimal cartSubtotal;
    private BigDecimal shippingTotal;
    private BigDecimal shippingSubtotal;
    private BigDecimal checkoutTotal;
    private BigDecimal checkoutSubtotal;
    
}
