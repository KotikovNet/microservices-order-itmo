# Order Management Microservices

Учебное REST-приложение управления пользователями и заказами. Проект состоит из трёх независимых Spring Boot сервисов;
каждый хранит данные в собственной базе PostgreSQL.

## Стек

- Java 21, Spring Boot 3.5.16, Spring Web, Spring Data JPA, Jakarta Validation
- PostgreSQL 16, Maven multi-module
- Docker и Docker Compose

Межсервисные запросы выполняются синхронно через Spring `RestClient`, с ограниченными таймаутами. Очередей, общей базы,
Kubernetes и Spring Cloud нет.

## Сервисы и поток создания заказа

| Сервис | Порт | База | Назначение |
|---|---:|---|---|
| User Service | 8081 | `user-db` | CRUD пользователей, проверка ID и уникальности email |
| Order Service | 8082 | `order-db` | CRUD заказов и проверка пользователя при создании |
| Notification Service | 8083 | `notification-db` | Сохраняет имитацию отправленного уведомления |

При `POST /api/orders` Order Service запрашивает `GET /api/users/{id}`. При 404 заказ не сохраняется. При успехе заказ
сохраняется в `order-db`, после чего Order Service отправляет DTO в Notification Service. Уведомление сохраняется со
статусом `SENT` и фиксируется в логе. Если Notification Service недоступен после сохранения, запрос заказа получает 503,
но заказ остаётся в базе; перед повтором клиенту следует проверить `GET /api/orders`.

## Структура

```text
.
├── pom.xml
├── docker-compose.yml
├── .env.example
├── user-service/
├── order-service/
└── notification-service/
```

В каждом модуле находятся приложение, `controller`, `service`, `repository`, `entity`, `dto`, `exception`, конфигурация,
`application.yml` и отдельный Dockerfile. DTO межсервисного вызова определены в вызывающем сервисе; сервисы не читают
чужие базы.

## Запуск

```bash
cp .env.example .env
# При необходимости замените демонстрационные пароли в .env
docker compose up --build
```

Compose публикует сервисы на портах 8081, 8082 и 8083, PostgreSQL — на 5433, 5434 и 5435. Для сохранения данных
используются volumes. Для остановки: `docker compose down`; для удаления данных тоже: `docker compose down -v`.

Вне Docker сервисы можно собрать командой `mvn -DskipTests package`. Для локального запуска PostgreSQL должны слушать
эти порты и иметь учётные данные из `.env.example`; адреса сервисов задаются `USER_SERVICE_URL` и
`NOTIFICATION_SERVICE_URL`.

## REST API

Все тела запросов и ответов — JSON. Ошибки имеют вид
`{"timestamp":"...","status":404,"code":"USER_NOT_FOUND","message":"User was not found"}`. Некорректная валидация
возвращает 400, дубликат email — 409, отсутствие ресурса — 404, недоступность зависимого сервиса — 503. Успешное
создание возвращает 201, удаление — 204.

### User Service

| Метод | Путь | Результат |
|---|---|---|
| POST | `/api/users` | Создать пользователя |
| GET | `/api/users/{id}` | Получить пользователя |
| GET | `/api/users` | Список пользователей |
| PUT | `/api/users/{id}` | Обновить пользователя |
| DELETE | `/api/users/{id}` | Удалить пользователя |
| GET | `/api/users/{id}/exists` | 204, если существует; иначе 404 |

```bash
curl -i -X POST http://localhost:8081/api/users -H 'Content-Type: application/json' \
  -d '{"name":"Иван Петров","email":"ivan@example.com"}'
curl http://localhost:8081/api/users
curl http://localhost:8081/api/users/1
curl -i -X PUT http://localhost:8081/api/users/1 -H 'Content-Type: application/json' \
  -d '{"name":"Иван Петров","email":"ivan.petrov@example.com"}'
curl -i http://localhost:8081/api/users/1/exists
curl -i -X DELETE http://localhost:8081/api/users/1
```

### Order Service

| Метод | Путь | Результат |
|---|---|---|
| POST | `/api/orders` | Проверить пользователя, создать заказ и уведомление |
| GET | `/api/orders/{id}` | Получить заказ |
| GET | `/api/orders` | Список заказов |
| GET | `/api/orders/user/{userId}` | Заказы пользователя |
| PUT | `/api/orders/{id}` | Обновить описание, сумму и/или статус |
| DELETE | `/api/orders/{id}` | Удалить заказ |

Статусы: `NEW`, `PROCESSING`, `COMPLETED`, `CANCELLED`; по умолчанию `NEW`. Сумма должна быть больше нуля.

```bash
curl -i -X POST http://localhost:8082/api/orders -H 'Content-Type: application/json' \
  -d '{"userId":1,"description":"Заказ №1","amount":1250.00}'
curl http://localhost:8082/api/orders
curl http://localhost:8082/api/orders/1
curl http://localhost:8082/api/orders/user/1
curl -i -X PUT http://localhost:8082/api/orders/1 -H 'Content-Type: application/json' \
  -d '{"userId":1,"description":"Обновлённый заказ","amount":1350.00,"status":"PROCESSING"}'
curl -i -X DELETE http://localhost:8082/api/orders/1
```

### Notification Service

| Метод | Путь | Результат |
|-------|---|---|
| POST  | `/api/notifications` | Сохранить уведомление со статусом `SENT ` ||  GET   | `/api/notifications/{id}` | Получить уведомле ние | 
| GET   | `/api/notifications` | Список уведомлений  |
| GET   | `/api/notifications/user/{userId}` | Уведомления пользователя |

```bash
# Вручную; обычно этот вызов выполняет Order Service
curl -i -X POST http://localhost:8083/api/notifications -H 'Content-Type: application/json' \
  -d '{"userId":1,"orderId":1,"recipientEmail":"maria@example.com","message":"Your order has been created."}'
curl http://localhost:8083/api/notifications
curl http://localhost:8083/api/notifications/1
curl http://localhost:8083/api/notifications/user/1
```

## Полный сценарий

```bash
# 1. Пользователь
curl -sS -X POST http://localhost:8081/api/users -H 'Content-Type: application/json' \
  -d '{"name":"Мария Смирнова","email":"maria@example.com"}'

# 2. Заказ. В userId укажите id из ответа предыдущего запроса.
curl -i -X POST http://localhost:8082/api/orders -H 'Content-Type: application/json' \
  -d '{"userId":1,"description":"Кофемашина","amount":24990.00}'

# 3. Заказ и созданное вслед за ним уведомление
curl http://localhost:8082/api/orders/user/1
curl http://localhost:8083/api/notifications/user/1
```

Проверка отсутствующего пользователя: отправьте заказ с `userId`, которого нет в User Service. Ожидаемый ответ — 404;
заказ не должен появиться в списке.

## Сборка и проверки

```bash
mvn -DskipTests package
docker compose config
docker compose up --build
```

В приложении нет тестовых классов и зависимостей тестирования.