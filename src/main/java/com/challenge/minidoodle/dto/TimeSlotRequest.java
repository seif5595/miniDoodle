package com.challenge.minidoodle.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class TimeSlotRequest {

    private Long userId;
    private Instant startTime;
    private Instant endTime;
}
