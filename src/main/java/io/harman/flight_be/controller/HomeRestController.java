package io.harman.flight_be.controller;

import io.harman.flight_be.dto.rest.BaseResponseDTO;
import io.harman.flight_be.service.HomeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
public class HomeRestController {

    private final HomeService homeService;

    public HomeRestController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping
    public ResponseEntity<BaseResponseDTO<Map<String, Object>>> getHomeStatistics() {
        var baseResponseDTO = new BaseResponseDTO<Map<String, Object>>();

        try {
            Map<String, Object> statistics = homeService.getHomeStatistics();

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(statistics);
            baseResponseDTO.setMessage("Home statistics retrieved successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve home statistics: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
