package com.challenge.minidoodle.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "calendars")
@Getter
@Setter
@NoArgsConstructor
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeSlot> timeSlots = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Calendar(User user) {
        this.user = user;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public void addTimeSlot(TimeSlot timeSlot) {
        timeSlots.add(timeSlot);
        timeSlot.setCalendar(this);
    }

    public void removeTimeSlot(TimeSlot timeSlot) {
        timeSlots.remove(timeSlot);
        timeSlot.setCalendar(null);
    }

    public List<TimeSlot> getAvailableSlots() {
        return timeSlots.stream()
                .filter(TimeSlot::isAvailable)
                .toList();
    }

    public List<TimeSlot> getBusySlots() {
        return timeSlots.stream()
                .filter(TimeSlot::isBusy)
                .toList();
    }

    public List<TimeSlot> getSlotsInRange(Instant start, Instant end) {
        return timeSlots.stream()
                .filter(slot -> !slot.getStartTime().isBefore(start) && !slot.getEndTime().isAfter(end))
                .toList();
    }
}
