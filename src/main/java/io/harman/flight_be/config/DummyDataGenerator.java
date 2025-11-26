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
import io.harman.flight_be.model.loyalty.Coupon;
import io.harman.flight_be.model.loyalty.LoyaltyPoints;
import io.harman.flight_be.model.loyalty.PurchasedCoupon;
import io.harman.flight_be.repository.loyalty.CouponRepository;
import io.harman.flight_be.repository.loyalty.LoyaltyPointsRepository;
import io.harman.flight_be.repository.loyalty.PurchasedCouponRepository;
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
    private final CouponRepository couponRepository;
    private final LoyaltyPointsRepository loyaltyPointsRepository;
    private final PurchasedCouponRepository purchasedCouponRepository;

    public void generate() {
        // ANSI color codes
        String RESET = "\u001B[0m";
        String BOLD = "\u001B[1m";
        String CYAN = "\u001B[36m";
        String GREEN = "\u001B[32m";
        String YELLOW = "\u001B[33m";
        String BLUE = "\u001B[34m";
        String MAGENTA = "\u001B[35m";

        System.out.println("\n" + CYAN + "╔" + "═".repeat(78) + "╗" + RESET);
        System.out.println(CYAN + "║" + RESET + BOLD + "  ✈️  Flight Booking System - Dummy Data Generator"
                + " ".repeat(26) + CYAN + "║" + RESET);
        System.out.println(CYAN + "╚" + "═".repeat(78) + "╝" + RESET + "\n");

        Faker faker = new Faker(Locale.of("id", "ID"));
        Random random = new Random();

        // 1. Create Airlines (10)
        System.out.println(BOLD + BLUE + "\n┌─ [1/7] Creating Airlines" + RESET);
        System.out.println(BLUE + "│" + RESET);
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
            System.out.println(
                    BLUE + "│  " + RESET + "➤ " + airlineNames[i] + " (" + airlineCodes[i] + ") - " + countries[i]);
        }
        System.out.println(BLUE + "│" + RESET);
        System.out.println(BLUE + "└─" + RESET + GREEN + " ✓ Created " + airlineIds.size() + " airlines" + RESET);

        // 2. Create Airplanes (15)
        System.out.println(BOLD + MAGENTA + "\n┌─ [2/7] Creating Airplanes" + RESET);
        System.out.println(MAGENTA + "│" + RESET);
        List<String> airplaneIds = new ArrayList<>();
        String[] airplaneModels = { "Boeing 737", "Airbus A320", "Boeing 777", "Airbus A330",
                "Boeing 787", "ATR 72", "Bombardier CRJ1000" };

        for (int i = 0; i < 15; i++) {
            String airlineId = airlineIds.get(random.nextInt(airlineIds.size()));
            String model = airplaneModels[random.nextInt(airplaneModels.length)];
            int seatCapacity = 100 + random.nextInt(251);
            int manufactureYear = 2010 + random.nextInt(15);
            CreateAirplaneDto airplane = CreateAirplaneDto.builder()
                    .airlineId(airlineId)
                    .model(model)
                    .seatCapacity(seatCapacity) // 100-350 seats
                    .manufactureYear(manufactureYear) // 2010-2024
                    .build();
            String id = airplaneService.createAirplane(airplane).getId();
            airplaneIds.add(id);
            System.out.println(MAGENTA + "│  " + RESET + "➤ " + id + " - " + model + " (" + seatCapacity
                    + " seats, Year: " + manufactureYear + ")");
        }
        System.out.println(MAGENTA + "│" + RESET);
        System.out.println(MAGENTA + "└─" + RESET + GREEN + " ✓ Created " + airplaneIds.size() + " airplanes" + RESET);

        // 3. Create Flights (20 departure + 20 return = 40 total)
        System.out.println(BOLD + YELLOW + "\n┌─ [3/7] Creating Flights (with return flights)" + RESET);
        System.out.println(YELLOW + "│" + RESET);
        List<String> flightIds = new ArrayList<>();
        String[] airports = { "CGK", "SUB", "DPS", "UPG", "KNO", "BPN", "SOC", "PLM", "PDG", "JOG",
                "BDO", "PKU", "BTH", "TKG", "SRG", "MDC", "AMQ", "MLG", "SBY", "HLP" };
        String[] terminals = { "1A", "1B", "1C", "2", "2A", "2B", "2C", "2D", "2E", "2F", "3", "3A", "3B" };
        String[] gates = { "A1", "A2", "A3", "A4", "A5", "B1", "B2", "B3", "B4", "B5",
                "C1", "C2", "C3", "C4", "C5", "D1", "D2", "D3", "D4", "D5" };
        String[] facilitiesList = {
                "WiFi, In-flight Entertainment, Meal",
                "WiFi, Meal, Blanket",
                "In-flight Entertainment, Snacks",
                "WiFi, In-flight Entertainment",
                "Meal, Blanket, Pillow",
                "WiFi, Meal, In-flight Entertainment, Power Outlet",
                "In-flight Entertainment, Meal, Blanket, Pillow",
                "WiFi, Snacks",
                "Meal, In-flight Entertainment",
                "WiFi, Meal, Blanket, Pillow, Power Outlet"
        };
        // Flight status: 1=Scheduled, 2=In Flight, 3=Finished, 4=Delayed, 5=Cancelled
        Integer[] flightStatuses = { 1, 1, 1, 1, 1, 2, 2, 4, 4, 4 }; // Mostly scheduled, some in-flight/delayed
        String[] statusNames = { "", "Scheduled", "In Flight", "Finished", "Delayed", "Cancelled" };

        for (int i = 0; i < 20; i++) {
            try {
                String selectedAirplaneId = airplaneIds.get(random.nextInt(airplaneIds.size()));
                // Extract airline ID from airplane ID (format: XXX-XXX where first 3 are
                // airline code)
                String airlineId = selectedAirplaneId.substring(0, 3);
                String origin = airports[random.nextInt(airports.length)];
                String destination;
                do {
                    destination = airports[random.nextInt(airports.length)];
                } while (destination.equals(origin));

                LocalDateTime departureTime = LocalDateTime.now().plusDays(random.nextInt(60));
                LocalDateTime arrivalTime = departureTime.plusHours(1 + random.nextInt(8));
                long flightDurationHours = java.time.Duration.between(departureTime, arrivalTime).toHours();

                // Generate flight ID: {AirplaneID}-{3-digit sequence}
                String flightId = selectedAirplaneId + "-" + String.format("%03d", i + 1);

                // Store common flight details for reuse
                String selectedTerminal = terminals[random.nextInt(terminals.length)];
                String selectedGate = gates[random.nextInt(gates.length)];
                int selectedBaggage = 20 + random.nextInt(21); // 20-40 kg
                String selectedFacilities = facilitiesList[random.nextInt(facilitiesList.length)];
                Integer selectedStatus = flightStatuses[random.nextInt(flightStatuses.length)];

                // Create departure flight
                CreateFlightDto departureFlight = CreateFlightDto.builder()
                        .id(flightId)
                        .airlineId(airlineId)
                        .airplaneId(selectedAirplaneId)
                        .originAirportCode(origin)
                        .destinationAirportCode(destination)
                        .departureTime(departureTime)
                        .arrivalTime(arrivalTime)
                        .terminal(selectedTerminal)
                        .gate(selectedGate)
                        .baggageAllowance(selectedBaggage)
                        .facilities(selectedFacilities)
                        .status(selectedStatus)
                        .build();
                flightService.createFlight(departureFlight);
                flightIds.add(flightId);
                System.out.println(YELLOW + "│  " + RESET + "➤ Departure: " + origin + " → " + destination + " ("
                        + flightId + ") - " + statusNames[selectedStatus]);

                // Create return flight (reversed route) - a few days later
                String returnFlightId = selectedAirplaneId + "-" + String.format("%03d", 100 + i + 1);
                LocalDateTime returnDepartureTime = departureTime.plusDays(3 + random.nextInt(4)); // 3-6 days after
                                                                                                   // departure
                LocalDateTime returnArrivalTime = returnDepartureTime.plusHours(flightDurationHours); // Same flight
                                                                                                      // duration

                CreateFlightDto returnFlight = CreateFlightDto.builder()
                        .id(returnFlightId)
                        .airlineId(airlineId)
                        .airplaneId(selectedAirplaneId)
                        .originAirportCode(destination) // Reversed: destination becomes origin
                        .destinationAirportCode(origin) // Reversed: origin becomes destination
                        .departureTime(returnDepartureTime)
                        .arrivalTime(returnArrivalTime)
                        .terminal(selectedTerminal)
                        .gate(selectedGate)
                        .baggageAllowance(selectedBaggage)
                        .facilities(selectedFacilities)
                        .status(selectedStatus)
                        .build();
                flightService.createFlight(returnFlight);
                flightIds.add(returnFlightId);
                System.out.println(YELLOW + "│  " + RESET + "➤ Return:    " + destination + " → " + origin + " ("
                        + returnFlightId + ") - " + statusNames[selectedStatus]);

            } catch (Exception e) {
                System.err.println("Warning: Failed to create flight " + i + ": " + e.getMessage());
            }
        }
        System.out.println(YELLOW + "│" + RESET);
        System.out.println(YELLOW + "└─" + RESET + GREEN + " ✓ Created " + flightIds.size() + " flights ("
                + (flightIds.size() / 2) + " departure + "
                + (flightIds.size() / 2) + " return)" + RESET);

        // 4. Create Class Flights (60 - 3 classes per flight)
        System.out.println(BOLD + CYAN + "\n┌─ [4/7] Creating Class Flights" + RESET);
        System.out.println(CYAN + "│" + RESET);
        List<Integer> classFlightIds = new ArrayList<>();
        String[] classTypes = { "Economy", "Business", "First Class" };
        int flightCounter = 0;

        for (String flightId : flightIds) {
            flightCounter++;
            System.out.println(CYAN + "│  " + RESET + "Flight " + flightId + ":");
            for (int i = 0; i < 3; i++) {
                try {
                    int capacity = 30 + random.nextInt(71); // 30-100 seats per class
                    BigDecimal price;
                    // More realistic pricing for Indonesian market
                    if (i == 0) { // Economy
                        price = BigDecimal.valueOf(800000.0 + random.nextDouble() * 700000.0); // 800K - 1.5M
                    } else if (i == 1) { // Business
                        price = BigDecimal.valueOf(2000000.0 + random.nextDouble() * 2000000.0); // 2M - 4M
                    } else { // First Class
                        price = BigDecimal.valueOf(5000000.0 + random.nextDouble() * 3000000.0); // 5M - 8M
                    }
                    CreateClassFlightDto classFlight = CreateClassFlightDto.builder()
                            .flightId(flightId)
                            .classType(classTypes[i])
                            .price(price)
                            .seatCapacity(capacity)
                            .build();
                    Integer id = classFlightService.createClassFlight(classFlight).getId();
                    classFlightIds.add(id);
                    System.out.println(CYAN + "│    " + RESET + "  • " + classTypes[i] + ": IDR "
                            + String.format("%,.0f", price) + " (" + capacity + " seats)");
                } catch (Exception e) {
                    System.err.println("Warning: Failed to create class flight for " + flightId + " - " + classTypes[i]
                            + ": " + e.getMessage());
                }
            }
        }
        System.out.println(CYAN + "│" + RESET);
        System.out.println(
                CYAN + "└─" + RESET + GREEN + " ✓ Created " + classFlightIds.size() + " class flights" + RESET);

        // 5. Create Seats (based on class flight seat capacity)
        System.out.println(BOLD + MAGENTA + "\n┌─ [5/7] Creating Seats" + RESET);
        System.out.println(MAGENTA + "│  " + RESET + "Generating seats for all class flights...");
        System.out.println(MAGENTA + "│" + RESET);
        int seatCount = 0;
        Map<Integer, List<String>> classFlightSeatNumbers = new HashMap<>();

        for (Integer classFlightId : classFlightIds) {
            try {
                // Get the seat capacity for this class flight
                var classFlight = classFlightService.getClassFlightById(classFlightId);
                int seatCapacity = classFlight.getSeatCapacity();

                List<String> seatNumbers = new ArrayList<>();
                for (int i = 0; i < seatCapacity; i++) {
                    // Generate seat number: row number (1-based) + seat letter (A-F)
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
            } catch (Exception e) {
                System.err.println(
                        "Warning: Failed to create seats for class flight " + classFlightId + ": " + e.getMessage());
            }
        }
        System.out.println(MAGENTA + "│" + RESET);
        System.out.println(MAGENTA + "└─" + RESET + GREEN + " ✓ Created " + seatCount + " seats" + RESET);

        // 6. Create Passengers (30)
        System.out.println(BOLD + BLUE + "\n┌─ [6/7] Creating Passengers" + RESET);
        System.out.println(BLUE + "│" + RESET);
        List<UUID> passengerIds = new ArrayList<>();
        String[] genderNames = { "", "Male", "Female", "Other" };

        for (int i = 0; i < 30; i++) {
            com.github.javafaker.Name name = faker.name();
            int gender = random.nextInt(3) + 1;
            String idPassport = "ID" + String.format("%010d", random.nextInt(1000000000));
            CreatePassengerDto passenger = CreatePassengerDto.builder()
                    .fullName(name.fullName())
                    .gender(gender) // 1=Male, 2=Female, 3=Other
                    .birthDate(faker.date().birthday(18, 70).toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate())
                    .idPassport(idPassport)
                    .build();
            UUID id = passengerService.createPassenger(passenger).getId();
            passengerIds.add(id);
            System.out.println(BLUE + "│  " + RESET + "➤ " + name.fullName() + " (" + genderNames[gender] + ", "
                    + idPassport + ")");
        }
        System.out.println(BLUE + "│" + RESET);
        System.out.println(BLUE + "└─" + RESET + GREEN + " ✓ Created " + passengerIds.size() + " passengers" + RESET);

        // 7. Create Bookings (15)
        System.out.println(BOLD + YELLOW + "\n┌─ [7/7] Creating Bookings" + RESET);
        System.out.println(YELLOW + "│" + RESET);
        int bookingCount = 0;
        // Booking status: 1=Unpaid, 2=Paid, 3=Cancelled, 4=Rescheduled
        Integer[] bookingStatuses = { 1, 1, 1, 1, 1, 2, 2, 2, 4 }; // Mostly unpaid, some paid, few rescheduled
        String[] bookingStatusNames = { "", "Unpaid", "Paid", "Cancelled", "Rescheduled" };

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
                var classFlightDto = classFlightService.getClassFlightById(selectedClass);
                BigDecimal classPrice = classFlightDto.getPrice();
                BigDecimal totalPrice = classPrice.multiply(BigDecimal.valueOf(numPassengers));

                // Extract origin and destination from flight
                var flightDto = flightService.getFlightById(flightId);
                String bookingId = flightId + "-" + flightDto.getOriginAirportCode() + "-"
                        + flightDto.getDestinationAirportCode() + "-" + String.format("%03d", i + 1);

                Integer selectedStatus = bookingStatuses[random.nextInt(bookingStatuses.length)];
                CreateBookingDto booking = CreateBookingDto.builder()
                        .id(bookingId)
                        .flightId(flightId)
                        .classFlightId(selectedClass)
                        .contactEmail(faker.internet().emailAddress())
                        .contactPhone(faker.phoneNumber().phoneNumber())
                        .passengerCount(numPassengers)
                        .totalPrice(totalPrice)
                        .status(selectedStatus)
                        .passengerIds(bookingPassengerIds)
                        .build();

                bookingService.createBooking(booking);
                bookingCount++;
                System.out.println(YELLOW + "│  " + RESET + "➤ " + bookingId + " - " + classFlightDto.getClassType() +
                        " (" + numPassengers + " pax, IDR " + String.format("%,.0f", totalPrice) + ") - "
                        + bookingStatusNames[selectedStatus]);
            } catch (Exception e) {
                System.err.println("Warning: Failed to create booking " + i + ": " + e.getMessage());
            }
        }
        System.out.println(YELLOW + "│" + RESET);
        System.out.println(YELLOW + "└─" + RESET + GREEN + " ✓ Created " + bookingCount + " bookings" + RESET);
        System.out.println("\n" + CYAN + "╔" + "═".repeat(78) + "╗" + RESET);
        System.out.println(CYAN + "║" + RESET + BOLD + GREEN + "  ✓ Flight Booking System Data Generation Complete!"
                + " ".repeat(25) + CYAN + "║" + RESET);
        System.out.println(CYAN + "╠" + "═".repeat(78) + "╣" + RESET);
        System.out.println(CYAN + "║" + RESET + BOLD + "  Summary:" + " ".repeat(68) + CYAN + "║" + RESET);
        System.out.println(
                CYAN + "║" + RESET + "    • Airlines:      " + String.format("%-56s", "10") + CYAN + "║" + RESET);
        System.out.println(
                CYAN + "║" + RESET + "    • Airplanes:     " + String.format("%-56s", "15") + CYAN + "║" + RESET);
        System.out.println(CYAN
                + "║" + RESET + "    • Flights:       " + String.format("%-56s", flightIds.size() + " ("
                        + (flightIds.size() / 2) + " departure + " + (flightIds.size() / 2) + " return)")
                + CYAN + "║" + RESET);
        System.out.println(CYAN + "║" + RESET + "    • Class Flights: " + String.format("%-56s", classFlightIds.size())
                + CYAN + "║" + RESET);
        System.out.println(
                CYAN + "║" + RESET + "    • Seats:         " + String.format("%-56s", seatCount) + CYAN + "║" + RESET);
        System.out.println(
                CYAN + "║" + RESET + "    • Passengers:    " + String.format("%-56s", "30") + CYAN + "║" + RESET);
        System.out.println(CYAN + "║" + RESET + "    • Bookings:      " + String.format("%-56s", bookingCount) + CYAN
                + "║" + RESET);
        System.out.println(CYAN + "╚" + "═".repeat(78) + "╝" + RESET);

        // Create Coupons
        List<Coupon> coupons = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            coupons.add(Coupon.builder()
                    .name(faker.commerce().productName() + " Discount")
                    .description(faker.lorem().sentence())
                    .points(faker.number().numberBetween(100, 1000))
                    .percentOff(faker.number().numberBetween(5, 50))
                    .build());
        }
        couponRepository.saveAll(coupons);

        // Create Loyalty Points for a fixed user
        UUID customerId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        LoyaltyPoints points = loyaltyPointsRepository.findByCustomerId(customerId)
                .orElse(LoyaltyPoints.builder().customerId(customerId).points(0).build());
        points.setPoints(points.getPoints() + 5000);
        loyaltyPointsRepository.save(points);

        // Create Purchased Coupons
        List<PurchasedCoupon> purchasedCoupons = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Coupon coupon = coupons.get(faker.random().nextInt(coupons.size()));
            purchasedCoupons.add(PurchasedCoupon.builder()
                    .code(UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .customerId(customerId)
                    .couponId(coupon.getId())
                    .purchasedDate(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)))
                    .build());
        }
        purchasedCouponRepository.saveAll(purchasedCoupons);

        System.out.println("\n" + MAGENTA + "╔" + "═".repeat(78) + "╗" + RESET);
        System.out.println(MAGENTA + "║" + RESET + BOLD + GREEN + "  ✓ Loyalty System Data Generation Complete!"
                + " ".repeat(32) + MAGENTA + "║" + RESET);
        System.out.println(MAGENTA + "╠" + "═".repeat(78) + "╣" + RESET);
        System.out.println(MAGENTA + "║" + RESET + "    • Coupons:           " + String.format("%-52s", coupons.size())
                + MAGENTA + "║" + RESET);
        System.out.println(MAGENTA + "║" + RESET + "    • Loyalty Points:    "
                + String.format("%-52s", "5000 pts (Customer: " + customerId + ")") + MAGENTA + "║" + RESET);
        System.out.println(MAGENTA + "║" + RESET + "    • Purchased Coupons: "
                + String.format("%-52s", purchasedCoupons.size()) + MAGENTA + "║" + RESET);
        System.out.println(MAGENTA + "╚" + "═".repeat(78) + "╝" + RESET + "\n");
    }
}
