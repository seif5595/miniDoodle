package com.challenge.minidoodle.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class MeetingUpdateRequest {

    private String title;
    private String description;
    private Set<Long> participantIds;
}
