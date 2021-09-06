package com.example.tongue.checkout.filters;

import com.example.tongue.checkout.models.Checkout;

import javax.servlet.http.HttpSession;

public interface CheckoutFilter {
    public Checkout doFilter(Checkout checkout, HttpSession session);
}
