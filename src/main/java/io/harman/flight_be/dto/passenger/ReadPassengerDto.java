package io.harman.flight_be.dto.passenger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadPassengerDto {
    private UUID id;
    private String fullName;
    private LocalDate birthDate;
    private Integer age;
    private Integer gender; // 1 = Male, 2 = Female, 3 = Other
    private String genderLabel;
    private String idPassport;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
