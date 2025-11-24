package io.harman.flight_be.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.harman.flight_be.dto.seat.CreateSeatDto;
import io.harman.flight_be.dto.seat.ReadSeatDto;
import io.harman.flight_be.dto.seat.UpdateSeatDto;
import io.harman.flight_be.model.flight.Seat;
import io.harman.flight_be.repository.flight.ClassFlightRepository;
import io.harman.flight_be.repository.flight.PassengerRepository;
import io.harman.flight_be.repository.flight.SeatRepository;

@Service
@Transactional
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final ClassFlightRepository classFlightRepository;
    private final PassengerRepository passengerRepository;

    public SeatServiceImpl(SeatRepository seatRepository,
                          ClassFlightRepository classFlightRepository,
                          PassengerRepository passengerRepository) {
        this.seatRepository = seatRepository;
        this.classFlightRepository = classFlightRepository;
        this.passengerRepository = passengerRepository;
    }

    @Override
    public List<ReadSeatDto> getAllSeats() {
        return seatRepository.findAll().stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReadSeatDto getSeatById(Long id) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seat with ID " + id + " not found"));
        return mapToReadDto(seat);
    }

    @Override
    public ReadSeatDto createSeat(CreateSeatDto createSeatDto) {
        // Validate class flight exists
        classFlightRepository.findById(createSeatDto.getClassFlightId())
                .orElseThrow(() -> new RuntimeException("Class Flight with ID " + createSeatDto.getClassFlightId() + " not found"));

        // Check if seat number already exists for this class flight
        if (seatRepository.existsByClassFlightIdAndSeatNumber(createSeatDto.getClassFlightId(), createSeatDto.getSeatNumber())) {
            throw new RuntimeException("Seat number " + createSeatDto.getSeatNumber() + " already exists for this class flight");
        }

        Seat seat = Seat.builder()
                .classFlightId(createSeatDto.getClassFlightId())
                .seatNumber(createSeatDto.getSeatNumber())
                .isAvailable(createSeatDto.getPassengerId() == null)
                .passengerId(createSeatDto.getPassengerId())
                .build();

        Seat savedSeat = seatRepository.save(seat);
        return mapToReadDto(savedSeat);
    }

    @Override
    public ReadSeatDto updateSeat(UpdateSeatDto updateSeatDto) {
        Seat seat = seatRepository.findById(updateSeatDto.getId())
                .orElseThrow(() -> new RuntimeException("Seat with ID " + updateSeatDto.getId() + " not found"));

        seat.setClassFlightId(updateSeatDto.getClassFlightId());
        seat.setSeatNumber(updateSeatDto.getSeatNumber());
        seat.setIsAvailable(updateSeatDto.getIsAvailable());
        seat.setPassengerId(updateSeatDto.getPassengerId());

        Seat updatedSeat = seatRepository.save(seat);
        return mapToReadDto(updatedSeat);
    }

    @Override
    public void deleteSeat(Long id) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seat with ID " + id + " not found"));

        seatRepository.delete(seat);
    }

    @Override
    public List<ReadSeatDto> getSeatsByClassFlightId(Integer classFlightId) {
        return seatRepository.findByClassFlightIdOrderBySeatNumberAsc(classFlightId).stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadSeatDto> getAvailableSeatsByClassFlightId(Integer classFlightId) {
        return seatRepository.findByClassFlightIdAndIsAvailableTrueOrderBySeatNumberAsc(classFlightId).stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadSeatDto> getSeatsByFlightId(String flightId) {
        return seatRepository.findByFlightId(flightId).stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadSeatDto> getAvailableSeatsByFlightId(String flightId) {
        return seatRepository.findAvailableSeatsByFlightId(flightId).stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReadSeatDto assignSeatToPassenger(Long seatId, UUID passengerId) {
        // Validate passenger exists
        passengerRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException("Passenger with ID " + passengerId + " not found"));

        // Validate seat exists and is available
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat with ID " + seatId + " not found"));
        
        if (!seat.getIsAvailable()) {
            throw new RuntimeException("Seat is already assigned to another passenger");
        }

        int updated = seatRepository.assignSeatToPassenger(seatId, passengerId);
        if (updated == 0) {
            throw new RuntimeException("Cannot assign seat. Seat is not available or not found.");
        }

        return getSeatById(seatId);
    }

    @Override
    @Transactional
    public ReadSeatDto assignSeatToPassenger(Long seatId, UUID passengerId, Integer classFlightId) {
        // Validate passenger exists
        passengerRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException("Passenger with ID " + passengerId + " not found"));

        // Validate seat exists and is available
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat with ID " + seatId + " not found"));
        
        // Validate seat belongs to the specified class flight
        if (!seat.getClassFlightId().equals(classFlightId)) {
            throw new RuntimeException("Seat does not belong to the specified class flight");
        }
        
        if (!seat.getIsAvailable()) {
            throw new RuntimeException("Seat is already assigned to another passenger");
        }

        int updated = seatRepository.assignSeatToPassenger(seatId, passengerId);
        if (updated == 0) {
            throw new RuntimeException("Cannot assign seat. Seat is not available or not found.");
        }

        return getSeatById(seatId);
    }

    @Override
    public ReadSeatDto releaseSeat(Long seatId) {
        int updated = seatRepository.releaseSeat(seatId);
        if (updated == 0) {
            throw new RuntimeException("Cannot release seat. Seat not found.");
        }

        return getSeatById(seatId);
    }

    @Override
    public void releaseSeatsByPassenger(UUID passengerId) {
        seatRepository.releaseSeatsByPassenger(passengerId);
    }

    @Override
    public boolean isSeatAvailable(Long seatId) {
        Boolean isAvailable = seatRepository.isSeatAvailable(seatId);
        return Boolean.TRUE.equals(isAvailable);
    }

    @Override
    public String generateSeatCode(String flightId, String classType, Long seatId) {
        // Format: {idFlight}-{prefix classType 2 huruf}{idSeat}
        String classPrefix = "";
        if (classType != null && classType.length() >= 2) {
            classPrefix = classType.substring(0, 2).toUpperCase();
        }
        
        return String.format("%s-%s%03d", flightId, classPrefix, seatId);
    }

    private ReadSeatDto mapToReadDto(Seat seat) {
        String classType = null;
        String flightId = null;
        String passengerName = null;

        if (seat.getClassFlight() != null) {
            classType = seat.getClassFlight().getClassType();
            if (seat.getClassFlight().getFlight() != null) {
                flightId = seat.getClassFlight().getFlight().getId();
            }
        }

        if (seat.getPassenger() != null) {
            passengerName = seat.getPassenger().getFullName();
        }

        return ReadSeatDto.builder()
                .id(seat.getId())
                .classFlightId(seat.getClassFlightId())
                .classType(classType)
                .flightId(flightId)
                .seatNumber(seat.getSeatNumber())
                .isAvailable(seat.getIsAvailable())
                .passengerId(seat.getPassengerId())
                .passengerName(passengerName)
                .build();
    }
}
