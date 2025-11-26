package io.harman.flight_be.controller;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.harman.flight_be.dto.coupon.CouponResponseDTO;
import io.harman.flight_be.dto.coupon.CreateCouponRequestDTO;
import io.harman.flight_be.dto.coupon.UpdateCouponRequestDTO;
import io.harman.flight_be.dto.rest.BaseResponseDTO;
import io.harman.flight_be.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponRestController {

    private final CouponService couponService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('Customer', 'Superadmin')")
    public ResponseEntity<BaseResponseDTO<List<CouponResponseDTO>>> getCoupons() {
        var baseResponse = new BaseResponseDTO<List<CouponResponseDTO>>();

        List<CouponResponseDTO> coupons = couponService.getCoupons();
        baseResponse.setStatus(HttpStatus.OK.value());
        baseResponse.setData(coupons);
        baseResponse.setMessage("Coupons retrieved successfully");
        baseResponse.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('Customer', 'Superadmin')")
    public ResponseEntity<BaseResponseDTO<CouponResponseDTO>> getCoupon(@PathVariable UUID id) {
        var baseResponse = new BaseResponseDTO<CouponResponseDTO>();
        try {
            CouponResponseDTO coupon = couponService.getCoupon(id);
            baseResponse.setStatus(HttpStatus.OK.value());
            baseResponse.setData(coupon);
            baseResponse.setMessage("Coupon retrieved successfully");
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            baseResponse.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponse.setMessage(e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('Superadmin')")
    public ResponseEntity<BaseResponseDTO<CouponResponseDTO>> createCoupon(
            @Valid @RequestBody CreateCouponRequestDTO request,
            BindingResult bindingResult) {
        var baseResponse = new BaseResponseDTO<CouponResponseDTO>();

        if (bindingResult.hasErrors()) {
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage(buildValidationMessage(bindingResult));
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        CouponResponseDTO coupon = couponService.createCoupon(request);
        baseResponse.setStatus(HttpStatus.CREATED.value());
        baseResponse.setData(coupon);
        baseResponse.setMessage("Coupon created successfully");
        baseResponse.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('Superadmin')")
    public ResponseEntity<BaseResponseDTO<CouponResponseDTO>> updateCoupon(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCouponRequestDTO request,
            BindingResult bindingResult) {
        var baseResponse = new BaseResponseDTO<CouponResponseDTO>();

        if (bindingResult.hasErrors()) {
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage(buildValidationMessage(bindingResult));
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        try {
            CouponResponseDTO coupon = couponService.updateCoupon(id, request);
            baseResponse.setStatus(HttpStatus.OK.value());
            baseResponse.setData(coupon);
            baseResponse.setMessage("Coupon updated successfully");
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            baseResponse.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponse.setMessage(e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('Superadmin')")
    public ResponseEntity<BaseResponseDTO<Void>> deleteCoupon(@PathVariable UUID id) {
        var baseResponse = new BaseResponseDTO<Void>();
        try {
            couponService.deleteCoupon(id);
            baseResponse.setStatus(HttpStatus.NO_CONTENT.value());
            baseResponse.setMessage("Coupon deleted successfully");
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            baseResponse.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponse.setMessage(e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.NOT_FOUND);
        }
    }

    private String buildValidationMessage(BindingResult bindingResult) {
        StringBuilder builder = new StringBuilder();
        for (FieldError error : bindingResult.getFieldErrors()) {
            builder.append(error.getDefaultMessage()).append("; ");
        }
        return builder.toString();
    }
}
