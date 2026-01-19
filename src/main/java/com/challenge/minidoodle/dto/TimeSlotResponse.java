package com.challenge.minidoodle.dto;

import com.challenge.minidoodle.domain.TimeSlot;
import com.challenge.minidoodle.domain.TimeSlotStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class TimeSlotResponse {

    private Long id;
    private Long userId;
    private String userEmail;
    private Instant startTime;
    private Instant endTime;
    private TimeSlotStatus status;
    private Long durationInMinutes;
    private Long meetingId;
    private Instant createdAt;

    public static TimeSlotResponse fromEntity(TimeSlot timeSlot) {
        TimeSlotResponse response = new TimeSlotResponse();
        response.setId(timeSlot.getId());
        response.setUserId(timeSlot.getCalendar().getUser().getId());
        response.setUserEmail(timeSlot.getCalendar().getUser().getEmail());
        response.setStartTime(timeSlot.getStartTime());
        response.setEndTime(timeSlot.getEndTime());
        response.setStatus(timeSlot.getStatus());
        response.setDurationInMinutes(timeSlot.getDurationInMinutes());
        response.setCreatedAt(timeSlot.getCreatedAt());
        if (timeSlot.getMeeting() != null) {
            response.setMeetingId(timeSlot.getMeeting().getId());
        }
        return response;
    }
}
