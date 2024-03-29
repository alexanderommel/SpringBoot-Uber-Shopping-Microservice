package com.example.tongue.integration.orders;

import com.example.tongue.domain.checkout.PaymentInfo;
import com.example.tongue.domain.shopping.ShoppingCart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {

    private String artifactId;
    private String shoppingCartId;
    private BigDecimal total;
    private PaymentInfo.PaymentMethod paymentMethod;

}
