package com.challenge.minidoodle.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "time_slots")
@Getter
@Setter
@NoArgsConstructor
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    private Calendar calendar;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeSlotStatus status = TimeSlotStatus.AVAILABLE;

    @OneToOne(mappedBy = "timeSlot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Meeting meeting;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public TimeSlot(Calendar calendar, Instant startTime, Instant endTime) {
        this.calendar = calendar;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = TimeSlotStatus.AVAILABLE;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public boolean isAvailable() {
        return this.status == TimeSlotStatus.AVAILABLE;
    }

    public boolean isBusy() {
        return this.status == TimeSlotStatus.BUSY;
    }

    public void markAsBusy() {
        this.status = TimeSlotStatus.BUSY;
    }

    public void markAsAvailable() {
        this.status = TimeSlotStatus.AVAILABLE;
    }

    public long getDurationInMinutes() {
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }
}
