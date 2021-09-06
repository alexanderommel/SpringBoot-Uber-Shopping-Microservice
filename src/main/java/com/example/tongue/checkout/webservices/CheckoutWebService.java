package com.example.tongue.checkout.webservices;

import com.example.tongue.checkout.filters.*;
import com.example.tongue.checkout.models.Checkout;
import com.example.tongue.checkout.models.CheckoutAttribute;
import com.example.tongue.checkout.repositories.CheckoutRepository;
import com.example.tongue.core.converters.CheckoutAttributeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CheckoutWebService {

    private CheckoutRepository checkoutRepository;
    private CheckoutRequestChain requestChain;
    private CheckoutUpdateChain updateChain;

    public CheckoutWebService(@Autowired CheckoutRepository checkoutRepository){
        this.checkoutRepository = checkoutRepository;
        this.requestChain = new CheckoutRequestChain();
        this.updateChain = new CheckoutUpdateChain();
    }


    @GetMapping(value = "/checkouts",params = {"page","size"})
    public ResponseEntity<Map<String,Object>> all(@RequestParam(defaultValue = "0",required = false) int page
            , @RequestParam(defaultValue = "50",required = false) int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<Checkout> checkoutPage = checkoutRepository.findAll(pageable);
        return getResponseEntityByPageable(checkoutPage);
    }


    /**
     * Only origin and storeVariant must be populated
     * It creates a first checkout instance inside user's session
     * @param session
     * @param checkout
     * @return
     */

    @GetMapping("/checkout/create")
    public ResponseEntity<Map<String,Object>> create(HttpSession session, Checkout checkout){
        Map<String,Object> response = new HashMap<>();
        Checkout checkout1 = this.requestChain.doFilter(checkout,session);
        response.put("response",checkout1);
        return new ResponseEntity<>(response,
                HttpStatus.OK);
    }


    @PostMapping(value = "/checkout/update")
    public ResponseEntity<Map<String,Object>>
    update(HttpSession session, @RequestBody  String attribute){

            CheckoutAttributeConverter converter = new CheckoutAttributeConverter();
            CheckoutAttribute checkoutAttribute =
                    (CheckoutAttribute) converter.convert(attribute,null,null);
            Checkout checkout = this.updateChain.doFilter(checkoutAttribute,session);
            Map<String,Object> response = new HashMap<>();
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
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}