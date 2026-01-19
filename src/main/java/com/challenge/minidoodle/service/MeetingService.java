package com.challenge.minidoodle.service;

import com.challenge.minidoodle.domain.Meeting;
import com.challenge.minidoodle.domain.TimeSlot;
import com.challenge.minidoodle.domain.TimeSlotStatus;
import com.challenge.minidoodle.domain.User;
import com.challenge.minidoodle.repository.MeetingRepository;
import com.challenge.minidoodle.repository.TimeSlotRepository;
import com.challenge.minidoodle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;

    public Meeting createMeeting(Long timeSlotId, Long organizerId, String title,
                                 String description, Set<Long> participantIds) {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new IllegalArgumentException("Time slot not found with id: " + timeSlotId));

        // Check if time slot is available
        if (timeSlot.getStatus() != TimeSlotStatus.AVAILABLE) {
            throw new IllegalArgumentException("Time slot is not available for booking. Current status: " + timeSlot.getStatus());
        }

        // Check if a meeting already exists for this time slot (database check)
        Optional<Meeting> existingMeeting = meetingRepository.findByTimeSlotId(timeSlotId);
        if (existingMeeting.isPresent()) {
            throw new IllegalArgumentException("Time slot already has a meeting scheduled with ID: " + existingMeeting.get().getId());
        }

        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new IllegalArgumentException("Organizer not found with id: " + organizerId));

        // Verify organizer owns the time slot
        if (!timeSlot.getCalendar().getUser().getId().equals(organizerId)) {
            throw new IllegalArgumentException("Organizer does not own this time slot");
        }

        // Create the meeting
        Meeting meeting = new Meeting(title, description, timeSlot, organizer);

        // Add participants (excluding the organizer)
        if (participantIds != null && !participantIds.isEmpty()) {
            Set<User> participants = new HashSet<>();
            for (Long participantId : participantIds) {
                if (!participantId.equals(organizerId)) {
                    User participant = userRepository.findById(participantId)
                            .orElseThrow(() -> new IllegalArgumentException("Participant not found with id: " + participantId));
                    participants.add(participant);
                }
            }
            meeting.setParticipants(participants);
        }

        // Save the meeting first
        Meeting savedMeeting = meetingRepository.save(meeting);

        // Update the time slot - set both sides of the relationship
        timeSlot.setStatus(TimeSlotStatus.BUSY);
        timeSlot.setMeeting(savedMeeting);
        timeSlotRepository.save(timeSlot);

        return savedMeeting;
    }

    @Transactional(readOnly = true)
    public Optional<Meeting> getMeetingById(Long id) {
        return meetingRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Meeting> getMeetingsByOrganizerId(Long organizerId) {
        return meetingRepository.findByOrganizerId(organizerId);
    }

    @Transactional(readOnly = true)
    public List<Meeting> getMeetingsByParticipantId(Long participantId) {
        return meetingRepository.findByParticipantId(participantId);
    }

    @Transactional(readOnly = true)
    public List<Meeting> getAllMeetingsForUser(Long userId) {
        return meetingRepository.findByUserIdAsOrganizerOrParticipant(userId);
    }

    @Transactional(readOnly = true)
    public List<Meeting> getMeetingsByTimeRange(Instant start, Instant end) {
        return meetingRepository.findByTimeRange(start, end);
    }

    @Transactional(readOnly = true)
    public List<Meeting> getMeetingsByOrganizerAndTimeRange(Long organizerId, Instant start, Instant end) {
        return meetingRepository.findByOrganizerIdAndTimeRange(organizerId, start, end);
    }

    public Meeting updateMeeting(Long id, String title, String description, Set<Long> participantIds) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found with id: " + id));

        if (title != null && !title.isBlank()) {
            meeting.setTitle(title);
        }

        if (description != null) {
            meeting.setDescription(description);
        }

        if (participantIds != null) {
            Set<User> participants = new HashSet<>(userRepository.findAllById(participantIds));
            participants.removeIf(p -> p.getId().equals(meeting.getOrganizer().getId()));
            meeting.setParticipants(participants);
        }

        return meetingRepository.save(meeting);
    }

    public Meeting addParticipant(Long meetingId, Long userId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found with id: " + meetingId));

        if (meeting.getOrganizer().getId().equals(userId)) {
            throw new IllegalArgumentException("Organizer cannot be added as a participant");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        meeting.addParticipant(user);
        return meetingRepository.save(meeting);
    }

    public Meeting removeParticipant(Long meetingId, Long userId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found with id: " + meetingId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        meeting.removeParticipant(user);
        return meetingRepository.save(meeting);
    }

    public void cancelMeeting(Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found with id: " + id));

        TimeSlot timeSlot = meeting.getTimeSlot();

        // Clear the relationship on both sides
        timeSlot.setMeeting(null);
        timeSlot.setStatus(TimeSlotStatus.AVAILABLE);
        timeSlotRepository.save(timeSlot);

        meetingRepository.delete(meeting);
    }
}
