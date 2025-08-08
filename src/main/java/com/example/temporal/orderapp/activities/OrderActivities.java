package com.example.temporal.orderapp.activities;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface OrderActivities {
    void createOrder(String orderId);
    boolean checkStock(String orderId);
    boolean processPayment(String orderId);
    void notifyCustomer(String orderId);
}
