package io.harman.flight_be.controller;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.harman.flight_be.dto.flight.CreateFlightDto;
import io.harman.flight_be.dto.flight.ReadFlightDto;
import io.harman.flight_be.dto.flight.UpdateFlightDto;
import io.harman.flight_be.dto.rest.BaseResponseDTO;
import io.harman.flight_be.service.FlightService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/flights")
public class FlightRestController {

    private final FlightService flightService;

    public FlightRestController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('FLIGHT_AIRLINE', 'SUPERADMIN', 'CUSTOMER')")
    public ResponseEntity<BaseResponseDTO<List<ReadFlightDto>>> getAllFlights(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDate,
            @RequestParam(required = false) String airlineId,
            @RequestParam(required = false) Integer status) {

        var baseResponseDTO = new BaseResponseDTO<List<ReadFlightDto>>();

        try {
            List<ReadFlightDto> flights;

            if (origin != null || destination != null || departureDate != null || airlineId != null || status != null) {
                flights = flightService.searchFlights(origin, destination, departureDate, airlineId, status);
            } else {
                flights = flightService.getAllFlights();
            }

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(flights);
            baseResponseDTO.setMessage("Flights retrieved successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve flights: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('FLIGHT_AIRLINE', 'SUPERADMIN', 'CUSTOMER')")
    public ResponseEntity<BaseResponseDTO<ReadFlightDto>> getFlightById(@PathVariable String id) {
        var baseResponseDTO = new BaseResponseDTO<ReadFlightDto>();

        try {
            ReadFlightDto flight = flightService.getFlightById(id);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(flight);
            baseResponseDTO.setMessage("Flight retrieved successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (RuntimeException e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve flight: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('FLIGHT_AIRLINE', 'SUPERADMIN')")
    public ResponseEntity<BaseResponseDTO<ReadFlightDto>> createFlight(
            @Valid @RequestBody CreateFlightDto createFlightDto,
            BindingResult bindingResult) {

        var baseResponseDTO = new BaseResponseDTO<ReadFlightDto>();

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
            ReadFlightDto flight = flightService.createFlight(createFlightDto);

            baseResponseDTO.setStatus(HttpStatus.CREATED.value());
            baseResponseDTO.setData(flight);
            baseResponseDTO.setMessage("Flight created successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to create flight: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasAnyAuthority('FLIGHT_AIRLINE', 'SUPERADMIN')")
    public ResponseEntity<BaseResponseDTO<ReadFlightDto>> updateFlight(
            @PathVariable String id,
            @Valid @RequestBody UpdateFlightDto updateFlightDto,
            BindingResult bindingResult) {

        var baseResponseDTO = new BaseResponseDTO<ReadFlightDto>();

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
            updateFlightDto.setId(id);
            ReadFlightDto flight = flightService.updateFlight(updateFlightDto);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(flight);
            baseResponseDTO.setMessage("Flight updated successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (RuntimeException e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to update flight: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAnyAuthority('FLIGHT_AIRLINE', 'SUPERADMIN')")
    public ResponseEntity<BaseResponseDTO<?>> cancelFlight(@PathVariable String id) {
        var baseResponseDTO = new BaseResponseDTO<>();

        try {
            flightService.deleteFlight(id);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Flight cancelled successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (RuntimeException e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to cancel flight: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyAuthority('FLIGHT_AIRLINE', 'SUPERADMIN', 'CUSTOMER')")
    public ResponseEntity<BaseResponseDTO<List<ReadFlightDto>>> getUpcomingFlights() {
        var baseResponseDTO = new BaseResponseDTO<List<ReadFlightDto>>();

        try {
            List<ReadFlightDto> flights = flightService.getUpcomingFlights();

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(flights);
            baseResponseDTO.setMessage("Upcoming flights retrieved successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve upcoming flights: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/today")
    @PreAuthorize("hasAnyAuthority('FLIGHT_AIRLINE', 'SUPERADMIN', 'CUSTOMER')")
    public ResponseEntity<BaseResponseDTO<List<ReadFlightDto>>> getFlightsDepartingToday() {
        var baseResponseDTO = new BaseResponseDTO<List<ReadFlightDto>>();

        try {
            List<ReadFlightDto> flights = flightService.getFlightsDepartingToday();

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(flights);
            baseResponseDTO.setMessage("Today's flights retrieved successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve today's flights: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyAuthority('FLIGHT_AIRLINE', 'SUPERADMIN', 'CUSTOMER')")
    public ResponseEntity<BaseResponseDTO<List<ReadFlightDto>>> getFlightsWithAvailableSeats() {
        var baseResponseDTO = new BaseResponseDTO<List<ReadFlightDto>>();

        try {
            List<ReadFlightDto> flights = flightService.getFlightsWithAvailableSeats();

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(flights);
            baseResponseDTO.setMessage("Flights with available seats retrieved successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve flights: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/reminder")
    @PreAuthorize("hasAnyAuthority('FLIGHT_AIRLINE', 'SUPERADMIN', 'CUSTOMER')")
    public ResponseEntity<BaseResponseDTO<List<io.harman.flight_be.dto.flight.FlightReminderDto>>> getFlightReminders(
            @RequestParam(required = false) Integer interval,
            @RequestParam(required = false) String customerId) {

        var baseResponseDTO = new BaseResponseDTO<List<io.harman.flight_be.dto.flight.FlightReminderDto>>();

        try {
            List<io.harman.flight_be.dto.flight.FlightReminderDto> reminders = flightService
                    .getFlightReminders(interval, customerId);

            if (reminders.isEmpty()) {
                baseResponseDTO.setStatus(HttpStatus.OK.value());
                baseResponseDTO.setData(reminders);
                baseResponseDTO.setMessage("No upcoming flights found.");
                baseResponseDTO.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
            }

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(reminders);
            baseResponseDTO.setMessage("Flight reminders retrieved successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve flight reminders: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
