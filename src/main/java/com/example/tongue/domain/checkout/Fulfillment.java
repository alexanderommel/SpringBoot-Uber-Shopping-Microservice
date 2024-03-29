package com.example.tongue.domain.checkout;

import com.example.tongue.integration.orders.Order;
import com.example.tongue.integration.payments.Payment;
import com.example.tongue.integration.shipping.Shipping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Fulfillment {

    @Id @GeneratedValue
    private Long fulfillmentId;
    @OneToOne
    private Checkout checkout;
    @OneToOne
    private Payment payment;
    @OneToOne
    private Order order;
    @OneToOne
    private Shipping shipping;
}
