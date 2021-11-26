package com.example.tongue.checkout.models;

public class CheckoutAttribute {

    private Object attribute;
    private CheckoutAttributeName name;

    public CheckoutAttribute(){}

    public CheckoutAttribute(Object attribute,CheckoutAttributeName name){
        this.attribute=attribute;
        this.name=name;
    }

    public Object getAttribute() {
        return attribute;
    }

    public void setAttribute(Object attribute) {
        this.attribute = attribute;
    }

    public CheckoutAttributeName getName() {
        return name;
    }

    public void setName(CheckoutAttributeName name) {
        this.name = name;
    }
}
