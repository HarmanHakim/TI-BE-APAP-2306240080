package io.harman.flight_be.controller;

import io.harman.flight_be.dto.airline.*;
import io.harman.flight_be.dto.rest.BaseResponseDTO;
import io.harman.flight_be.service.AirlineService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/airlines")
public class AirlineRestController {

    private final AirlineService airlineService;

    public AirlineRestController(AirlineService airlineService) {
        this.airlineService = airlineService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('Flight Airline', 'Superadmin', 'Customer')")
    public ResponseEntity<BaseResponseDTO<List<ReadAirlineDto>>> getAllAirlines(
            @RequestParam(required = false) String country) {

        var baseResponseDTO = new BaseResponseDTO<List<ReadAirlineDto>>();

        try {
            List<ReadAirlineDto> airlines;

            if (country != null) {
                airlines = airlineService.getAirlinesByCountry(country);
            } else {
                airlines = airlineService.getAllAirlines();
            }

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(airlines);
            baseResponseDTO.setMessage("Airlines retrieved successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve airlines: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('Flight Airline', 'Superadmin', 'Customer')")
    public ResponseEntity<BaseResponseDTO<ReadAirlineDto>> getAirlineById(@PathVariable String id) {
        var baseResponseDTO = new BaseResponseDTO<ReadAirlineDto>();

        try {
            ReadAirlineDto airline = airlineService.getAirlineById(id);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(airline);
            baseResponseDTO.setMessage("Airline retrieved successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (RuntimeException e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve airline: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('Flight Airline', 'Superadmin')")
    public ResponseEntity<BaseResponseDTO<ReadAirlineDto>> createAirline(
            @Valid @RequestBody CreateAirlineDto createAirlineDto,
            BindingResult bindingResult) {

        var baseResponseDTO = new BaseResponseDTO<ReadAirlineDto>();

        if (bindingResult.hasFieldErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();

            for (FieldError error : errors) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }

            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages.toString());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            ReadAirlineDto airline = airlineService.createAirline(createAirlineDto);

            baseResponseDTO.setStatus(HttpStatus.CREATED.value());
            baseResponseDTO.setData(airline);
            baseResponseDTO.setMessage("Airline created successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to create airline: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasAnyAuthority('Flight Airline', 'Superadmin')")
    public ResponseEntity<BaseResponseDTO<ReadAirlineDto>> updateAirline(
            @PathVariable String id,
            @Valid @RequestBody UpdateAirlineDto updateAirlineDto,
            BindingResult bindingResult) {

        var baseResponseDTO = new BaseResponseDTO<ReadAirlineDto>();

        if (bindingResult.hasFieldErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();

            for (FieldError error : errors) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }

            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages.toString());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            updateAirlineDto.setId(id);
            ReadAirlineDto airline = airlineService.updateAirline(updateAirlineDto);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(airline);
            baseResponseDTO.setMessage("Airline updated successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (RuntimeException e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to update airline: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAnyAuthority('Flight Airline', 'Superadmin')")
    public ResponseEntity<BaseResponseDTO<?>> deleteAirline(@PathVariable String id) {
        var baseResponseDTO = new BaseResponseDTO<>();

        try {
            airlineService.deleteAirline(id);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Airline with id " + id + " deleted successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (RuntimeException e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to delete airline with id " + id + ": " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('Flight Airline', 'Superadmin', 'Customer')")
    public ResponseEntity<BaseResponseDTO<List<ReadAirlineDto>>> searchAirlines(
            @RequestParam String name) {

        var baseResponseDTO = new BaseResponseDTO<List<ReadAirlineDto>>();

        try {
            List<ReadAirlineDto> airlines = airlineService.searchAirlinesByName(name);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(airlines);
            baseResponseDTO.setMessage("Airlines search results retrieved successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to search airlines: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/countries")
    @PreAuthorize("hasAnyAuthority('Flight Airline', 'Superadmin', 'Customer')")
    public ResponseEntity<BaseResponseDTO<List<String>>> getDistinctCountries() {
        var baseResponseDTO = new BaseResponseDTO<List<String>>();

        try {
            List<String> countries = airlineService.getDistinctCountries();

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(countries);
            baseResponseDTO.setMessage("Countries retrieved successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve countries: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
