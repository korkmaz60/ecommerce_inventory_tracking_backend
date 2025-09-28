package com.ecommerce.ecommerce.shipping.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShippingTrackingResponse {
    private String trackingNumber;
    private String status;
    private String currentLocation;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private List<TrackingEvent> trackingHistory;
    
    @Data
    public static class TrackingEvent {
        private String status;
        private String description;
        private String location;
        private LocalDateTime timestamp;
        private String eventCode;
    }
}