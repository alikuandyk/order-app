package com.example.temporal.orderapp.workflow;

import com.example.temporal.orderapp.activities.OrderActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ApplicationFailure;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class OrderWorkflowImpl implements OrderWorkflow {

    private enum Status {CREATED, CHECKING_STOCK, PAYMENT, NOTIFYING, COMPLETED, CANCELLED, FAILED}

    private Status status = Status.CREATED;
    private boolean cancelRequested = false;

    private final OrderActivities activities = Workflow.newActivityStub(
            OrderActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(10))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(1))
                            .setMaximumInterval(Duration.ofSeconds(5))
                            .setMaximumAttempts(3)
                            .build())
                    .build()
    );

    @Override
    public void processOrder(String orderId) {
        log.info("[Workflow] processOrder started: {}", orderId);
        try {
            if (cancelRequested) {
                status = Status.CANCELLED;
                return;
            }

            status = Status.CREATED;
            activities.createOrder(orderId);

            status = Status.CHECKING_STOCK;
            boolean inStock = activities.checkStock(orderId);
            if (!inStock) {
                status = Status.FAILED;
                throw ApplicationFailure.newNonRetryableFailure("Item not found", "NOT_IN_STOCK");
            }

            if (cancelRequested) {
                status = Status.CANCELLED;
                return;
            }

            status = Status.PAYMENT;
            boolean paid = activities.processPayment(orderId);
            if (!paid) {
                status = Status.FAILED;
                throw ApplicationFailure.newNonRetryableFailure("Payment failed", "PAYMENT_FAILED");
            }

            if (cancelRequested) {
                status = Status.CANCELLED;
                return;
            }

            status = Status.NOTIFYING;
            activities.notifyCustomer(orderId);

            status = Status.COMPLETED;
            log.info("[Workflow] processOrder completed: {}", orderId);
        } catch (Exception e) {
            log.error("[Workflow] Error in workflow: {}", e.toString());
            throw e;
        }
    }

    @Override
    public void cancelOrder() {
        log.info("[Workflow] cancelOrder signal received");
        this.cancelRequested = true;
        this.status = Status.CANCELLED;
    }

    @Override
    public String getOrderStatus() {
        return this.status.name();
    }
}