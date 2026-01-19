package com.challenge.minidoodle.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class AvailabilityResponse {

    private Long userId;
    private String userEmail;
    private Instant queryStart;
    private Instant queryEnd;
    private List<TimeSlotResponse> availableSlots;
    private List<TimeSlotResponse> busySlots;
    private int totalAvailableSlots;
    private int totalBusySlots;

    public static AvailabilityResponse create(
            Long userId,
            String userEmail,
            Instant queryStart,
            Instant queryEnd,
            List<TimeSlotResponse> availableSlots,
            List<TimeSlotResponse> busySlots) {

        AvailabilityResponse response = new AvailabilityResponse();
        response.setUserId(userId);
        response.setUserEmail(userEmail);
        response.setQueryStart(queryStart);
        response.setQueryEnd(queryEnd);
        response.setAvailableSlots(availableSlots);
        response.setBusySlots(busySlots);
        response.setTotalAvailableSlots(availableSlots.size());
        response.setTotalBusySlots(busySlots.size());
        return response;
    }
}
