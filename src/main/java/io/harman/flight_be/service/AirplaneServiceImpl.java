package io.harman.flight_be.service;

import io.harman.flight_be.dto.airplane.CreateAirplaneDto;
import io.harman.flight_be.dto.airplane.ReadAirplaneDto;
import io.harman.flight_be.dto.airplane.UpdateAirplaneDto;
import io.harman.flight_be.model.flight.Airplane;
import io.harman.flight_be.model.flight.Flight;
import io.harman.flight_be.repository.flight.AirlineRepository;
import io.harman.flight_be.repository.flight.AirplaneRepository;
import io.harman.flight_be.repository.flight.FlightRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class AirplaneServiceImpl implements AirplaneService {

    private final AirplaneRepository airplaneRepository;
    private final AirlineRepository airlineRepository;
    private final FlightRepository flightRepository;

    public AirplaneServiceImpl(AirplaneRepository airplaneRepository,
            AirlineRepository airlineRepository,
            FlightRepository flightRepository) {
        this.airplaneRepository = airplaneRepository;
        this.airlineRepository = airlineRepository;
        this.flightRepository = flightRepository;
    }

    @Override
    public List<ReadAirplaneDto> getAllAirplanes() {
        return airplaneRepository.findAll().stream()
                .sorted((a, b) -> {
                    // Sort by model name first (case-insensitive)
                    int modelCompare = a.getModel().compareToIgnoreCase(b.getModel());
                    // If models are equal, sort by ID for stable ordering
                    return modelCompare != 0 ? modelCompare : a.getId().compareTo(b.getId());
                })
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadAirplaneDto> getAllActiveAirplanes() {
        return airplaneRepository.findByIsDeletedFalse().stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReadAirplaneDto getAirplaneById(String id) {
        Airplane airplane = airplaneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Airplane with ID " + id + " not found"));
        return mapToReadDto(airplane);
    }

    @Override
    public ReadAirplaneDto createAirplane(CreateAirplaneDto createAirplaneDto) {
        // Validate airline exists
        airlineRepository.findById(createAirplaneDto.getAirlineId())
                .orElseThrow(() -> new RuntimeException(
                        "Airline with ID " + createAirplaneDto.getAirlineId() + " not found"));

        // Generate airplane ID
        String airplaneId = generateAirplaneId(createAirplaneDto.getAirlineId());

        Airplane airplane = Airplane.builder()
                .id(airplaneId)
                .airlineId(createAirplaneDto.getAirlineId())
                .model(createAirplaneDto.getModel())
                .seatCapacity(createAirplaneDto.getSeatCapacity())
                .manufactureYear(createAirplaneDto.getManufactureYear())
                .isDeleted(false)
                .build();

        Airplane savedAirplane = airplaneRepository.save(airplane);
        return mapToReadDto(savedAirplane);
    }

    @Override
    public ReadAirplaneDto updateAirplane(UpdateAirplaneDto updateAirplaneDto) {
        Airplane airplane = airplaneRepository.findById(updateAirplaneDto.getId())
                .orElseThrow(
                        () -> new RuntimeException("Airplane with ID " + updateAirplaneDto.getId() + " not found"));

        // Check if airplane is active
        if (Boolean.TRUE.equals(airplane.getIsDeleted())) {
            throw new RuntimeException("Cannot update inactive airplane");
        }

        airplane.setAirlineId(updateAirplaneDto.getAirlineId());
        airplane.setModel(updateAirplaneDto.getModel());
        airplane.setSeatCapacity(updateAirplaneDto.getSeatCapacity());
        airplane.setManufactureYear(updateAirplaneDto.getManufactureYear());

        Airplane updatedAirplane = airplaneRepository.save(airplane);
        return mapToReadDto(updatedAirplane);
    }

    @Override
    public void deleteAirplane(String id) {
        Airplane airplane = airplaneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Airplane with ID " + id + " not found"));

        // Check if airplane can be deleted
        if (!canDeleteAirplane(id)) {
            throw new RuntimeException(
                    "Cannot delete airplane. It is being used in scheduled, in-flight, or delayed flights");
        }

        // Soft delete - mark as deleted
        airplane.setIsDeleted(true);
        airplaneRepository.save(airplane);

        // Cancel all related flights
        List<Flight> relatedFlights = flightRepository.findByAirplaneIdAndIsDeletedFalse(id);
        for (Flight flight : relatedFlights) {
            flight.setIsDeleted(true);
            flight.setStatus(5); // Cancelled
            flightRepository.save(flight);
        }
    }

    @Override
    public void activateAirplane(String id) {
        Airplane airplane = airplaneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Airplane with ID " + id + " not found"));

        airplane.setIsDeleted(false);
        airplaneRepository.save(airplane);
    }

    @Override
    public List<ReadAirplaneDto> getAirplanesByAirlineId(String airlineId) {
        return airplaneRepository.findByAirlineIdAndIsDeletedFalse(airlineId).stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadAirplaneDto> searchAirplanes(String airlineId, String model, Integer manufactureYear,
            Boolean isDeleted) {
        List<Airplane> airplanes = airplaneRepository.findAll();

        return airplanes.stream()
                .filter(a -> airlineId == null || a.getAirlineId().equals(airlineId))
                .filter(a -> model == null || a.getModel().toLowerCase().contains(model.toLowerCase()))
                .filter(a -> manufactureYear == null || a.getManufactureYear().equals(manufactureYear))
                .filter(a -> isDeleted == null || a.getIsDeleted().equals(isDeleted))
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadAirplaneDto> getAirplanesByManufactureYearRange(Integer startYear, Integer endYear) {
        return airplaneRepository.findByManufactureYearBetween(startYear, endYear).stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public String generateAirplaneId(String airlineId) {
        // Format: {AirlineId}-{3 random letters}
        String randomLetters;
        String airplaneId;

        do {
            randomLetters = generateRandomLetters(3);
            airplaneId = airlineId + "-" + randomLetters;
        } while (airplaneRepository.existsById(airplaneId));

        return airplaneId;
    }

    @Override
    public boolean canDeleteAirplane(String airplaneId) {
        // Check if airplane is used in flights with status: Scheduled (1), In Flight
        // (2), or Delayed (4)
        List<Flight> activeFlights = flightRepository.findByAirplaneIdAndIsDeletedFalse(airplaneId);

        return activeFlights.stream()
                .noneMatch(flight -> flight.getStatus() == 1 || flight.getStatus() == 2 || flight.getStatus() == 4);
    }

    private String generateRandomLetters(int length) {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            result.append(letters.charAt(random.nextInt(letters.length())));
        }

        return result.toString();
    }

    private ReadAirplaneDto mapToReadDto(Airplane airplane) {
        String airlineName = null;
        if (airplane.getAirline() != null) {
            airlineName = airplane.getAirline().getName();
        }

        return ReadAirplaneDto.builder()
                .id(airplane.getId())
                .airlineId(airplane.getAirlineId())
                .airlineName(airlineName)
                .model(airplane.getModel())
                .seatCapacity(airplane.getSeatCapacity())
                .manufactureYear(airplane.getManufactureYear())
                .createdAt(airplane.getCreatedAt())
                .updatedAt(airplane.getUpdatedAt())
                .isDeleted(airplane.getIsDeleted())
                .build();
    }
}
