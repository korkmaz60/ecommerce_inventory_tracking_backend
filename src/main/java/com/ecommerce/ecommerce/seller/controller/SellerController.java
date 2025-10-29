package com.ecommerce.ecommerce.seller.controller;

import com.ecommerce.ecommerce.seller.dto.request.SellerApplicationRequest;
import com.ecommerce.ecommerce.seller.dto.request.SellerUpdateRequest;
import com.ecommerce.ecommerce.seller.dto.response.SellerResponse;
import com.ecommerce.ecommerce.seller.entity.SellerStatus;
import com.ecommerce.ecommerce.seller.service.SellerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sellers")
@Tag(name = "Seller Management")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @PostMapping("/apply")
    public ResponseEntity<SellerResponse> applyForSeller(
            @RequestParam Long userId,
            @RequestBody SellerApplicationRequest request) {
        SellerResponse response = sellerService.applyForSeller(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellerResponse> getSellerById(@PathVariable Long id) {
        SellerResponse response = sellerService.getSellerById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<SellerResponse> getSellerByUserId(@PathVariable Long userId) {
        SellerResponse response = sellerService.getSellerByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SellerResponse>> getAllSellers() {
        List<SellerResponse> sellers = sellerService.getAllSellers();
        return ResponseEntity.ok(sellers);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SellerResponse>> getSellersByStatus(@PathVariable SellerStatus status) {
        List<SellerResponse> sellers = sellerService.getSellersByStatus(status);
        return ResponseEntity.ok(sellers);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<SellerResponse> updateSeller(
            @PathVariable Long id,
            @RequestBody SellerUpdateRequest request) {
        SellerResponse response = sellerService.updateSeller(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SellerResponse> approveSeller(@PathVariable Long id) {
        SellerResponse response = sellerService.approveSeller(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SellerResponse> rejectSeller(@PathVariable Long id) {
        SellerResponse response = sellerService.rejectSeller(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SellerResponse> suspendSeller(@PathVariable Long id) {
        SellerResponse response = sellerService.suspendSeller(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) {
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }
}
