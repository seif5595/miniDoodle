package com.challenge.minidoodle.dto;

import com.challenge.minidoodle.domain.Meeting;
import com.challenge.minidoodle.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class MeetingResponse {

    private Long id;
    private String title;
    private String description;
    private Long timeSlotId;
    private Instant startTime;
    private Instant endTime;
    private Long organizerId;
    private String organizerEmail;
    private Set<ParticipantInfo> participants;
    private Instant createdAt;

    @Getter
    @Setter
    public static class ParticipantInfo {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;

        public static ParticipantInfo fromUser(User user) {
            ParticipantInfo info = new ParticipantInfo();
            info.setId(user.getId());
            info.setEmail(user.getEmail());
            info.setFirstName(user.getFirstName());
            info.setLastName(user.getLastName());
            return info;
        }
    }

    public static MeetingResponse fromEntity(Meeting meeting) {
        MeetingResponse response = new MeetingResponse();
        response.setId(meeting.getId());
        response.setTitle(meeting.getTitle());
        response.setDescription(meeting.getDescription());
        response.setTimeSlotId(meeting.getTimeSlot().getId());
        response.setStartTime(meeting.getStartTime());
        response.setEndTime(meeting.getEndTime());
        response.setOrganizerId(meeting.getOrganizer().getId());
        response.setOrganizerEmail(meeting.getOrganizer().getEmail());
        response.setCreatedAt(meeting.getCreatedAt());
        response.setParticipants(
                meeting.getParticipants().stream()
                        .map(ParticipantInfo::fromUser)
                        .collect(Collectors.toSet())
        );
        return response;
    }
}
