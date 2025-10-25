package com.endo1116.combinationSpring.attendance.record;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findByEmployeeId(String employeeId);

    List<AttendanceRecord> findByEmployeeIdAndWorkDateBetween(String employeeId, LocalDate start, LocalDate end);
}
