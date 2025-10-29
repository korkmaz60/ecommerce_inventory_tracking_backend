package com.ecommerce.ecommerce.seller.service;

import com.ecommerce.ecommerce.common.exception.DuplicateResourceException;
import com.ecommerce.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.common.exception.ValidationException;
import com.ecommerce.ecommerce.seller.dto.request.SellerApplicationRequest;
import com.ecommerce.ecommerce.seller.dto.request.SellerUpdateRequest;
import com.ecommerce.ecommerce.seller.dto.response.SellerResponse;
import com.ecommerce.ecommerce.seller.entity.Seller;
import com.ecommerce.ecommerce.seller.entity.SellerStatus;
import com.ecommerce.ecommerce.seller.repository.SellerRepository;
import com.ecommerce.ecommerce.user.model.Role;
import com.ecommerce.ecommerce.user.model.User;
import com.ecommerce.ecommerce.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    public SellerService(SellerRepository sellerRepository, UserRepository userRepository) {
        this.sellerRepository = sellerRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SellerResponse applyForSeller(Long userId, SellerApplicationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı", "id", userId));

        if (sellerRepository.findByUserId(userId).isPresent()) {
            throw new DuplicateResourceException("Satıcı başvurusu", "userId", userId);
        }

        if (sellerRepository.existsByTaxNumber(request.getTaxNumber())) {
            throw new DuplicateResourceException("Satıcı", "vergi numarası", request.getTaxNumber());
        }

        if (sellerRepository.existsByIban(request.getIban())) {
            throw new DuplicateResourceException("Satıcı", "IBAN", request.getIban());
        }

        Seller seller = new Seller();
        seller.setUser(user);
        seller.setCompanyName(request.getCompanyName());
        seller.setTaxNumber(request.getTaxNumber());
        seller.setAddress(request.getAddress());
        seller.setIban(request.getIban());
        seller.setDescription(request.getDescription());
        seller.setStatus(SellerStatus.PENDING);

        Seller saved = sellerRepository.save(seller);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public SellerResponse getSellerById(Long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Satıcı", "id", id));
        return mapToResponse(seller);
    }

    @Transactional(readOnly = true)
    public SellerResponse getSellerByUserId(Long userId) {
        Seller seller = sellerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Satıcı", "userId", userId));
        return mapToResponse(seller);
    }

    @Transactional(readOnly = true)
    public List<SellerResponse> getAllSellers() {
        return sellerRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SellerResponse> getSellersByStatus(SellerStatus status) {
        return sellerRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SellerResponse updateSeller(Long id, SellerUpdateRequest request) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Satıcı", "id", id));

        if (request.getCompanyName() != null) {
            seller.setCompanyName(request.getCompanyName());
        }

        if (request.getAddress() != null) {
            seller.setAddress(request.getAddress());
        }

        if (request.getIban() != null) {
            if (!seller.getIban().equals(request.getIban()) &&
                sellerRepository.existsByIban(request.getIban())) {
                throw new DuplicateResourceException("Satıcı", "IBAN", request.getIban());
            }
            seller.setIban(request.getIban());
        }

        if (request.getDescription() != null) {
            seller.setDescription(request.getDescription());
        }

        if (request.getLogoUrl() != null) {
            seller.setLogoUrl(request.getLogoUrl());
        }

        Seller updated = sellerRepository.save(seller);
        return mapToResponse(updated);
    }

    @Transactional
    public SellerResponse approveSeller(Long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Satıcı", "id", id));

        if (seller.getStatus() != SellerStatus.PENDING) {
            throw new ValidationException("Sadece PENDING durumundaki başvurular onaylanabilir");
        }

        seller.setStatus(SellerStatus.APPROVED);
        seller.setApprovedAt(LocalDateTime.now());

        User user = seller.getUser();
        user.setRole(Role.SELLER);
        userRepository.save(user);

        Seller updated = sellerRepository.save(seller);
        return mapToResponse(updated);
    }

    @Transactional
    public SellerResponse rejectSeller(Long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Satıcı", "id", id));

        if (seller.getStatus() != SellerStatus.PENDING) {
            throw new ValidationException("Sadece PENDING durumundaki başvurular reddedilebilir");
        }

        seller.setStatus(SellerStatus.REJECTED);
        Seller updated = sellerRepository.save(seller);
        return mapToResponse(updated);
    }

    @Transactional
    public SellerResponse suspendSeller(Long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Satıcı", "id", id));

        seller.setStatus(SellerStatus.SUSPENDED);
        seller.setActive(false);
        Seller updated = sellerRepository.save(seller);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteSeller(Long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Satıcı", "id", id));
        seller.setActive(false);
        sellerRepository.save(seller);
    }

    private SellerResponse mapToResponse(Seller seller) {
        SellerResponse response = new SellerResponse();
        response.setId(seller.getId());
        response.setUserId(seller.getUser().getId());
        response.setCompanyName(seller.getCompanyName());
        response.setTaxNumber(seller.getTaxNumber());
        response.setAddress(seller.getAddress());
        response.setIban(seller.getIban());
        response.setCommissionRate(seller.getCommissionRate());
        response.setStatus(seller.getStatus());
        response.setDescription(seller.getDescription());
        response.setLogoUrl(seller.getLogoUrl());
        response.setActive(seller.getActive());
        response.setCreatedAt(seller.getCreatedAt());
        response.setApprovedAt(seller.getApprovedAt());
        return response;
    }
}
