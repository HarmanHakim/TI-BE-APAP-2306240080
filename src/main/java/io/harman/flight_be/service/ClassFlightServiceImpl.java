package io.harman.flight_be.service;

import io.harman.flight_be.dto.classflight.CreateClassFlightDto;
import io.harman.flight_be.dto.classflight.ReadClassFlightDto;
import io.harman.flight_be.dto.classflight.UpdateClassFlightDto;
import io.harman.flight_be.model.ClassFlight;
import io.harman.flight_be.repository.ClassFlightRepository;
import io.harman.flight_be.repository.FlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClassFlightServiceImpl implements ClassFlightService {

    private final ClassFlightRepository classFlightRepository;
    private final FlightRepository flightRepository;

    public ClassFlightServiceImpl(ClassFlightRepository classFlightRepository,
                                  FlightRepository flightRepository) {
        this.classFlightRepository = classFlightRepository;
        this.flightRepository = flightRepository;
    }

    @Override
    public List<ReadClassFlightDto> getAllClassFlights() {
        return classFlightRepository.findAll().stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReadClassFlightDto getClassFlightById(Integer id) {
        ClassFlight classFlight = classFlightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class Flight with ID " + id + " not found"));
        return mapToReadDto(classFlight);
    }

    @Override
    public ReadClassFlightDto createClassFlight(CreateClassFlightDto createClassFlightDto) {
        // Validate flight exists
        flightRepository.findById(createClassFlightDto.getFlightId())
                .orElseThrow(() -> new RuntimeException("Flight with ID " + createClassFlightDto.getFlightId() + " not found"));

        // Check if class type already exists for this flight
        if (classFlightRepository.existsByFlightIdAndClassType(createClassFlightDto.getFlightId(), createClassFlightDto.getClassType())) {
            throw new RuntimeException("Class type " + createClassFlightDto.getClassType() + " already exists for flight " + createClassFlightDto.getFlightId());
        }

        ClassFlight classFlight = ClassFlight.builder()
                .flightId(createClassFlightDto.getFlightId())
                .classType(createClassFlightDto.getClassType())
                .seatCapacity(createClassFlightDto.getSeatCapacity())
                .availableSeats(createClassFlightDto.getSeatCapacity()) // Initially all seats available
                .price(createClassFlightDto.getPrice())
                .build();

        ClassFlight savedClassFlight = classFlightRepository.save(classFlight);
        return mapToReadDto(savedClassFlight);
    }

    @Override
    public ReadClassFlightDto updateClassFlight(UpdateClassFlightDto updateClassFlightDto) {
        ClassFlight classFlight = classFlightRepository.findById(updateClassFlightDto.getId())
                .orElseThrow(() -> new RuntimeException("Class Flight with ID " + updateClassFlightDto.getId() + " not found"));

        classFlight.setFlightId(updateClassFlightDto.getFlightId());
        classFlight.setClassType(updateClassFlightDto.getClassType());
        classFlight.setSeatCapacity(updateClassFlightDto.getSeatCapacity());
        classFlight.setAvailableSeats(updateClassFlightDto.getAvailableSeats());
        classFlight.setPrice(updateClassFlightDto.getPrice());

        ClassFlight updatedClassFlight = classFlightRepository.save(classFlight);
        return mapToReadDto(updatedClassFlight);
    }

    @Override
    public void deleteClassFlight(Integer id) {
        ClassFlight classFlight = classFlightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class Flight with ID " + id + " not found"));

        classFlightRepository.delete(classFlight);
    }

    @Override
    public List<ReadClassFlightDto> getClassFlightsByFlightId(String flightId) {
        return classFlightRepository.findByFlightId(flightId).stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadClassFlightDto> getClassFlightsWithAvailableSeats() {
        return classFlightRepository.findAllWithAvailableSeats().stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReadClassFlightDto getClassFlightByFlightIdAndType(String flightId, String classType) {
        ClassFlight classFlight = classFlightRepository.findByFlightIdAndClassType(flightId, classType)
                .orElseThrow(() -> new RuntimeException("Class Flight not found for flight " + flightId + " and class type " + classType));
        return mapToReadDto(classFlight);
    }

    @Override
    public void decreaseAvailableSeats(Integer classFlightId, Integer count) {
        int updated = classFlightRepository.decreaseAvailableSeats(classFlightId, count);
        if (updated == 0) {
            throw new RuntimeException("Cannot decrease seats. Not enough available seats or class flight not found.");
        }
    }

    @Override
    public void increaseAvailableSeats(Integer classFlightId, Integer count) {
        int updated = classFlightRepository.increaseAvailableSeats(classFlightId, count);
        if (updated == 0) {
            throw new RuntimeException("Cannot increase seats. Class flight not found.");
        }
    }

    @Override
    public Integer getTotalAvailableSeatsByFlight(String flightId) {
        Integer total = classFlightRepository.getTotalAvailableSeatsByFlight(flightId);
        return total != null ? total : 0;
    }

    private ReadClassFlightDto mapToReadDto(ClassFlight classFlight) {
        String flightNumber = classFlight.getFlightId();
        if (classFlight.getFlight() != null) {
            flightNumber = classFlight.getFlight().getId();
        }

        return ReadClassFlightDto.builder()
                .id(classFlight.getId())
                .flightId(classFlight.getFlightId())
                .flightNumber(flightNumber)
                .classType(classFlight.getClassType())
                .seatCapacity(classFlight.getSeatCapacity())
                .availableSeats(classFlight.getAvailableSeats())
                .price(classFlight.getPrice())
                .build();
    }
}
