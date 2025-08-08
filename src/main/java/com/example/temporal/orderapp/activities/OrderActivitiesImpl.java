package com.example.temporal.orderapp.activities;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class OrderActivitiesImpl implements OrderActivities {

    @Override
    public void createOrder(String orderId) {
        log.info("[Activity] Создание заказа {}", orderId);
        // симуляция задержки
        sleepShort();
    }

    @Override
    public boolean checkStock(String orderId) {
        log.info("[Activity] Проверка наличия товара для заказа {}", orderId);
        sleepShort();
        // симуляция: 80% в наличии
        boolean inStock = ThreadLocalRandom.current().nextInt(100) < 80;
        log.info("[Activity] checkStock -> {}", inStock);
        return inStock;
    }

    @Override
    public boolean processPayment(String orderId) {
        log.info("[Activity] Обработка оплаты заказа {}", orderId);
        sleepShort();
        // симуляция: 85% успешных платежей
        boolean paid = ThreadLocalRandom.current().nextInt(100) < 85;
        log.info("[Activity] processPayment -> {}", paid);
        return paid;
    }

    @Override
    public void notifyCustomer(String orderId) {
        log.info("[Activity] Отправка уведомления клиенту по заказу {}", orderId);
        sleepShort();
    }

    private void sleepShort() {
        try {
            Thread.sleep(500 + ThreadLocalRandom.current().nextInt(500));
        } catch (InterruptedException ignored) {
        }
    }
}
