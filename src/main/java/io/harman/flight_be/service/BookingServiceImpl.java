package io.harman.flight_be.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.harman.flight_be.dto.booking.CreateBookingDto;
import io.harman.flight_be.dto.booking.ReadBookingDto;
import io.harman.flight_be.dto.booking.UpdateBookingDto;
import io.harman.flight_be.model.Booking;
import io.harman.flight_be.model.Flight;
import io.harman.flight_be.model.Passenger;
import io.harman.flight_be.model.Seat;
import io.harman.flight_be.repository.BookingRepository;
import io.harman.flight_be.repository.ClassFlightRepository;
import io.harman.flight_be.repository.FlightRepository;
import io.harman.flight_be.repository.PassengerRepository;
import io.harman.flight_be.repository.SeatRepository;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final ClassFlightRepository classFlightRepository;
    private final PassengerRepository passengerRepository;
    private final SeatRepository seatRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
            FlightRepository flightRepository,
            ClassFlightRepository classFlightRepository,
            PassengerRepository passengerRepository,
            SeatRepository seatRepository) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.classFlightRepository = classFlightRepository;
        this.passengerRepository = passengerRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public List<ReadBookingDto> getAllBookings() {
        return bookingRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadBookingDto> getAllActiveBookings() {
        return bookingRepository.findByIsDeletedFalse().stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReadBookingDto getBookingById(String id) {
        Booking booking = bookingRepository.findByIdWithPassengers(id)
                .orElseThrow(() -> new RuntimeException("Booking with ID " + id + " not found"));
        return mapToReadDto(booking);
    }

    @Override
    public ReadBookingDto createBooking(CreateBookingDto createBookingDto) {
        // Validate flight exists and is scheduled
        Flight flight = flightRepository.findByIdAndIsDeletedFalse(createBookingDto.getFlightId())
                .orElseThrow(() -> new RuntimeException(
                        "Flight with ID " + createBookingDto.getFlightId() + " not found or inactive"));

        if (flight.getStatus() != 1) {
            throw new RuntimeException("Can only book flights with Scheduled status");
        }

        // Validate class flight exists
        classFlightRepository.findById(createBookingDto.getClassFlightId())
                .orElseThrow(() -> new RuntimeException(
                        "Class Flight with ID " + createBookingDto.getClassFlightId() + " not found"));

        // Validate passenger count (max 10)
        if (createBookingDto.getPassengerCount() > 10) {
            throw new RuntimeException("Maximum 10 passengers allowed per booking");
        }

        // Validate all passengers exist and fetch them
        List<Passenger> passengers = new ArrayList<>();
        if (createBookingDto.getPassengerIds() != null) {
            for (UUID passengerId : createBookingDto.getPassengerIds()) {
                Passenger passenger = passengerRepository.findById(passengerId)
                        .orElseThrow(() -> new RuntimeException("Passenger with ID " + passengerId + " not found"));
                passengers.add(passenger);
            }
        }

        // Generate booking ID
        String bookingId = generateBookingId(createBookingDto.getFlightId(),
                flight.getOriginAirportCode(),
                flight.getDestinationAirportCode());

        Booking booking = Booking.builder()
                .id(bookingId)
                .flightId(createBookingDto.getFlightId())
                .classFlightId(createBookingDto.getClassFlightId())
                .contactEmail(createBookingDto.getContactEmail())
                .contactPhone(createBookingDto.getContactPhone())
                .passengerCount(createBookingDto.getPassengerCount())
                .status(createBookingDto.getStatus() != null ? createBookingDto.getStatus() : 1) // Use provided status
                                                                                                 // or default to Unpaid
                .totalPrice(createBookingDto.getTotalPrice())
                .passengers(passengers)
                .isDeleted(false)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        // Assign seats to passengers
        if (!passengers.isEmpty()) {
            // Get available seats for the class flight
            List<Seat> availableSeats = seatRepository.findByClassFlightIdAndIsAvailableTrueOrderBySeatNumberAsc(
                    createBookingDto.getClassFlightId());

            // Assign seats to each passenger
            int seatIndex = 0;
            for (Passenger passenger : passengers) {
                if (seatIndex < availableSeats.size()) {
                    Seat seat = availableSeats.get(seatIndex);
                    seatRepository.assignSeatToPassenger(seat.getId(), passenger.getId());
                    seatIndex++;
                } else {
                    // Not enough available seats
                    System.err.println("Warning: Not enough available seats for all passengers in booking "
                            + savedBooking.getId());
                    break;
                }
            }
        }

        // Fetch the saved booking with passengers to return complete data
        return mapToReadDto(bookingRepository.findByIdWithPassengers(savedBooking.getId())
                .orElse(savedBooking));
    }

    @Override
    public ReadBookingDto updateBooking(UpdateBookingDto updateBookingDto) {
        Booking booking = bookingRepository.findById(updateBookingDto.getId())
                .orElseThrow(() -> new RuntimeException("Booking with ID " + updateBookingDto.getId() + " not found"));

        // Check if booking can be updated
        if (!canUpdateBooking(updateBookingDto.getId())) {
            throw new RuntimeException("Can only update bookings with status Unpaid or Paid");
        }

        // Validate flight is Scheduled or Delayed
        Flight flight = flightRepository.findById(updateBookingDto.getFlightId())
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        if (flight.getStatus() != 1 && flight.getStatus() != 4) {
            throw new RuntimeException("Can only update bookings for Scheduled or Delayed flights");
        }

        booking.setFlightId(updateBookingDto.getFlightId());
        booking.setClassFlightId(updateBookingDto.getClassFlightId());
        booking.setContactEmail(updateBookingDto.getContactEmail());
        booking.setContactPhone(updateBookingDto.getContactPhone());
        booking.setPassengerCount(updateBookingDto.getPassengerCount());
        booking.setStatus(updateBookingDto.getStatus());
        booking.setTotalPrice(updateBookingDto.getTotalPrice());

        Booking updatedBooking = bookingRepository.save(booking);
        return mapToReadDto(updatedBooking);
    }

    @Override
    public void deleteBooking(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking with ID " + id + " not found"));

        // Check if booking can be deleted
        if (!canDeleteBooking(id)) {
            throw new RuntimeException("Can only cancel bookings with status Unpaid or Paid");
        }

        // Soft delete - mark as deleted and set status to Cancelled
        booking.setIsDeleted(true);
        booking.setStatus(3); // Cancelled

        // Release all seats associated with this booking
        // This would need BookingPassenger logic to be fully implemented

        bookingRepository.save(booking);
    }

    @Override
    public List<ReadBookingDto> getBookingsByStatus(Integer status) {
        return bookingRepository.findByStatusAndIsDeletedFalse(status).stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadBookingDto> getBookingsByEmail(String email) {
        return bookingRepository.findByContactEmailAndIsDeletedFalse(email).stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadBookingDto> getBookingsByFlightId(String flightId) {
        return bookingRepository.findByFlightIdAndIsDeletedFalse(flightId).stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadBookingDto> getBookingsByDateRange(LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findByCreatedAtBetweenAndIsDeletedFalse(start, end).stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getBookingStatistics(LocalDateTime start, LocalDateTime end) {
        List<Booking> bookings = bookingRepository.findByCreatedAtBetweenAndIsDeletedFalse(start, end);

        // Filter only Paid and Unpaid bookings
        List<Booking> validBookings = bookings.stream()
                .filter(b -> b.getStatus() == 1 || b.getStatus() == 2)
                .collect(Collectors.toList());

        // Group by flight ID
        Map<String, List<Booking>> bookingsByFlight = validBookings.stream()
                .collect(Collectors.groupingBy(Booking::getFlightId));

        // Calculate statistics per flight
        List<Map<String, Object>> flightStats = new ArrayList<>();
        for (Map.Entry<String, List<Booking>> entry : bookingsByFlight.entrySet()) {
            String flightId = entry.getKey();
            List<Booking> flightBookings = entry.getValue();

            BigDecimal totalRevenue = flightBookings.stream()
                    .map(Booking::getTotalPrice)
                    .filter(price -> price != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            int bookingCount = flightBookings.size();
            int totalPassengers = flightBookings.stream()
                    .mapToInt(Booking::getPassengerCount)
                    .sum();

            Map<String, Object> stat = new HashMap<>();
            stat.put("flightId", flightId);
            stat.put("bookingCount", bookingCount);
            stat.put("totalPassengers", totalPassengers);
            stat.put("totalRevenue", totalRevenue);

            flightStats.add(stat);
        }

        // Calculate overall totals
        BigDecimal overallRevenue = validBookings.stream()
                .map(Booking::getTotalPrice)
                .filter(price -> price != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int overallPassengers = validBookings.stream()
                .mapToInt(Booking::getPassengerCount)
                .sum();

        Map<String, Object> result = new HashMap<>();
        result.put("period", Map.of("start", start, "end", end));
        result.put("totalBookings", validBookings.size());
        result.put("totalPassengers", overallPassengers);
        result.put("totalRevenue", overallRevenue);
        result.put("flightStatistics", flightStats);

        return result;
    }

    @Override
    public BigDecimal getTotalRevenue(LocalDateTime start, LocalDateTime end) {
        BigDecimal revenue = bookingRepository.getTotalRevenueByDateRange(start, end);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalRevenueByFlight(String flightId) {
        BigDecimal revenue = bookingRepository.getTotalRevenueByFlight(flightId);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    public String generateBookingId(String flightId, String originAirportCode, String destinationAirportCode) {
        // Format: FlightID-OriginAirportCode-DestinationAirportCode-Urutan
        String prefix = String.format("%s-%s-%s", flightId, originAirportCode, destinationAirportCode);

        List<Booking> existingBookings = bookingRepository.findByFlightId(flightId);

        int maxNumber = 0;
        for (Booking booking : existingBookings) {
            String id = booking.getId();
            if (id.startsWith(prefix + "-")) {
                try {
                    String numberPart = id.substring(prefix.length() + 1);
                    int number = Integer.parseInt(numberPart);
                    maxNumber = Math.max(maxNumber, number);
                } catch (Exception e) {
                    // Ignore parsing errors
                }
            }
        }

        return String.format("%s-%03d", prefix, maxNumber + 1);
    }

    @Override
    public boolean canUpdateBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return false;
        }

        // Can only update Unpaid or Paid bookings
        return booking.getStatus() == 1 || booking.getStatus() == 2;
    }

    @Override
    public boolean canDeleteBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return false;
        }

        // Can only delete Unpaid or Paid bookings
        return booking.getStatus() == 1 || booking.getStatus() == 2;
    }

    private ReadBookingDto mapToReadDto(Booking booking) {
        String flightNumber = booking.getFlightId();
        String originAirportCode = null;
        String destinationAirportCode = null;
        LocalDateTime departureTime = null;
        LocalDateTime arrivalTime = null;

        if (booking.getFlight() != null) {
            flightNumber = booking.getFlight().getId();
            originAirportCode = booking.getFlight().getOriginAirportCode();
            destinationAirportCode = booking.getFlight().getDestinationAirportCode();
            departureTime = booking.getFlight().getDepartureTime();
            arrivalTime = booking.getFlight().getArrivalTime();
        }

        String classType = null;
        if (booking.getClassFlight() != null) {
            classType = booking.getClassFlight().getClassType();
        }

        String statusLabel = getStatusLabel(booking.getStatus());

        // Map passengers to PassengerSummary
        List<ReadBookingDto.PassengerSummary> passengerSummaries = new ArrayList<>();
        if (booking.getPassengers() != null && !booking.getPassengers().isEmpty()) {
            for (Passenger passenger : booking.getPassengers()) {
                // Find seat assigned to this passenger for this booking's class flight
                String seatNumber = null;
                if (booking.getClassFlightId() != null) {
                    List<Seat> seats = seatRepository.findByClassFlightIdAndPassengerId(
                            booking.getClassFlightId(), passenger.getId());
                    if (!seats.isEmpty()) {
                        seatNumber = seats.get(0).getSeatNumber();
                    }
                }

                ReadBookingDto.PassengerSummary summary = ReadBookingDto.PassengerSummary.builder()
                        .id(passenger.getId().toString())
                        .fullName(passenger.getFullName())
                        .seatNumber(seatNumber)
                        .build();
                passengerSummaries.add(summary);
            }
        }

        return ReadBookingDto.builder()
                .id(booking.getId())
                .flightId(booking.getFlightId())
                .flightNumber(flightNumber)
                .originAirportCode(originAirportCode)
                .destinationAirportCode(destinationAirportCode)
                .departureTime(departureTime)
                .arrivalTime(arrivalTime)
                .classFlightId(booking.getClassFlightId())
                .classType(classType)
                .contactEmail(booking.getContactEmail())
                .contactPhone(booking.getContactPhone())
                .passengerCount(booking.getPassengerCount())
                .status(booking.getStatus())
                .statusLabel(statusLabel)
                .totalPrice(booking.getTotalPrice())
                .passengers(passengerSummaries)
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .isDeleted(booking.getIsDeleted())
                .build();
    }

    private String getStatusLabel(Integer status) {
        if (status == null)
            return "Unknown";
        switch (status) {
            case 1:
                return "Unpaid";
            case 2:
                return "Paid";
            case 3:
                return "Cancelled";
            case 4:
                return "Rescheduled";
            default:
                return "Unknown";
        }
    }
}
