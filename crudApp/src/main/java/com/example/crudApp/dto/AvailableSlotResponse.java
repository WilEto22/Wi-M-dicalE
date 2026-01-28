package com.example.crudApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailableSlotResponse {

    private LocalDateTime dateTime;
    private boolean available;
    private String displayTime; // Format lisible, ex: "10:00 - 10:30"

    public static AvailableSlotResponse of(LocalDateTime dateTime, boolean available) {
        return AvailableSlotResponse.builder()
                .dateTime(dateTime)
                .available(available)
                .displayTime(dateTime.toLocalTime().toString())
                .build();
    }
}
