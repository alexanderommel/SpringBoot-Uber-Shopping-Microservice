package com.example.tongue.sales.models;

import com.example.tongue.merchants.models.Discount;
import com.example.tongue.merchants.models.GroupModifier;
import com.example.tongue.merchants.models.Modifier;
import com.example.tongue.merchants.models.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class LineItem {
    private @Id @GeneratedValue Long id;

    @ManyToOne
    @NotNull
    private Product product;

    @NotNull
    private int quantity=1;

    @Embedded
    private LineItemPrice price;

    private String instructions;

    @ManyToOne
    private Discount discount;

    @ManyToMany
    private List<Modifier> modifiers;

    public List<Modifier> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<Modifier> modifiers) {
        this.modifiers = modifiers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LineItemPrice getPrice() {
        return price;
    }

    public void setPrice(LineItemPrice price) {
        this.price = price;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    @JsonIgnore
    public void ignoreDiscountAndUpdatePrice(){
        Double unitPrice = product.getPrice();
        Double itemPrice = unitPrice * quantity;
        price.setUnitPrice(unitPrice);
        price.setTotalPrice(itemPrice);
        price.setFinalPrice(itemPrice);
    }

    @JsonIgnore
    public void updatePrice(){ // Discounts not ignored

    }

}
