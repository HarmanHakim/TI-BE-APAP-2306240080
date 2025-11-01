package io.harman.flight_be.service;

import io.harman.flight_be.dto.airline.CreateAirlineDto;
import io.harman.flight_be.dto.airline.ReadAirlineDto;
import io.harman.flight_be.dto.airline.UpdateAirlineDto;
import io.harman.flight_be.model.Airline;
import io.harman.flight_be.repository.AirlineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AirlineServiceImpl implements AirlineService {

    private final AirlineRepository airlineRepository;

    public AirlineServiceImpl(AirlineRepository airlineRepository) {
        this.airlineRepository = airlineRepository;
    }

    @Override
    public List<ReadAirlineDto> getAllAirlines() {
        return airlineRepository.findAllByOrderByNameAsc().stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReadAirlineDto getAirlineById(String id) {
        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Airline with ID " + id + " not found"));
        return mapToReadDto(airline);
    }

    @Override
    public ReadAirlineDto createAirline(CreateAirlineDto createAirlineDto) {
        // Check if airline ID already exists
        if (airlineRepository.existsById(createAirlineDto.getId())) {
            throw new RuntimeException("Airline with ID " + createAirlineDto.getId() + " already exists");
        }

        Airline airline = Airline.builder()
                .id(createAirlineDto.getId())
                .name(createAirlineDto.getName())
                .country(createAirlineDto.getCountry())
                .build();

        Airline savedAirline = airlineRepository.save(airline);
        return mapToReadDto(savedAirline);
    }

    @Override
    public ReadAirlineDto updateAirline(UpdateAirlineDto updateAirlineDto) {
        Airline airline = airlineRepository.findById(updateAirlineDto.getId())
                .orElseThrow(() -> new RuntimeException("Airline with ID " + updateAirlineDto.getId() + " not found"));

        airline.setName(updateAirlineDto.getName());
        airline.setCountry(updateAirlineDto.getCountry());

        Airline updatedAirline = airlineRepository.save(airline);
        return mapToReadDto(updatedAirline);
    }

    @Override
    public void deleteAirline(String id) {
        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Airline with ID " + id + " not found"));

        airlineRepository.delete(airline);
    }

    @Override
    public List<ReadAirlineDto> getAirlinesByCountry(String country) {
        return airlineRepository.findByCountryOrderByNameAsc(country).stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadAirlineDto> searchAirlinesByName(String name) {
        return airlineRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getDistinctCountries() {
        return airlineRepository.findDistinctCountries();
    }

    @Override
    public long countAirlinesByCountry(String country) {
        return airlineRepository.countByCountry(country);
    }

    @Override
    public boolean existsById(String id) {
        return airlineRepository.existsById(id);
    }

    private ReadAirlineDto mapToReadDto(Airline airline) {
        return ReadAirlineDto.builder()
                .id(airline.getId())
                .name(airline.getName())
                .country(airline.getCountry())
                .createdAt(airline.getCreatedAt())
                .updatedAt(airline.getUpdatedAt())
                .build();
    }
}
