# Order App — Пример работы с Temporal и Spring Boot

## Инструкции по запуску

1. Установи Docker и docker-compose  
   Убедись, что Docker и docker-compose установлены и работают.

2. Запусти Temporal:  
   git clone https://github.com/temporalio/docker-compose  
   cd docker-compose  
   docker-compose up -d  

   Дождись, пока все сервисы поднимутся (особенно temporal и temporal-admin).  
   Temporal Web UI будет доступен по адресу:  
   http://localhost:8080

3. Склонируй этот проект и собери jar:  
   mvn clean package

4. Запусти приложение Spring Boot:  
   mvn spring-boot:run

5. Открой Web UI приложения:  
   http://localhost:8081

   - Start — запускает Workflow с workflowId, равным введённому значению.
   - Cancel — отменяет Workflow с тем же workflowId.
   - Status — показывает текущее состояние Workflow.

## Краткое описание архитектуры

Приложение построено на Spring Boot и интегрировано с Temporal Java SDK.  
В проекте реализован один Workflow OrderWorkflow, который включает следующие шаги (Activities):
1. Создание заказа
2. Проверка наличия товара
3. Обработка оплаты
4. Уведомление клиента

Архитектура:
[Web UI] → [Spring Boot REST API + Temporal Worker] → [Temporal Server] → [Activities]

Workflow и Activities регистрируются в Temporal Worker, который слушает задачи из Task Queue.  
Temporal Server хранит состояние и управляет исполнением шагов, а Web UI приложения позволяет управлять запуском, отменой и проверкой статуса Workflows.
