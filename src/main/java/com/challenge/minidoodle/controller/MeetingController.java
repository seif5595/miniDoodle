package com.challenge.minidoodle.controller;

import com.challenge.minidoodle.domain.Meeting;
import com.challenge.minidoodle.dto.MeetingRequest;
import com.challenge.minidoodle.dto.MeetingResponse;
import com.challenge.minidoodle.dto.MeetingUpdateRequest;
import com.challenge.minidoodle.service.MeetingService;
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
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
@Tag(name = "Meetings", description = "Meeting scheduling APIs")
public class MeetingController {

    private final MeetingService meetingService;

    @Operation(summary = "Create a meeting", description = "Converts an available time slot into a meeting")
    @PostMapping
    public ResponseEntity<MeetingResponse> createMeeting(@RequestBody MeetingRequest request) {
        Meeting meeting = meetingService.createMeeting(
                request.getTimeSlotId(),
                request.getOrganizerId(),
                request.getTitle(),
                request.getDescription(),
                request.getParticipantIds()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(MeetingResponse.fromEntity(meeting));
    }

    @Operation(summary = "Get meeting by ID")
    @GetMapping("/{id}")
    public ResponseEntity<MeetingResponse> getMeetingById(@PathVariable Long id) {
        return meetingService.getMeetingById(id)
                .map(meeting -> ResponseEntity.ok(MeetingResponse.fromEntity(meeting)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get meetings organized by a user")
    @GetMapping("/organizer/{userId}")
    public ResponseEntity<List<MeetingResponse>> getMeetingsByOrganizer(@PathVariable Long userId) {
        List<MeetingResponse> meetings = meetingService.getMeetingsByOrganizerId(userId).stream()
                .map(MeetingResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(meetings);
    }

    @Operation(summary = "Get meetings where user is a participant")
    @GetMapping("/participant/{userId}")
    public ResponseEntity<List<MeetingResponse>> getMeetingsByParticipant(@PathVariable Long userId) {
        List<MeetingResponse> meetings = meetingService.getMeetingsByParticipantId(userId).stream()
                .map(MeetingResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(meetings);
    }

    @Operation(summary = "Get all meetings for a user", description = "Returns meetings where user is organizer or participant")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MeetingResponse>> getAllMeetingsForUser(@PathVariable Long userId) {
        List<MeetingResponse> meetings = meetingService.getAllMeetingsForUser(userId).stream()
                .map(MeetingResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(meetings);
    }

    @Operation(summary = "Get meetings in time range")
    @GetMapping("/range")
    public ResponseEntity<List<MeetingResponse>> getMeetingsInRange(
            @Parameter(description = "Start of time range (ISO-8601)") @RequestParam Instant start,
            @Parameter(description = "End of time range (ISO-8601)") @RequestParam Instant end) {
        List<MeetingResponse> meetings = meetingService.getMeetingsByTimeRange(start, end).stream()
                .map(MeetingResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(meetings);
    }

    @Operation(summary = "Update meeting details")
    @PutMapping("/{id}")
    public ResponseEntity<MeetingResponse> updateMeeting(
            @PathVariable Long id,
            @RequestBody MeetingUpdateRequest request) {
        Meeting meeting = meetingService.updateMeeting(
                id,
                request.getTitle(),
                request.getDescription(),
                request.getParticipantIds()
        );
        return ResponseEntity.ok(MeetingResponse.fromEntity(meeting));
    }

    @Operation(summary = "Add participant to meeting")
    @PostMapping("/{meetingId}/participants/{userId}")
    public ResponseEntity<MeetingResponse> addParticipant(
            @PathVariable Long meetingId,
            @PathVariable Long userId) {
        Meeting meeting = meetingService.addParticipant(meetingId, userId);
        return ResponseEntity.ok(MeetingResponse.fromEntity(meeting));
    }

    @Operation(summary = "Remove participant from meeting")
    @DeleteMapping("/{meetingId}/participants/{userId}")
    public ResponseEntity<MeetingResponse> removeParticipant(
            @PathVariable Long meetingId,
            @PathVariable Long userId) {
        Meeting meeting = meetingService.removeParticipant(meetingId, userId);
        return ResponseEntity.ok(MeetingResponse.fromEntity(meeting));
    }

    @Operation(summary = "Cancel a meeting", description = "Cancels the meeting and marks the time slot as available again")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelMeeting(@PathVariable Long id) {
        meetingService.cancelMeeting(id);
        return ResponseEntity.noContent().build();
    }
}
