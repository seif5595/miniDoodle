package com.challenge.minidoodle.dto;

import com.challenge.minidoodle.domain.TimeSlotStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class TimeSlotUpdateRequest {

    private Instant startTime;
    private Instant endTime;
    private TimeSlotStatus status;
}
