package com.example.temporal.orderapp.controller;

import com.example.temporal.orderapp.workflow.OrderWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final String TASK_QUEUE = "ORDER_TASK_QUEUE";
    private final WorkflowClient client;

    public OrderController(WorkflowClient client) {
        this.client = client;
    }

    @PostMapping("/start/{orderId}")
    public ResponseEntity<String> startOrder(@PathVariable String orderId) {
        OrderWorkflow workflow = client.newWorkflowStub(
                OrderWorkflow.class,
                WorkflowOptions.newBuilder().setTaskQueue(TASK_QUEUE).build()
        );
        WorkflowClient.start(workflow::processOrder, orderId);
        return ResponseEntity.ok("Заказ " + orderId + " запущен");
    }

    @PostMapping("/cancel/{workflowId}")
    public ResponseEntity<String> cancelOrder(@PathVariable String workflowId) {
        WorkflowStub stub = client.newUntypedWorkflowStub(workflowId);
        stub.signal("cancelOrder");
        return ResponseEntity.ok("Отправлен сигнал отмены для workflow: " + workflowId);
    }

    @GetMapping("/status/{workflowId}")
    public ResponseEntity<String> getStatus(@PathVariable String workflowId) {
        WorkflowStub stub = client.newUntypedWorkflowStub(workflowId);
        String status = stub.query("getOrderStatus", String.class);
        return ResponseEntity.ok(status);
    }
}
