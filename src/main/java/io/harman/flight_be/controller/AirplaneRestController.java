package io.harman.flight_be.controller;

import java.util.Date;
import java.util.List;

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

import io.harman.flight_be.dto.airplane.CreateAirplaneDto;
import io.harman.flight_be.dto.airplane.ReadAirplaneDto;
import io.harman.flight_be.dto.airplane.UpdateAirplaneDto;
import io.harman.flight_be.dto.rest.BaseResponseDTO;
import io.harman.flight_be.service.AirplaneService;
import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/airplanes")
public class AirplaneRestController {

    private final AirplaneService airplaneService;

    public AirplaneRestController(AirplaneService airplaneService) {
        this.airplaneService = airplaneService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('FLIGHT_AIRLINE', 'SUPERADMIN', 'CUSTOMER')")
    public ResponseEntity<BaseResponseDTO<List<ReadAirplaneDto>>> getAllAirplanes(
            @RequestParam(required = false) String airlineId,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer manufactureYear,
            @RequestParam(required = false) Boolean isDeleted) {

        var baseResponseDTO = new BaseResponseDTO<List<ReadAirplaneDto>>();

        try {
            List<ReadAirplaneDto> airplanes;

            if (airlineId != null || model != null || manufactureYear != null || isDeleted != null) {
                airplanes = airplaneService.searchAirplanes(airlineId, model, manufactureYear, isDeleted);
            } else {
                airplanes = airplaneService.getAllAirplanes();
            }

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(airplanes);
            baseResponseDTO.setMessage("Airplanes retrieved successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve airplanes: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('FLIGHT_AIRLINE', 'SUPERADMIN', 'CUSTOMER')")
    public ResponseEntity<BaseResponseDTO<ReadAirplaneDto>> getAirplaneById(@PathVariable String id) {
        var baseResponseDTO = new BaseResponseDTO<ReadAirplaneDto>();

        try {
            ReadAirplaneDto airplane = airplaneService.getAirplaneById(id);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(airplane);
            baseResponseDTO.setMessage("Airplane retrieved successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (RuntimeException e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve airplane: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('FLIGHT_AIRLINE', 'SUPERADMIN')")
    public ResponseEntity<BaseResponseDTO<ReadAirplaneDto>> createAirplane(
            @Valid @RequestBody CreateAirplaneDto createAirplaneDto,
            BindingResult bindingResult) {

        var baseResponseDTO = new BaseResponseDTO<ReadAirplaneDto>();

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
            ReadAirplaneDto airplane = airplaneService.createAirplane(createAirplaneDto);

            baseResponseDTO.setStatus(HttpStatus.CREATED.value());
            baseResponseDTO.setData(airplane);
            baseResponseDTO.setMessage("Airplane created successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to create airplane: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasAnyAuthority('FLIGHT_AIRLINE', 'SUPERADMIN')")
    public ResponseEntity<BaseResponseDTO<ReadAirplaneDto>> updateAirplane(
            @PathVariable String id,
            @Valid @RequestBody UpdateAirplaneDto updateAirplaneDto,
            BindingResult bindingResult) {

        var baseResponseDTO = new BaseResponseDTO<ReadAirplaneDto>();

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
            updateAirplaneDto.setId(id);
            ReadAirplaneDto airplane = airplaneService.updateAirplane(updateAirplaneDto);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(airplane);
            baseResponseDTO.setMessage("Airplane updated successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (RuntimeException e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to update airplane: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAnyAuthority('FLIGHT_AIRLINE', 'SUPERADMIN')")
    public ResponseEntity<BaseResponseDTO<?>> deleteAirplane(@PathVariable String id) {
        var baseResponseDTO = new BaseResponseDTO<>();

        try {
            airplaneService.deleteAirplane(id);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Airplane marked as inactive successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (RuntimeException e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to delete airplane: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAnyAuthority('FLIGHT_AIRLINE', 'SUPERADMIN')")
    public ResponseEntity<BaseResponseDTO<?>> activateAirplane(@PathVariable String id) {
        var baseResponseDTO = new BaseResponseDTO<>();

        try {
            airplaneService.activateAirplane(id);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Airplane activated successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (RuntimeException e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to activate airplane: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
