package com.challenge.minidoodle.repository;

import com.challenge.minidoodle.domain.TimeSlot;
import com.challenge.minidoodle.domain.TimeSlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot> findByCalendarId(Long calendarId);

    List<TimeSlot> findByCalendarIdAndStatus(Long calendarId, TimeSlotStatus status);

    @Query("SELECT ts FROM TimeSlot ts WHERE ts.calendar.id = :calendarId " +
            "AND ts.startTime >= :start AND ts.endTime <= :end")
    List<TimeSlot> findByCalendarIdAndTimeRange(
            @Param("calendarId") Long calendarId,
            @Param("start") Instant start,
            @Param("end") Instant end);

    @Query("SELECT ts FROM TimeSlot ts WHERE ts.calendar.id = :calendarId " +
            "AND ts.status = :status " +
            "AND ts.startTime >= :start AND ts.endTime <= :end")
    List<TimeSlot> findByCalendarIdAndStatusAndTimeRange(
            @Param("calendarId") Long calendarId,
            @Param("status") TimeSlotStatus status,
            @Param("start") Instant start,
            @Param("end") Instant end);

    @Query("SELECT ts FROM TimeSlot ts WHERE ts.calendar.user.id = :userId")
    List<TimeSlot> findByUserId(@Param("userId") Long userId);

    @Query("SELECT ts FROM TimeSlot ts WHERE ts.calendar.user.id = :userId AND ts.status = :status")
    List<TimeSlot> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") TimeSlotStatus status);

    @Query("SELECT CASE WHEN COUNT(ts) > 0 THEN true ELSE false END FROM TimeSlot ts " +
            "WHERE ts.calendar.id = :calendarId " +
            "AND ((ts.startTime < :endTime AND ts.endTime > :startTime))")
    boolean existsOverlappingSlot(
            @Param("calendarId") Long calendarId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime);
}
