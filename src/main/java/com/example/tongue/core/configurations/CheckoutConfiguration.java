package com.example.tongue.core.configurations;

import com.example.tongue.checkout.flows.*;
import com.example.tongue.core.converters.CheckoutAttributeConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CheckoutConfiguration {

    @Bean
    public CheckoutValidation checkoutValidation(){
        return new CheckoutValidation();
    }

    @Bean
    public CheckoutSession checkoutSession(){
        return new CheckoutSession();
    }

    @Bean
    public CheckoutCompletionFlow completionFlow(){
        return new CheckoutCompletionFlow();
    }

    @Bean
    public CheckoutUpgradeFlow upgradeFlow(){ return new CheckoutUpgradeFlow();}

    @Bean
    public CheckoutCreationFlow creationFlow(){ return new CheckoutCreationFlow();}

    @Bean
    public CheckoutAttributeConverter checkoutAttributeConverter(){return new CheckoutAttributeConverter();}

}
