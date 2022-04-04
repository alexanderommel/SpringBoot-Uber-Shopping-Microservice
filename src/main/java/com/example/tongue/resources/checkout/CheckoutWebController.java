package com.example.tongue.resources.checkout;

import com.example.tongue.domain.checkout.Checkout;
import com.example.tongue.domain.checkout.CheckoutAttribute;
import com.example.tongue.domain.checkout.CheckoutAttributeName;
import com.example.tongue.domain.checkout.FlowMessage;
import com.example.tongue.integration.customers.Customer;
import com.example.tongue.integration.customers.CustomerReplicationRepository;
import com.example.tongue.integration.payments.PaymentServiceBroker;
import com.example.tongue.integration.shipping.ShippingServiceBroker;
import com.example.tongue.repositories.checkout.CheckoutRepository;
import com.example.tongue.services.CheckoutCompletionFlow;
import com.example.tongue.services.CheckoutCreationFlow;
import com.example.tongue.services.CheckoutSession;
import com.example.tongue.services.CheckoutUpgradeFlow;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
public class CheckoutWebController {

    private CheckoutRepository checkoutRepository;
    private CheckoutCompletionFlow completionFlow;
    private CheckoutCreationFlow creationFlow;
    private CheckoutUpgradeFlow upgradeFlow;
    private CustomerReplicationRepository customerReplicationRepository;
    private PaymentServiceBroker paymentServiceBroker;
    private ShippingServiceBroker shippingServiceBroker;
    private CheckoutSession checkoutSession;

    public CheckoutWebController(@Autowired CheckoutRepository checkoutRepository,
                                 @Autowired CheckoutCompletionFlow completionFlow,
                                 @Autowired CheckoutCreationFlow creationFlow,
                                 @Autowired CheckoutUpgradeFlow upgradeFlow,
                                 @Autowired CustomerReplicationRepository customerReplicationRepository,
                                 @Autowired PaymentServiceBroker paymentServiceBroker,
                                 @Autowired ShippingServiceBroker shippingServiceBroker){

        this.checkoutRepository = checkoutRepository;
        this.completionFlow=completionFlow;
        this.upgradeFlow=upgradeFlow;
        this.creationFlow=creationFlow;
        this.customerReplicationRepository=customerReplicationRepository;
        this.paymentServiceBroker = paymentServiceBroker;
        this.shippingServiceBroker=shippingServiceBroker;
    }


    @GetMapping(value = "/checkouts",params = {"page","size"})
    public ResponseEntity<Map<String,Object>> all(@RequestParam(defaultValue = "0",required = false) int page
            , @RequestParam(defaultValue = "50",required = false) int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<Checkout> checkoutPage = checkoutRepository.findAll(pageable);
        return getResponseEntityByPageable(checkoutPage);
    }

    @GetMapping(value = "/checkout/create", consumes = "application/json")
    public ResponseEntity<Map<String,Object>> create(HttpSession session, @RequestBody  Checkout checkout){
        log.info("Creating Checkout for session id->"+session.getId());
        Map<String,Object> response = new HashMap<>();
        return getMapResponseEntity(response, creationFlow.run(checkout,session));
    }

    @PostMapping("/checkout/complete")
    public ResponseEntity<Map<String,Object>> complete(HttpSession session, Principal principal){
        log.info("Completing checkout...");
        Map<String,Object> response = new HashMap<>();
        Optional<Customer> optional = customerReplicationRepository.findByUsername(principal.getName());
        Object checkoutO = session.getAttribute("CHECKOUT");
        if (checkoutO==null){
            log.warn("Checkout not found on session!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Checkout checkout = (Checkout) checkoutO;

        Boolean validPaymentSession = paymentServiceBroker
                .validatePaymentSession(checkout.getPaymentInfo().getPaymentSession());

        Boolean validShippingSession = shippingServiceBroker
                .validatePaymentSession(checkout.getShippingInfo().getShippingSession());

        if (!validPaymentSession){
            response.put("error","Not valid PaymentSession");
            log.info("Not valid PaymentSession");
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        if (!validShippingSession){
            response.put("error","Not valid ShippingSession");
            log.info("Not valid ShippingSession");
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        FlowMessage message = completionFlow.run(session,optional.get());
        if (!message.isSolved()){
            response.put("error",message.getErrorMessage());
            log.info("Error found on CompletionFlow ("+message.getErrorMessage()+")");
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        Checkout checkout1 = (Checkout) message.getAttribute("checkout");
        response.put("response",checkout1);
        log.info("Responding with HttpStatus.OK");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping(value = "/checkout/update", consumes = "application/json")
    public ResponseEntity<Map<String,Object>> update(
            HttpSession session
            , @RequestBody Checkout checkout
            , @RequestParam(name = "attribute") CheckoutAttributeName attribute){

        log.info("Updating Checkout for session id '"+session.getId()+"'");
        log.info("Attribute name to change is '"+attribute.name()+"'");
        Map<String,Object> response = new HashMap<>();

        CheckoutAttribute checkoutAttribute = new CheckoutAttribute();
        checkoutAttribute.setName(attribute);
        if (attribute==CheckoutAttributeName.CART)
            checkoutAttribute.setAttribute(checkout.getShoppingCart());
        if (attribute==CheckoutAttributeName.PAYMENT)
            checkoutAttribute.setAttribute(checkout.getPaymentInfo());
        if (attribute==CheckoutAttributeName.SHIPPING)
            checkoutAttribute.setAttribute(checkout.getShippingInfo());


        FlowMessage message = upgradeFlow.run(checkoutAttribute,session);

        if (!message.isSolved()){
            response.put("error",message.getErrorMessage());
            log.info("FlowMessage for Attribute Updating has errors ("+message.getErrorMessage()+")");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        Checkout c = (Checkout) message.getAttribute("checkout");
        response.put("response",c);

        log.info("Call to /checkout/update has been solved successfully!");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/checkout")
    public ResponseEntity<Map<String,Object>> getMyCheckoutOnSession(HttpSession session){
        log.info("Retrieving current Checkout from HttpSession");
        Map<String,Object> response = new HashMap<>();
        Checkout checkout = (Checkout) session.getAttribute("CHECKOUT");
        response.put("response",checkout);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    //----------------------------------- PRIVATE METHODS ------------------------------------------------

    private ResponseEntity<Map<String,Object>> getResponseEntityByPageable(Page page){
        try {
            if (page==null){
                throw  new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No checkouts found");
            }
            List<Checkout> checkouts = page.getContent();
            Map<String,Object> response = new HashMap<>();
            response.put("checkouts",checkouts);
            response.put("page",page.getNumber());
            response.put("pages",page.getTotalPages());
            log.info("Ok");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e){
            log.info("Error");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @NotNull
    private ResponseEntity<Map<String, Object>> getMapResponseEntity(Map<String, Object> response, FlowMessage run) {
        FlowMessage message = run;
        if (!message.isSolved()){
            response.put("error",message.getErrorMessage());
            log.info("FlowMessage for Updating has errors ("+message.getErrorMessage()+")");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        Checkout checkout = (Checkout) message.getAttribute("checkout");
        response.put("response",checkout);
        log.info("Responding with HttpStatus.OK");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


}
