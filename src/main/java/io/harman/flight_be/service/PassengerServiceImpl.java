package io.harman.flight_be.service;

import io.harman.flight_be.dto.passenger.CreatePassengerDto;
import io.harman.flight_be.dto.passenger.ReadPassengerDto;
import io.harman.flight_be.dto.passenger.UpdatePassengerDto;
import io.harman.flight_be.model.Passenger;
import io.harman.flight_be.repository.PassengerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;

    public PassengerServiceImpl(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    @Override
    public List<ReadPassengerDto> getAllPassengers() {
        return passengerRepository.findAllByOrderByFullNameAsc().stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReadPassengerDto getPassengerById(UUID id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Passenger with ID " + id + " not found"));
        return mapToReadDto(passenger);
    }

    @Override
    public ReadPassengerDto createPassenger(CreatePassengerDto createPassengerDto) {
        // Check if passenger with same ID/Passport already exists
        if (passengerRepository.existsByIdPassport(createPassengerDto.getIdPassport())) {
            throw new RuntimeException("Passenger with ID/Passport " + createPassengerDto.getIdPassport() + " already exists");
        }

        Passenger passenger = Passenger.builder()
                .fullName(createPassengerDto.getFullName())
                .birthDate(createPassengerDto.getBirthDate())
                .gender(createPassengerDto.getGender())
                .idPassport(createPassengerDto.getIdPassport())
                .build();

        Passenger savedPassenger = passengerRepository.save(passenger);
        return mapToReadDto(savedPassenger);
    }

    @Override
    public ReadPassengerDto updatePassenger(UpdatePassengerDto updatePassengerDto) {
        Passenger passenger = passengerRepository.findById(updatePassengerDto.getId())
                .orElseThrow(() -> new RuntimeException("Passenger with ID " + updatePassengerDto.getId() + " not found"));

        // Check if ID/Passport is being changed and if it already exists
        if (!passenger.getIdPassport().equals(updatePassengerDto.getIdPassport())) {
            if (passengerRepository.existsByIdPassport(updatePassengerDto.getIdPassport())) {
                throw new RuntimeException("Passenger with ID/Passport " + updatePassengerDto.getIdPassport() + " already exists");
            }
        }

        passenger.setFullName(updatePassengerDto.getFullName());
        passenger.setBirthDate(updatePassengerDto.getBirthDate());
        passenger.setGender(updatePassengerDto.getGender());
        passenger.setIdPassport(updatePassengerDto.getIdPassport());

        Passenger updatedPassenger = passengerRepository.save(passenger);
        return mapToReadDto(updatedPassenger);
    }

    @Override
    public void deletePassenger(UUID id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Passenger with ID " + id + " not found"));

        passengerRepository.delete(passenger);
    }

    @Override
    public List<ReadPassengerDto> searchPassengers(String fullName, String idPassport, Integer gender) {
        return passengerRepository.searchPassengers(fullName, idPassport, gender).stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReadPassengerDto getPassengerByIdPassport(String idPassport) {
        Passenger passenger = passengerRepository.findByIdPassport(idPassport)
                .orElseThrow(() -> new RuntimeException("Passenger with ID/Passport " + idPassport + " not found"));
        return mapToReadDto(passenger);
    }

    @Override
    public List<ReadPassengerDto> getPassengersByGender(Integer gender) {
        return passengerRepository.findByGender(gender).stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadPassengerDto> getAdultPassengers() {
        return passengerRepository.findAdultPassengers().stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadPassengerDto> getChildPassengers() {
        return passengerRepository.findChildPassengers().stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    @Override
    public String getGenderLabel(Integer gender) {
        if (gender == null) {
            return "Unknown";
        }
        switch (gender) {
            case 1:
                return "Male";
            case 2:
                return "Female";
            case 3:
                return "Other";
            default:
                return "Unknown";
        }
    }

    private ReadPassengerDto mapToReadDto(Passenger passenger) {
        return ReadPassengerDto.builder()
                .id(passenger.getId())
                .fullName(passenger.getFullName())
                .birthDate(passenger.getBirthDate())
                .age(calculateAge(passenger.getBirthDate()))
                .gender(passenger.getGender())
                .genderLabel(getGenderLabel(passenger.getGender()))
                .idPassport(passenger.getIdPassport())
                .createdAt(passenger.getCreatedAt())
                .updatedAt(passenger.getUpdatedAt())
                .build();
    }
}
