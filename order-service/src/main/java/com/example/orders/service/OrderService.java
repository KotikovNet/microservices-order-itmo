package com.example.orders.service;

import com.example.orders.dto.*;
import com.example.orders.entity.Order;
import com.example.orders.exception.ApiException;
import com.example.orders.repository.OrderRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.*;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orders;
    private final RestClient http;
    private final String userUrl, notificationUrl;

    public OrderService(OrderRepository orders, RestClient http, @Value("${services.user-url}") String userUrl, @Value("${services.notification-url}") String notificationUrl) {
        this.orders = orders;
        this.http = http;
        this.userUrl = userUrl;
        this.notificationUrl = notificationUrl;
    }

    public OrderResponse create(OrderRequest request) {
        UserLookupResponse user = lookupUser(request.userId());
        String status = request.status() == null ? "NEW" : request.status();
        Order order = orders.saveAndFlush(new Order(request.userId(), request.description(), request.amount(), status));
        try {
            http.post().uri(notificationUrl + "/api/notifications").body(new NotificationRequest(user.id(), order.getId(), user.email(), "Your order " + order.getId() + " has been created.")).retrieve().toBodilessEntity();
        } catch (RestClientException ex) {
            throw ApiException.dependency();
        }
        return OrderResponse.from(order);
    }

    private UserLookupResponse lookupUser(long userId) {
        try {
            return http.get().uri(userUrl + "/api/users/{id}", userId).retrieve().onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                if (res.getStatusCode().value() == 404) throw ApiException.userNotFound();
                throw ApiException.dependency();
            }).body(UserLookupResponse.class);
        } catch (ApiException e) {
            throw e;
        } catch (RestClientException e) {
            throw ApiException.dependency();
        }
    }

    @Transactional(readOnly = true)
    public OrderResponse get(long id) {
        return OrderResponse.from(find(id));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> list() {
        return orders.findAll().stream().map(OrderResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> byUser(long id) {
        return orders.findByUserId(id).stream().map(OrderResponse::from).toList();
    }

    public OrderResponse update(long id, OrderRequest r) {
        Order o = find(id);
        lookupUser(r.userId());
        o.update(r.userId(), r.description(), r.amount(), r.status() == null ? o.getStatus() : r.status());
        return OrderResponse.from(orders.save(o));
    }

    public void delete(long id) {
        orders.delete(find(id));
    }

    private Order find(long id) {
        return orders.findById(id).orElseThrow(ApiException::notFound);
    }
}
