package io.harman.flight_be.controller;

import io.harman.flight_be.dto.classflight.*;
import io.harman.flight_be.dto.rest.BaseResponseDTO;
import io.harman.flight_be.service.ClassFlightService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/class-flights")
public class ClassFlightRestController {

    private final ClassFlightService classFlightService;

    public ClassFlightRestController(ClassFlightService classFlightService) {
        this.classFlightService = classFlightService;
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponseDTO<List<ReadClassFlightDto>>> getAllClassFlights(
            @RequestParam(required = false) String flightId) {
        
        var baseResponseDTO = new BaseResponseDTO<List<ReadClassFlightDto>>();

        try {
            List<ReadClassFlightDto> classFlights;
            
            if (flightId != null) {
                classFlights = classFlightService.getClassFlightsByFlightId(flightId);
            } else {
                classFlights = classFlightService.getAllClassFlights();
            }

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(classFlights);
            baseResponseDTO.setMessage("Class flights retrieved successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve class flights: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<ReadClassFlightDto>> getClassFlightById(@PathVariable Integer id) {
        var baseResponseDTO = new BaseResponseDTO<ReadClassFlightDto>();

        try {
            ReadClassFlightDto classFlight = classFlightService.getClassFlightById(id);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(classFlight);
            baseResponseDTO.setMessage("Class flight retrieved successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (RuntimeException e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve class flight: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponseDTO<ReadClassFlightDto>> createClassFlight(
            @Valid @RequestBody CreateClassFlightDto createClassFlightDto,
            BindingResult bindingResult) {

        var baseResponseDTO = new BaseResponseDTO<ReadClassFlightDto>();

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
            ReadClassFlightDto classFlight = classFlightService.createClassFlight(createClassFlightDto);

            baseResponseDTO.setStatus(HttpStatus.CREATED.value());
            baseResponseDTO.setData(classFlight);
            baseResponseDTO.setMessage("Class flight created successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to create class flight: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<BaseResponseDTO<ReadClassFlightDto>> updateClassFlight(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateClassFlightDto updateClassFlightDto,
            BindingResult bindingResult) {

        var baseResponseDTO = new BaseResponseDTO<ReadClassFlightDto>();

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
            updateClassFlightDto.setId(id);
            ReadClassFlightDto classFlight = classFlightService.updateClassFlight(updateClassFlightDto);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(classFlight);
            baseResponseDTO.setMessage("Class flight updated successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (RuntimeException e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to update class flight: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<BaseResponseDTO<?>> deleteClassFlight(@PathVariable Integer id) {
        var baseResponseDTO = new BaseResponseDTO<>();

        try {
            classFlightService.deleteClassFlight(id);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Class flight deleted successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (RuntimeException e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to delete class flight: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/available")
    public ResponseEntity<BaseResponseDTO<List<ReadClassFlightDto>>> getAvailableClassFlights(
            @RequestParam String flightId) {
        
        var baseResponseDTO = new BaseResponseDTO<List<ReadClassFlightDto>>();

        try {
            List<ReadClassFlightDto> classFlights = classFlightService.getClassFlightsByFlightId(flightId)
                .stream()
                .filter(cf -> cf.getAvailableSeats() != null && cf.getAvailableSeats() > 0)
                .toList();

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(classFlights);
            baseResponseDTO.setMessage("Available class flights retrieved successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve available class flights: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
