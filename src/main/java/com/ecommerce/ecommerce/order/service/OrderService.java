package com.ecommerce.ecommerce.order.service;

import com.ecommerce.ecommerce.common.exception.BadRequestException;
import com.ecommerce.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.inventory.dto.request.StockMovementRequest;
import com.ecommerce.ecommerce.inventory.entity.ProductStock;
import com.ecommerce.ecommerce.inventory.entity.StockMovement;
import com.ecommerce.ecommerce.inventory.repository.ProductStockRepository;
import com.ecommerce.ecommerce.inventory.service.InventoryService;
import com.ecommerce.ecommerce.order.dto.request.OrderCreateRequest;
import com.ecommerce.ecommerce.order.dto.response.OrderResponse;
import com.ecommerce.ecommerce.order.entity.Order;
import com.ecommerce.ecommerce.order.entity.OrderItem;
import com.ecommerce.ecommerce.order.entity.OrderStatus;
import com.ecommerce.ecommerce.order.entity.PaymentStatus;
import com.ecommerce.ecommerce.order.repository.OrderRepository;
import com.ecommerce.ecommerce.product.entity.Product;
import com.ecommerce.ecommerce.product.repository.ProductRepository;
import com.ecommerce.ecommerce.seller.entity.Seller;
import com.ecommerce.ecommerce.seller.repository.SellerRepository;
import com.ecommerce.ecommerce.user.model.User;
import com.ecommerce.ecommerce.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;
    private final InventoryService inventoryService;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;

    public OrderService(OrderRepository orderRepository,
                       ProductRepository productRepository,
                       ProductStockRepository productStockRepository,
                       InventoryService inventoryService,
                       UserRepository userRepository,
                       SellerRepository sellerRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productStockRepository = productStockRepository;
        this.inventoryService = inventoryService;
        this.userRepository = userRepository;
        this.sellerRepository = sellerRepository;
    }

    @Transactional
    public OrderResponse createOrder(Long userId, OrderCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + userId));

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Sipariş kalemleri boş olamaz");
        }

        // Stok kontrolü
        for (OrderCreateRequest.OrderItemRequest itemRequest : request.getItems()) {
            ProductStock stock = productStockRepository.findByProductId(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ürün stoğu bulunamadı: " + itemRequest.getProductId()));

            if (stock.getQuantity() < itemRequest.getQuantity()) {
                Product product = productRepository.findById(itemRequest.getProductId()).orElseThrow();
                throw new BadRequestException(
                        String.format("Yetersiz stok! Ürün: %s, Mevcut: %d, İstenen: %d",
                                product.getName(), stock.getQuantity(), itemRequest.getQuantity()));
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setNotes(request.getNotes());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderCreateRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı: " + itemRequest.getProductId()));

            if (!product.getActive()) {
                throw new BadRequestException("Ürün aktif değil: " + product.getName());
            }

            if (product.getProductPrice() == null) {
                throw new BadRequestException("Ürün fiyatı bulunamadı: " + product.getName());
            }

            BigDecimal unitPrice = product.getProductPrice().getEffectivePrice();

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setSeller(product.getSeller());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(unitPrice);
            orderItem.setSubtotal(unitPrice.multiply(new BigDecimal(itemRequest.getQuantity())));

            if (product.getSeller() != null) {
                orderItem.setCommissionRate(product.getSeller().getCommissionRate());
                orderItem.calculateCommission();
            }

            order.addItem(orderItem);
            orderItems.add(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // Stok azaltma
        for (OrderCreateRequest.OrderItemRequest itemRequest : request.getItems()) {
            StockMovementRequest stockMovementRequest = new StockMovementRequest();
            stockMovementRequest.setProductId(itemRequest.getProductId());
            stockMovementRequest.setQuantity(itemRequest.getQuantity());
            stockMovementRequest.setMovementType(StockMovement.MovementType.OUT);
            stockMovementRequest.setReason("Sipariş: " + savedOrder.getOrderNumber());

            inventoryService.addStockMovement(stockMovementRequest);
        }

        // TODO: Seller metrics güncellemesi sipariş tamamlandığında yapılacak

        return mapToOrderResponse(savedOrder);
    }

    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sipariş bulunamadı: " + orderId));
        return mapToOrderResponse(order);
    }

    public List<OrderResponse> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sipariş bulunamadı: " + orderId));

        order.setOrderStatus(newStatus);

        if (newStatus == OrderStatus.SHIPPED) {
            order.setShippedAt(LocalDateTime.now());
        } else if (newStatus == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
            // TODO: Sipariş teslim edildiğinde seller metrics güncelleme
            updateSellerMetrics(order);
        }

        Order updated = orderRepository.save(order);
        return mapToOrderResponse(updated);
    }

    @Transactional
    public OrderResponse updatePaymentStatus(Long orderId, PaymentStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sipariş bulunamadı: " + orderId));

        order.setPaymentStatus(newStatus);

        if (newStatus == PaymentStatus.PAID) {
            order.setPaidAt(LocalDateTime.now());
        }

        Order updated = orderRepository.save(order);
        return mapToOrderResponse(updated);
    }

    private void updateSellerMetrics(Order order) {
        for (OrderItem item : order.getItems()) {
            if (item.getSeller() != null) {
                Seller seller = sellerRepository.findById(item.getSeller().getId()).orElse(null);
                if (seller != null) {
                    seller.setCompletedOrders(seller.getCompletedOrders() + 1);
                    seller.setTotalRevenue(seller.getTotalRevenue().add(item.getSellerAmount()));
                    sellerRepository.save(seller);
                }
            }
        }
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setTotalAmount(order.getTotalAmount());
        response.setShippingFee(order.getShippingFee());
        response.setShippingAddress(order.getShippingAddress());
        response.setOrderStatus(order.getOrderStatus().name());
        response.setPaymentStatus(order.getPaymentStatus().name());
        response.setNotes(order.getNotes());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setPaidAt(order.getPaidAt());
        response.setShippedAt(order.getShippedAt());
        response.setDeliveredAt(order.getDeliveredAt());

        List<OrderResponse.OrderItemResponse> itemResponses = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            OrderResponse.OrderItemResponse itemResponse = new OrderResponse.OrderItemResponse();
            itemResponse.setId(item.getId());
            itemResponse.setProductId(item.getProduct().getId());
            itemResponse.setProductName(item.getProduct().getName());

            if (item.getSeller() != null) {
                itemResponse.setSellerId(item.getSeller().getId());
                itemResponse.setSellerName(item.getSeller().getCompanyName());
            }

            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setUnitPrice(item.getUnitPrice());
            itemResponse.setSubtotal(item.getSubtotal());
            itemResponse.setCommissionRate(item.getCommissionRate());
            itemResponse.setCommissionAmount(item.getCommissionAmount());
            itemResponse.setSellerAmount(item.getSellerAmount());

            itemResponses.add(itemResponse);
        }

        response.setItems(itemResponses);
        return response;
    }
}
