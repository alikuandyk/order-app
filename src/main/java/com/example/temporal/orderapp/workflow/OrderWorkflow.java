package com.example.temporal.orderapp.workflow;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface OrderWorkflow {

    @WorkflowMethod
    void processOrder(String orderId);

    @SignalMethod
    void cancelOrder();

    @QueryMethod
    String getOrderStatus();
}
