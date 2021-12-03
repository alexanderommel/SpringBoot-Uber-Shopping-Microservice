package com.example.tongue.checkout.flows;

import com.example.tongue.checkout.models.Checkout;
import com.example.tongue.checkout.models.CheckoutAttribute;
import com.example.tongue.checkout.models.CheckoutAttributeName;
import com.example.tongue.checkout.models.ValidationResponse;
import com.example.tongue.locations.models.Location;
import com.example.tongue.merchants.models.Discount;
import com.example.tongue.merchants.models.Modifier;
import com.example.tongue.merchants.models.Product;
import com.example.tongue.merchants.models.StoreVariant;
import com.example.tongue.merchants.repositories.DiscountRepository;
import com.example.tongue.merchants.repositories.ModifierRepository;
import com.example.tongue.merchants.repositories.ProductRepository;
import com.example.tongue.merchants.repositories.StoreVariantRepository;
import com.example.tongue.shopping.models.Cart;
import com.example.tongue.shopping.models.LineItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CheckoutValidation {

    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private StoreVariantRepository storeVariantRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ModifierRepository modifierRepository;

    public ValidationResponse hardValidation(Checkout checkout){
        ValidationResponse response;
        response = validateAttributes(checkout);
        return response;
    }

    private ValidationResponse validateAttributes(Checkout checkout){
        ValidationResponse response = new ValidationResponse();
        response.setSolved(false);
        Cart cart = checkout.getCart();
        Location destination = checkout.getDestination();
        Location origin = checkout.getOrigin();
        response = attributeValidation(new CheckoutAttribute(cart,CheckoutAttributeName.CART));
        if (!response.isSolved())
            return response;
        response = attributeValidation(new CheckoutAttribute(destination,CheckoutAttributeName.DESTINATION));
        if (!response.isSolved())
            return response;
        response = attributeValidation(new CheckoutAttribute(origin,CheckoutAttributeName.ORIGIN));
        return response;
    }

    /** Attribute Validation validates that everything that's in the shopping cart has a real id **/
    public ValidationResponse attributeValidation(CheckoutAttribute checkoutAttribute) {
        ValidationResponse response = new ValidationResponse();
        response.setSolved(false);
        if (checkoutAttribute == null) {
            response.setErrorMessage("CheckoutAttribute instance is empty");
        }
        if (checkoutAttribute.getAttribute()==null){
            response.setErrorMessage("CheckoutAttribute Attribute field is empty");
            return response;
        }

        if (checkoutAttribute.getName() == CheckoutAttributeName.CART) {
            response = validateCartAttribute(checkoutAttribute);
        }

        if (checkoutAttribute.getName() == CheckoutAttributeName.DESTINATION) {
            response = validateDestinationAttribute(checkoutAttribute);
        }

        if (checkoutAttribute.getName() == CheckoutAttributeName.ORIGIN) {
            response = validateDestinationAttribute(checkoutAttribute);
        }
        return response;
    }

    public ValidationResponse softValidation(Checkout checkout){
        ValidationResponse response = new ValidationResponse();
        response.setSolved(false);
        Location origin = checkout.getOrigin();

        if (origin==null){
            response.setErrorMessage("Origin location object is mandatory");
            return response;
        }

        Float latitude = origin.getLatitude();
        Float longitude = origin.getLongitude();
        if(latitude==null || longitude==null){
            response.setErrorMessage("Origin location attributes must be populated");
            return response;
        }

        StoreVariant storeVariant = checkout.getStoreVariant();
        if (storeVariant==null){
            response.setErrorMessage("StoreVariant object is mandatory");
            return response;
        }
        Long storeVariantId = storeVariant.getId();
        if (storeVariantId==null){
            response.setErrorMessage("Store variant field 'id' is mandatory");
            return response;
        }
        if (!storeVariantRepository.existsById(storeVariantId)){
            response.setErrorMessage("No such Store Variant");
            return response;
        }
        try{
            Cart cart = checkout.getCart();
            LineItem item = cart.getItems().get(0);
            Product product = item.getProduct();
            if (!productRepository.existsById(product.getId())){
                response.setErrorMessage("No such Product with id '"+product.getId()+"'");
                return response;
            }
        }catch (Exception e){
            response.setErrorMessage("Please add at least one item to your Checkout instance");
            return response;
        }
        response.setSolved(true);
        return response;
    }


    private ValidationResponse validateDestinationAttribute(CheckoutAttribute checkoutAttribute){
        ValidationResponse response = new ValidationResponse();
        response.setSolved(false);
        Location destination = (Location) checkoutAttribute.getAttribute();
        if (destination == null){
            response.setErrorMessage("Origin location object is mandatory");
            return response;
        }
        if (!destination.isValid()){
            response.setErrorMessage("Destination location object has no valid format");
            return response;
        }
        response.setSolved(true);
        return response;
    }

    /** It verifies that everything in the cart has an existing associated id (Except store variant)**/
    private ValidationResponse validateCartAttribute(CheckoutAttribute attribute) {
        ValidationResponse response = new ValidationResponse();
        response.setSolved(false);
        Cart cart = (Cart) attribute.getAttribute();
        Discount discount = cart.getDiscount();
        List<LineItem> items = cart.getItems();
        if (discount != null) {

            response = discountValidationWrapper(discount, response);
            if (!response.isSolved())
                return response;
            response.setSolved(false);

        }
        if (items != null) {
            if (items.isEmpty()){
                response.setErrorMessage("Your Cart shouldn't be empty");
                return response;
            }
            for (LineItem item : items) {
                Long productId = item.getProduct().getId();
                List<Modifier> modifiers = item.getModifiers();
                if (!productRepository.existsById(productId)) {
                    response.setErrorMessage("Product with id '" + productId + "' not found");
                    return response;
                }
                // Item Discount Validation
                Discount itemDiscount = item.getDiscount();
                if (!(itemDiscount == null)) {

                    response = discountValidationWrapper(discount, response);
                    if (!response.isSolved())
                        return response;
                    response.setSolved(false);

                }
                // Modifiers Validation
                if (modifiers != null) {
                    for (Modifier modifier : modifiers
                    ) {
                        Long modifierId = modifier.getId();
                        if (!modifierRepository.existsById(modifierId)) {
                            response.setErrorMessage("Modifier with id '" + modifierId + "' not found");
                            return response;
                        }
                    }
                }
            }
        }
        response.setSolved(true);
        return response;
    }

    private ValidationResponse discountValidationWrapper(Discount discount,ValidationResponse response){
        Long discountId = discount.getId();
        response.setSolved(true);
        if (discountId==null){
            response.setErrorMessage("Empty Id reference");
            response.setSolved(false);
            return response;
        }
        if (!discountRepository.existsById(discountId)){
            response.setErrorMessage("No such discount with id '"+discountId+"'");
            response.setSolved(false);
        }
        return response;
    }

}