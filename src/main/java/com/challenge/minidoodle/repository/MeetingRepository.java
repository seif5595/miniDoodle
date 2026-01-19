package com.challenge.minidoodle.repository;

import com.challenge.minidoodle.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    boolean existsByTimeSlotId(Long timeSlotId);

    Optional<Meeting> findByTimeSlotId(Long timeSlotId);

    @Query("SELECT m FROM Meeting m WHERE m.organizer.id = :userId")
    List<Meeting> findByOrganizerId(@Param("userId") Long userId);

    @Query("SELECT m FROM Meeting m JOIN m.participants p WHERE p.id = :userId")
    List<Meeting> findByParticipantId(@Param("userId") Long userId);

    @Query("SELECT m FROM Meeting m WHERE m.organizer.id = :userId OR :userId IN (SELECT p.id FROM m.participants p)")
    List<Meeting> findByUserIdAsOrganizerOrParticipant(@Param("userId") Long userId);

    @Query("SELECT m FROM Meeting m WHERE m.timeSlot.startTime >= :start AND m.timeSlot.endTime <= :end")
    List<Meeting> findByTimeRange(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT m FROM Meeting m WHERE m.organizer.id = :userId " +
            "AND m.timeSlot.startTime >= :start AND m.timeSlot.endTime <= :end")
    List<Meeting> findByOrganizerIdAndTimeRange(
            @Param("userId") Long userId,
            @Param("start") Instant start,
            @Param("end") Instant end);
}
