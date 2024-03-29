package com.example.tongue.resources.merchant;


import com.example.tongue.domain.merchant.Discount;
import com.example.tongue.repositories.merchant.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DiscountsRestController {
    //FIELDS
    private DiscountRepository discountRepository;
    //CONSTRUCTOR
    public DiscountsRestController(@Autowired DiscountRepository discountRepository){
        this.discountRepository = discountRepository;
    }
    //METHODS

  /*
    POST MAPPINGS ----------------------------------------------------------------------------------------------------------- START
     */
  @PostMapping(value="/discounts", consumes={"application/json"})
  public ResponseEntity<Map<String,Object>> insert(@Valid @RequestBody Discount discount){

          //OffsetDateTime.parse(discount.getExpiresAt());
          Map<String,Object> response = new HashMap<>();
          discount.setId(null);
          Discount discount1 = discountRepository.save(discount);
          response.put("discount",discount1);
          return new ResponseEntity<>(response,HttpStatus.OK);

  }
    /*
   POST MAPPINGS ----------------------------------------------------------------------------------------------------------- END
    */

    /*
    GET MAPPINGS ----------------------------------------------------------------------------------------------------------- START
    (Get mappings by usual filtering are at the end of file)
     */

    @GetMapping(value = "/discounts", params = {"page","size"})
    public ResponseEntity<Map<String,Object>> all(@RequestParam(defaultValue = "0", required = false) int page
            , @RequestParam(defaultValue = "50", required = false) int size){

        Pageable pageable = (Pageable) PageRequest.of(page, size);
        Page<Discount> discountPage = discountRepository.findAll(pageable);
        return getResponseEntityByPageable(discountPage);
    }

    /*
    GET MAPPINGS ----------------------------------------------------------------------------------------------------------- END
     */

     /*
    INTERNAL METHODS ------------------------------------------------------------------------------------------------------ START
     */

    private ResponseEntity<Map<String,Object>> getResponseEntityByDiscount(Discount discount){
        try {
            if (discount==null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Map<String,Object> response = new HashMap<>();
            response.put("discount",discount);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Map<String,Object>> getResponseEntityByPageable(Page page){
        try{
            if (page==null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            List<Discount> discounts = page.getContent();
            Map<String,Object> response = new HashMap<>();
            response.put("discounts",discounts);
            response.put("page",page.getNumber());
            response.put("pages",page.getTotalPages());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
