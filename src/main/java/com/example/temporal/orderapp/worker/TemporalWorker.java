package com.example.temporal.orderapp.worker;

import com.example.temporal.orderapp.activities.OrderActivitiesImpl;
import com.example.temporal.orderapp.workflow.OrderWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TemporalWorker {

    private static final String TASK_QUEUE = "ORDER_TASK_QUEUE";

    private final WorkflowServiceStubs service;
    private final WorkflowClient client;

    public TemporalWorker(WorkflowServiceStubs service, WorkflowClient client) {
        this.service = service;
        this.client = client;
    }

    @PostConstruct
    public void startWorker() {
        log.info("Starting Temporal Worker...");
        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker(TASK_QUEUE);
        worker.registerWorkflowImplementationTypes(OrderWorkflowImpl.class);
        worker.registerActivitiesImplementations(new OrderActivitiesImpl());
        factory.start();
        log.info("Temporal Worker started, listening on {}", TASK_QUEUE);
    }
}
