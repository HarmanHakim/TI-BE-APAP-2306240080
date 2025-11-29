package io.harman.flight_be.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.harman.flight_be.dto.flight.CreateFlightDto;
import io.harman.flight_be.dto.flight.ReadFlightDto;
import io.harman.flight_be.dto.flight.UpdateFlightDto;
import io.harman.flight_be.model.flight.Flight;
import io.harman.flight_be.repository.flight.AirlineRepository;
import io.harman.flight_be.repository.flight.AirplaneRepository;
import io.harman.flight_be.repository.flight.BookingRepository;
import io.harman.flight_be.repository.flight.FlightRepository;

@Service
@Transactional
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;
    private final AirplaneRepository airplaneRepository;
    private final BookingRepository bookingRepository;

    public FlightServiceImpl(FlightRepository flightRepository,
            AirlineRepository airlineRepository,
            AirplaneRepository airplaneRepository,
            BookingRepository bookingRepository) {
        this.flightRepository = flightRepository;
        this.airlineRepository = airlineRepository;
        this.airplaneRepository = airplaneRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<ReadFlightDto> getAllFlights() {
        return flightRepository.findAll().stream()
                .sorted((a, b) -> b.getDepartureTime().compareTo(a.getDepartureTime())) // Descending order - most
                                                                                        // recent first
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadFlightDto> getAllActiveFlights() {
        return flightRepository.findByIsDeletedFalse().stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReadFlightDto getFlightById(String id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight with ID " + id + " not found"));
        return mapToReadDto(flight);
    }

    @Override
    public ReadFlightDto createFlight(CreateFlightDto createFlightDto) {
        // Validate airline exists
        airlineRepository.findById(createFlightDto.getAirlineId())
                .orElseThrow(
                        () -> new RuntimeException("Airline with ID " + createFlightDto.getAirlineId() + " not found"));

        // Validate airplane exists and is active
        airplaneRepository.findByIdAndIsDeletedFalse(createFlightDto.getAirplaneId())
                .orElseThrow(() -> new RuntimeException(
                        "Airplane with ID " + createFlightDto.getAirplaneId() + " not found or inactive"));

        // Validate departure time < arrival time
        if (!createFlightDto.getDepartureTime().isBefore(createFlightDto.getArrivalTime())) {
            throw new RuntimeException("Departure time must be before arrival time");
        }

        // Check if airplane is available (not overlapping with other flights)
        if (!isAirplaneAvailable(createFlightDto.getAirplaneId(), createFlightDto.getDepartureTime(),
                createFlightDto.getArrivalTime(), null)) {
            throw new RuntimeException("Airplane is not available for the specified time period");
        }

        // Generate flight ID or use provided ID
        String flightId = createFlightDto.getId() != null && !createFlightDto.getId().isBlank()
                ? createFlightDto.getId()
                : generateFlightId(createFlightDto.getAirplaneId());

        Flight flight = Flight.builder()
                .id(flightId)
                .airlineId(createFlightDto.getAirlineId())
                .airplaneId(createFlightDto.getAirplaneId())
                .originAirportCode(createFlightDto.getOriginAirportCode())
                .destinationAirportCode(createFlightDto.getDestinationAirportCode())
                .departureTime(createFlightDto.getDepartureTime())
                .arrivalTime(createFlightDto.getArrivalTime())
                .terminal(createFlightDto.getTerminal())
                .gate(createFlightDto.getGate())
                .baggageAllowance(createFlightDto.getBaggageAllowance())
                .facilities(createFlightDto.getFacilities())
                .status(createFlightDto.getStatus() != null ? createFlightDto.getStatus() : 1) // Use provided status or
                                                                                               // default to Scheduled
                .isDeleted(false)
                .build();

        Flight savedFlight = flightRepository.save(flight);
        return mapToReadDto(savedFlight);
    }

    @Override
    public ReadFlightDto updateFlight(UpdateFlightDto updateFlightDto) {
        Flight flight = flightRepository.findById(updateFlightDto.getId())
                .orElseThrow(() -> new RuntimeException("Flight with ID " + updateFlightDto.getId() + " not found"));

        // Check if flight can be updated (only Scheduled or Delayed)
        if (!canUpdateFlight(updateFlightDto.getId())) {
            throw new RuntimeException("Can only update flights with status Scheduled or Delayed");
        }

        // Validate departure time < arrival time
        if (!updateFlightDto.getDepartureTime().isBefore(updateFlightDto.getArrivalTime())) {
            throw new RuntimeException("Departure time must be before arrival time");
        }

        // Check if airplane is available (excluding current flight)
        if (!isAirplaneAvailable(updateFlightDto.getAirplaneId(), updateFlightDto.getDepartureTime(),
                updateFlightDto.getArrivalTime(), updateFlightDto.getId())) {
            throw new RuntimeException("Airplane is not available for the specified time period");
        }

        flight.setAirlineId(updateFlightDto.getAirlineId());
        flight.setAirplaneId(updateFlightDto.getAirplaneId());
        flight.setOriginAirportCode(updateFlightDto.getOriginAirportCode());
        flight.setDestinationAirportCode(updateFlightDto.getDestinationAirportCode());
        flight.setDepartureTime(updateFlightDto.getDepartureTime());
        flight.setArrivalTime(updateFlightDto.getArrivalTime());
        flight.setTerminal(updateFlightDto.getTerminal());
        flight.setGate(updateFlightDto.getGate());
        flight.setBaggageAllowance(updateFlightDto.getBaggageAllowance());
        flight.setFacilities(updateFlightDto.getFacilities());
        flight.setStatus(updateFlightDto.getStatus());

        Flight updatedFlight = flightRepository.save(flight);
        return mapToReadDto(updatedFlight);
    }

    @Override
    public void deleteFlight(String id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight with ID " + id + " not found"));

        // Check if flight can be deleted
        if (!canDeleteFlight(id)) {
            throw new RuntimeException(
                    "Cannot delete flight. Only Scheduled or Delayed flights without active bookings can be cancelled");
        }

        // Soft delete - mark as deleted and set status to Cancelled
        flight.setIsDeleted(true);
        flight.setStatus(5); // Cancelled
        flightRepository.save(flight);
    }

    @Override
    public List<ReadFlightDto> searchFlights(String origin, String destination, LocalDateTime departureDate,
            String airlineId, Integer status) {
        List<Flight> flights = flightRepository.findByIsDeletedFalse();

        return flights.stream()
                .filter(f -> origin == null || f.getOriginAirportCode().equalsIgnoreCase(origin))
                .filter(f -> destination == null || f.getDestinationAirportCode().equalsIgnoreCase(destination))
                .filter(f -> departureDate == null
                        || f.getDepartureTime().toLocalDate().equals(departureDate.toLocalDate()))
                .filter(f -> airlineId == null || f.getAirlineId().equals(airlineId))
                .filter(f -> status == null || f.getStatus().equals(status))
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadFlightDto> getFlightsByRoute(String origin, String destination) {
        return flightRepository.findByOriginAirportCodeAndDestinationAirportCodeAndIsDeletedFalse(origin, destination)
                .stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadFlightDto> getFlightsByStatus(Integer status) {
        return flightRepository.findByStatusAndIsDeletedFalse(status).stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadFlightDto> getUpcomingFlights() {
        return flightRepository.findUpcomingFlights(LocalDateTime.now()).stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadFlightDto> getFlightsDepartingToday() {
        return flightRepository.findFlightsDepartingOnDate(LocalDateTime.now()).stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadFlightDto> getFlightsByAirline(String airlineId) {
        return flightRepository.findByAirlineIdAndIsDeletedFalse(airlineId).stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadFlightDto> getFlightsWithAvailableSeats() {
        return flightRepository.findFlightsWithAvailableSeats().stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public String generateFlightId(String airplaneId) {
        // Format: {AirplaneID}-{NomorUrut3Digit}
        List<Flight> existingFlights = flightRepository.findByAirplaneId(airplaneId);

        int maxNumber = 0;
        for (Flight flight : existingFlights) {
            String id = flight.getId();
            if (id.startsWith(airplaneId + "-")) {
                try {
                    String numberPart = id.substring(airplaneId.length() + 1);
                    int number = Integer.parseInt(numberPart);
                    maxNumber = Math.max(maxNumber, number);
                } catch (Exception e) {
                    // Ignore parsing errors
                }
            }
        }

        return String.format("%s-%03d", airplaneId, maxNumber + 1);
    }

    @Override
    public boolean canDeleteFlight(String flightId) {
        Flight flight = flightRepository.findById(flightId).orElse(null);
        if (flight == null) {
            return false;
        }

        // Can only delete Scheduled or Delayed flights
        if (flight.getStatus() != 1 && flight.getStatus() != 4) {
            return false;
        }

        // Check if there are active bookings (Paid or Rescheduled)
        List<io.harman.flight_be.model.flight.Booking> activeBookings = bookingRepository
                .findByFlightIdAndIsDeletedFalse(flightId);
        return activeBookings.stream()
                .noneMatch(booking -> booking.getStatus() == 2 || booking.getStatus() == 4);
    }

    @Override
    public boolean canUpdateFlight(String flightId) {
        Flight flight = flightRepository.findById(flightId).orElse(null);
        if (flight == null) {
            return false;
        }

        // Can only update Scheduled or Delayed flights
        return flight.getStatus() == 1 || flight.getStatus() == 4;
    }

    @Override
    public boolean isAirplaneAvailable(String airplaneId, LocalDateTime departureTime, LocalDateTime arrivalTime,
            String excludeFlightId) {
        List<Flight> existingFlights = flightRepository.findByAirplaneIdAndIsDeletedFalse(airplaneId);

        for (Flight flight : existingFlights) {
            // Skip the current flight if we're updating
            if (excludeFlightId != null && flight.getId().equals(excludeFlightId)) {
                continue;
            }

            // Only check scheduled, in-flight, or delayed flights
            if (flight.getStatus() == 1 || flight.getStatus() == 2 || flight.getStatus() == 4) {
                // Check if time ranges overlap
                if (!(arrivalTime.isBefore(flight.getDepartureTime())
                        || departureTime.isAfter(flight.getArrivalTime()))) {
                    return false; // Overlapping time found
                }
            }
        }

        return true;
    }

    private ReadFlightDto mapToReadDto(Flight flight) {
        String airlineName = null;
        String airlineCountry = null;
        String airplaneModel = null;

        if (flight.getAirline() != null) {
            airlineName = flight.getAirline().getName();
            airlineCountry = flight.getAirline().getCountry();
        }

        if (flight.getAirplane() != null) {
            airplaneModel = flight.getAirplane().getModel();
        }

        String statusLabel = getStatusLabel(flight.getStatus());

        // Map classes to ClassFlightSummary
        List<ReadFlightDto.ClassFlightSummary> classesSummary = new ArrayList<>();
        if (flight.getClasses() != null && !flight.getClasses().isEmpty()) {
            classesSummary = flight.getClasses().stream()
                    .map(classFlight -> ReadFlightDto.ClassFlightSummary.builder()
                            .id(classFlight.getId())
                            .classType(classFlight.getClassType())
                            .seatCapacity(classFlight.getSeatCapacity())
                            .availableSeats(classFlight.getAvailableSeats())
                            .price(classFlight.getPrice())
                            .build())
                    .collect(Collectors.toList());
        }

        return ReadFlightDto.builder()
                .id(flight.getId())
                .airlineId(flight.getAirlineId())
                .airlineName(airlineName)
                .airlineCountry(airlineCountry)
                .airplaneId(flight.getAirplaneId())
                .airplaneModel(airplaneModel)
                .originAirportCode(flight.getOriginAirportCode())
                .destinationAirportCode(flight.getDestinationAirportCode())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .terminal(flight.getTerminal())
                .gate(flight.getGate())
                .baggageAllowance(flight.getBaggageAllowance())
                .facilities(flight.getFacilities())
                .status(flight.getStatus())
                .statusLabel(statusLabel)
                .classes(classesSummary)
                .createdAt(flight.getCreatedAt())
                .updatedAt(flight.getUpdatedAt())
                .isDeleted(flight.getIsDeleted())
                .build();
    }

    private String getStatusLabel(Integer status) {
        if (status == null)
            return "Unknown";
        switch (status) {
            case 1:
                return "Scheduled";
            case 2:
                return "In Flight";
            case 3:
                return "Finished";
            case 4:
                return "Delayed";
            case 5:
                return "Cancelled";
            default:
                return "Unknown";
        }
    }

    @Override
    public List<io.harman.flight_be.dto.flight.FlightReminderDto> getFlightReminders(Integer intervalHours,
            String customerId) {
        // Validate and default interval to 3 hours if invalid
        if (intervalHours == null || intervalHours < 0) {
            intervalHours = 3;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusHours(intervalHours);

        // Find flights departing within the time window
        List<Flight> flights = flightRepository.findFlightsDepartingBetween(now, endTime);

        // If customerId is provided, filter flights to only those with bookings by this
        // customer
        if (customerId != null && !customerId.isBlank()) {
            flights = flights.stream()
                    .filter(flight -> {
                        // Check if customer has any Paid bookings for this flight
                        return bookingRepository.findByFlightIdAndIsDeletedFalse(flight.getId()).stream()
                                .anyMatch(booking -> booking.getStatus() == 2 && // Paid status
                                        booking.getPassengers().stream()
                                                .anyMatch(
                                                        passenger -> customerId.equals(passenger.getId().toString())));
                    })
                    .collect(Collectors.toList());
        }

        // Map to FlightReminderDto
        return flights.stream()
                .map(flight -> {
                    // Calculate remaining time
                    long remainingMinutes = java.time.Duration.between(now, flight.getDepartureTime()).toMinutes();
                    String remainingTime = formatRemainingTime(remainingMinutes);

                    // Count bookings by status
                    List<io.harman.flight_be.model.flight.Booking> bookings = bookingRepository
                            .findByFlightIdAndIsDeletedFalse(flight.getId());

                    long paidBookings = bookings.stream()
                            .filter(b -> b.getStatus() == 2) // Paid
                            .count();

                    long unpaidBookings = bookings.stream()
                            .filter(b -> b.getStatus() == 1) // Unpaid
                            .count();

                    // Get airline name
                    String airlineName = flight.getAirline() != null ? flight.getAirline().getName() : "Unknown";

                    return io.harman.flight_be.dto.flight.FlightReminderDto.builder()
                            .flightNumber(flight.getId())
                            .airline(airlineName)
                            .origin(flight.getOriginAirportCode())
                            .destination(flight.getDestinationAirportCode())
                            .departureTime(flight.getDepartureTime())
                            .remainingMinutes(remainingMinutes)
                            .remainingTime(remainingTime)
                            .status(flight.getStatus())
                            .statusLabel(getStatusLabel(flight.getStatus()))
                            .totalPaidBookings(paidBookings)
                            .totalUnpaidBookings(unpaidBookings)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private String formatRemainingTime(long minutes) {
        if (minutes < 0) {
            return "Departed";
        }

        long hours = minutes / 60;
        long mins = minutes % 60;

        if (hours > 0) {
            return String.format("%dh %dm", hours, mins);
        } else {
            return String.format("%dm", mins);
        }
    }
}
