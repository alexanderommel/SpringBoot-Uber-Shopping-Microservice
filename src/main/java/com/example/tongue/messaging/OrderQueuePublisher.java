package com.example.tongue.messaging;

import com.example.tongue.integration.orders.OrderCompleted;
import com.example.tongue.integration.orders.OrderConfirmation;
import com.example.tongue.integration.orders.OrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderQueuePublisher {

    private RabbitTemplate rabbitTemplate;
    private String orderRequestQueue;
    private String orderConfirmationQueue;
    private String orderCompletionQueue;

    public OrderQueuePublisher(@Autowired RabbitTemplate rabbitTemplate,
                               @Value("${shopping.queues.out.order.request}") String orderRequestQueueName,
                               @Value("${shopping.queues.out.order.confirmation}") String orderConfirmationQueue,
                               @Value("${shopping.queues.out.order.completion}") String orderCompletionQueue) {

        this.rabbitTemplate=rabbitTemplate;
        this.orderRequestQueue=orderRequestQueueName;
        this.orderConfirmationQueue=orderConfirmationQueue;
        this.orderCompletionQueue=orderCompletionQueue;
    }

    @Async
    public void publishOrderRequest(OrderRequest orderRequest){
        log.info("Publishing Order Request to Queue: "+orderRequestQueue);
        log.info("Order request -> "+orderRequest);
        rabbitTemplate.convertAndSend(orderRequestQueue, orderRequest);
    }

    @Async
    public void publishOrderConfirmation(OrderConfirmation orderConfirmation){
        log.info("Publishing Order Confirmation to Queue: "+orderConfirmationQueue);
        log.info("Order Confirmation -> "+orderConfirmation);
        rabbitTemplate.convertAndSend(orderConfirmationQueue,orderConfirmation);
    }

    @Async
    public void publishOrderCompletion(OrderCompleted orderCompleted){
        log.info("Publishing Order Confirmation to Queue: "+orderCompletionQueue);
        log.info("Order Confirmation -> "+orderCompleted);
        rabbitTemplate.convertAndSend(orderCompletionQueue,orderCompleted);
    }

}
