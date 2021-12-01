package com.example.tongue.checkout.flows;

import com.example.tongue.checkout.models.CheckoutAttribute;
import com.example.tongue.checkout.models.CheckoutAttributeName;
import com.example.tongue.checkout.models.ValidationResponse;
import com.example.tongue.locations.models.Location;
import com.example.tongue.merchants.models.Discount;
import com.example.tongue.merchants.models.Product;
import com.example.tongue.merchants.repositories.DiscountRepository;
import com.example.tongue.merchants.repositories.ModifierRepository;
import com.example.tongue.merchants.repositories.ProductRepository;
import com.example.tongue.shopping.models.Cart;
import com.example.tongue.shopping.models.LineItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@DisplayName("Checkout Attribute Validation Unit Testing")
@RunWith(MockitoJUnitRunner.class)
public class CheckoutAttributeValidationUnitTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModifierRepository modifierRepository;

    @Mock
    private DiscountRepository discountRepository;

    @InjectMocks
    private CheckoutValidation checkoutValidation;

    @BeforeAll
    public void setUp(){
        this.checkoutValidation = new CheckoutValidation();
    }

    @Before
    public void mockWhen(){
        Mockito.when(productRepository.existsById(100L)).thenReturn(true);
        Mockito.when(discountRepository.existsById(99L)).thenReturn(true);
        //Mockito.when(modifierRepository.existsById(10L)).thenReturn(true);
    }

    @Test
    public void givenEmptyLocationWhenValidatingLocationAttributeThenReturnFalse(){
        CheckoutAttribute locationAttribute =
                new CheckoutAttribute();
        Location location = new Location();
        locationAttribute.setAttribute(location);
        locationAttribute.setName(CheckoutAttributeName.DESTINATION);
        ValidationResponse response =
                checkoutValidation.attributeValidation(locationAttribute);
        assert Boolean.FALSE == response.isSolved();
    }

    @Test
    public void givenEmptyCartWhenValidatingCartAttributeThenReturnFalse(){
        CheckoutAttribute cartAttribute =
                new CheckoutAttribute();
        cartAttribute.setName(CheckoutAttributeName.CART);
        cartAttribute.setAttribute(null);
        ValidationResponse response =
                checkoutValidation.attributeValidation(cartAttribute);
        assert Boolean.FALSE == response.isSolved();
    }

    @Test
    public void givenCartWithEmptyItemsWhenValidatingCartAttributeThenReturnFalse(){
        CheckoutAttribute cartAttribute =
                new CheckoutAttribute();
        cartAttribute.setName(CheckoutAttributeName.CART);
        Cart cart = new Cart();
        cart.setDiscount(new Discount());
        cartAttribute.setAttribute(cart);
        ValidationResponse response =
                checkoutValidation.attributeValidation(cartAttribute);
        assert Boolean.FALSE == response.isSolved();
    }

    @Test
    public void givenCartWithNoExistingProductWhenCartValidationThenReturnMessage(){
        CheckoutAttribute cartAttribute =
                new CheckoutAttribute();
        cartAttribute.setName(CheckoutAttributeName.CART);
        Cart cart = new Cart();
        LineItem item = new LineItem();
        Product product = new Product();
        product.setId(101L);
        item.setProduct(product);
        item.setQuantity(2);
        cart.addItem(item);
        cartAttribute.setAttribute(cart);
        ValidationResponse response =
                checkoutValidation.attributeValidation(cartAttribute);
        assertEquals("Product with id '101' not found",response.getErrorMessage());
    }

    @Test
    public void givenCartWithExistingProductWhenCartValidationThenReturnTrue(){
        CheckoutAttribute cartAttribute =
                new CheckoutAttribute();
        cartAttribute.setName(CheckoutAttributeName.CART);
        Cart cart = new Cart();
        LineItem item = new LineItem();
        Product product = new Product();
        product.setId(100L);
        // Store Variant validation is done on HardValidation
        product.setStoreVariant(null);
        item.setProduct(product);
        item.setQuantity(2);
        cart.addItem(item);
        cartAttribute.setAttribute(cart);
        ValidationResponse response =
                checkoutValidation.attributeValidation(cartAttribute);
        assertTrue(response.isSolved());
    }

    @Test
    public void givenCartWithNoExistingDiscountWhenCartValidationThenReturnMessage(){
        String expected = "No such discount with id '6'";
        /** We copy the values from the last test because they are valid**/
        CheckoutAttribute cartAttribute =
                new CheckoutAttribute();
        cartAttribute.setName(CheckoutAttributeName.CART);
        Cart cart = new Cart();
        LineItem item = new LineItem();
        Product product = new Product();
        product.setId(100L);
        // Store Variant validation is done on HardValidation
        product.setStoreVariant(null);
        item.setProduct(product);
        item.setQuantity(2);
        cart.addItem(item);
        /** No registered Discount **/
        Discount discount = new Discount();
        discount.setId(6L);
        cart.setDiscount(discount);
        cartAttribute.setAttribute(cart);
        /** Assert **/
        ValidationResponse response =
                checkoutValidation.attributeValidation(cartAttribute);
        assertEquals(expected,response.getErrorMessage());
    }

    @Test
    public void givenCartWithExistingDiscountWhenCartValidationThenReturnTrue(){
        /** We copy the values from the last test because they are valid**/
        CheckoutAttribute cartAttribute =
                new CheckoutAttribute();
        cartAttribute.setName(CheckoutAttributeName.CART);
        Cart cart = new Cart();
        LineItem item = new LineItem();
        Product product = new Product();
        product.setId(100L);
        // Store Variant validation is done on HardValidation
        product.setStoreVariant(null);
        item.setProduct(product);
        item.setQuantity(2);
        cart.addItem(item);
        /** Registered Discount (Mocked)**/
        Discount discount = new Discount();
        discount.setId(99L);
        cart.setDiscount(discount);
        cartAttribute.setAttribute(cart);
        /** Assert **/
        ValidationResponse response =
                checkoutValidation.attributeValidation(cartAttribute);
        assertTrue(response.isSolved());
    }


}