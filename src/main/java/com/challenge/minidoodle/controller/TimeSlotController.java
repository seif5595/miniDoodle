package com.challenge.minidoodle.controller;

import com.challenge.minidoodle.domain.TimeSlot;
import com.challenge.minidoodle.domain.TimeSlotStatus;
import com.challenge.minidoodle.domain.User;
import com.challenge.minidoodle.dto.AvailabilityResponse;
import com.challenge.minidoodle.dto.TimeSlotRequest;
import com.challenge.minidoodle.dto.TimeSlotResponse;
import com.challenge.minidoodle.dto.TimeSlotUpdateRequest;
import com.challenge.minidoodle.service.TimeSlotService;
import com.challenge.minidoodle.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/time-slots")
@RequiredArgsConstructor
@Tag(name = "Time Slots", description = "Time slot management APIs")
public class TimeSlotController {

    private final TimeSlotService timeSlotService;
    private final UserService userService;

    @Operation(summary = "Create a new time slot", description = "Creates an available time slot for a user")
    @PostMapping
    public ResponseEntity<TimeSlotResponse> createTimeSlot(@RequestBody TimeSlotRequest request) {
        TimeSlot timeSlot = timeSlotService.createTimeSlot(
                request.getUserId(),
                request.getStartTime(),
                request.getEndTime()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(TimeSlotResponse.fromEntity(timeSlot));
    }

    @Operation(summary = "Get time slot by ID")
    @GetMapping("/{id}")
    public ResponseEntity<TimeSlotResponse> getTimeSlotById(@PathVariable Long id) {
        return timeSlotService.getTimeSlotById(id)
                .map(slot -> ResponseEntity.ok(TimeSlotResponse.fromEntity(slot)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all time slots for a user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TimeSlotResponse>> getTimeSlotsByUserId(@PathVariable Long userId) {
        List<TimeSlotResponse> slots = timeSlotService.getTimeSlotsByUserId(userId).stream()
                .map(TimeSlotResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(slots);
    }

    @Operation(summary = "Get available time slots for a user")
    @GetMapping("/user/{userId}/available")
    public ResponseEntity<List<TimeSlotResponse>> getAvailableSlots(@PathVariable Long userId) {
        List<TimeSlotResponse> slots = timeSlotService.getAvailableSlotsByUserId(userId).stream()
                .map(TimeSlotResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(slots);
    }

    @Operation(summary = "Get busy time slots for a user")
    @GetMapping("/user/{userId}/busy")
    public ResponseEntity<List<TimeSlotResponse>> getBusySlots(@PathVariable Long userId) {
        List<TimeSlotResponse> slots = timeSlotService.getBusySlotsByUserId(userId).stream()
                .map(TimeSlotResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(slots);
    }

    @Operation(summary = "Get user availability for a time range", description = "Returns aggregated view of free and busy slots")
    @GetMapping("/user/{userId}/availability")
    public ResponseEntity<AvailabilityResponse> getUserAvailability(
            @PathVariable Long userId,
            @Parameter(description = "Start of time range (ISO-8601)") @RequestParam Instant start,
            @Parameter(description = "End of time range (ISO-8601)") @RequestParam Instant end) {

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<TimeSlotResponse> availableSlots = timeSlotService
                .getSlotsByUserIdAndStatusAndTimeRange(userId, TimeSlotStatus.AVAILABLE, start, end)
                .stream()
                .map(TimeSlotResponse::fromEntity)
                .collect(Collectors.toList());

        List<TimeSlotResponse> busySlots = timeSlotService
                .getSlotsByUserIdAndStatusAndTimeRange(userId, TimeSlotStatus.BUSY, start, end)
                .stream()
                .map(TimeSlotResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(AvailabilityResponse.create(
                userId, user.getEmail(), start, end, availableSlots, busySlots));
    }

    @Operation(summary = "Update a time slot")
    @PutMapping("/{id}")
    public ResponseEntity<TimeSlotResponse> updateTimeSlot(
            @PathVariable Long id,
            @RequestBody TimeSlotUpdateRequest request) {
        TimeSlot timeSlot = timeSlotService.updateTimeSlot(
                id,
                request.getStartTime(),
                request.getEndTime(),
                request.getStatus()
        );
        return ResponseEntity.ok(TimeSlotResponse.fromEntity(timeSlot));
    }

    @Operation(summary = "Mark time slot as busy")
    @PatchMapping("/{id}/busy")
    public ResponseEntity<TimeSlotResponse> markAsBusy(@PathVariable Long id) {
        TimeSlot timeSlot = timeSlotService.markSlotAsBusy(id);
        return ResponseEntity.ok(TimeSlotResponse.fromEntity(timeSlot));
    }

    @Operation(summary = "Mark time slot as available")
    @PatchMapping("/{id}/available")
    public ResponseEntity<TimeSlotResponse> markAsAvailable(@PathVariable Long id) {
        TimeSlot timeSlot = timeSlotService.markSlotAsAvailable(id);
        return ResponseEntity.ok(TimeSlotResponse.fromEntity(timeSlot));
    }

    @Operation(summary = "Delete a time slot")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeSlot(@PathVariable Long id) {
        timeSlotService.deleteTimeSlot(id);
        return ResponseEntity.noContent().build();
    }
}
