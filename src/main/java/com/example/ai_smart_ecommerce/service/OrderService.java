package com.example.ai_smart_ecommerce.service;

import com.example.ai_smart_ecommerce.dto.OrderItemResponseDTO;
import com.example.ai_smart_ecommerce.dto.OrderResponseDTO;
import com.example.ai_smart_ecommerce.entity.*;
import com.example.ai_smart_ecommerce.enums.OrderStatus;
import com.example.ai_smart_ecommerce.enums.PaymentStatus;
import com.example.ai_smart_ecommerce.repository.OrderRepository;
import com.example.ai_smart_ecommerce.repository.PaymentRepository;
import com.example.ai_smart_ecommerce.repository.ProductRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PaymentRepository paymentRepository;




    @Transactional
    public Order placeOrder(User user, Map<Long, Integer> products) {

        if (products == null || products.isEmpty()) {
            throw new RuntimeException("Order must contain at least one product");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());


        order.setStatus(OrderStatus.PAYMENT_PENDING);


        double totalAmount = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : products.entrySet()) {

            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() ->
                            new RuntimeException("Product not found with id: " + productId));

            if (product.getStock() < quantity) {
                throw new RuntimeException(
                        "Insufficient stock for product: " + product.getName()
                );
            }

            product.setStock(product.getStock() - quantity);
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(product.getPrice() * quantity);
            orderItem.setOrder(order);

            totalAmount += orderItem.getPrice();
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        return orderRepository.save(order);
    }


    public List<OrderResponseDTO> getOrdersForUser(User user) {
        return orderRepository.findByUser(user)
                .stream()
                .map(this::mapToOrderDTO)
                .toList();
    }

    public Page<OrderResponseDTO> getAllOrders(Pageable pageable){
        return orderRepository.findAll(pageable)
                .map(this::mapToOrderDTO);
    }

    private OrderResponseDTO mapToOrderDTO(Order order){
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());

        List<OrderItemResponseDTO> itemDTOs = order.getOrderItems()
                .stream()
                .map(item -> {
                    OrderItemResponseDTO itemDTO = new OrderItemResponseDTO();
                    itemDTO.setProductId(item.getProduct().getId());
                    itemDTO.setProductName(item.getProduct().getName());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setPrice(item.getPrice());
                    return itemDTO;
                })
                .toList();

        dto.setItems(itemDTOs);
        dto.setStatus(order.getStatus().name());

        return dto;
    }

    @Transactional
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus currentStatus = order.getStatus();

        //  Cannot modify DELIVERED or CANCELLED orders
        if (currentStatus == OrderStatus.DELIVERED ||
                currentStatus == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order status cannot be changed from " + currentStatus);
        }

        // ✅ Valid transitions
        if (currentStatus == OrderStatus.PLACED &&
                (newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELLED)) {

            order.setStatus(newStatus);
        }
        else if (currentStatus == OrderStatus.SHIPPED &&
                (newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.CANCELLED)) {

            order.setStatus(newStatus);
        }
        else {
            throw new RuntimeException("Invalid order status transition");
        }

        Order updated = orderRepository.save(order);

        return mapToOrderDTO(updated);
    }

    @Transactional
    public OrderResponseDTO cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Delivered orders cannot be cancelled");
        }

        // Restore stock
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        //  Update status
        order.setStatus(OrderStatus.CANCELLED);

        Order updated = orderRepository.save(order);

        return mapToOrderDTO(updated);

    }

    public OrderResponseDTO trackOrder(Long orderId, User user) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Security check: user should only track their own order
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to view this order");
        }

        return mapToOrderDTO(order);
    }

    @Transactional
    public OrderResponseDTO processPayment(Long orderId, boolean paymentSuccess) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {
            throw new RuntimeException("Invalid order state for payment");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setTransactionId("TXN-" + System.currentTimeMillis());

        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.SUCCESS);
            order.setStatus(OrderStatus.PLACED);
        } else {

            payment.setStatus(PaymentStatus.FAILED);

            // Restore stock
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }

            order.setStatus(OrderStatus.CANCELLED);
        }

        paymentRepository.save(payment);
        orderRepository.save(order);

        return mapToOrderDTO(order);
    }

    public List<Payment> getPaymentHistory(Long userId) {
        return paymentRepository.findByOrder_User_Id(userId);
    }

    public List<Product> recommendProducts(User user) {

        List<Order> orders = orderRepository.findByUser(user);

        if (orders.isEmpty()) {
            return productRepository.findAll()
                    .stream()
                    .limit(5)
                    .toList(); // fallback recommendation
        }

        Map<String, Integer> categoryCount = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                String category = item.getProduct().getCategory();
                categoryCount.put(category,
                        categoryCount.getOrDefault(category, 0) + 1);
            }
        }

        // Find most ordered category
        String topCategory = Collections.max(
                categoryCount.entrySet(),
                Map.Entry.comparingByValue()
        ).getKey();

        return productRepository.findTop5ByCategory(topCategory);
    }



}
