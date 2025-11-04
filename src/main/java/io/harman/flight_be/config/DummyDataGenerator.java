package io.harman.flight_be.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

import io.harman.flight_be.dto.airline.CreateAirlineDto;
import io.harman.flight_be.dto.airplane.CreateAirplaneDto;
import io.harman.flight_be.dto.booking.CreateBookingDto;
import io.harman.flight_be.dto.classflight.CreateClassFlightDto;
import io.harman.flight_be.dto.flight.CreateFlightDto;
import io.harman.flight_be.dto.passenger.CreatePassengerDto;
import io.harman.flight_be.dto.seat.CreateSeatDto;
import io.harman.flight_be.service.AirlineService;
import io.harman.flight_be.service.AirplaneService;
import io.harman.flight_be.service.BookingService;
import io.harman.flight_be.service.ClassFlightService;
import io.harman.flight_be.service.FlightService;
import io.harman.flight_be.service.PassengerService;
import io.harman.flight_be.service.SeatService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DummyDataGenerator {
    
    private final AirlineService airlineService;
    private final AirplaneService airplaneService;
    private final PassengerService passengerService;
    private final FlightService flightService;
    private final ClassFlightService classFlightService;
    private final SeatService seatService;
    private final BookingService bookingService;
    
    public void generate() {
        System.out.println("=".repeat(60));
        System.out.println("Generating dummy data for Flight Booking System...");
        System.out.println("=".repeat(60));
        

        Faker faker = new Faker(Locale.of("id", "ID"));
        Random random = new Random();

        // 1. Create Airlines (10)
        System.out.println("\n[1/7] Creating Airlines...");
        List<String> airlineIds = new ArrayList<>();
        String[] airlineNames = { "Garuda Indonesia", "Lion Air", "Batik Air", "Citilink", "AirAsia",
                "Sriwijaya Air", "Wings Air", "Nam Air", "Super Air Jet", "TransNusa" };
        String[] countries = { "Indonesia", "Indonesia", "Indonesia", "Indonesia", "Malaysia",
                "Indonesia", "Indonesia", "Indonesia", "Indonesia", "Indonesia" };
        String[] airlineCodes = { "GIA", "LNI", "BTK", "CTL", "AXM", "SJY", "WON", "NAM", "IAJ", "TNU" };

        for (int i = 0; i < 10; i++) {
            CreateAirlineDto airline = CreateAirlineDto.builder()
                    .id(airlineCodes[i])
                    .name(airlineNames[i])
                    .country(countries[i])
                    .build();
            airlineService.createAirline(airline);
            airlineIds.add(airlineCodes[i]);
        }
        System.out.println("✓ Created " + airlineIds.size() + " airlines");

        // 2. Create Airplanes (15)
        System.out.println("\n[2/7] Creating Airplanes...");
        List<String> airplaneIds = new ArrayList<>();
        String[] airplaneModels = { "Boeing 737", "Airbus A320", "Boeing 777", "Airbus A330",
                "Boeing 787", "ATR 72", "Bombardier CRJ1000" };

        for (int i = 0; i < 15; i++) {
            CreateAirplaneDto airplane = CreateAirplaneDto.builder()
                    .airlineId(airlineIds.get(random.nextInt(airlineIds.size())))
                    .model(airplaneModels[random.nextInt(airplaneModels.length)])
                    .seatCapacity(100 + random.nextInt(251)) // 100-350 seats
                    .manufactureYear(2010 + random.nextInt(15)) // 2010-2024
                    .build();
            String id = airplaneService.createAirplane(airplane).getId();
            airplaneIds.add(id);
        }
        System.out.println("✓ Created " + airplaneIds.size() + " airplanes");

        // 3. Create Flights (20)
        System.out.println("\n[3/7] Creating Flights...");
        List<String> flightIds = new ArrayList<>();
        String[] airports = { "CGK", "SUB", "DPS", "UPG", "KNO", "BPN", "SOC", "PLM", "PDG", "JOG",
                "BDO", "PKU", "BTH", "TKG", "SRG", "MDC", "AMQ", "MLG", "SBY", "HLP" };

        for (int i = 0; i < 20; i++) {
            try {
                String selectedAirplaneId = airplaneIds.get(random.nextInt(airplaneIds.size()));
                // Extract airline ID from airplane ID (format: XXX-XXX where first 3 are airline code)
                String airlineId = selectedAirplaneId.substring(0, 3);
                String origin = airports[random.nextInt(airports.length)];
                String destination;
                do {
                    destination = airports[random.nextInt(airports.length)];
                } while (destination.equals(origin));

                LocalDateTime departureTime = LocalDateTime.now().plusDays(random.nextInt(60));
                LocalDateTime arrivalTime = departureTime.plusHours(1 + random.nextInt(8));

                // Generate flight ID: {AirplaneID}-{3-digit sequence}
                String flightId = selectedAirplaneId + "-" + String.format("%03d", i + 1);

                CreateFlightDto flight = CreateFlightDto.builder()
                        .id(flightId)
                        .airlineId(airlineId)
                        .airplaneId(selectedAirplaneId)
                        .originAirportCode(origin)
                        .destinationAirportCode(destination)
                        .departureTime(departureTime)
                        .arrivalTime(arrivalTime)
                        .baggageAllowance(20 + random.nextInt(21)) // 20-40 kg
                        .build();
                flightService.createFlight(flight);
                flightIds.add(flightId);
            } catch (Exception e) {
                System.err.println("Warning: Failed to create flight " + i + ": " + e.getMessage());
            }
        }
        System.out.println("✓ Created " + flightIds.size() + " flights");

        // 4. Create Class Flights (60 - 3 classes per flight)
        System.out.println("\n[4/7] Creating Class Flights...");
        List<Integer> classFlightIds = new ArrayList<>();
        String[] classTypes = { "Economy", "Business", "First Class" };

        for (String flightId : flightIds) {
            for (int i = 0; i < 3; i++) {
                try {
                    int capacity = 30 + random.nextInt(71); // 30-100 seats per class
                    CreateClassFlightDto classFlight = CreateClassFlightDto.builder()
                            .flightId(flightId)
                            .classType(classTypes[i])
                            .price(BigDecimal.valueOf(500000.0 + (i * 1000000.0) + random.nextDouble() * 1000000.0))
                            .seatCapacity(capacity)
                            .build();
                    Integer id = classFlightService.createClassFlight(classFlight).getId();
                    classFlightIds.add(id);
                } catch (Exception e) {
                    System.err.println("Warning: Failed to create class flight for " + flightId + " - " + classTypes[i] + ": " + e.getMessage());
                }
            }
        }
        System.out.println("✓ Created " + classFlightIds.size() + " class flights");

        // 5. Create Seats (at least 300 - 5 seats per class flight)
        System.out.println("\n[5/7] Creating Seats...");
        int seatCount = 0;
        Map<Integer, List<String>> classFlightSeatNumbers = new HashMap<>();

        for (Integer classFlightId : classFlightIds) {
            List<String> seatNumbers = new ArrayList<>();
            for (int i = 0; i < 5; i++) { // 5 seats per class
                String seatNumber = String.format("%d%c", (i / 6) + 1, 'A' + (i % 6));
                CreateSeatDto seat = CreateSeatDto.builder()
                        .classFlightId(classFlightId)
                        .seatNumber(seatNumber)
                        .build();
                seatService.createSeat(seat);
                seatNumbers.add(seatNumber);
                seatCount++;
            }
            classFlightSeatNumbers.put(classFlightId, seatNumbers);
        }
        System.out.println("✓ Created " + seatCount + " seats");

        // 6. Create Passengers (30)
        System.out.println("\n[6/7] Creating Passengers...");
        List<UUID> passengerIds = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            com.github.javafaker.Name name = faker.name();
            CreatePassengerDto passenger = CreatePassengerDto.builder()
                    .fullName(name.fullName())
                    .gender(random.nextInt(3) + 1) // 1=Male, 2=Female, 3=Other
                    .birthDate(faker.date().birthday(18, 70).toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate())
                    .idPassport("ID" + String.format("%010d", random.nextInt(1000000000)))
                    .build();
            UUID id = passengerService.createPassenger(passenger).getId();
            passengerIds.add(id);
        }
        System.out.println("✓ Created " + passengerIds.size() + " passengers");

        // 7. Create Bookings (15)
        System.out.println("\n[7/7] Creating Bookings...");
        int bookingCount = 0;

        for (int i = 0; i < 15; i++) {
            try {
                // Skip if no flights available
                if (flightIds.isEmpty() || classFlightIds.isEmpty()) {
                    System.err.println("Warning: No flights or class flights available for booking creation");
                    break;
                }

                // Get random flight and its class
                String flightId = flightIds.get(random.nextInt(flightIds.size()));

                // Get available class flights for this flight
                List<Integer> availableClasses = classFlightIds.stream()
                        .filter(cfId -> {
                            try {
                                return classFlightService.getClassFlightById(cfId)
                                        .getFlightId().equals(flightId);
                            } catch (Exception e) {
                                return false;
                            }
                        })
                        .toList();

                if (availableClasses.isEmpty())
                    continue;

                Integer selectedClass = availableClasses.get(random.nextInt(availableClasses.size()));

                // Random number of passengers (1-3)
                int numPassengers = 1 + random.nextInt(Math.min(3, passengerIds.size()));

                // Get unique passengers
                Set<Integer> usedPassengerIndices = new HashSet<>();
                List<UUID> bookingPassengerIds = new ArrayList<>();

                for (int j = 0; j < numPassengers; j++) {
                    int passengerIndex;
                    do {
                        passengerIndex = random.nextInt(passengerIds.size());
                    } while (usedPassengerIndices.contains(passengerIndex));

                    usedPassengerIndices.add(passengerIndex);
                    bookingPassengerIds.add(passengerIds.get(passengerIndex));
                }

                // Calculate total price
                BigDecimal classPrice = classFlightService.getClassFlightById(selectedClass).getPrice();
                BigDecimal totalPrice = classPrice.multiply(BigDecimal.valueOf(numPassengers));

                // Extract origin and destination from flight
                var flightDto = flightService.getFlightById(flightId);
                String bookingId = flightId + "-" + flightDto.getOriginAirportCode() + "-"
                        + flightDto.getDestinationAirportCode() + "-" + String.format("%03d", i + 1);

                CreateBookingDto booking = CreateBookingDto.builder()
                        .id(bookingId)
                        .flightId(flightId)
                        .classFlightId(selectedClass)
                        .contactEmail(faker.internet().emailAddress())
                        .contactPhone(faker.phoneNumber().phoneNumber())
                        .passengerCount(numPassengers)
                        .totalPrice(totalPrice)
                        .passengerIds(bookingPassengerIds)
                        .build();

                bookingService.createBooking(booking);
                bookingCount++;
            } catch (Exception e) {
                System.err.println("Warning: Failed to create booking " + i + ": " + e.getMessage());
            }
        }
        System.out.println("✓ Created " + bookingCount + " bookings");
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Dummy data generation complete!");
        System.out.println("Summary:");
        System.out.println("  - Airlines: 10");
        System.out.println("  - Airplanes: 15");
        System.out.println("  - Flights: " + flightIds.size());
        System.out.println("  - Class Flights: " + classFlightIds.size());
        System.out.println("  - Seats: " + seatCount);
        System.out.println("  - Passengers: 30");
        System.out.println("  - Bookings: " + bookingCount);
        System.out.println("=".repeat(60));
    }
}
