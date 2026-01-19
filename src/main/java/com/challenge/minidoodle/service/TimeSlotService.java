package com.challenge.minidoodle.service;

import com.challenge.minidoodle.domain.TimeSlot;
import com.challenge.minidoodle.domain.TimeSlotStatus;
import com.challenge.minidoodle.domain.User;
import com.challenge.minidoodle.repository.TimeSlotRepository;
import com.challenge.minidoodle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;

    public TimeSlot createTimeSlot(Long userId, Instant startTime, Instant endTime) {
        validateTimeRange(startTime, endTime);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Long calendarId = user.getCalendar().getId();

        if (timeSlotRepository.existsOverlappingSlot(calendarId, startTime, endTime)) {
            throw new IllegalArgumentException("Time slot overlaps with an existing slot");
        }

        TimeSlot timeSlot = new TimeSlot(user.getCalendar(), startTime, endTime);
        return timeSlotRepository.save(timeSlot);
    }

    @Transactional(readOnly = true)
    public Optional<TimeSlot> getTimeSlotById(Long id) {
        return timeSlotRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<TimeSlot> getTimeSlotsByUserId(Long userId) {
        return timeSlotRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<TimeSlot> getAvailableSlotsByUserId(Long userId) {
        return timeSlotRepository.findByUserIdAndStatus(userId, TimeSlotStatus.AVAILABLE);
    }

    @Transactional(readOnly = true)
    public List<TimeSlot> getBusySlotsByUserId(Long userId) {
        return timeSlotRepository.findByUserIdAndStatus(userId, TimeSlotStatus.BUSY);
    }

    @Transactional(readOnly = true)
    public List<TimeSlot> getSlotsByUserIdAndTimeRange(Long userId, Instant start, Instant end) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return timeSlotRepository.findByCalendarIdAndTimeRange(user.getCalendar().getId(), start, end);
    }

    @Transactional(readOnly = true)
    public List<TimeSlot> getSlotsByUserIdAndStatusAndTimeRange(
            Long userId, TimeSlotStatus status, Instant start, Instant end) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return timeSlotRepository.findByCalendarIdAndStatusAndTimeRange(
                user.getCalendar().getId(), status, start, end);
    }

    public TimeSlot updateTimeSlot(Long id, Instant startTime, Instant endTime, TimeSlotStatus status) {
        TimeSlot timeSlot = timeSlotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Time slot not found with id: " + id));

        if (timeSlot.getMeeting() != null && status == TimeSlotStatus.AVAILABLE) {
            throw new IllegalArgumentException("Cannot mark slot as available when it has a meeting scheduled");
        }

        if (startTime != null && endTime != null) {
            validateTimeRange(startTime, endTime);
            timeSlot.setStartTime(startTime);
            timeSlot.setEndTime(endTime);
        }

        if (status != null) {
            timeSlot.setStatus(status);
        }

        return timeSlotRepository.save(timeSlot);
    }

    public TimeSlot markSlotAsBusy(Long id) {
        TimeSlot timeSlot = timeSlotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Time slot not found with id: " + id));

        timeSlot.markAsBusy();
        return timeSlotRepository.save(timeSlot);
    }

    public TimeSlot markSlotAsAvailable(Long id) {
        TimeSlot timeSlot = timeSlotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Time slot not found with id: " + id));

        if (timeSlot.getMeeting() != null) {
            throw new IllegalArgumentException("Cannot mark slot as available when it has a meeting scheduled");
        }

        timeSlot.markAsAvailable();
        return timeSlotRepository.save(timeSlot);
    }

    public void deleteTimeSlot(Long id) {
        TimeSlot timeSlot = timeSlotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Time slot not found with id: " + id));

        if (timeSlot.getMeeting() != null) {
            throw new IllegalArgumentException("Cannot delete slot with a scheduled meeting. Cancel the meeting first.");
        }

        timeSlotRepository.delete(timeSlot);
    }

    private void validateTimeRange(Instant startTime, Instant endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time are required");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        if (startTime.isBefore(Instant.now())) {
            throw new IllegalArgumentException("Cannot create time slot in the past");
        }
    }
}
